package com.saomc.events;

import com.saomc.SoundCore;
import com.saomc.renders.StaticRenderer;
import com.saomc.screens.death.DeathScreen;
import com.saomc.screens.ingame.IngameGUI;
import com.saomc.screens.menu.IngameMenuGUI;
import com.saomc.screens.menu.MainMenuGUI;
import com.saomc.screens.menu.StartupGUI;
import com.saomc.screens.window.ScreenGUI;
import com.saomc.util.OptionCore;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderHandler {

    private static final List<EntityLivingBase> deadHandlers = new ArrayList<>();
    private static boolean ticked = false;
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static int REPLACE_GUI_DELAY = 0;
    public static boolean replaceGUI;
    private static boolean menuGUI = true;

    static void checkingameGUI() {
        boolean b = mc.ingameGUI instanceof IngameGUI;
        if (mc.ingameGUI != null && OptionCore.VANILLA_UI.getValue() == b)
            mc.ingameGUI = b ? new GuiIngameForge(mc) : new IngameGUI(mc);
        deadHandlers.forEach(ent -> {
            if (ent != null) {
                final boolean deadStart = (ent.deathTime == 1);
                final boolean deadExactly = (ent.deathTime >= 18);
                if (deadStart) {
                    ent.deathTime++;
                    SoundCore.playAtEntity(ent, SoundCore.PARTICLES_DEATH);
                }

                if (deadExactly) {
                    StaticRenderer.doSpawnDeathParticles(mc, ent);
                    ent.setDead();
                }
            }
        });
        deadHandlers.removeIf(ent -> ent.isDead);
    }

    static void checkGuiInstance() {
        if ((mc.currentScreen == null) && (mc.inGameHasFocus)) replaceGUI = true;
        else if (replaceGUI) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof ScreenGUI)) {
                if (REPLACE_GUI_DELAY > 0) REPLACE_GUI_DELAY--;
                else if ((mc.currentScreen instanceof GuiIngameMenu) || ((mc.currentScreen instanceof GuiInventory) && (!OptionCore.DEFAULT_INVENTORY.getValue()))) {
                    final boolean inv = (mc.currentScreen instanceof GuiInventory);

                    mc.currentScreen.mc = mc;
                    if (mc.playerController.isInCreativeMode() && mc.currentScreen instanceof GuiInventory)
                        mc.displayGuiScreen(new GuiContainerCreative(mc.thePlayer));
                    else try {
                        SoundCore.play(mc, SoundCore.ORB_DROPDOWN);
                        mc.displayGuiScreen(new IngameMenuGUI((inv ? (GuiInventory)mc.currentScreen : null)));
                        replaceGUI = false;
                    } catch (NullPointerException ignored) {
                    }
                } else if ((mc.currentScreen instanceof GuiGameOver) && (!OptionCore.DEFAULT_DEATH_SCREEN.getValue())) {
                    mc.currentScreen.mc = mc;

                    if (mc.ingameGUI instanceof IngameGUI) {
                        try {
                            mc.displayGuiScreen(null);
                            mc.displayGuiScreen(new DeathScreen((GuiGameOver) mc.currentScreen));
                            replaceGUI = false;
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            }
        }
    }

    static void renderTickEvent(TickEvent.RenderTickEvent event) {
        if ((event.type == TickEvent.Type.RENDER || event.type == TickEvent.Type.CLIENT) && event.phase == TickEvent.Phase.END) {
            if (!ticked && mc.ingameGUI != null) {
                mc.ingameGUI = new IngameGUI(mc);
                ticked = true;
            }
        }
    }

    static void renderPlayer(RenderPlayerEvent.Post e) {
        if (!OptionCore.UI_ONLY.getValue()) {
            RenderManager manager = RenderManager.instance;
            if (e.entityPlayer != null) {
                StaticRenderer.render(manager, e.entityPlayer, e.entityPlayer.posX, e.entityPlayer.posY, e.entityPlayer.posZ);
            }
        }
    }

    static void renderEntity(RenderLivingEvent.Post e) {
        if (!OptionCore.UI_ONLY.getValue()) {
            RenderManager manager = RenderManager.instance;
            if (e.entity != mc.thePlayer) {
                StaticRenderer.render(manager, e.entity, e.x, e.y, e.z);
            }
        }
    }

    static void mainMenuGUI(GuiOpenEvent e){
        if (menuGUI)
            if (e.gui instanceof GuiMainMenu)
                if (StartupGUI.shouldShow()) {
                    e.gui = new StartupGUI();
                    menuGUI = false;
                }
        else if (e.gui instanceof GuiMainMenu)
                    e.gui = new MainMenuGUI();
    }

}