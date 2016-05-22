package com.saomc.screens;

import com.saomc.GLCore;
import com.saomc.resources.StringNames;
import com.saomc.util.ColorUtil;
import com.saomc.util.OptionCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class VLine extends Elements {

    public int lineWidth;

    public VLine(ParentElement gui, int xPos, int yPos, int size) {
        super(gui, xPos, yPos, size, 2);
        lineWidth = size;
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if (visibility > 0) {
            GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);
            GLCore.glColorRGBA(ColorUtil.DEFAULT_FONT_COLOR.multiplyAlpha(visibility));

            final int left = getX(false) + (width - lineWidth) / 2;
            final int top = getY(false);

            GLCore.glTexturedRect(left, top, lineWidth, 2, 42, 42, 4, 2);
        }
    }

}
