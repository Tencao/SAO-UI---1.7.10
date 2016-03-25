package com.tencao.saoui.screens.ingame;

import com.tencao.saoui.GLCore;
import com.tencao.saoui.social.StaticPlayerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public enum HealthStep {

    VERY_LOW(0.1F, 0xBD0000FF),
    LOW(0.2F, 0xF40000FF),
    VERY_DAMAGED(0.3F, 0xF47800FF),
    DAMAGED(0.4F, 0xF4BD00FF),
    OKAY(0.5F, 0xEDEB38FF),
    GOOD(1.0F, 0x93F43EFF),
    CREATIVE(-1.0F, 0xB32DE3FF);

    private final float healthLimit;
    private final int color;

    HealthStep(float limit, int argb) {
        healthLimit = limit;
        color = argb;
    }

    public static HealthStep getStep(Minecraft mc, EntityLivingBase entity, float time) {
        if (entity instanceof EntityPlayer && (((EntityPlayer) entity).capabilities.isCreativeMode)) return CREATIVE;
        final float value = StaticPlayerHelper.getHealth(mc, entity, time) / StaticPlayerHelper.getMaxHealth(entity);
        HealthStep step = first();

        while ((value > step.getLimit()) && (step.ordinal() + 1 < values().length)) step = next(step);

        return step;
    }

    public static HealthStep getStep(Minecraft mc, float health, float time) {
        HealthStep step = first();

        while ((health > step.getLimit()) && (step.ordinal() + 1 < values().length)) step = next(step);

        return step;
    }

    private static HealthStep first() {
        return values()[0];
    }

    private static HealthStep next(HealthStep step) {
        return values()[step.ordinal() + 1];
    }

    private float getLimit() {
        return healthLimit;
    }

    public final void glColor() {
        GLCore.glColorRGBA(color);
    }

}
