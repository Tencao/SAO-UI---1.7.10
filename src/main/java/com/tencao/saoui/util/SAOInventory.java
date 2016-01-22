package com.tencao.saoui.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.item.*;

@SideOnly(Side.CLIENT)
public enum SAOInventory {

    EQUIPMENT((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return (item instanceof ItemArmor) || ((item instanceof ItemBlock) && (((ItemBlock) item).blockInstance instanceof BlockPumpkin));
    }),

    WEAPONS((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return (item instanceof ItemSword) || (item instanceof ItemTool) || (item instanceof ItemBow);
    }),

    ACCESSORY((ItemFilter) (stack, state) -> {
        final Item item = stack.getItem();

        return (
                (item instanceof ItemExpBottle) ||
                        (item instanceof ItemBucket) ||
                        (item instanceof ItemPotion) ||
                        (item instanceof ItemFishingRod) ||
                        (item instanceof ItemCarrotOnAStick) ||
                        (item instanceof ItemEnchantedBook) ||
                        (item instanceof ItemEditableBook) ||
                        (item instanceof ItemMapBase) ||
                        (item instanceof ItemNameTag) ||
                        (item instanceof ItemSaddle) ||
                        (item instanceof ItemWritableBook) ||
                        (item instanceof ItemLead) ||
                        (item instanceof ItemFlintAndSteel) ||
                        (item instanceof ItemShears)
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
