package com.tencao.saoui;

import com.tencao.saoui.commands.Command;
import com.tencao.saoui.util.*;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class SAOEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean isPlaying = false;

    public static void stateChanger(EntityLivingBase entity, boolean major){
        if (entity instanceof EntityPlayer) {
            SAOColorState state = ColorStateHandler.getCurrent(entity);
            if (major){
                if (state == SAOColorState.VIOLENT)
                    ColorStateHandler.set(entity, SAOColorState.KILLER, true);
                else if (state == SAOColorState.INNOCENT)
                    ColorStateHandler.set(entity, SAOColorState.VIOLENT, true);
            } else if (state == SAOColorState.INNOCENT)
                ColorStateHandler.set(entity, SAOColorState.VIOLENT, true);
        }
        else {
            SAOColorState state = major ? ColorStateHandler.getCurrent(entity) : ColorStateHandler.getDefault(entity);
            if (state == SAOColorState.INNOCENT)
                ColorStateHandler.set(entity, SAOColorState.VIOLENT, true);
            else if (state == SAOColorState.VIOLENT)
                ColorStateHandler.set(entity, SAOColorState.KILLER, true);
        }
    }

    @SubscribeEvent
    public void checkAggro(LivingSetAttackTargetEvent e) {
        if (e.target instanceof EntityPlayer)
            if (!(e.entityLiving.getLastAttacker() == e.target)){
                stateChanger(e.entityLiving, false);
                System.out.print(e.entityLiving.getCommandSenderName() + " sent to State Changer from checkAggro");
            }
    }

    @SubscribeEvent
    public void checkAttack(LivingAttackEvent e) {
        if (e.source.getEntity() instanceof IAnimals)
            if (e.entityLiving instanceof EntityPlayer) {
                if (e.entityLiving.getHealth() <= 0) stateChanger((EntityLivingBase) e.source.getEntity(), true);
                else stateChanger((EntityLivingBase) e.source.getEntity(), false);
                System.out.print(e.source.getEntity().getCommandSenderName() + " sent to State Changer from checkAttack");
            }
    }

    @SubscribeEvent
    public void checkPlayerAttack(AttackEntityEvent e) {
        if (e.target instanceof EntityPlayer && e.target.getUniqueID() != e.entityPlayer.getUniqueID()) {
            if (((EntityPlayer) e.target).getHealth() <= 0) stateChanger(e.entityPlayer, true);
            else stateChanger(e.entityPlayer, false);
            System.out.print(e.entityPlayer.getCommandSenderName() + " sent to State Changer from checkPlayerAttack");
        }
    }

    @SubscribeEvent
    public void checkKill(LivingDeathEvent e){
        if (e.source.getEntity() instanceof EntityLivingBase)
            if (e.entityLiving instanceof EntityPlayer) {
                stateChanger((EntityLivingBase) e.source.getEntity(), true);
                System.out.print(e.source.getEntity().getCommandSenderName() + " sent to State Changer from checkKill");
            }
        if (SAOOption.PARTICLES.getValue() && e.entity.worldObj.isRemote) SAORenderHandler.deadHandlers.add(e.entityLiving);
    }

    @SubscribeEvent
    public void resetState(TickEvent.WorldTickEvent e){
        if (!(ColorStateHandler.stateKeeper.isEmpty())) {
            for (Map.Entry<UUID, Integer> entry : ColorStateHandler.stateKeeper.entrySet()) {
                UUID uuid = entry.getKey();
                int ticks = entry.getValue();
                --ticks;
                if (ticks == 0) {
                    ColorStateHandler.reset(uuid);
                } else {
                    ColorStateHandler.stateKeeper.put(uuid, ticks);
                }
            }
        }
    }

    @SubscribeEvent
    public void abilityCheck(TickEvent.ClientTickEvent e) {
        if (mc.thePlayer == null) {
            SAOMod.IS_SPRINTING = false;
            SAOMod.IS_SNEAKING = false;
        } else if (mc.inGameHasFocus) {
            if (SAOMod.IS_SPRINTING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (SAOMod.IS_SNEAKING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void chatEvent(ClientChatReceivedEvent evt) {
        if (Command.processCommand(evt.message.getUnformattedText())) evt.setCanceled(true);// TODO: add pm feature and PT chat
    }

}
