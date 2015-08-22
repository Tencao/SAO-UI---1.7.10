package com.thejackimonster.sao.ui;

import com.thejackimonster.sao.util.SAOInventory;
import com.thejackimonster.sao.util.SAOParentGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class SAOInventoryGUI extends SAOListGUI {

	public final Container slots;
	public final SAOInventory filter;

	private boolean opened;

	public SAOInventoryGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h, Container containerSlots, SAOInventory inventory) {
		super(gui, xPos, yPos, w, h);
		slots = containerSlots;
		filter = inventory;
		opened = false;
	}

	public void update(Minecraft mc) {
		if (!opened) {
			mc.thePlayer.openContainer = slots;
			opened = true;
		}
		
		super.update(mc);
		
		for (int i = 0; i < slots.inventorySlots.size(); i++) {
			final Slot slot = slots.getSlot(i);
			
			if (slot != null) {
				final ItemStack stack = slot.getStack();
				boolean found = false;
				
				for (int j = elements.size() - 1; j >= 0; j--) {
					if (j >= elements.size()) {
						continue;
					}
					
					if (elements.get(j) instanceof SAOSlotGUI) {
						final SAOSlotGUI gui = (SAOSlotGUI) elements.get(j);
						
						if (gui.getSlotNumber() == slot.slotNumber) {
							gui.refreshSlot(slot);
							
							if (!gui.removed()) {
								if (filter.isFine(gui.getStack())) {
									found = true;
								} else {
									gui.remove();
								}
							}
						}
					}
				}
				
				if ((!found) && (stack != null) && (filter.isFine(stack))) {
					elements.add(new SAOSlotGUI(this, 0, getOffset(elements.size()), slot));
				}
			}
		}
		
		slots.detectAndSendChanges();
	}

	protected void update(Minecraft mc, int index, SAOElementGUI element) {
		super.update(mc, index, element);
		
		if (element instanceof SAOSlotGUI) {
			final SAOSlotGUI slot = (SAOSlotGUI) element;
			final int number = slot.getSlotNumber();
			
			slot.highlight = ((number >= 5) && (number < 9)) || ((number >= 36) && (number < 45));
		}
	}

	public void handleMouseClick(Minecraft mc, Slot slot, int slotNumber, int flag, int method) {
		if (slot != null) {
			slotNumber = slot.slotNumber;
		}
		
		mc.playerController.windowClick(slots.windowId, slotNumber, flag, method, mc.thePlayer);
	}

	public void close(Minecraft mc) {
		super.close(mc);
		
		if (mc.thePlayer != null) {
			slots.onContainerClosed(mc.thePlayer);
		}
	}

}
