package com.tencao.saoui.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public enum SAOColorState {

    INNOCENT(0x93F43EFF),
    VIOLENT(0xF49B00FF),
    KILLER(0xBD0000FF),

    CREATIVE(0x4CEDC5FF),
    OP(0xFFFFFFFF),
    INVALID(0x8B8B8BFF),
    GAMEMASTER(0x79139EFF);

    private final int color;

    SAOColorState(int argb) {
        color = argb;
    }

    public static SAOColorState getColorState(Minecraft mc, Entity entity, float time) {
        if (entity instanceof EntityPlayer) return getPlayerColorState(mc, (EntityPlayer) entity, time);
        else if (entity instanceof EntityLiving)
            return ((EntityLiving) entity).getAttackTarget() instanceof EntityPlayer ? KILLER : getState(mc, (EntityLiving) entity, time);
        else return INVALID;
    }

    private static SAOColorState getState(Minecraft mc, EntityLiving entity, float time) {
        if (entity instanceof EntityWolf && ((EntityWolf) entity).isAngry()) return KILLER;
        else if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
            return ((EntityTameable) entity).getOwner() != mc.thePlayer ? SAOColorState.getColorState(mc, ((EntityTameable) entity).getOwner(), time) : INNOCENT;
        else if (entity instanceof IMob) return KILLER;
        else if (entity instanceof IAnimals) return INNOCENT;
        else if (entity instanceof IEntityOwnable) return VIOLENT;
        else return INVALID;
    }

    private static SAOColorState getPlayerColorState(Minecraft mc, EntityPlayer player, float time) {
        if (isDev(StaticPlayerHelper.getName(player))) return GAMEMASTER;
        else if (StaticPlayerHelper.isCreative((AbstractClientPlayer) player)) return CREATIVE;
        else return ColorStateHandler.instance().get(player);
    }

    private static boolean getCreative(EntityPlayer player) {
        NBTTagCompound c = player.getEntityData();
        if (c.hasKey("playerGameType", 1)) return true;
        else return false;
    }

    private static boolean isDev(final String pl) {
        return Stream.of("_Bluexin_", "Blaez", "Felphor", "LordCruaver", "Tencao").anyMatch(name -> name.equals(pl));
    }

    public final void glColor() {
        SAOGL.glColorRGBA(color);
    }

}