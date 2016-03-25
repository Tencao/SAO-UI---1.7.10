package com.tencao.saoui.screens.buttons;

import com.tencao.saoui.screens.Elements;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ActionHandler {

    void actionPerformed(Elements element, Actions action, int data);

}
