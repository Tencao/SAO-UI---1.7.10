package com.tencao.saoui.events;

import com.tencao.saoui.SAOCore;
import com.tencao.saoui.SoundCore;
import com.tencao.saoui.colorstates.ColorStateHandler;
import com.tencao.saoui.colorstates.ColorState;
import com.tencao.saoui.commands.Command;
import com.tencao.saoui.util.*;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

@SideOnly(Side.CLIENT)
public class EventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean isPlaying = false;

    public void stateChanger(EntityLivingBase entity, boolean major, boolean aggro){
        if (entity instanceof EntityPlayer) {
            ColorState state = ColorStateHandler.getInstance().getSavedState(entity);
            if (major){
                if (state == ColorState.VIOLENT) {
                    ColorStateHandler.getInstance().set(entity, ColorState.KILLER, true);
                }else if (state == ColorState.INNOCENT || state == null)
                    ColorStateHandler.getInstance().set(entity, ColorState.VIOLENT, true);
            } else if (state == ColorState.INNOCENT || state == null)
                ColorStateHandler.getInstance().set(entity, ColorState.VIOLENT, true);
        }
        else {
            ColorState defaultState =ColorStateHandler.getInstance().getDefault(entity);
            ColorState state = ColorStateHandler.getInstance().getSavedState(entity);
            if (aggro && defaultState == ColorState.VIOLENT)
                ColorStateHandler.getInstance().set(entity, ColorState.KILLER, true);
            else if (major) {
                if (state == ColorState.INNOCENT)
                    ColorStateHandler.getInstance().set(entity, ColorState.VIOLENT, true);
                else if (state == ColorState.VIOLENT)
                    ColorStateHandler.getInstance().set(entity, ColorState.KILLER, true);
            } else {
                if (defaultState == ColorState.INNOCENT && state != ColorState.VIOLENT)
                    ColorStateHandler.getInstance().set(entity, ColorState.VIOLENT, true);
                else if (defaultState == ColorState.VIOLENT && state != ColorState.KILLER)
                    ColorStateHandler.getInstance().set(entity, ColorState.KILLER, true);
            }
        }
    }

    public static void getColor(EntityLivingBase entity){
        ColorStateHandler.getInstance().stateColor(entity);
    }

    @SubscribeEvent
    public void checkAggro(LivingSetAttackTargetEvent e) {
        if (OptionCore.AGGRO_SYSTEM.getValue() && ColorStateHandler.getInstance().getSavedState(e.entityLiving) != ColorState.KILLER  && e.getPhase().equals(TickEvent.Phase.END))
            if (e.target instanceof EntityPlayer) {
                stateChanger(e.entityLiving, false, true);
                System.out.print(e.entityLiving.getCommandSenderName() + " sent to State Changer from checkAggro" + "\n");
            }
    }

    @SubscribeEvent
    public void checkAttack(LivingAttackEvent e) {
        if (OptionCore.AGGRO_SYSTEM.getValue() && e.getPhase().equals(TickEvent.Phase.END))
            if (e.source.getEntity() instanceof IAnimals)
                if (e.entityLiving instanceof EntityPlayer) {
                    if (e.entityLiving.getHealth() <= 0)
                        stateChanger((EntityLivingBase) e.source.getEntity(), true, false);
                    else stateChanger((EntityLivingBase) e.source.getEntity(), false, false);
                    if (OptionCore.DEBUG_MODE.getValue())
                        System.out.print(e.source.getEntity().getCommandSenderName() + " sent to State Changer from checkAttack" + "\n");
                }
    }

    @SubscribeEvent
    public void checkPlayerAttack(AttackEntityEvent e) {
        if (OptionCore.AGGRO_SYSTEM.getValue() && e.getPhase().equals(TickEvent.Phase.END))
            if (e.target instanceof EntityPlayer && e.target.getUniqueID() != e.entityPlayer.getUniqueID()) {
                if (((EntityPlayer) e.target).getHealth() <= 0) stateChanger(e.entityPlayer, true, false);
                else stateChanger(e.entityPlayer, false, false);
                if (OptionCore.DEBUG_MODE.getValue())
                    System.out.print(e.entityPlayer.getCommandSenderName() + " sent to State Changer from checkPlayerAttack" + "\n");
            }
    }

    @SubscribeEvent
    public void checkKill(LivingDeathEvent e){
        if (OptionCore.AGGRO_SYSTEM.getValue() && e.getPhase().equals(TickEvent.Phase.END)) {
            if (e.source.getEntity() instanceof EntityLivingBase)
                if (e.entityLiving instanceof EntityPlayer) {
                    stateChanger((EntityLivingBase) e.source.getEntity(), true, false);
                    if (OptionCore.DEBUG_MODE.getValue())
                        System.out.print(e.source.getEntity().getCommandSenderName() + " sent to State Changer from checkKill" + "\n");
                }
            if (!(e.entityLiving instanceof EntityPlayer)) ColorStateHandler.getInstance().remove(e.entityLiving);
        }
        if (OptionCore.PARTICLES.getValue() && e.entity.worldObj.isRemote) RenderHandler.deadHandlers.add(e.entityLiving);
    }

    @SubscribeEvent
    public void resetState(TickEvent.RenderTickEvent e){
        if (OptionCore.AGGRO_SYSTEM.getValue())ColorStateHandler.getInstance().updateKeeper();
        if (!OptionCore.AGGRO_SYSTEM.getValue()){
            if (!ColorStateHandler.getInstance().isEmpty())ColorStateHandler.getInstance().clean();
        }
    }

    @SubscribeEvent
    public void cleanStateMaps(FMLNetworkEvent.ClientDisconnectionFromServerEvent e){
        ColorStateHandler.getInstance().clean();
    }

    @SubscribeEvent
    public void disableAggroOnServer(FMLNetworkEvent.ClientConnectedToServerEvent e){
        if (!e.isLocal && OptionCore.AGGRO_SYSTEM.getValue()) OptionCore.AGGRO_SYSTEM.flip();
    }

    @SubscribeEvent
    public void genStateMaps(EntityEvent.EntityConstructing e){
        if (e.entity instanceof EntityLivingBase)
            if (ColorStateHandler.getInstance().getDefault((EntityLivingBase)e.entity) == null && !(e.entity instanceof EntityPlayer))
                ColorStateHandler.getInstance().genDefaultState((EntityLivingBase)e.entity);
    }

    @SubscribeEvent
    public void nameNotification(ClientChatReceivedEvent e){
        if (e.message.getUnformattedTextForChat().contains(mc.thePlayer.getDisplayName())) SoundCore.play(mc, SoundCore.MESSAGE);
    }

    @SubscribeEvent
    public void abilityCheck(TickEvent.ClientTickEvent e) {
        if (mc.thePlayer == null) {
            SAOCore.IS_SPRINTING = false;
            SAOCore.IS_SNEAKING = false;
        } else if (mc.inGameHasFocus) {
            if (SAOCore.IS_SPRINTING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (SAOCore.IS_SNEAKING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void chatEvent(ClientChatReceivedEvent evt) {
        if (Command.processCommand(evt.message.getUnformattedText())) evt.setCanceled(true);// TODO: add pm feature and PT chat
    }

}
