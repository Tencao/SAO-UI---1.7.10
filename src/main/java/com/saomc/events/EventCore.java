package com.saomc.events;

import com.saomc.colorstates.ColorStateHandler;
import com.saomc.effects.RenderDispatcher;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * This is the core for all event handlers, listening to events then passing on to the other events that need it.
 */
public class EventCore {

    static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void chatListener(ClientChatReceivedEvent e){
        EventHandler.nameNotification(e);
        EventHandler.chatCommand(e);
    }

    @SubscribeEvent
    public void clientTickListener(TickEvent.ClientTickEvent e) {
        EventHandler.abilityCheck();
    }

    @SubscribeEvent
    public void renderTickListener(TickEvent.RenderTickEvent e) {
        RenderHandler.deathHandlers();
        StateEventHandler.checkTicks(e);
        RenderHandler.deathCheck();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e){
        ColorStateHandler.getInstance().clean();
    }

    @SubscribeEvent
    public void constructingListener(EntityEvent.EntityConstructing e){
        StateEventHandler.genStateMaps(e);
    }

    @SubscribeEvent
    public void renderPlayerListener(RenderPlayerEvent.Post e){
        RenderHandler.renderPlayer(e);
    }

    @SubscribeEvent
    public void renderEntityListener(RenderLivingEvent.Post e){
        RenderHandler.renderEntity(e);
    }

    @SubscribeEvent
    public void renderEntityListener(LivingDeathEvent e){
        EventHandler.onKill(e);
    }

    @SubscribeEvent
    public void renderWorldListener(RenderWorldLastEvent event) {
        RenderDispatcher.dispatch();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void guiOpenListener (GuiOpenEvent e){
        RenderHandler.guiInstance(e);
        RenderHandler.mainMenuGUI(e);
    }

    @SubscribeEvent
    public void guiListener (GuiScreenEvent e){
        RenderHandler.checkingameGUI();
    }
}