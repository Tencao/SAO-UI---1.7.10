package com.thejackimonster.sao.ui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.thejackimonster.sao.SAOIngameGUI;
import com.thejackimonster.sao.util.SAOAction;
import com.thejackimonster.sao.util.SAOParentGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public abstract class SAOScreenGUI extends GuiScreen implements SAOParentGUI {

	public final List<SAOElementGUI> elements;

	public SAOScreenGUI() {
		super();
		elements = new ArrayList<SAOElementGUI>();
	}

	public void initGui() {
		super.initGui();
		elements.clear();
		init();
	}

	protected void init() {}

	public int getX(boolean relative) {
		return 0;
	}

	public int getY(boolean relative) {
		return 0;
	}

	public void updateScreen() {
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			if (elements.get(i).removed()) {
				elements.get(i).close(mc);
				elements.remove(i);
				continue;
			}
			
			elements.get(i).update(mc);
		}
	}

	public void drawScreen(int cursorX, int cursorY, float f) {
		super.drawScreen(cursorX, cursorY, f);
		
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			elements.get(i).draw(mc, cursorX, cursorY);
		}
	}

	protected void keyTyped(char ch, int key) {
		super.keyTyped(ch, key);
		
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			if (elements.get(i).focus) {
				if (elements.get(i).keyTyped(mc, ch, key)) {
					actionPerformed(elements.get(i), SAOAction.KEY_TYPED, key);
				}
			}
		}
	}

	protected void mouseClicked(int cursorX, int cursorY, int button) {
		super.mouseClicked(cursorX, cursorY, button);
		boolean clickedElement = false;
		
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			if (elements.get(i).mouseOver(cursorX, cursorY)) {
				if (elements.get(i).mousePressed(mc, cursorX, cursorY, button)) {
					actionPerformed(elements.get(i), SAOAction.getAction(button, true), button);
				}
				
				clickedElement = true;
			}
		}
		
		if (!clickedElement) {
			backgroundClicked(cursorX, cursorY, button);
		}
	}

	protected void backgroundClicked(int cursorX, int cursorY, int button) {
		if (button == 0) {
			if (!((SAOIngameGUI) mc.ingameGUI).backgroundClicked(cursorX, cursorY, button)) {
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
			}
		}
	}

	protected void mouseMovedOrUp(int cursorX, int cursorY, int button) {
		super.mouseMovedOrUp(cursorX, cursorY, button);
		
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			if (elements.get(i).mouseOver(cursorX, cursorY, button)) {
				if (elements.get(i).mouseReleased(mc, cursorX, cursorY, button)) {
					actionPerformed(elements.get(i), SAOAction.getAction(button, false), button);
				}
			}
		}
	}

	protected void mouseWheel(int cursorX, int cursorY, int delta) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			if (elements.get(i).mouseOver(cursorX, cursorY)) {
				if (elements.get(i).mouseWheel(mc, cursorX, cursorY, delta)) {
					actionPerformed(elements.get(i), SAOAction.MOUSE_WHEEL, delta);
				}
			}
		}
	}

	public void actionPerformed(SAOElementGUI element, SAOAction action, int data) {
		element.click(mc.getSoundHandler());
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		
		if (Mouse.hasWheel()) {
			final int x = Mouse.getEventX() * width / mc.displayWidth;
			final int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
			final int delta = Mouse.getEventDWheel();
			
			if (delta != 0) {
				mouseWheel(x, y, delta);
			}
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void onGuiClosed() {
		close();
	}

	public void close() {
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			elements.get(i).close(mc);
			elements.remove(i);
		}
	}

}
