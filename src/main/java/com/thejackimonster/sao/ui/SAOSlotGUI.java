package com.thejackimonster.sao.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import com.thejackimonster.sao.util.SAOColor;
import com.thejackimonster.sao.util.SAOID;
import com.thejackimonster.sao.util.SAOIcon;
import com.thejackimonster.sao.util.SAOInventory;
import com.thejackimonster.sao.util.SAOParentGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOSlotGUI extends SAOButtonGUI {

	private static final String UNKNOWN = "???";

	private Slot buttonSlot;

	public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h, Slot slot) {
		super(gui, SAOID.SLOT, xPos, yPos, w, h, getCaption(slot), getIcon(slot));
		buttonSlot = slot;
	}

	public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, int w, Slot slot) {
		this(gui, xPos, yPos, w, 20, slot);
	}

	public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, Slot slot) {
		this(gui, xPos, yPos, 150, slot);
	}

	public void refreshSlot(Slot slot) {
		if (slot != null) {
			buttonSlot = slot;
			
			caption = getCaption(buttonSlot);
			icon = getIcon(buttonSlot);
		}
		
		if (isEmpty()) {
			remove();
		}
	}

	public boolean isEmpty() {
		return (!buttonSlot.getHasStack()) || (buttonSlot.getStack() == null);
	}

	public Slot getSlot() {
		return buttonSlot;
	}

	public int getSlotNumber() {
		return buttonSlot.slotNumber;
	}

	public ItemStack getStack() {
		if (isEmpty()) {
			return null;
		} else {
			return buttonSlot.getStack();
		}
	}

	protected int getColor(int hoverState, boolean bg) {
		final int color = super.getColor(hoverState, bg);
		
		if (highlight) {
			return SAOColor.mediumColor(color, SAOColor.mediumColor(SAOColor.DEFAULT_COLOR, 0xFF));
		} else {
			return color;
		}
	}

	public boolean keyTyped(Minecraft mc, char ch, int key) {
		return true;
	}

	public boolean mouseOver(int cursorX, int cursorY, int flag) {
		return (focus = super.mouseOver(cursorX, cursorY, flag));
	}

	public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
		return super.mouseReleased(mc, cursorX, cursorY, button) || (button == 1) || (button == 2);
	}

	private static final SAOIcon getIcon(Slot slot) {
		if ((slot.getHasStack()) && (slot.getStack().getItem() != null)) {
			final ItemStack stack = slot.getStack();
			final Item item = stack.getItem();
			
			if (SAOInventory.WEAPONS.isFine(stack)) {
				return SAOIcon.EQUIPMENT;
			} else
			if (SAOInventory.EQUIPMENT.isFine(stack)) {
				return SAOIcon.ARMOR;
			} else
			if (SAOInventory.ACCESSORY.isFine(stack)) {
				return SAOIcon.ACCESSORY;
			} else {
				return SAOIcon.ITEMS;
			}
		} else {
			return SAOIcon.HELP;
		}
	}

	private static final String getCaption(Slot slot) {
		if ((slot.getHasStack()) && (slot.getStack().getItem() != null)) {
			return slot.getStack().getDisplayName();
		} else {
			return UNKNOWN;
		}
	}

}
