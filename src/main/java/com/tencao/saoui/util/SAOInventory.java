package com.tencao.saoui.util;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;

@SideOnly(Side.CLIENT)
public enum SAOInventory{

    EQUIPMENT((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return (item instanceof ItemArmor) || ((item instanceof ItemBlock) && (((ItemBlock) item).blockInstance instanceof BlockPumpkin));
    }),

    WEAPONS((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return item instanceof ItemSword;
    }),

    BOWS((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return item instanceof ItemBow;
    }),

    PICKAXE((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return item instanceof ItemPickaxe;
    }),

    AXE((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return item instanceof ItemAxe;
    }),

    SHOVEL((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return item instanceof ItemSpade;
    }),

    ACCESSORY((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return (
                (item instanceof IBauble)
        );
    }),

    CONSUMABLES((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return (
                (item instanceof ItemExpBottle) ||
                        (item instanceof ItemPotion) ||
                        (item instanceof ItemFood)
        );
    }),

    ITEMS((stack, state) -> !state || (!EQUIPMENT.isFine(stack, state)));

    private final ItemFilter itemFilter;

    private SAOInventory(ItemFilter filter) {
        itemFilter = filter;
    }

    public final boolean isFine(ItemStack stack, boolean state) {
        return itemFilter.filter(stack, state);
    }

    private interface ItemFilter {
        public boolean filter(ItemStack stack, boolean state);
    }

}
