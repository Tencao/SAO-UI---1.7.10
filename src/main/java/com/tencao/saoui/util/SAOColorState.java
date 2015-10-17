package com.tencao.saoui.util;

import com.tencao.saoui.SAOMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SAOColorState {

    INNOCENT(0x93F43EFF),
    VIOLENT(0xF49B00FF),
    KILLER(0xBD0000FF),

    CREATIVE(0xEDE24CFF),
    GAMEMASTER(0x79139EFF);

    private final int color;

    SAOColorState(int argb) {
        color = argb;
    }

    public final void glColor() {
        SAOGL.glColorRGBA(color);
    }

    public static SAOColorState getColorState(Minecraft mc, Entity entity, float time) {
        if (entity instanceof EntityPlayer) {
            return SAOMod.isCreative((AbstractClientPlayer) entity)? CREATIVE : getPlayerColorState(mc, (EntityPlayer) entity, time);
        } else if (entity instanceof EntityCreature) {
            return getCreatureColorState((EntityCreature) entity);
        } else if (entity instanceof EntityLiving) {
        	return getLivingColorState((EntityLiving) entity);
        } else if ((entity.hitByEntity(entity)) && (entity instanceof EntityLiving) && (((EntityCreature)entity).getAITarget() instanceof EntityPlayer) || 
        		(entity.hitByEntity(entity)) && (entity instanceof EntityLiving) && (((EntityCreature)entity).getAttackTarget() instanceof EntityPlayer)) {
         		return KILLER;
        } else
        	return VIOLENT;
    }

    private static SAOColorState getCreatureColorState(EntityCreature creature) {
    	if ((creature instanceof EntityCreature) && (creature instanceof IMob) && !(creature instanceof EntityPigZombie) ||
    		(creature instanceof EntityWolf) && (((EntityWolf) creature).isAngry())){
    		return KILLER;
    	} else if ((creature instanceof EntityCreature) && (creature instanceof EntityAnimal) && !(creature instanceof EntityWolf) ||
    		(creature instanceof EntityWolf) && !(creature.getAttackTarget() instanceof EntityPlayer) && !(creature.getAITarget() instanceof EntityPlayer) && !(((EntityWolf) creature).isAngry()) ||
    		(creature instanceof EntityCreature) && (creature instanceof INpc)){
    		return INNOCENT;
    	} else{
    		return VIOLENT;
    	}
    }
    
    private static SAOColorState getLivingColorState(EntityLiving creature) {
        if ((creature instanceof EntityLiving) && (creature instanceof IMob)) {
            return KILLER;
        } else if ((creature instanceof EntityLiving) && (creature instanceof IAnimals)) {
            return INNOCENT;
        } else {
        	return VIOLENT;
        }
    }

    private static SAOColorState getPlayerColorState(Minecraft mc, EntityPlayer player, float time) {
        if (isDev(SAOMod.getName(player))) {
			return GAMEMASTER;
		} else {
            return SAOMod.getColorState(player);
        }
    }

    private static boolean isDev(String pl) {
        String[] devs = new String[] {"_Bluexin_", "Blaez", "Felphor", "LordCruaver", "Tencao"};
        for (String dev: devs) {
            if (dev.equals(pl)) return true;
        }

        return false;
    }

}