package com.tencao.saoui;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOEventHandler {

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

}
