package com.tencao.sao.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemWritableBook;

@SideOnly(Side.CLIENT)
public enum SAOInventory {

	EQUIPMENT(new ItemFilter() {

		public boolean filter(ItemStack stack, boolean state) {
			final Item item = stack.getItem();
			
			return (item instanceof ItemArmor) || ((item instanceof ItemBlock) && (((ItemBlock) item).field_150939_a instanceof BlockPumpkin));
		}


	}),

	WEAPONS(new ItemFilter() {

		public boolean filter(ItemStack stack, boolean state) {
			final Item item = stack.getItem();
			
			return (item instanceof ItemSword) || (item instanceof ItemTool) || (item instanceof ItemBow);
		}

	}),

	ACCESSORY(new ItemFilter() {

		public boolean filter(ItemStack stack, boolean state) {
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
		}

	}),

	ITEMS(new ItemFilter() {

        @Override
		public boolean filter(ItemStack stack, boolean state) {
			return (!EQUIPMENT.isFine(stack, state)) && (!WEAPONS.isFine(stack, state) && (!ACCESSORY.isFine(stack, state)));
		}

	});

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
