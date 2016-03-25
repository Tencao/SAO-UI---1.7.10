package com.tencao.saoui.screens.buttons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public interface StateHandler {

    boolean isStateEnabled(Minecraft mc, ButtonState button);

}
