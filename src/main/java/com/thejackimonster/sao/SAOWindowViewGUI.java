package com.thejackimonster.sao;

import net.minecraft.client.gui.GuiScreen;

import com.thejackimonster.sao.ui.SAOConfirmGUI;
import com.thejackimonster.sao.ui.SAOElementGUI;
import com.thejackimonster.sao.ui.SAOMessageGUI;
import com.thejackimonster.sao.ui.SAOScreenGUI;
import com.thejackimonster.sao.ui.SAOWindowGUI;
import com.thejackimonster.sao.util.SAOActionHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOWindowViewGUI extends SAOScreenGUI {

	private final int windowWidth, windowHeight;

	private SAOWindowViewGUI(int width, int height) {
		super();
		windowWidth = width;
		windowHeight = height;
	}

	protected void init() {
		elements.add(createWindow(windowWidth, windowHeight));
	}

	public SAOWindowGUI createWindow(int width, int height) {
		return null;
	}

	public final SAOWindowGUI getWindow() {
		return (SAOWindowGUI) elements.get(0);
	}

	public int getX(boolean relative) {
		return (width - windowWidth) / 2;
	}

	public int getY(boolean relative) {
		return (height - windowHeight) / 2;
	}

	public void drawScreen(int cursorX, int cursorY, float f) {
		drawDefaultBackground();
		
		super.drawScreen(cursorX, cursorY, f);
	}

	protected void backgroundClicked(int cursorX, int cursorY, int button) {}

	public static final SAOWindowViewGUI viewMessage(final String username, final String message) {
		return new SAOWindowViewGUI(200, 40) {

			public SAOWindowGUI createWindow(int width, int height) {
				return new SAOMessageGUI(this, 0, 0, width, height, message, username);
			}

		};
	}

	public static final SAOWindowViewGUI viewConfirm(final String title, final String message, final SAOActionHandler handler) {
		return new SAOWindowViewGUI(200, 60) {

			public SAOWindowGUI createWindow(int width, int height) {
				return new SAOConfirmGUI(this, 0, 0, width, height, title, message, handler);
			}

		};
	}

}
