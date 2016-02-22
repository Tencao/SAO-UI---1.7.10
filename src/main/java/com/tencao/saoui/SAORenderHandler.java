package com.tencao.saoui;

import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.util.ColorStateHandler;
import com.tencao.saoui.util.SAOOption;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SAORenderHandler {

    public static final List<EntityLivingBase> deadHandlers = new ArrayList<>();
    private boolean ticked = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    public static int REPLACE_GUI_DELAY = 0;
    public static boolean replaceGUI;

    @SubscribeEvent
    public void checkingameGUI(TickEvent.RenderTickEvent e) {
        boolean b = mc.ingameGUI instanceof SAOIngameGUI;
        if (mc.ingameGUI != null && SAOOption.DEFAULT_UI.getValue() == b)
            mc.ingameGUI = b ? new GuiIngameForge(mc) : new SAOIngameGUI(mc);
        deadHandlers.forEach(ent -> {
            if (ent != null) {
                final boolean deadStart = (ent.deathTime == 1);
                final boolean deadExactly = (ent.deathTime >= 18);
                if (deadStart) {
                    ent.deathTime++;
                    SAOSound.playAtEntity(ent, SAOSound.PARTICLES_DEATH);
                }

                if (deadExactly) {
                    StaticRenderer.doSpawnDeathParticles(mc, ent);
                    ent.setDead();
                }
            }
        });
        deadHandlers.removeIf(ent -> ent.isDead);
    }

    @SubscribeEvent
    public void checkGuiInstance(TickEvent.RenderTickEvent e) {
        if ((mc.currentScreen == null) && (mc.inGameHasFocus)) replaceGUI = true;
        else if (replaceGUI) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof SAOScreenGUI)) {
                if (REPLACE_GUI_DELAY > 0) REPLACE_GUI_DELAY--;
                else if ((mc.currentScreen instanceof GuiIngameMenu) || ((mc.currentScreen instanceof GuiInventory) && (!SAOOption.DEFAULT_INVENTORY.getValue()))) {
                    final boolean inv = (mc.currentScreen instanceof GuiInventory);

                    mc.currentScreen.mc = mc;
                    if (mc.playerController.isInCreativeMode() && mc.currentScreen instanceof GuiInventory)
                        mc.displayGuiScreen(new GuiContainerCreative(mc.thePlayer));
                    else try {
                        SAOSound.play(mc, SAOSound.ORB_DROPDOWN);
                        mc.displayGuiScreen(new SAOIngameMenuGUI((GuiInventory) (inv ? mc.currentScreen : null)));
                        replaceGUI = false;
                    } catch (NullPointerException ignored) {
                    }
                } else if ((mc.currentScreen instanceof GuiGameOver) && (!SAOOption.DEFAULT_DEATH_SCREEN.getValue())) {
                    mc.currentScreen.mc = mc;

                    if (mc.ingameGUI instanceof SAOIngameGUI) {
                        try {
                            mc.displayGuiScreen(null);
                            mc.displayGuiScreen(new SAODeathGUI((GuiGameOver) mc.currentScreen));
                            replaceGUI = false;
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void renderTickEvent(TickEvent.RenderTickEvent event) {
        if ((event.type == TickEvent.Type.RENDER || event.type == TickEvent.Type.CLIENT) && event.phase == TickEvent.Phase.END) {
            if (!ticked && mc.ingameGUI != null) {
                mc.ingameGUI = new SAOIngameGUI(mc);
                ticked = true;
            }
        }
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post e) {
        RenderManager manager = RenderManager.instance;
        if (e.entityPlayer != null) {
            ColorStateHandler.getInstance().genPlayerStates(e.entityPlayer);
            StaticRenderer.render(manager, e.entityPlayer, e.entityPlayer.posX, e.entityPlayer.posY, e.entityPlayer.posZ);
        }
    }

    @SubscribeEvent
    public void renderEntity(RenderLivingEvent.Post e) {
        RenderManager manager = RenderManager.instance;
        if (e.entity != mc.thePlayer) {
            ColorStateHandler.getInstance().genColorStates(e.entity);
            StaticRenderer.render(manager, e.entity, e.x, e.y, e.z);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        SAORenderDispatcher.dispatch();
    }

}