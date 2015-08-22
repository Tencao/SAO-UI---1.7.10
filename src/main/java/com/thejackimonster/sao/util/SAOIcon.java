package com.thejackimonster.sao.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SAOIcon {

	NONE,
	OPTION,
	HELP,
	LOGOUT,
	CANCEL,
	CONFIRM,
	SETTINGS,
	NAVIGATION,
	MESSAGE,
	SOCIAL,
	PROFILE,
	EQUIPMENT,
	ITEMS,
	SKILLS,
	GUILD,
	PARTY,
	FRIEND,
	CREATE,
	INVITE,
	QUEST,
	FIELD_MAP,
	DUNGEON_MAP,
	ARMOR,
	ACCESSORY,
	MESSAGE_RECEIVED,
	CRAFTING,
	SPRINTING,
	SNEAKING;

	public static final int SRC_SIZE = 16;

	public final int getSrcX() {
		return (index() % 16) * SRC_SIZE;
	}

	public final int getSrcY() {
		return (index() / 16) * SRC_SIZE;
	}

	private final int index() {
		return (ordinal() - 1);
	}

	public final void glDraw(int x, int y, float z) {
		if (index() >= 0) {
			SAOGL.glBindTexture(SAOResources.icons);
			SAOGL.glTexturedRect(x, y, z, getSrcX(), getSrcY(), SRC_SIZE, SRC_SIZE);
		}
	}

	public final void glDraw(int x, int y) {
		if (index() >= 0) {
			SAOGL.glBindTexture(SAOResources.icons);
			SAOGL.glTexturedRect(x, y, getSrcX(), getSrcY(), SRC_SIZE, SRC_SIZE);
		}
	}

}
