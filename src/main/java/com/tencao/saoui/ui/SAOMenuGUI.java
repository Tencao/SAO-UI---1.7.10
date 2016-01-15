package com.tencao.saoui.ui;

import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class SAOMenuGUI extends SAOContainerGUI {

    public boolean innerMenu;
    public boolean fullArrow;

    public SAOMenuGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h) {
        super(gui, xPos, yPos, w, h);
        fullArrow = true;
        innerMenu = false;
    }

    protected int getOffset(int index) {
        return elements.stream().limit(index).mapToInt(this::getOffsetSize).sum();
    }

    protected int getOffsetSize(SAOElementGUI element) {
        return element.height;
    }

    public void update(Minecraft mc) {
        height = getSize();

        if (width <= 0) width = elements.stream().mapToInt(el -> el.width).max().orElse(width);

        super.update(mc);
    }

    protected int getSize() {
        return getOffset(elements.size());
    }

    protected void update(Minecraft mc, int index, SAOElementGUI element) {
        element.y = getOffset(index);
        element.width = width - element.x;

        super.update(mc, index, element);
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if (visibility > 0 && parent != null && height > 0) {
            if (x > 0) {
                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);
                SAOGL.glColorRGBA(SAOColor.DEFAULT_COLOR.multiplyAlpha(visibility));

                final int left = getX(false);
                final int top = getY(false) + 1;

                final int arrowTop = super.getY(false) - height / 2;

                SAOGL.glTexturedRect(left - 2, top, 2, height - 1, 40, 41, 2, 4);
                SAOGL.glTexturedRect(left - 10, arrowTop + (height - 10) / 2, 20, 25 + (fullArrow ? 10 : 0), 10, 10);
            } else if (x < 0) {
                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);
                SAOGL.glColorRGBA(SAOColor.DEFAULT_COLOR.multiplyAlpha(visibility));

                final int left = getX(false);
                final int top = getY(false) + 1;

                final int arrowTop = super.getY(false) - height / 2;

                SAOGL.glTexturedRect(left + width, top, 2, height - 1, 40, 41, 2, 4);
                SAOGL.glTexturedRect(left + width, arrowTop + (height - 10) / 2, 30, 25 + (fullArrow ? 10 : 0), 10, 10);
            }
        }

        super.draw(mc, cursorX, cursorY);
    }

    public int getY(boolean relative) {
        return super.getY(relative) - (relative || innerMenu ? 0 : height / 2);
    }

}
