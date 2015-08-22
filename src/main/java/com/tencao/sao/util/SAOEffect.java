package com.tencao.sao.util;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import scala.Array;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@SideOnly(Side.CLIENT)
public enum SAOEffect {

    PARALYZED,
    POISONED,
    STARVING,
    HUNGRY,
    ROTTEN,
    ILL,
    WEAK,
    CURSED,
    BLIND,
    WET,
    DROWNING,
    BURNING,
    SATURATION,
    SPEED_BOOST,
    WATER_BREATH,
    STRENGTH,
    ABSORPTION,
    FIRE_RES,
    HASTE,
    HEALTH_BOOST,
    INST_HEALTH, // Probably won't be used here
    INVISIBILITY,
    JUMP_BOOST,
    NIGHT_VISION,
    REGEN,
    RESIST;

    private static final int SRC_X = 0;
    private static final int SRC_Y = 135;
    private static final int SRC_WIDTH = 15;
    private static final int SRC_HEIGHT = 10;

	public final int getSrcX() {
        return SRC_X + ordinal() % 14 * SRC_WIDTH;
	}

	public final int getSrcY() {
        return SRC_Y + ordinal() * SRC_HEIGHT / 14;
	}

	public final void glDraw(int x, int y, float z) {
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.effects: SAOResources.effectsCustom);
		SAOGL.glTexturedRect(x, y, z, getSrcX(), getSrcY(), SRC_WIDTH, SRC_HEIGHT);
	}

	public final void glDraw(int x, int y) {
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.effects: SAOResources.effectsCustom);
		SAOGL.glTexturedRect(x, y, getSrcX(), getSrcY(), SRC_WIDTH, SRC_HEIGHT);
	}

	public static final List<SAOEffect> getEffects(EntityPlayer player) {
		final List<SAOEffect> effects = new ArrayList<SAOEffect>();
		
		for (final Object potionEffect0 : player.getActivePotionEffects()) {
			if (potionEffect0 instanceof PotionEffect) {
				final PotionEffect potionEffect = (PotionEffect) potionEffect0;

	            if ((potionEffect.getPotionID() == Potion.moveSlowdown.getId()) && (potionEffect.getAmplifier() > 5)) {
	                effects.add(PARALYZED);
	            } else if (potionEffect.getPotionID() == Potion.poison.getId()) {
	                effects.add(POISONED);
	            } else if (potionEffect.getPotionID() == Potion.hunger.getId()) {
	                effects.add(ROTTEN);
	            } else if (potionEffect.getPotionID() == Potion.confusion.getId()) {
	                effects.add(ILL);
	            } else if (potionEffect.getPotionID() == Potion.weakness.getId()) {
	                effects.add(WEAK);
	            } else if (potionEffect.getPotionID() == Potion.wither.getId()) {
	                effects.add(CURSED);
	            } else if (potionEffect.getPotionID() == Potion.blindness.getId()) {
	                effects.add(BLIND);
	            } else if (potionEffect.getPotionID() == Potion.moveSpeed.getId()) {
	                effects.add(SPEED_BOOST);
	            } else if (potionEffect.getPotionID() == Potion.waterBreathing.getId()){
	                effects.add(WATER_BREATH);
	            } else if (potionEffect.getPotionID() == Potion.damageBoost.getId()) {
	                effects.add(STRENGTH);
	            } else if (potionEffect.getPotionID() == Potion.fireResistance.getId()) {
	                effects.add(FIRE_RES);
	            } else if (potionEffect.getPotionID() == Potion.digSpeed.getId()) {
	                effects.add(HASTE);
	            } else if (potionEffect.getPotionID() == Potion.heal.getId()) {
	                effects.add(INST_HEALTH);
	            } else if (potionEffect.getPotionID() == Potion.invisibility.getId()) {
	                effects.add(INVISIBILITY);
	            } else if (potionEffect.getPotionID() == Potion.jump.getId()) {
	                effects.add(JUMP_BOOST);
	            } else if (potionEffect.getPotionID() == Potion.nightVision.getId()) {
	                effects.add(NIGHT_VISION);
	            } else if (potionEffect.getPotionID() == Potion.regeneration.getId()) {
	                effects.add(REGEN);
	            } else if (potionEffect.getPotionID() == Potion.resistance.getId()) {
	                effects.add(RESIST);
	            }
			}
		}
		
		if (player.getFoodStats().getFoodLevel() <= 6) {
			effects.add(STARVING);
		} else
		if (player.getFoodStats().getFoodLevel() <= 18) {
			effects.add(HUNGRY);
		}

        if (player.isInWater()) {
            if (player.getAir() <= 0) {
                effects.add(DROWNING);
            } else if (player.getAir() < 300) {
                effects.add(WET);
            }
        }

        if (player.isBurning()) {
            effects.add(BURNING);
        }

		return effects;
	}

}
