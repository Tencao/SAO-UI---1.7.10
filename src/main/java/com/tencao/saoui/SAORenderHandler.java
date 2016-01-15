package com.tencao.saoui;

import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.util.SAOOption;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.event.entity.EntityEvent;

@SideOnly(Side.CLIENT)
public class SAORenderHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean replaceGUI = SAOMod.replaceGUI;

    @SubscribeEvent
    public void checkingameGUI(TickEvent.RenderTickEvent e) {
        boolean b = mc.ingameGUI instanceof SAOIngameGUI;
        if (mc.ingameGUI != null && SAOOption.DEFAULT_UI.getValue() == b)
            mc.ingameGUI = b ? new GuiIngameForge(mc) : new SAOIngameGUI(mc);
    }

    @SubscribeEvent
    public void checkGuiInstance(TickEvent.RenderTickEvent e) {
        if ((mc.currentScreen == null) && (mc.inGameHasFocus)) replaceGUI = true;
        else if (replaceGUI) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof SAOScreenGUI)) {
                if (SAOMod.REPLACE_GUI_DELAY > 0) SAOMod.REPLACE_GUI_DELAY--;
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void RenderEntities(EntityEvent.EntityConstructing e) {
        final Minecraft mc = Minecraft.getMinecraft();
        RenderManager manager = RenderManager.instance;
        manager.entityRenderMap.keySet().stream().filter(key -> key instanceof Class<?>).filter(key -> EntityLivingBase.class.isAssignableFrom((Class<?>) key)).forEach(key -> {
            final Object value = manager.entityRenderMap.get(key);

            //if (EntityLivingBase.canEntityBeSeen()
            if (value instanceof Render) {
                if (e.entity instanceof EntityPlayer && !(value instanceof SAORenderPlayer)) {
                    final Render render = new SAORenderPlayer((Render) value);
                    manager.entityRenderMap.put(key, render);
                    render.setRenderManager(manager);
                } else if (!(value instanceof SAORenderBase)) {
                    final Render render = new SAORenderBase((Render) value);
                    manager.entityRenderMap.put(key, render);
                    render.setRenderManager(manager);
                }
            }
        });/*
        try {
            Class<? extends RenderManager> mgCl = manager.getClass();
            Field skinField = mgCl.getDeclaredField("skinMap");
            skinField.setAccessible(true);
            Map skinMap = (Map) skinField.get(manager);
            skinMap.keySet().stream().forEach(key -> {
                final Object value = skinMap.get(key);

                if (value instanceof Render && !(value instanceof SAORenderPlayer)) {
                    final Render render = new SAORenderPlayer((Render) value);
                    skinMap.put(key, render);
                    render.setRenderManager(manager);
                }
            });
        } catch (NoSuchFieldException | IllegalAccessException er) {
            er.printStackTrace();
            System.err.println("SAOUI Couldn't change the PlayerRenderer!");
        }*/
    }

}
