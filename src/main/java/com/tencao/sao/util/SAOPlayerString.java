package com.tencao.sao.util;

import java.util.Collection;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;


public final class SAOPlayerString implements SAOString {

    private final EntityPlayer player;

    public SAOPlayerString(EntityPlayer entityPlayer) {
        player = entityPlayer;
    }

    public final String toString() {
        final StringBuilder builder = new StringBuilder();

        if (player != null) {
            final int level = player.experienceLevel;
            final int experience = (int) (player.experience * 100);

            final float health = attr(player.getHealth());

            final float maxHealth = attr(player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue());
            final float attackDamage = attr(player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
            // final float movementSpeed = attr(player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            // final float knowbackResistance = attr(player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue());

            float itemDamage = 0.0F;

            if (player.getCurrentEquippedItem() != null) {
                final Multimap map = player.getCurrentEquippedItem().getAttributeModifiers();
                final Collection itemAttackDamage = map.get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());

                for (Object value : itemAttackDamage) {
                    if (value instanceof AttributeModifier) {
                        final AttributeModifier attrMod = (AttributeModifier) value;

                        if (attrMod.getName().equals("Weapon modifier")) {
                            switch (attrMod.getOperation()) {
                                default:
                                    itemDamage += attrMod.getAmount();
                                    break;
                            }
                        }
                    }
                }
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

    private static float attr(double attributeValue) {
        return (float) ((int) (attributeValue * 1000)) / 1000;
    }

}
