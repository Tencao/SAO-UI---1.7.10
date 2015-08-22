package com.tencao.sao.ui;

import net.minecraft.client.Minecraft;

import com.tencao.sao.util.SAOID;
import com.tencao.sao.util.SAOIcon;
import com.tencao.sao.util.SAOParentGUI;
import com.tencao.sao.util.SAOStateHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOStateButtonGUI extends SAOButtonGUI {

	private final SAOStateHandler state;

	public SAOStateButtonGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, int w, int h, String string, SAOIcon saoIcon, SAOStateHandler handler) {
		super(gui, saoID, xPos, yPos, w, h, string, saoIcon);
		state = handler;
	}

	public SAOStateButtonGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, int w, String string, SAOIcon saoIcon, SAOStateHandler handler) {
		this(gui, saoID, xPos, yPos, w, 20, string, saoIcon, handler);
	}

	public SAOStateButtonGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, String string, SAOIcon saoIcon, SAOStateHandler handler) {
		this(gui, saoID, xPos, yPos, 100, string, saoIcon, handler);
	}
	
    @Override
	public void update(Minecraft mc) {
		if (state != null) {
			enabled = state.isStateEnabled(mc, this);
		}
		
		super.update(mc);
	}

}
