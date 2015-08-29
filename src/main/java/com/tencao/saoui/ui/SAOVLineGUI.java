package com.tencao.saoui.ui;

import net.minecraft.client.Minecraft;

import com.tencao.saoui.util.SAOColor;
import com.tencao.saoui.util.SAOGL;
import com.tencao.saoui.util.SAOParentGUI;
import com.tencao.saoui.util.SAOResources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOVLineGUI extends SAOElementGUI {

	public int lineWidth;

	public SAOVLineGUI(SAOParentGUI gui, int xPos, int yPos, int size) {
		super(gui, xPos, yPos, size, 2);
		lineWidth = size;
	}

	public void draw(Minecraft mc, int cursorX, int cursorY) {
		super.draw(mc, cursorX, cursorY);
		
		if (visibility > 0) {
			SAOGL.glBindTexture(SAOResources.gui);
			SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.DEFAULT_FONT_COLOR, visibility));
			
			final int left = getX(false) + (width - lineWidth) / 2;
			final int top = getY(false);
			
			SAOGL.glTexturedRect(left, top, lineWidth, 2, 42, 42, 4, 2);
		}
	}

}
