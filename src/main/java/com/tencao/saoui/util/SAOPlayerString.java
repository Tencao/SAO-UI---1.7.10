package com.tencao.saoui.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import java.text.DecimalFormat;
import java.util.Collection;


public final class SAOPlayerString implements SAOString {

    private final EntityPlayer player;

    public SAOPlayerString(EntityPlayer entityPlayer) {
        player = entityPlayer;
    }

    private static float attr(double attributeValue) {
        return (float) ((int) (attributeValue * 1000)) / 1000;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        EntityLivingBase mount = (EntityLivingBase)player.ridingEntity;

        if (player.isRiding() && SAOOption.MOUNT_STAT_VIEW.getValue()){
            final String name = ((EntityLiving)mount).getCustomNameTag();
            final double maxHealth = attr(mount.getMaxHealth());
            final double health = attr(mount.getHealth());
            final double resistance = attr(mount.getTotalArmorValue());
            final double speed = attr(mount.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            final double jump;
            DecimalFormat df = new DecimalFormat("0.000");
            String speedFormated = df.format(speed);

            builder.append(StatCollector.translateToLocal("displayName")).append(": ").append(name).append('\n');
            builder.append(StatCollector.translateToLocal("displayHpLong")).append(": ").append(health).append("/").append(maxHealth).append('\n');
            builder.append(StatCollector.translateToLocal("displayResLong")).append(": ").append(resistance).append('\n');
            builder.append(StatCollector.translateToLocal("displaySpdLong")).append(": ").append(speedFormated).append('\n');
            if (mount instanceof EntityHorse) {
                jump = ((EntityHorse) mount).getHorseJumpStrength();
                String jumpFormated = df.format(jump);
                builder.append(StatCollector.translateToLocal("displayJmpLong")).append(": ").append(jumpFormated).append('\n');
            }
        } else {
            final int level = player.experienceLevel;
            final int experience = (int) (player.experience * 100);
            final float health = attr(player.getHealth());

            final float maxHealth = attr(player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue());
            final float attackDamage = attr(player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());

            float itemDamage = 0.0F;

            if (player.getCurrentEquippedItem() != null) {
                @SuppressWarnings("unchecked") final Collection<?> itemAttackDamage = player.getCurrentEquippedItem().getAttributeModifiers().get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());

                itemDamage += itemAttackDamage.stream().filter(value -> value instanceof AttributeModifier).map(value -> (AttributeModifier) value)
                        .filter(mod -> mod.getName().equals("Weapon modifier")).mapToDouble(AttributeModifier::getAmount).sum();
             }

            final float strength = attr(attackDamage + itemDamage);
            final float agility = attr(player.getAIMoveSpeed());
            final float resistance = attr(player.getTotalArmorValue());

            builder.append(StatCollector.translateToLocal("displayLvLong")).append(": ").append(level).append('\n');
            builder.append(StatCollector.translateToLocal("displayXpLong")).append(": ").append(experience).append("%\n");

            builder.append(StatCollector.translateToLocal("displayHpLong")).append(": ").append(health).append("/").append(maxHealth).append('\n');
            builder.append(StatCollector.translateToLocal("displayStrLong")).append(": ").append(strength).append('\n');
            builder.append(StatCollector.translateToLocal("displayDexLong")).append(": ").append(agility).append('\n');
            builder.append(StatCollector.translateToLocal("displayResLong")).append(": ").append(resistance).append("\n");
        }

        return builder.toString();
    }

}
