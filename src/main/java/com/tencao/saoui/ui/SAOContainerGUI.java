package com.tencao.saoui.ui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

import com.tencao.saoui.util.SAOAction;
import com.tencao.saoui.util.SAOParentGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOContainerGUI extends SAOElementGUI {

	public final List<SAOElementGUI> elements;

	public SAOContainerGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h) {
		super(gui, xPos, yPos, w, h);
		elements = new ArrayList<SAOElementGUI>();
	}

	public void update(Minecraft mc) {
		focus = false;
		
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			update(mc, i, elements.get(i));
		}
	}

	protected void update(Minecraft mc, int index, SAOElementGUI element) {
		if (element.removed()) {
			elements.remove(index);
			return;
		}
		
		element.update(mc);
		focus |= element.focus;
	}

	public void draw(Minecraft mc, int cursorX, int cursorY) {
		super.draw(mc, cursorX, cursorY);
		
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			elements.get(i).draw(mc, cursorX, cursorY);
		}
	}

	public boolean keyTyped(Minecraft mc, char ch, int key) {
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
		
		return super.keyTyped(mc, ch, key);
	}

	public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			if (elements.get(i).mouseOver(cursorX, cursorY)) {
				if (elements.get(i).mousePressed(mc, cursorX, cursorY, button)) {
					actionPerformed(elements.get(i), SAOAction.getAction(button, true), button);
				}
			}
		}
		
		return super.mousePressed(mc, cursorX, cursorY, button);
	}

	public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
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
		
		return super.mouseReleased(mc, cursorX, cursorY, button);
	}

	public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
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
		
		return super.mouseWheel(mc, cursorX, cursorY, delta);
	}

	public void close(Minecraft mc) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			if (i >= elements.size()) {
				continue;
			}
			
			elements.get(i).close(mc);
			elements.remove(i);
		}
		
		super.close(mc);
	}

}
