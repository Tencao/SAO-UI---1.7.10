package com.thejackimonster.sao.ui;

import net.minecraft.client.Minecraft;

import com.thejackimonster.sao.SAOMod;
import com.thejackimonster.sao.util.SAOID;
import com.thejackimonster.sao.util.SAOIcon;
import com.thejackimonster.sao.util.SAOParentGUI;
import com.thejackimonster.sao.util.SAOStateHandler;

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
