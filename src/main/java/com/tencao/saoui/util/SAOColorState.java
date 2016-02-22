package com.tencao.saoui.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public enum SAOColorState {

    INNOCENT(0x93F43EFF),
    VIOLENT(0xF49B00FF),
    KILLER(0xBD0000FF),
    BOSS(0xBD0000FF),

    CREATIVE(0x4CEDC5FF),
    OP(0xFFFFFFFF),
    INVALID(0x8B8B8BFF),
    GAMEMASTER(0x79139EFF);

    private final int color;

    SAOColorState(int argb) {
        color = argb;
    }

    public static SAOColorState getColorState(Minecraft mc, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) return getPlayerColorState(mc, (EntityPlayer) entity);
        else if (entity instanceof EntityLiving) return getState(mc, (EntityLiving) entity);
        else return INVALID;
    }

    private static SAOColorState getState(Minecraft mc, EntityLiving entity) {
        if (entity instanceof EntityWolf && ((EntityWolf) entity).isAngry()) return KILLER;
        else if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
            return ((EntityTameable) entity).getOwner() != mc.thePlayer ? VIOLENT : INNOCENT;
        else if (entity instanceof IBossDisplayData) return BOSS;
        else if (entity instanceof IMob) return SAOOption.AGGRO_SYSTEM.getValue() ? VIOLENT : KILLER;
        else if (entity instanceof IAnimals) return INNOCENT;
        else if (entity instanceof IEntityOwnable) return VIOLENT;
        else return INVALID;
    }

    public static boolean checkValidState(Entity entity){
        return entity instanceof IAnimals || entity instanceof EntityPlayer || entity instanceof IEntityOwnable;
    }

    private static SAOColorState getPlayerColorState(Minecraft mc, EntityPlayer player) {
        if (isDev(StaticPlayerHelper.getName(player))) return GAMEMASTER;
        //else if (StaticPlayerHelper.isCreative(player)) return CREATIVE;
        else if (PartyHelper.instance().isMember(StaticPlayerHelper.getName(player))) return CREATIVE;
        else return INNOCENT;
    }

    private static boolean isDev(final String pl) {
        return Stream.of("_Bluexin_", "Blaez", "Felphor", "LordCruaver", "Tencao").anyMatch(name -> name.equals(pl));
    }


    public void glColor() {
        SAOGL.glColorRGBA(color);
    }

}