package com.tencao.sao.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface SAOParentGUI extends SAOActionHandler {

	int getX(boolean relative);
	int getY(boolean relative);

}
