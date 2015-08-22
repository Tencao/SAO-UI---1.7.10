package com.tencao.sao.util;

import com.tencao.sao.ui.SAOStateButtonGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public interface SAOStateHandler {

	public boolean isStateEnabled(Minecraft mc, SAOStateButtonGUI button);

}
