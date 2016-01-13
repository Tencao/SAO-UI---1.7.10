package com.tencao.saoui.ui;

import com.tencao.saoui.util.SAOColor;
import com.tencao.saoui.util.SAOIcon;
import com.tencao.saoui.util.SAOParentGUI;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public final class SAOEmptySlot extends SAOSlotGUI {
    public SAOEmptySlot(SAOParentGUI gui, int xPos, int yPos) {
        super(gui, xPos, yPos, null);
    }

    @Override
    protected String getCaption() {
        return String.format("(%s)", StatCollector.translateToLocal("gui.empty"));
    }

    @Override
    protected boolean isEmpty() {
        return false;
    }

    @Override
    protected SAOIcon getIcon() {
        return SAOIcon.NONE;
    }

    @Override
    public void refreshSlot(Slot slot) {

    }

    @Override
    public int getSlotNumber() {
        return -1;
    }

    @Override
    public ItemStack getStack() {
        return null;
    }

    @Override
    int getColor(int hoverState, boolean bg) {
        return bg? SAOColor.DEFAULT_COLOR.rgba: SAOColor.DEFAULT_FONT_COLOR.rgba;
    }
}