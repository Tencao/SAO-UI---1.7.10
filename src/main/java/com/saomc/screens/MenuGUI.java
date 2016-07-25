package com.saomc.screens;

import com.saomc.GLCore;
import com.saomc.resources.StringNames;
import com.saomc.util.ColorUtil;
import com.saomc.util.OptionCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class MenuGUI extends ContainerGUI {

    public boolean innerMenu;
    public boolean fullArrow;

    public MenuGUI(ParentElement gui, int xPos, int yPos, int x, int y) {
        super(gui, xPos, yPos, x, y);
        fullArrow = true;
        innerMenu = false;
    }

    protected int getOffset(int index) {
        return elements.stream().limit(index).mapToInt(this::getOffsetSize).sum();
    }

    int getReverseOffset(int index) {
        return elements.stream().skip(index).mapToInt(this::getOffsetSize).sum();
    }

    protected int getOffsetSize(Elements element) {
        return element.height;
    }

    public void update(Minecraft mc) {
        height = getSize();

        if (width <= 0) width = elements.stream().mapToInt(el -> el.width).max().orElse(width);

        super.update(mc);
    }

    protected int getSize() {
        return getOffset(elements.size());
    }

    protected void update(Minecraft mc, int index, Elements element) {
        element.y = getOffset(index);
        element.width = width - element.x;

        super.update(mc, index, element);
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if (visibility > 0 && parent != null && height > 0) {
            if (x > 0) {
                GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);
                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(visibility));

                final int left = getX(false);
                final int top = getY(false) + 1;

                final int arrowTop = super.getY(false) - height / 2;

                GLCore.glTexturedRect(left - 2, top, 2, height - 1, 40, 41, 2, 4);
                GLCore.glTexturedRect(left - 10, arrowTop + (height - 10) / 2, 20, 25 + (fullArrow ? 10 : 0), 10, 10);
            } else if (x < 0) {
                GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);
                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(visibility));

                final int left = getX(false);
                final int top = getY(false) + 1;

                final int arrowTop = super.getY(false) - height / 2;

                GLCore.glTexturedRect(left + width, top, 2, height - 1, 40, 41, 2, 4);
                GLCore.glTexturedRect(left + width, arrowTop + (height - 10) / 2, 30, 25 + (fullArrow ? 10 : 0), 10, 10);
            }
        }

        super.draw(mc, cursorX, cursorY);
    }

    public int getY(boolean relative) {
        return super.getY(relative) - (relative || innerMenu ? 0 : height / 2);
    }

}
