package com.tencao.saoui.util;

import com.tencao.saoui.ui.SAOElementGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface SAOActionHandler {

	void actionPerformed(SAOElementGUI element, SAOAction action, int data);

}
