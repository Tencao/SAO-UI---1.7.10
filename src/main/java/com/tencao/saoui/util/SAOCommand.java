package com.tencao.saoui.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SAOCommand {

	INVITE_PARTY,
	DISSOLVE_PARTY,
	UPDATE_PARTY,

	CONFIRM_INVITE_PARTY,
	CANCEL_INVITE_PARTY,

	ADD_FRIEND_REQUEST,

	ACCEPT_ADD_FRIEND,
	CANCEL_ADD_FRIEND;

	private static final String PREFIX = String.valueOf("SAO:");
	private static final String SUFFIX = String.valueOf(";");

	public final String[] getContent(String data) {
		final int index = toString().length() + 1;
		
		if (index >= data.length()) {
			return new String[0];
		} else {
			return data.substring(index).split(" ");
		}
	}

	public final String toString() {
		return (PREFIX + name() + SUFFIX).toLowerCase();
	}

	public static final SAOCommand getCommand(String data) {
		if (data.startsWith(PREFIX.toLowerCase())) {
			final int nextData = data.indexOf(SUFFIX, PREFIX.length());
			final String id = data.substring(PREFIX.length(), nextData).toUpperCase();
			
			try {
				return valueOf(id);
			} catch (NumberFormatException e) {
				return null;
			}
		} else {
			return null;
		}
	}

}
