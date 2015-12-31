package com.tencao.saoui.util;

import com.tencao.saoui.ui.SAOStateButtonGUI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public interface SAOStateHandler {

	boolean isStateEnabled(Minecraft mc, SAOStateButtonGUI button);

}
