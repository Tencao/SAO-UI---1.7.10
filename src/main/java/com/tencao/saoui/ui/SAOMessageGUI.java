package com.tencao.saoui.ui;

import net.minecraft.client.Minecraft;

import com.tencao.saoui.SAOMod;
import com.tencao.saoui.util.SAOAlign;
import com.tencao.saoui.util.SAOParentGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOMessageGUI /*extends SAOWindowGUI*/ {
	/*
	private final SAOTextGUI textText;
	private final SAOLabelGUI fromLable;

	public SAOMessageGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h, String text, String from) {
		super(gui, xPos, yPos, w, h, SAOMod._MESSAGE_TITLE);
		final String fromString = String.format(SAOMod._MESSAGE_FROM, from);
		
		elements.add(textText = new SAOTextGUI(this, 0, 0, text, width));
		elements.add(fromLable = new SAOLabelGUI(this, 0, 0, fromString, SAOAlign.RIGHT));
		textText.visibility = 0;
	}

	public final void setText(String text) {
		textText.setText(text);
	}

	public final String getText() {
		return textText.getText();
	}

	public final void setSender(String sender) {
		fromLable.caption = sender;
	}

	public final String getSender() {
		return fromLable.caption;
	}

	protected int getSize() {
		return Math.max(super.getSize() - 20, 40);
	}

	public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
		if (button == 0) {
			if (textText.visibility < 1) {
				textText.visibility = 1;
			} else {
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
			}
		}
		
		return super.mouseReleased(mc, cursorX, cursorY, button);
	}*/

}
