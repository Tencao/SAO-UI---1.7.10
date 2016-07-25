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
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.ArrayList;
import java.util.List;

import static com.saomc.events.EventCore.mc;

@SideOnly(Side.CLIENT)
public class RenderHandler {

    static final List<EntityLivingBase> deadHandlers = new ArrayList<>();
    private static boolean menuGUI = true;

    static void checkingameGUI() {
        if (mc.ingameGUI != null && !(mc.ingameGUI instanceof IngameGUI))
            mc.ingameGUI = new IngameGUI(mc);
    }

    static void deathHandlers() {
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
                    System.out.print(ent.getCommandSenderName() + " passed deathHandlers \n");
                }
            }
        });
        deadHandlers.removeIf(ent -> ent.isDead);
    }

    static void guiInstance(GuiOpenEvent e) {
        if (OptionCore.DEBUG_MODE.getValue()) System.out.print(e.gui + " called GuiOpenEvent \n");

        if (e.gui instanceof GuiIngameMenu) {
            if (!(mc.currentScreen instanceof IngameMenuGUI)) {
                e.gui = new IngameMenuGUI(null);
            }
        }
        if (e.gui instanceof GuiInventory && !OptionCore.DEFAULT_INVENTORY.getValue()){
            if (mc.playerController.isInCreativeMode())
                e.gui = new GuiContainerCreative(mc.thePlayer);
            else if (!(mc.currentScreen instanceof IngameMenuGUI))
                e.gui = new IngameMenuGUI((GuiInventory) mc.currentScreen);
            else e.setCanceled(true);
        }
        if (e.gui instanceof GuiGameOver && (!OptionCore.DEFAULT_DEATH_SCREEN.getValue())){
            if (!(e.gui instanceof DeathScreen)) {
                e.gui = new DeathScreen();
            }
        }
        if (e.gui instanceof IngameMenuGUI)
            if (mc.currentScreen instanceof GuiOptions){
                e.setCanceled(true);
                mc.currentScreen.onGuiClosed();
                mc.setIngameFocus();
            }

    }

    static void deathCheck(){
        if (mc.currentScreen instanceof DeathScreen && mc.thePlayer.getHealth() > 0.0F){
            mc.currentScreen.onGuiClosed();
            mc.setIngameFocus();
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
            if (e.entity != null && e.entity != mc.thePlayer) {
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
        if (e.gui instanceof GuiMainMenu)
            e.gui = new MainMenuGUI();
    }

}