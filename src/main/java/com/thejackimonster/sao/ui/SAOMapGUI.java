package com.thejackimonster.sao.ui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.thejackimonster.sao.util.SAOColor;
import com.thejackimonster.sao.util.SAOGL;
import com.thejackimonster.sao.util.SAOParentGUI;
import com.thejackimonster.sao.util.SAOResources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOMapGUI extends SAOElementGUI {

	public static final int MAP_SIZE = 16;
	public static final int MAP_DATA_SIZE = (MAP_SIZE * 2 + 1);

	private final EntityPlayer character;
	private int[][] map;
	private long medium;
	private int min, max;

	public int scan;
	public int zoom;

	public SAOMapGUI(SAOParentGUI gui, int xPos, int yPos, int size, EntityPlayer player) {
		super(gui, xPos, yPos, MAP_DATA_SIZE * size, MAP_DATA_SIZE * size + 16);
		character = player;
		map = null;
		scan = 16;
		zoom = 1;
	}

	public final void scanMap() {
		if ((character != null) && (character.worldObj != null)) {
			int i, j;
			
			map = new int[MAP_DATA_SIZE][MAP_DATA_SIZE];
			medium = 0L;
			min = character.worldObj.getActualHeight();
			max = 0;
			
			final int originY = (int) (character.posY - 1.0);
			int count = 0;
			
			for (i = -16; i <= 16; i++) {
				for (j = -16; j <= 16; j++) {
					final int x = (int) ((Math.round(character.posX) / zoom + i) * zoom);
					final int z = (int) ((Math.round(character.posZ) / zoom + j) * zoom);
					
					int endScan = 0x0;
					int y = 0;
					
					do {
						if ((originY + y < character.worldObj.getActualHeight()) &&
							(character.worldObj.isAirBlock(x, originY + y, z) != character.worldObj.isAirBlock(x, originY + (y + 1), z))) {
							break;
						} else {
							endScan |= 0x1;
						}
						
						if ((originY - y > 0) &&
							(character.worldObj.isAirBlock(x, originY - y, z) != character.worldObj.isAirBlock(x, originY - (y + 1), z))) {
							y *= -1;
							break;
						} else {
							endScan |= 0x2;
						}
						
						y++;
					} while (y < scan);
					
					if (y == scan) {
						map[16 + i][16 + j] = -endScan;
					} else {
						map[16 + i][16 + j] = (originY + y);
						
						medium += map[16 + i][16 + j];
						count++;
						
						if (map[16 + i][16 + j] < min) {
							min = map[16 + i][16 + j];
						}
						
						if (map[16 + i][16 + j] > max) {
							max = map[16 + i][16 + j];
						}
					}
				}
			}
			
			for (i = -16; i <= 16; i++) {
				for (j = -16; j <= 16; j++) {
					if (map[16 + i][16 + j] < 0) {
						map[16 + i][16 + j] = (map[16 + i][16 + j] == -2? min : max);
						
						medium += map[16 + i][16 + j];
						count++;
					}
				}
			}
			
			if (count > 0) {
				medium /= count;
			}
		} else {
			map = null;
		}
	}

	public void update(Minecraft mc) {
		super.update(mc);
		
		if (mc.thePlayer != character) {
			final int zoom_factor = (int) (character.getDistanceSqToEntity(mc.thePlayer) / (MAP_DATA_SIZE * MAP_DATA_SIZE));
			
			if (zoom_factor > zoom) {
				zoom = zoom_factor;
			}
		}
		
		if (map == null) {
			scanMap();
		}
	}

	public void draw(Minecraft mc, int cursorX, int cursorY) {
		super.draw(mc, cursorX, cursorY);
		
		if ((visibility > 0) && (map != null)) {
			final int left = getX(false) + width / 2;
			final int top = getY(false) + height / 2;
			
			final int size = (width / map.length + (height - 16) / map.length) / 2;
			
			if (size > 0) {
				int i, j;
				
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				final int direction = (Math.round(mc.thePlayer.rotationYaw / 90) % 4);
				
				for (i = -16; i <= 16; i++) {
					for (j = -16; j <= 16; j++) {
						final int x, z;
						
						if (direction == 0) {
							x = -i;
							z = -j;
						} else 
						if (direction == 1) {
							x = j;
							z = -i;
						} else
						if (direction == 2) {
							x = i;
							z = j;
						} else {
							x = -j;
							z = i;
						}
						
						final int y = (int) (map[16 + x][16 + z] - medium);
						
						final float valueY;
						
						if ((y < 0) && (medium != min)) {
							valueY = (float) y / (medium - min);
						} else
						if ((y >= 0) && (medium != max)) {
							valueY = (float) y / (max - medium);
						} else {
							valueY = y;
						}
						
						final float blue = (1.0F + valueY) / 2;
						
						SAOGL.glColor(0, 0, blue, visibility);
						SAOGL.glRect(left + i * size, top + j * size, size, size);
					}
				}
				
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				
				SAOGL.glBindTexture(SAOResources.gui);
				
				if (mc.thePlayer != character) {
					SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.CANCEL_COLOR, visibility));
					SAOGL.glTexturedRect(left - size, top - size, size * 2, size * 2, 0, 25, 20, 20);
					
					final int offsetX = (int) ((character.posZ - mc.thePlayer.posZ) / zoom);
					final int offsetY = (int) ((character.posX - mc.thePlayer.posX) / zoom);
					
					final int x, y;
					
					if (direction == 0) {
						x = offsetY;
						y = offsetX;
					} else 
					if (direction == 1) {
						x = offsetX;
						y = -offsetY;
					} else
					if (direction == 2) {
						x = -offsetY;
						y = -offsetX;
					} else {
						x = -offsetX;
						y = offsetY;
					}
					
					if ((Math.abs(x) < MAP_SIZE) && (Math.abs(y) < MAP_SIZE)) {
						SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.HOVER_COLOR, visibility));
						SAOGL.glTexturedRect(left - size + x * size, top - size + y * size, size * 2, size * 2, 0, 25, 20, 20);
					}
				} else {
					SAOGL.glColorRGBA(SAOColor.multiplyAlpha(SAOColor.HOVER_COLOR, visibility));
					SAOGL.glTexturedRect(left - size, top - size, size * 2, size * 2, 0, 25, 20, 20);
				}
			}
		}
	}

}
