package com.thejackimonster.sao.util;

import com.thejackimonster.sao.SAOMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;

@SideOnly(Side.CLIENT)
public enum SAOHealthStep {

	VERY_LOW(0.1F, 0xBD0000FF),
	LOW(0.2F, 0xF40000FF),
	VERY_DAMAGED(0.3F, 0xF47800FF),
	DAMAGED(0.4F, 0xF4BD00FF),
	OKAY(0.5F, 0xEDEB38FF),
	GOOD(1.0F, 0x93F43EFF);

	private final float healthLimit;
	private final int color;

	private SAOHealthStep(float limit, int argb) {
		healthLimit = limit;
		color = argb;
	}

	public final float getLimit() {
		return healthLimit;
	}

	public final void glColor() {
		SAOGL.glColorRGBA(color);
	}

	public final void glColor(EntityLivingBase entity) {
		if (entity instanceof EntityMob) {
			final int red = (color >> 24) & 0xFF;
			final int green = (color >> 24) & 0xFF;
			final int blue = (color >> 24) & 0xFF;
			
			final float value = Math.min((red + green + blue) / 3, 0xFF);
			
			SAOGL.glColor(value / 0xFF, 0, 0, (float) (color & 0xFF) / 0xFF);
		} else {
			glColor();
		}
	}

	public static final SAOHealthStep getStep(Minecraft mc, EntityLivingBase entity, float time) {
		final float value = SAOMod.getHealth(mc, entity, time) / SAOMod.getMaxHealth(entity);
		SAOHealthStep step = first();
		
		while ((value > step.getLimit()) && (step.ordinal() + 1 < values().length)) {
			step = next(step);
		}
		
		return step;
	}

	private static final SAOHealthStep first() {
		return values()[0];
	}

	private static final SAOHealthStep next(SAOHealthStep step) {
		return values()[step.ordinal() + 1];
	}

}
