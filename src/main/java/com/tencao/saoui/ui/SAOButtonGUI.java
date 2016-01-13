package com.tencao.saoui.ui;

import com.tencao.saoui.util.*;
import net.minecraft.client.Minecraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOButtonGUI extends SAOElementGUI {

	private final SAOID id;

	public String caption;
	public SAOIcon icon;
	public boolean highlight;

	public SAOButtonGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, int w, int h, String string, SAOIcon saoIcon) {
		super(gui, xPos, yPos, w, h);
		id = saoID;
		caption = string;
		icon = saoIcon;
		highlight = false;
	}

	public SAOButtonGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, int w, String string, SAOIcon saoIcon) {
		this(gui, saoID, xPos, yPos, w, 20, string, saoIcon);
	}

	public SAOButtonGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, String string, SAOIcon saoIcon) {
		this(gui, saoID, xPos, yPos, 100, string, saoIcon);
	}

	public SAOButtonGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, String string, SAOIcon saoIcon, boolean highlighted) {
		this(gui, saoID, xPos, yPos, 100, string, saoIcon);
		highlight = highlighted;
	}

	public SAOButtonGUI(SAOParentGUI gui, SAOID slot, int xPos, int yPos, int w, int h) {
		this(gui, slot, xPos, yPos, w, h, "", SAOIcon.NONE);
	}

	@Override
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
			
			final int width2 = width / 2;
			final int height2 = height / 2;
			
			SAOGL.glTexturedRect(left, top, 0, 45, width2, height2);
			SAOGL.glTexturedRect(left + width2, top, 200 - width2, 45, width2, height2);
			SAOGL.glTexturedRect(left, top + height2, 0, 65 - height2, width2, height2);
			SAOGL.glTexturedRect(left + width2, top + height2, 200 - width2, 65 - height2, width2, height2);
			
			final int iconOffset = (height - 16) / 2;
			
			SAOGL.glColorRGBA(SAOColor.multiplyAlpha(color1, visibility));
			SAOGL.glTexturedRect(left + iconOffset, top + iconOffset, 140, 25, 16, 16);
			
			SAOGL.glColorRGBA(SAOColor.multiplyAlpha(color0, visibility));
			icon.glDraw(left + iconOffset, top + iconOffset);
			
			final int captionOffset = (height - SAOGL.glStringHeight()) / 2;
			
			SAOGL.glString(caption, left + iconOffset * 2 + 16 + 4, top + captionOffset, SAOColor.multiplyAlpha(color1, visibility));
		}
	}

	public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
		return (button == 0);
	}

	int getColor(int hoverState, boolean bg) {
		return bg ? hoverState == 1 ? SAOColor.DEFAULT_COLOR.rgba : hoverState >= 2 ? SAOColor.HOVER_COLOR.rgba : SAOColor.DISABLED_MASK.rgba : hoverState == 1 ? SAOColor.DEFAULT_FONT_COLOR.rgba : hoverState >= 2 ? SAOColor.HOVER_FONT_COLOR.rgba : SAOColor.DEFAULT_FONT_COLOR.rgba & SAOColor.DISABLED_MASK.rgba;
	}

	public int hoverState(int cursorX, int cursorY) {
		return mouseOver(cursorX, cursorY) ? 2 : highlight ? 3 : enabled ? 1 : 0;
	}

	public SAOID ID() {
		return id;
	}

}
