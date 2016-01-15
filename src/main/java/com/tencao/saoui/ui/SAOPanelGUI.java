package com.tencao.saoui.ui;

import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class SAOPanelGUI extends SAOMenuGUI {

    public SAOColor bgColor;

    public SAOPanelGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h) {
        super(gui, xPos, yPos, w, h);
        bgColor = SAOColor.DEFAULT_COLOR;
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if ((visibility > 0) && (height > 0)) {
            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);

            final int left = getX(false);
            final int top = getY(false);

            SAOGL.glColorRGBA(bgColor.multiplyAlpha(visibility));

            final int shadowSize = (x == 0 ? 0 : 5);

            if (shadowSize > 0) {
                SAOGL.glTexturedRect(left - shadowSize, top - shadowSize, 5 - shadowSize, 120 - shadowSize, shadowSize, shadowSize);
                SAOGL.glTexturedRect(left + width, top - shadowSize, 15, 120 - shadowSize, shadowSize, shadowSize);
                SAOGL.glTexturedRect(left - shadowSize, top + height, 5 - shadowSize, 130, shadowSize, shadowSize);
                SAOGL.glTexturedRect(left + width, top + height, 15, 130, shadowSize, shadowSize);

                SAOGL.glTexturedRect(left, top - shadowSize, width, shadowSize, 5, 120 - shadowSize, 10, shadowSize);
                SAOGL.glTexturedRect(left - shadowSize, top, shadowSize, height, 5 - shadowSize, 120, shadowSize, 10);
                SAOGL.glTexturedRect(left + width, top, shadowSize, height, 15, 120, shadowSize, 10);
                SAOGL.glTexturedRect(left, top + height, width, shadowSize, 5, 130, 10, shadowSize);
            }

            SAOGL.glTexturedRect(left, top, width, height, 5, 120, 10, 10);

            if (x == 0) {
                SAOGL.glColorRGBA(SAOColor.DEFAULT_COLOR.multiplyAlpha(visibility));

                SAOGL.glTexturedRect(left + 5, top, 156, 25, 10, 10);
            }
        }

        super.draw(mc, cursorX, cursorY);
    }

}
