package com.tencao.saoui.ui;

import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class SAOCharacterView extends SAOElementGUI {

    public static boolean IS_VIEWING = false;
    private final EntityPlayer character;
    private int clickIndex;

    public SAOCharacterView(SAOParentGUI gui, int xPos, int yPos, int w, int h, EntityPlayer player) {
        super(gui, xPos, yPos, w, h);
        character = player;
    }

    private void drawCharacter(int x, int y, int size, int cursorX, int cursorY) {
        final float mouseX = (float) x - cursorX;
        final float mouseY = (float) y - size * 1.67F - cursorY;

        IS_VIEWING = true;
        GuiInventory.func_147046_a(x, y, size, mouseX, mouseY, character);
        IS_VIEWING = false;

        SAOGL.glRescaleNormal(true);
        SAOGL.glTexture2D(true);
        SAOGL.glBlend(true);

        SAOGL.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        clickIndex = -1;

        if (visibility > 0) {
            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);
            SAOGL.glColorRGBA(SAOColor.DEFAULT_COLOR.multiplyAlpha(visibility));

            int left = getX(false) + width / 2;
            int top = getY(false) + height * 13 / 16;

            final int size = width * height / 550;

            final int shadowY = size / 2 + Math.max(Math.min((cursorY - top) / 12, 0), -size / 2 + 2);

            SAOGL.glTexturedRect(left - size / 2, (top - shadowY / 2), size, shadowY, 200, 85, 56, 30);

            drawCharacter(left, top, size, cursorX, cursorY);

            left = getX(false) + width / 2;
            top = getY(false) + height / 2;

            final int width2 = (width / 2) - 14;
            final int height2 = (height / 2) - 14;

            for (int angle = 0; angle < 12; angle++) {
                final int x = (int) (left + Math.sin(Math.toRadians(angle * 30)) * width2);
                final int y = (int) (top + Math.cos(Math.toRadians(angle * 30)) * height2);

                final boolean hovered = ((cursorX >= x - 10) && (cursorY >= y - 10) && (cursorX <= x + 10) && (cursorY <= y + 10));

                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);

                SAOGL.glColorRGBA((hovered ? SAOColor.HOVER_COLOR : SAOColor.DEFAULT_FONT_COLOR).multiplyAlpha(visibility));
                SAOGL.glTexturedRect(x - 10, y - 10, 0, 25, 20, 20);

                if ((angle + 4 < 9) || (angle + 4 >= 12)) {
                    final int index = (angle + 4 >= 12 ? (angle - 8) % 9 : (angle + 4) % 9);
                    final Slot slot = character.inventoryContainer.getSlotFromInventory(character.inventory, index);

                    if ((slot.getHasStack()) && (slot.getStack().getItem() != null)) {
                        SAOGL.glColorRGBA((hovered ? SAOColor.HOVER_FONT_COLOR : SAOColor.DEFAULT_COLOR).multiplyAlpha(visibility));
                        SAOSlotGUI.getIcon(slot.getStack()).glDraw(x - 8, y - 8);
                    }

                    if (hovered) clickIndex = index;
                }
            }
        }
    }

    @Override
    public boolean keyTyped(Minecraft mc, char ch, int key) {
        if (Objects.equals(character, mc.thePlayer)) for (int i = 0; i < 9; i++)
            if (key == mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
                character.inventory.currentItem = i;
                return true;
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

}