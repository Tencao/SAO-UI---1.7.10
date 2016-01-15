package com.tencao.saoui.ui;

import com.tencao.saoui.util.SAOAlign;
import com.tencao.saoui.util.SAOColor;
import com.tencao.saoui.util.SAOGL;
import com.tencao.saoui.util.SAOParentGUI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class SAOLabelGUI extends SAOElementGUI {

    public String caption;
    public SAOColor fontColor;
    public SAOAlign align;

    public SAOLabelGUI(SAOParentGUI gui, int xPos, int yPos, int width, String string, SAOAlign saoAlign) {
        super(gui, xPos, yPos, width, 20);
        caption = string;
        fontColor = SAOColor.DEFAULT_FONT_COLOR;
        align = saoAlign;
    }

    public SAOLabelGUI(SAOParentGUI gui, int xPos, int yPos, String string, SAOAlign saoAlign) {
        this(gui, xPos, yPos, 200, string, saoAlign);
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if (visibility > 0) {
            final int left = align.getX(this, false, SAOGL.glStringWidth(caption)) + getOffsetX();
            final int top = getY(false);

            SAOGL.glString(caption, left, top + (height - SAOGL.glStringHeight()) / 2, fontColor.multiplyAlpha(visibility));
        }
    }

    public int getOffsetX() {
        if (align == SAOAlign.LEFT) {
            return 8;
        } else if (align == SAOAlign.RIGHT) {
            return -8;
        } else {
            return 0;
        }
    }

}
