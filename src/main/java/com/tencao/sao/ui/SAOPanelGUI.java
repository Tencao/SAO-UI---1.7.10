package com.tencao.sao.ui;

import net.minecraft.client.Minecraft;

import com.tencao.sao.util.SAOColor;
import com.tencao.sao.util.SAOGL;
import com.tencao.sao.util.SAOOption;
import com.tencao.sao.util.SAOParentGUI;
import com.tencao.sao.util.SAOResources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOPanelGUI extends SAOMenuGUI {

	public int bgColor;

	public SAOPanelGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h) {
		super(gui, xPos, yPos, w, h);
		bgColor = SAOColor.DEFAULT_COLOR;
	}

    @Override
	public void draw(Minecraft mc, int cursorX, int cursorY) {
		if ((visibility > 0) && (height > 0)) {
            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);
			
			final int left = getX(false);
			final int top = getY(false);
			
			SAOGL.glColorRGBA(SAOColor.multiplyAlpha(bgColor, visibility));
			
			final int shadowSize = (x == 0? 0 : 5);
			
			if (shadowSize > 0) {
				SAOGL.glTexturedRect(left - shadowSize, top - shadowSize, 5 - shadowSize, 120 - shadowSize,  shadowSize, shadowSize);
				SAOGL.glTexturedRect(left + width, top - shadowSize, 15, 120 - shadowSize,  shadowSize, shadowSize);
				SAOGL.glTexturedRect(left - shadowSize, top + height, 5 - shadowSize, 130,  shadowSize, shadowSize);
				SAOGL.glTexturedRect(left + width, top + height, 15, 130,  shadowSize, shadowSize);
				
				SAOGL.glTexturedRect(left, top - shadowSize, width, shadowSize, 5, 120 - shadowSize, 10, shadowSize);
				SAOGL.glTexturedRect(left - shadowSize, top, shadowSize, height, 5 - shadowSize, 120, shadowSize, 10);
				SAOGL.glTexturedRect(left + width, top, shadowSize, height, 15, 120, shadowSize, 10);
				SAOGL.glTexturedRect(left, top + height, width, shadowSize, 5, 130, 10, shadowSize);
			}
			
			SAOGL.glTexturedRect(left, top, width, height, 5, 120, 10, 10);
			
			if (x == 0) {
				SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.DEFAULT_COLOR, visibility));
				
				SAOGL.glTexturedRect(left + 5, top, 156, 25, 10, 10);
			}
		}
		
		super.draw(mc, cursorX, cursorY);
	}

}
