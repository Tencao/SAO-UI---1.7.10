package com.tencao.saoui;

import com.tencao.saoui.ui.SAOConfirmGUI;
import com.tencao.saoui.ui.SAOMessageGUI;
import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.ui.SAOWindowGUI;
import com.tencao.saoui.util.SAOActionHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SAOWindowViewGUI extends SAOScreenGUI {

	private final int windowWidth, windowHeight;

	private SAOWindowViewGUI(int width, int height) {
		super();
		windowWidth = width;
		windowHeight = height;
	}

	protected void init() {
		elements.add(createWindow(windowWidth, windowHeight));
	}

	protected abstract SAOWindowGUI createWindow(int width, int height);

	public final SAOWindowGUI getWindow() {
		return (SAOWindowGUI) elements.get(0);
	}

	public int getX(boolean relative) {
        return super.getX(relative) + (width - windowWidth) / 2;
	}

	public int getY(boolean relative) {
        return super.getY(relative) + (height - windowHeight) / 2;
	}

	public void drawScreen(int cursorX, int cursorY, float f) {
		drawDefaultBackground();
		
		super.drawScreen(cursorX, cursorY, f);
	}

	protected void backgroundClicked(int cursorX, int cursorY, int button) {}
	
	public static SAOWindowViewGUI viewMessage(final String username, final String message) {
		return new SAOWindowViewGUI(200, 40) {

			@Override
			protected SAOWindowGUI createWindow(int width, int height) {
				return new SAOMessageGUI(this, 0, 0, width, height, message, username);
			}

		};
	}

	public static SAOWindowViewGUI viewConfirm(final String title, final String message, final SAOActionHandler handler) {
		return new SAOWindowViewGUI(200, 60) {

			@Override
			protected SAOWindowGUI createWindow(int width, int height) {
				return new SAOConfirmGUI(this, 0, 0, width, height, title, message, handler);
			}

		};
	}

}
