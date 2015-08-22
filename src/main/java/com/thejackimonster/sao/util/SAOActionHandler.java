package com.thejackimonster.sao.util;

import com.thejackimonster.sao.ui.SAOElementGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface SAOActionHandler {

	public void actionPerformed(SAOElementGUI element, SAOAction action, int data);

}
