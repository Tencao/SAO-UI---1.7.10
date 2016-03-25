package com.tencao.saoui.screens.buttons;

import com.tencao.saoui.GLCore;
import com.tencao.saoui.resources.StringNames;
import com.tencao.saoui.screens.Elements;
import com.tencao.saoui.screens.ParentElement;
import com.tencao.saoui.screens.menu.Categories;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class ButtonGUI extends Elements {

    private final Categories id;

    public String caption;
    public IconCore icon;
    public boolean highlight;

    public ButtonGUI(ParentElement gui, Categories saoID, int xPos, int yPos, int w, int h, String string, IconCore iconCore) {
        super(gui, xPos, yPos, w, h);
        id = saoID;
        caption = string;
        icon = iconCore;
        highlight = false;
    }

    public ButtonGUI(ParentElement gui, Categories saoID, int xPos, int yPos, int w, String string, IconCore iconCore) {
        this(gui, saoID, xPos, yPos, w, 20, string, iconCore);
    }

    public ButtonGUI(ParentElement gui, Categories saoID, int xPos, int yPos, String string, IconCore iconCore) {
        this(gui, saoID, xPos, yPos, 100, string, iconCore);
    }

    public ButtonGUI(ParentElement gui, Categories saoID, int xPos, int yPos, String string, IconCore iconCore, boolean highlighted) {
        this(gui, saoID, xPos, yPos, 100, string, iconCore);
        highlight = highlighted;
    }

    public ButtonGUI(ParentElement gui, Categories slot, int xPos, int yPos, int w, int h) {
        this(gui, slot, xPos, yPos, w, h, "", IconCore.NONE);
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if (visibility > 0) {
            GLCore.glStart();
            GLCore.glBindTexture(OptionCore.ORIGINAL_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

            final int hoverState = hoverState(cursorX, cursorY);

            final int color0 = getColor(hoverState, true);
            final int color1 = getColor(hoverState, false);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, visibility));

            final int left = getX(false);
            final int top = getY(false);

            final int width2 = width / 2;
            final int height2 = height / 2;

            GLCore.glTexturedRect(left, top, 0, 45, width2, height2);
            GLCore.glTexturedRect(left + width2, top, 200 - width2, 45, width2, height2);
            GLCore.glTexturedRect(left, top + height2, 0, 65 - height2, width2, height2);
            GLCore.glTexturedRect(left + width2, top + height2, 200 - width2, 65 - height2, width2, height2);

            final int iconOffset = (height - 16) / 2;

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, visibility));
            GLCore.glTexturedRect(left + iconOffset, top + iconOffset, 140, 25, 16, 16);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, visibility));
            icon.glDraw(left + iconOffset, top + iconOffset);

            final int captionOffset = (height - GLCore.glStringHeight()) / 2;

            GLCore.glString(caption, left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color1, visibility));
            GLCore.glEnd();
        }
    }

    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        return (button == 0);
    }

    protected int getColor(int hoverState, boolean bg) {
        return bg ? hoverState == 1 ? ColorUtil.DEFAULT_COLOR.rgba : hoverState >= 2 ? ColorUtil.HOVER_COLOR.rgba : ColorUtil.DISABLED_MASK.rgba : hoverState == 1 ? ColorUtil.DEFAULT_FONT_COLOR.rgba : hoverState >= 2 ? ColorUtil.HOVER_FONT_COLOR.rgba : ColorUtil.DEFAULT_FONT_COLOR.rgba & ColorUtil.DISABLED_MASK.rgba;
    }

    public int hoverState(int cursorX, int cursorY) {
        return mouseOver(cursorX, cursorY) ? 2 : highlight ? 3 : enabled ? 1 : 0;
    }

    public Categories ID() {
        return id;
    }

}
