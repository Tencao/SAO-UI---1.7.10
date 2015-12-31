package com.tencao.saoui.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tencao.saoui.util.SAOColor;
import com.tencao.saoui.util.SAOGL;
import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.util.SAOIcon;
import com.tencao.saoui.util.SAOInventory;
import com.tencao.saoui.util.SAOParentGUI;

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

	static SAOIcon getIcon(ItemStack stack) {
		if (stack != null) {
			if (SAOInventory.WEAPONS.isFine(stack, false)) {
				return SAOIcon.EQUIPMENT;
			} else if (SAOInventory.EQUIPMENT.isFine(stack, false)) {
				return SAOIcon.ARMOR;
			} else if (SAOInventory.ACCESSORY.isFine(stack, false)) {
				return SAOIcon.ACCESSORY;
			} else {
				return SAOIcon.ITEMS;
			}
		} else {
			return SAOIcon.NONE;
		}
	}

	private static SAOIcon getIcon(Slot slot) {
		if ((slot.getHasStack()) && (slot.getStack().getItem() != null)) {
			return getIcon(slot.getStack());
		} else {
			return SAOIcon.HELP;
		}
	}

	private static String getCaption(Slot slot) {
		if ((slot.getHasStack()) && (slot.getStack().getItem() != null)) {
			return slot.getStack().getDisplayName();
		} else {
			return UNKNOWN;
		}
	}
	
	@Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if ((visibility > 0) && (enabled)) {
            final int left = getX(false);
            final int top = getY(false);

            final ItemStack stack = getStack();

            if (stack != null) {
                final String sizeString = "x" + stack.stackSize;

                SAOGL.glString(sizeString, left + width + 2, top + height - 16, SAOColor.multiplyAlpha(getColor(hoverState(cursorX, cursorY), false), visibility), true);
            }
        }
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

    @Override
	int getColor(int hoverState, boolean bg) {
		final int color = super.getColor(hoverState, bg);
		
		if (highlight) {
			return SAOColor.mediumColor(color, SAOColor.DEFAULT_COLOR.mediumColor(0xFF));
		} else {
			return color;
		}
	}

    @Override
	public boolean keyTyped(Minecraft mc, char ch, int key) {
		return true;
	}

    @Override
	public boolean mouseOver(int cursorX, int cursorY, int flag) {
		return (focus = super.mouseOver(cursorX, cursorY, flag));
	}

    @Override
	public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
		return super.mouseReleased(mc, cursorX, cursorY, button) || (button == 1) || (button == 2);
	}


}
