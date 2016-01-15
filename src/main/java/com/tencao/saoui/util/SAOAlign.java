package com.tencao.saoui.util;

import com.tencao.saoui.ui.SAOElementGUI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SAOAlign {

    CENTER((SAOPositioner) (element, relative, width) -> element.getX(relative) + (element.width - width) / 2),

    LEFT((SAOPositioner) (element, relative, width) -> element.getX(relative)),

    RIGHT((SAOPositioner) (element, relative, width) -> element.getX(relative) + (element.width - width));

    private final SAOPositioner positioner;

    SAOAlign(SAOPositioner pos) {
        positioner = pos;
    }

    public int getX(SAOElementGUI element, boolean relative, int size) {
        return positioner.getX(element, relative, size);
    }

    private interface SAOPositioner {
        int getX(SAOElementGUI element, boolean relative, int width);
    }

}
