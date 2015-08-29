package com.tencao.saoui.ui;

import net.minecraft.client.Minecraft;

import com.tencao.saoui.util.SAOColor;
import com.tencao.saoui.util.SAOGL;
import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.util.SAOIcon;
import com.tencao.saoui.util.SAOParentGUI;
import com.tencao.saoui.util.SAOResources;

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

	public void draw(Minecraft mc, int cursorX, int cursorY) {
		super.draw(mc, cursorX, cursorY);
		
		if (visibility > 0) {
			SAOGL.glBindTexture(SAOResources.gui);
			
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
		if (bg) {
			return hoverState == 1? SAOColor.DEFAULT_COLOR : hoverState == 2? SAOColor.HOVER_COLOR : SAOColor.DEFAULT_COLOR & SAOColor.DISABLED_MASK;
		} else {
			return hoverState == 1? SAOColor.DEFAULT_FONT_COLOR : hoverState == 2? SAOColor.HOVER_FONT_COLOR : SAOColor.DEFAULT_FONT_COLOR & SAOColor.DISABLED_MASK;
		}
	}

	public int hoverState(int cursorX, int cursorY) {
		if ((highlight) || (mouseOver(cursorX, cursorY))) {
			return 2;
		} else
		if (enabled) {
			return 1;
		} else {
			return 0;
		}
	}

	public SAOID ID() {
		return id;
	}

}
