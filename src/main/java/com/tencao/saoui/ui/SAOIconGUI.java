package com.tencao.saoui.ui;

import com.tencao.saoui.SAOSound;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;

@SideOnly(Side.CLIENT)
public class SAOIconGUI extends SAOElementGUI {

    private final SAOID id;

    public boolean highlight;
    public SAOColor bgColor, disabledMask;
    private SAOIcon icon;

    public SAOIconGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, SAOIcon saoIcon) {
        super(gui, xPos, yPos, 20, 20);
        id = saoID;
        icon = saoIcon;
        highlight = false;
        bgColor = SAOColor.DEFAULT_COLOR;
        disabledMask = SAOColor.DISABLED_MASK;
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if (visibility > 0) {
            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);

            final int hoverState = hoverState(cursorX, cursorY);

            final int color0 = getColor(hoverState, true);
            final int color1 = getColor(hoverState, false);

            SAOGL.glColorRGBA(SAOColor.multiplyAlpha(color0, visibility));

            final int left = getX(false);
            final int top = getY(false);

            SAOGL.glTexturedRect(left, top, 0, 25, 20, 20);

            final int iconOffset = 2;

            SAOGL.glColorRGBA(SAOColor.multiplyAlpha(color1, visibility));
            icon.glDraw(left + iconOffset, top + iconOffset);
        }
    }

    protected int getColor(int hoverState, boolean bg) {
        if (icon == SAOIcon.CONFIRM)
            return bg ? hoverState == 1 ? SAOColor.CONFIRM_COLOR.rgba : hoverState == 2 ? SAOColor.CONFIRM_COLOR_LIGHT.rgba : SAOColor.CONFIRM_COLOR.rgba & disabledMask.rgba : hoverState > 0 ? SAOColor.HOVER_FONT_COLOR.rgba : disabledMask.rgba;
        else if (icon == SAOIcon.CANCEL)
            return bg ? hoverState == 1 ? SAOColor.CANCEL_COLOR.rgba : hoverState == 2 ? SAOColor.CANCEL_COLOR_LIGHT.rgba : SAOColor.CANCEL_COLOR.rgba & disabledMask.rgba : hoverState > 0 ? SAOColor.HOVER_FONT_COLOR.rgba : disabledMask.rgba;
        else
            return bg ? hoverState == 1 ? bgColor.rgba : hoverState == 2 ? SAOColor.HOVER_COLOR.rgba : bgColor.rgba & disabledMask.rgba : hoverState == 1 ? SAOColor.DEFAULT_FONT_COLOR.rgba : hoverState == 2 ? SAOColor.HOVER_FONT_COLOR.rgba : SAOColor.DEFAULT_FONT_COLOR.rgba & disabledMask.rgba;
    }

    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        return (button == 0);
    }

    @Override
    public void click(SoundHandler handler, boolean flag) {
        if (icon == SAOIcon.CONFIRM) SAOSound.play(handler, SAOSound.CONFIRM);
        else super.click(handler, flag);
    }

    public int hoverState(int cursorX, int cursorY) {
        return highlight || mouseOver(cursorX, cursorY) ? 2 : enabled ? 1 : 0;
    }

    @Override
    public SAOID ID() {
        return id;
    }

}
