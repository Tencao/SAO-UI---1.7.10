package com.saomc.screens.inventory;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;

@SideOnly(Side.CLIENT)
public enum InventoryCore {

    EQUIPMENT((stack, state) -> {
        final Item item = stack.getItem();

        return (item instanceof ItemArmor) || ((item instanceof ItemBlock) && (((ItemBlock) item).blockInstance instanceof BlockPumpkin));
    }),

    WEAPONS((stack, state) -> stack.getItem() instanceof ItemSword),

    BOWS((stack, state) -> stack.getItem() instanceof ItemBow),

    PICKAXE((stack, state) -> stack.getItem() instanceof ItemPickaxe),

    AXE((stack, state) -> stack.getItem() instanceof ItemAxe),

    SHOVEL((stack, state) -> stack.getItem() instanceof ItemSpade),

    COMPATTOOLS((stack, state) -> {
        final Item item = stack.getItem();

        return ((item instanceof ItemTool) || (item instanceof ItemBow) || (item instanceof ItemSword));
    }),

    ACCESSORY((stack, state) -> {
        return isBaublesLoaded() && stack.getItem() instanceof IBauble;
    }),

    CONSUMABLES((stack, state) -> {
        final Item item = stack.getItem();

        return (
                (item instanceof ItemExpBottle) ||
                        (item instanceof ItemPotion) ||
                        (item instanceof ItemFood)
        );
    }),

    ITEMS((stack, state) -> !state || (!EQUIPMENT.isFine(stack, state)));

    private final ItemFilter itemFilter;

    InventoryCore(ItemFilter filter) {
        itemFilter = filter;
    }

    public final boolean isFine(ItemStack stack, boolean state) {
        return stack != null && itemFilter.filter(stack, state);
    }

    public static IInventory getBaubles(EntityPlayer player)
    {
        if (!isBaublesLoaded())
        {
            return null;
        } else
        {
            return BaublesApi.getBaubles(player);
        }
    }

    public static boolean isBaublesLoaded(){
        return Loader.isModLoaded("Baubles");
    }

    @FunctionalInterface
    private interface ItemFilter {
        boolean filter(ItemStack stack, boolean state);
    }

}
