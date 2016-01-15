package com.tencao.saoui.ui;

import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class SAOSlotGUI extends SAOButtonGUI {

    private static final String UNKNOWN = "???";

    private Slot buttonSlot;

    public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h, Slot slot) {
        super(gui, SAOID.SLOT, xPos, yPos, w, h);
        buttonSlot = slot;
        super.caption = this.getCaption();
        super.icon = this.getIcon();
    }

    public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, int w, Slot slot) {
        this(gui, xPos, yPos, w, 20, slot);
    }

    public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, Slot slot) {
        this(gui, xPos, yPos, 150, slot);
    }

    static SAOIcon getIcon(ItemStack stack) {
        if (stack != null) {
            if (SAOInventory.WEAPONS.isFine(stack, false)) return SAOIcon.EQUIPMENT;
            else if (SAOInventory.EQUIPMENT.isFine(stack, false)) return SAOIcon.ARMOR;
            else if (SAOInventory.ACCESSORY.isFine(stack, false)) return SAOIcon.ACCESSORY;
            else return SAOIcon.ITEMS;
        } else return SAOIcon.HELP;
    }

    protected SAOIcon getIcon() {
        return getIcon(buttonSlot.getStack());
    }

    protected String getCaption() {
        return buttonSlot.getHasStack() && buttonSlot.getStack().getItem() != null ? buttonSlot.getStack().getDisplayName() : UNKNOWN;
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if ((visibility > 0) && (enabled)) {
            final int left = getX(false);
            final int top = getY(false);

            final ItemStack stack = getStack();

            if (stack != null)
                SAOGL.glString("x" + stack.stackSize, left + width + 2, top + height - 16, SAOColor.multiplyAlpha(getColor(hoverState(cursorX, cursorY), false), visibility), true);
        }
    }

    public void refreshSlot(Slot slot) {
        if (slot != null) {
            buttonSlot = slot;

            caption = getCaption();
            icon = getIcon();
        }

        if (isEmpty()) remove();
    }

    protected boolean isEmpty() {
        return (!buttonSlot.getHasStack());
    }

    public Slot getSlot() {
        return buttonSlot;
    }

    public int getSlotNumber() {
        return buttonSlot.slotNumber;
    }

    public ItemStack getStack() {
        return buttonSlot.getStack();
    }

    @Override
    int getColor(int hoverState, boolean bg) {
        final int color = super.getColor(hoverState, bg);

        return highlight && hoverState != 2 ? SAOColor.mediumColor(color, SAOColor.DEFAULT_COLOR.mediumColor(0xFF)) : color;
    }

    @Override
    public boolean keyTyped(Minecraft mc, char ch, int key) {
        return true;
    }

    @Override
    public boolean mouseOver(int cursorX, int cursorY, int flag) {
        return focus = super.mouseOver(cursorX, cursorY, flag);
    }

    @Override
    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        return super.mouseReleased(mc, cursorX, cursorY, button) || button == 1 || button == 2;
    }


}
