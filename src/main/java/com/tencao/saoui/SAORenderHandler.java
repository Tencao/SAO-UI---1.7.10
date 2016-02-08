package com.tencao.saoui;

import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.util.SAOColorState;
import com.tencao.saoui.util.SAOOption;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class SAORenderHandler {

    private boolean ticked = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    public static int REPLACE_GUI_DELAY = 0;
    public static boolean replaceGUI;
    public static HashMap<Class, Boolean> renderCheck = new HashMap<>();

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
    public void RenderTickEvent(TickEvent.RenderTickEvent event) {
        if ((event.type == TickEvent.Type.RENDER || event.type == TickEvent.Type.CLIENT) && event.phase == TickEvent.Phase.END) {
            if (!ticked && mc.ingameGUI != null) {
                mc.ingameGUI = new SAOIngameGUI(mc);
                ticked = true;
            }
        }
    }

    @SubscribeEvent
    public void RenderEntity(LivingEvent.LivingUpdateEvent event) {
        /*
        If some mobs don't get registered this way, that means the mods don't register their renderers at the right place.
         */
        if (!(renderCheck.containsKey(event.entityLiving.getClass()))){
            renderCheck.put(event.entityLiving.getClass(), false);
        } else if (renderCheck.get(event.entityLiving.getClass()).booleanValue()) return;
        else {
            RenderManager manager = RenderManager.instance;
            final Object value = manager.entityRenderMap.get(event.entityLiving.getClass());
            if (event.entityLiving instanceof EntityPlayer) {
                final RenderPlayer render = new SAORenderPlayer((RenderPlayer) value);
                manager.entityRenderMap.put(value, render);
                render.setRenderManager(manager);
                renderCheck.replace(event.entityLiving.getClass(), false, true);
                if (SAOOption.DEBUG_MODE.getValue()) System.out.print(event.entityLiving.getClass() + " passed to SAORenderPlayer" + "\n");
            } else {
                final Render render = new SAORenderBase((Render) value);
                manager.entityRenderMap.put(event.entityLiving.getClass(), render);
                render.setRenderManager(manager);
                renderCheck.replace(event.entityLiving.getClass(), false, true);
                if (SAOOption.DEBUG_MODE.getValue()) System.out.print(event.entityLiving.getClass() + " passed to SAORenderBase" + "\n");
            }
        }
    }

/*
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void RenderEntities(EntityEvent.EntityConstructing e) {
        if (!(renderCheck.containsKey(e.entity.getClass())))


        if (e.entity instanceof EntityLivingBase && !(renderCheck.containsKey(e.entity.getClass()))) {
            RenderManager manager = RenderManager.instance;
            EntityLivingBase entity = (EntityLivingBase) e.entity;
            final Object value = manager.entityRenderMap.get(e.entity.getClass());
            if (value instanceof SAORenderBase || value instanceof SAORenderPlayer) return;
            else if (value != null && !SAOColorState.savedState.containsKey(e.entity)) {
                if (!(entity instanceof AbstractClientPlayer) && !(value instanceof SAORenderBase)) {
                    final Render render = new SAORenderBase((Render) value);
                    manager.entityRenderMap.put(entity.getClass(), render);
                    render.setRenderManager(manager);
                    System.out.print(entity.getClass() + " passed to SAORenderBase" + "\n");
                } else if (!(value instanceof SAORenderPlayer)) {
                    final Render render = new SAORenderPlayer((Render) value);
                    manager.entityRenderMap.put(entity.getClass(), render);
                    render.setRenderManager(manager);
                    System.out.print(entity.getClass() + " passed to SAORenderPlayer" + "\n");
                } else System.out.print("Could not pass " + entity.getClass() + " to SAORender" + "\n");
            }
        }
    }
*/
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        SAORenderDispatcher.dispatch();
    }

}