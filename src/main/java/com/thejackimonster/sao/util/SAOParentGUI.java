package com.thejackimonster.sao.util;

import com.thejackimonster.sao.ui.SAOElementGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface SAOParentGUI extends SAOActionHandler {

	public int getX(boolean relative);
	public int getY(boolean relative);

}
