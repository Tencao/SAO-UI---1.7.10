package com.tencao.sao.ui;

import com.tencao.sao.util.SAOParentGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class SAOListGUI extends SAOMenuGUI {

	private float scrolledValue;
	public int scrollValue;
    private int size;
    private int minSize;

	private int lastDragY, dragY;
	private boolean dragging;

	public SAOListGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h, int minH) {
		super(gui, xPos, yPos, w, h);
		fullArrow = false;
		scrollValue = 0;
		size = h;
		minSize = minH;
	}

	public SAOListGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h) {
		this(gui, xPos, yPos, w, h, 0);
	}

    @Override
	int getOffset(int index) {
        return Math.round(super.getOffset(index) - scrolledValue);
    }

    @Override
	int getSize() {
        return Math.max(Math.min(size, super.getOffset(elements.size())), minSize);
    }

    @Override
	void update(Minecraft mc, int index, SAOElementGUI element) {
		super.update(mc, index, element);
		
		final int elementY = element.getY(false);
		final int elementSize = element.height;
		
		final int listY = getY(false);
		final int listSize = getSize();
		
		if (elementY < listY) {
			element.visibility = Math.max(1.0F - (float) (listY - elementY) / listSize, 0.0F);
		} else
		if (elementY + elementSize > listY + listSize) {
			element.visibility = Math.max(1.0F - (float) ((elementY + elementSize) - (listY + listSize)) / listSize, 0.0F);
		} else {
			element.visibility = 1;
		}
		
		if (element.visibility < 0.6F) {
			element.visibility = 0;
		} else {
			element.visibility *= element.visibility;
		}
	}

    @Override
	public void draw(Minecraft mc, int cursorX, int cursorY) {
		scrolledValue = (scrolledValue + scrollValue) / 2;
		
		super.draw(mc, cursorX, cursorY);
	}

    @Override
	public boolean mouseOver(int cursorX, int cursorY, int flag) {
		if (!super.mouseOver(cursorX, cursorY, flag)) {
			if (dragging) {
				dragY += scroll(cursorY - lastDragY);
				lastDragY = cursorY;
			}
			
			dragging = false;
			return false;
		} else {
			return true;
		}
	}

    @Override
	public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
		if (button == 0) {
			dragY = 0;
			lastDragY = cursorY;
			dragging = true;
		}
		
		return super.mousePressed(mc, cursorX, cursorY, button);
	}

    @Override
	public void mouseMoved(Minecraft mc, int cursorX, int cursorY) {
		if (dragging) {
			dragY += scroll(cursorY - lastDragY);
			lastDragY = cursorY;
		}
	}

    @Override
	public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
		boolean wasDragging = false;
		
		if (button == 0) {
			if (dragging) {
				dragY += scroll(cursorY - lastDragY);
				wasDragging = (dragY > 0);
				lastDragY = cursorY;
			}
			
			dragging = false;
		}
		
		return (!wasDragging) && (super.mouseReleased(mc, cursorX, cursorY, button));
	}

    @Override
	public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
		if (elements.size() > 0) {
			scroll(Math.abs(delta * 2 * getSize() / elements.size()) / delta);
		}
		return super.mouseWheel(mc, cursorX, cursorY, delta);
	}

	protected int scroll(int delta) {
		final int value = scrollValue;
		scrollValue = Math.min(Math.max(scrollValue - delta, 0), super.getOffset(elements.size()) - getSize());
		return Math.abs(value - scrollValue);
	}

}
