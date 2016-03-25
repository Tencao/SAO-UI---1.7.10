package com.tencao.saoui.screens.menu;

import com.tencao.saoui.GLCore;
import com.tencao.saoui.resources.StringNames;
import com.tencao.saoui.screens.MenuGUI;
import com.tencao.saoui.screens.ParentElement;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class Panel extends MenuGUI {

    public ColorUtil bgColor;

    public Panel(ParentElement gui, int xPos, int yPos, int w, int h) {
        super(gui, xPos, yPos, w, h);
        bgColor = ColorUtil.DEFAULT_COLOR;
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if ((visibility > 0) && (height > 0)) {
            GLCore.glStart();
            GLCore.glBindTexture(OptionCore.ORIGINAL_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

            final int left = getX(false);
            final int top = getY(false);

            GLCore.glColorRGBA(bgColor.multiplyAlpha(visibility));

            final int shadowSize = (x == 0 ? 0 : 5);

            if (shadowSize > 0) {
                GLCore.glTexturedRect(left - shadowSize, top - shadowSize, 5 - shadowSize, 120 - shadowSize, shadowSize, shadowSize);
                GLCore.glTexturedRect(left + width, top - shadowSize, 15, 120 - shadowSize, shadowSize, shadowSize);
                GLCore.glTexturedRect(left - shadowSize, top + height, 5 - shadowSize, 130, shadowSize, shadowSize);
                GLCore.glTexturedRect(left + width, top + height, 15, 130, shadowSize, shadowSize);

                GLCore.glTexturedRect(left, top - shadowSize, width, shadowSize, 5, 120 - shadowSize, 10, shadowSize);
                GLCore.glTexturedRect(left - shadowSize, top, shadowSize, height, 5 - shadowSize, 120, shadowSize, 10);
                GLCore.glTexturedRect(left + width, top, shadowSize, height, 15, 120, shadowSize, 10);
                GLCore.glTexturedRect(left, top + height, width, shadowSize, 5, 130, 10, shadowSize);
            }

            GLCore.glTexturedRect(left, top, width, height, 5, 120, 10, 10);

            if (x == 0) {
                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(visibility));

                GLCore.glTexturedRect(left + 5, top, 156, 25, 10, 10);
            }
            GLCore.glEnd();
        }

        super.draw(mc, cursorX, cursorY);
    }

}
