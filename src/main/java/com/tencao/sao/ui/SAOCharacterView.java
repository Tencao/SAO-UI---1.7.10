package com.tencao.sao.ui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

import com.tencao.sao.util.SAOColor;
import com.tencao.sao.util.SAOGL;
import com.tencao.sao.util.SAOOption;
import com.tencao.sao.util.SAOParentGUI;
import com.tencao.sao.util.SAOResources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOCharacterView extends SAOElementGUI {
	

	private final EntityPlayer character;
    private int clickIndex;

	public SAOCharacterView(SAOParentGUI gui, int xPos, int yPos, int w, int h, EntityPlayer player) {
		super(gui, xPos, yPos, w, h);
		character = player;
	}
	
    @Override
	public void draw(Minecraft mc, int cursorX, int cursorY) {
		super.draw(mc, cursorX, cursorY);
		
		if (visibility > 0) {
			SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);
			SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.DEFAULT_COLOR, visibility));
			
			int left = getX(false) + width / 2;
			int top = getY(false) + height * 13 / 16;
			
			final int size = width * height / 550;
			
			final int shadowX = size;
			final int shadowY = size / 2 + Math.max(Math.min((cursorY - top) / 12, 0), -size / 2 + 2);
			
			final int shadowOffset = Math.max((cursorY - top) / 10, 0);
			
			SAOGL.glTexturedRect(left - shadowX / 2, (top - shadowY / 2), shadowX, shadowY, 200, 85, 56, 30);
			
			drawCharacter(character, left, top, size, cursorX, cursorY);
			
			//SAOGL.glBindTexture(SAOResources.gui);
			
			left = getX(false) + width / 2;
			top = getY(false) + height / 2;
			
			final int width2 = (width / 2) - 14;
			final int height2 = (height / 2) - 14;
			
			for (int angle = 0; angle < 360; angle += 30) {
				final int x = (int) (left + Math.sin(Math.toRadians(angle)) * width2);
				final int y = (int) (top + Math.cos(Math.toRadians(angle)) * height2);

                final boolean hovered = ((cursorX >= x - 10) && (cursorY >= y - 10) && (cursorX <= x + 10) && (cursorY <= y + 10));

                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

                SAOGL.glColorRGBA(SAOColor.multiplyAlpha(hovered ? SAOColor.HOVER_COLOR : SAOColor.DEFAULT_FONT_COLOR, visibility));
                SAOGL.glTexturedRect(x - 10, y - 10, 0, 25, 20, 20);

                if ((angle + 4 < 9) || (angle + 4 >= 12)) {
                    final int index = (angle + 4 >= 12 ? (angle - 8) % 9 : (angle + 4) % 9);
                    final Slot slot = character.inventoryContainer.getSlotFromInventory(character.inventory, index);

                    if ((slot.getHasStack()) && (slot.getStack().getItem() != null)) {
                        SAOGL.glColorRGBA(SAOColor.multiplyAlpha(hovered ? SAOColor.HOVER_FONT_COLOR : SAOColor.DEFAULT_COLOR, visibility));
                        SAOSlotGUI.getIcon(slot.getStack()).glDraw(x - 8, y - 8);
                    }

                    if (hovered) {
                        clickIndex = index;
                    }
                }
			}
		}
	}

    @Override
	public boolean keyTyped(Minecraft mc, char ch, int key) {
        if (character == mc.thePlayer) {
            for (int i = 0; i < 9; i++) {
                if (key == mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
                    character.inventory.currentItem = i;
                    return true;
                }
            }
        }

        return super.keyTyped(mc, ch, key);
    }

    @Override
	public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
        if ((clickIndex >= 0) && (button == 0) && (character == mc.thePlayer)) {
            character.inventory.currentItem = clickIndex;
            return true;
        }

        return super.mousePressed(mc, cursorX, cursorY, button);
    }
    
	public static final void drawCharacter(EntityPlayer character, int x, int y, int size, int cursorX, int cursorY) {
		final float mouseX = (float) x - cursorX;
		final float mouseY = (float) y - size * 1.67F - cursorY;
		
        final boolean value = SAOOption.COLOR_CURSOR.value;

        SAOOption.COLOR_CURSOR.value = false;
		GuiInventory.func_147046_a(x, y, size, mouseX, mouseY, character);
        SAOOption.COLOR_CURSOR.value = value;
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	}

}
