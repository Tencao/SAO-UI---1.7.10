package com.thejackimonster.sao.ui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

import com.thejackimonster.sao.util.SAOColor;
import com.thejackimonster.sao.util.SAOGL;
import com.thejackimonster.sao.util.SAOParentGUI;
import com.thejackimonster.sao.util.SAOResources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOCharacterView extends SAOElementGUI {

	private final EntityPlayer character;

	public SAOCharacterView(SAOParentGUI gui, int xPos, int yPos, int w, int h, EntityPlayer player) {
		super(gui, xPos, yPos, w, h);
		character = player;
	}

	public void draw(Minecraft mc, int cursorX, int cursorY) {
		super.draw(mc, cursorX, cursorY);
		
		if (visibility > 0) {
			SAOGL.glBindTexture(SAOResources.gui);
			SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.DEFAULT_COLOR, visibility));
			
			int left = getX(false) + width / 2;
			int top = getY(false) + height * 13 / 16;
			
			final int size = width * height / 550;
			
			final int shadowX = size;
			final int shadowY = size / 2 + Math.max(Math.min((cursorY - top) / 12, 0), -size / 2 + 2);
			
			final int shadowOffset = Math.max((cursorY - top) / 10, 0);
			
			SAOGL.glTexturedRect(left - shadowX / 2, (top - shadowY / 2), shadowX, shadowY, 200, 85, 56, 30);
			
			drawCharacter(character, left, top, size, cursorX, cursorY);
			
			SAOGL.glBindTexture(SAOResources.gui);
			
			left = getX(false) + width / 2;
			top = getY(false) + height / 2;
			
			final int width2 = (width / 2) - 14;
			final int height2 = (height / 2) - 14;
			
			for (int angle = 0; angle < 360; angle += 30) {
				final int x = (int) (left + Math.sin(Math.toRadians(angle)) * width2);
				final int y = (int) (top + Math.cos(Math.toRadians(angle)) * height2);
				
				if ((cursorX >= x - 10) && (cursorY >= y - 10) && (cursorX <= x + 10) && (cursorY <= y + 10)) {
					SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.HOVER_COLOR, visibility));
				} else {
					SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.DEFAULT_FONT_COLOR, visibility));
				}
				
				SAOGL.glTexturedRect(x - 10, y - 10, 0, 25, 20, 20);
			}
		}
	}

	public static final void drawCharacter(EntityPlayer character, int x, int y, int size, int cursorX, int cursorY) {
		final float mouseX = (float) x - cursorX;
		final float mouseY = (float) y - size * 1.67F - cursorY;
		
		GuiInventory.func_147046_a(x, y, size, mouseX, mouseY, character);
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

}
