package com.tencao.saoui.ui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;

import com.tencao.saoui.SAOIngameGUI;
import com.tencao.saoui.util.SAOAction;
import com.tencao.saoui.util.SAOColor;
import com.tencao.saoui.util.SAOCursorStatus;
import com.tencao.saoui.util.SAOGL;
import com.tencao.saoui.util.SAOOption;
import com.tencao.saoui.util.SAOParentGUI;
import com.tencao.saoui.util.SAOResources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SAOScreenGUI extends GuiScreen implements SAOParentGUI {

    private static final float ROTATION_FACTOR = 0.25F;
    protected static SAOCursorStatus CURSOR_STATUS = SAOCursorStatus.SHOW;

    private int mouseX, mouseY;

    private int mouseDown;
    private float mouseDownValue;
    
	public final List<SAOElementGUI> elements;

    private float[] rotationYaw, rotationPitch;
    private boolean grabbed;
    
	public SAOScreenGUI() {
		super();
		elements = new ArrayList<SAOElementGUI>();
        grabbed = false;
	}

    @Override
	public void initGui() {
        if (CURSOR_STATUS != SAOCursorStatus.DEFAULT) {
            Mouse.setGrabbed(true);
            grabbed = true;
        }

        super.initGui();
        elements.clear();
        init();
	}

	protected void init() {
        if (mc.thePlayer != null) {
            rotationYaw = new float[]{mc.thePlayer.rotationYaw};
            rotationPitch = new float[]{mc.thePlayer.rotationPitch};
        }
    }
	
    private int getCursorX() {
    	if (SAOOption.CURSOR_MOVEMENT.value){
            return SAOOption.CURSOR_MOVEMENT.value ? (width / 2 - mouseX) / 2 : 0;
    	}
    	else {
            return 0;
    	}
    }

    private int getCursorY() {
    	if (SAOOption.CURSOR_MOVEMENT.value){
        return SAOOption.CURSOR_MOVEMENT.value ? (height / 2 - mouseY) / 2 : 0;
    	}
    	else {
            return 0;
    	}
    }

    @Override
	public int getX(boolean relative) {
        return getCursorX();
	}

    @Override
	public int getY(boolean relative) {
        return getCursorY();
	}

    @Override
	public void updateScreen() {
        if (this.elements == null) return;
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

    @Override
	public void drawScreen(int cursorX, int cursorY, float f) {
        if (this.elements == null) return;
        for (SAOElementGUI el: this.elements) if (el == null) return;
        mouseX = cursorX;
        mouseY = cursorY;

        if (mc.thePlayer != null) {
            mc.thePlayer.rotationYaw = rotationYaw[0] - getCursorX() * ROTATION_FACTOR;
            mc.thePlayer.rotationPitch = rotationPitch[0] - getCursorY() * ROTATION_FACTOR;
        }

        super.drawScreen(cursorX, cursorY, f);

        SAOGL.glStartUI(mc);

        SAOGL.glBlend(true);
        SAOGL.tryBlendFuncSeparate(770, 771, 1, 0);

        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                continue;
            }

            elements.get(i).draw(mc, cursorX, cursorY);
        }

        if (CURSOR_STATUS == SAOCursorStatus.SHOW) {
            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

            SAOGL.glBlend(true);
            SAOGL.tryBlendFuncSeparate(770, 771, 1, 0);

            if (mouseDown != 0) {
                final float fval = f * 0.1F;

                if (mouseDownValue + fval < 1.0F) {
                    mouseDownValue += fval;
                } else {
                    mouseDownValue = 1.0F;
                }

                SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.CURSOR_COLOR, mouseDownValue));
                SAOGL.glTexturedRect(cursorX - 7, cursorY - 7, 35, 115, 15, 15);

                SAOGL.glColorRGBA(SAOColor.DEFAULT_COLOR);
            } else {
                mouseDownValue = 0;

                SAOGL.glColorRGBA(SAOColor.CURSOR_COLOR);
            }

            SAOGL.glTexturedRect(cursorX - 7, cursorY - 7, 20, 115, 15, 15);
        }

        SAOGL.glEndUI(mc);
	}

    @Override
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

    @Override
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

    @Override
    protected void mouseMovedOrUp(int cursorX, int cursorY, int button) {
        super.mouseMovedOrUp(cursorX, cursorY, button);
        mouseDown &= (~(0x1 << button));

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
    
	protected void backgroundClicked(int cursorX, int cursorY, int button) {
		if (button == 0) {
			if (!((SAOIngameGUI) mc.ingameGUI).backgroundClicked(cursorX, cursorY, button)) {
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
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

    @Override
	public void actionPerformed(SAOElementGUI element, SAOAction action, int data) {
    	element.click(mc.getSoundHandler(), false);
	}

    @Override
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

    @Override
	public boolean doesGuiPauseGame() {
		return false;
	}

    @Override
	public void onGuiClosed() {
        if (grabbed) {
            Mouse.setGrabbed(false);
        }
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
