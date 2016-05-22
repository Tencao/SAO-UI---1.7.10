package com.saomc.screens.buttons;

import com.saomc.screens.Elements;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ActionHandler {

    void actionPerformed(Elements element, Actions action, int data);

}
