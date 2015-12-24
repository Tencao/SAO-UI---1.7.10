package com.tencao.saoui.ui;

import net.minecraft.client.Minecraft;

import com.tencao.saoui.SAOMod;
import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.util.SAOIcon;
import com.tencao.saoui.util.SAOParentGUI;
import com.tencao.saoui.util.SAOStateHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOPartyGUI extends SAOStateButtonGUI {
	
	public SAOPartyGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, int w, int h, String string, SAOIcon saoIcon, boolean partyFlag) {
		super(gui, saoID, xPos, yPos, w, h, string, saoIcon, new SAOPartyStateHandler(partyFlag));
	}

	public SAOPartyGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, int w, String string, SAOIcon saoIcon, boolean partyFlag) {
		this(gui, saoID, xPos, yPos, w, 20, string, saoIcon, partyFlag);
	}

	public SAOPartyGUI(SAOParentGUI gui, SAOID saoID, int xPos, int yPos, String string, SAOIcon saoIcon, boolean partyFlag) {
		this(gui, saoID, xPos, yPos, 100, string, saoIcon, partyFlag);
	}

	private static final class SAOPartyStateHandler implements SAOStateHandler {

		private final boolean flag;

		private SAOPartyStateHandler(boolean partyFlag) {
			flag = partyFlag;
		}

		public boolean isStateEnabled(Minecraft mc, SAOStateButtonGUI button) {
			return (SAOMod.isPartyMember(mc.thePlayer.getDisplayName()) == flag);
		}

	}

}
