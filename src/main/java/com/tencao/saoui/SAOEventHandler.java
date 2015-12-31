package com.tencao.saoui;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void livingAttack(LivingAttackEvent e) {
     	this.livingHit(e.entityLiving, e.source.getEntity());
    }

    @SubscribeEvent
    public void livingHurt(LivingHurtEvent e) {
     	this.livingHit(e.entityLiving, e.source.getEntity());
    }

    private void livingHit(EntityLivingBase target, Entity source) {
        if (target instanceof EntityPlayer && source instanceof EntityPlayer) {
            if (target.getHealth() <= 0) {
                SAOMod.onKillPlayer((EntityPlayer) source);
            } else {
                SAOMod.onDamagePlayer((EntityPlayer) source);
             }
         }
     }
    @SubscribeEvent
    public void livingDeath(LivingDeathEvent e) {
        if (e.entityLiving instanceof EntityPlayer) {
            if (e.source.getEntity() instanceof EntityPlayer) {
                SAOMod.onKillPlayer((EntityPlayer) e.source.getEntity());
            }
        }
    }

    @SubscribeEvent
    public void livingDrop(LivingDropsEvent e) {
        if (e.entityLiving instanceof EntityPlayer) {
            if (e.source.getEntity() instanceof EntityPlayer) {
                SAOMod.onKillPlayer((EntityPlayer) e.source.getEntity());
            }
        }
    }

    @SubscribeEvent
    public void playerAttackEntity(AttackEntityEvent e) {
        if (e.target instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) e.target;

            if (player.getHealth() <= 0) {
                SAOMod.onKillPlayer(e.entityPlayer);
            } else {
                SAOMod.onDamagePlayer(e.entityPlayer);
            }
        }
    }

    @SubscribeEvent
    public void playerDrops(PlayerDropsEvent e) {
        if (e.source.getEntity() instanceof EntityPlayer) {
            SAOMod.onKillPlayer((EntityPlayer) e.source.getEntity());
        }
    }

    @SubscribeEvent
    public void colorstateupdate (LivingEvent.LivingUpdateEvent e){
        long time = System.currentTimeMillis();
        long lasttime = time;

        long delay;

        time = System.currentTimeMillis();
        delay = Math.abs(time - lasttime);
        if (e.entityLiving != null) SAOMod.colorStates.values().stream().forEach(cursor -> cursor.update(delay));
    }

    @SubscribeEvent
    public void abilityCheck (TickEvent.ClientTickEvent e){
        if (mc.thePlayer == null) {
            SAOMod.IS_SPRINTING = false;
            SAOMod.IS_SNEAKING = false;
        } else if (mc.inGameHasFocus) {
            if (SAOMod.IS_SPRINTING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (SAOMod.IS_SNEAKING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }
}
