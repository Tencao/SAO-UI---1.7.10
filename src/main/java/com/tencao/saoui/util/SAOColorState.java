package com.tencao.saoui.util;

import com.tencao.saoui.SAOMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
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
            return getEntityColorState((EntityCreature) entity);
        } else {
            return INNOCENT;
        }
    }

    private static SAOColorState getEntityColorState(EntityCreature creature) {
        if ((creature instanceof EntityTameable) && (((EntityTameable) creature).isTamed())) {
            return VIOLENT;
        } else if (((creature instanceof EntityWolf) && (((EntityWolf) creature).isAngry())) ||
                (creature.getAttackTarget() instanceof EntityPlayer) ||
                ((creature.getAITarget() instanceof EntityPlayer)) ||
                ((creature instanceof EntityMob) && (!(creature instanceof EntityPigZombie)))) {
            return KILLER;
        } else {
            return INNOCENT;
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