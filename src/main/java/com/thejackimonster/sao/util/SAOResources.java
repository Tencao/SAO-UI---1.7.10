package com.thejackimonster.sao.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public final class SAOResources {

	public static final ResourceLocation gui = new ResourceLocation("sao", "textures/gui.png");
	public static final ResourceLocation icons = new ResourceLocation("sao", "textures/icons.png");
	public static final ResourceLocation effects = new ResourceLocation("sao", "textures/effects.png");
	public static final ResourceLocation entities = new ResourceLocation("sao", "textures/entities.png");

	private SAOResources() {}

	public static final String FRIEND_REQUEST_TITLE = "Friend Request";
	public static final String FRIEND_REQUEST_TEXT = "%s wants to add you as friend.";

	public static final String PARTY_INVITATION_TITLE = "Invite";
	public static final String PARTY_INVITATION_TEXT = "%s invites you to join a party.";

	public static final String PARTY_DISSOLVING_TITLE = "Dissolve";
	public static final String PARTY_DISSOLVING_TEXT = "You will disband your party?";

	public static final String PARTY_LEAVING_TITLE = "Leave";
	public static final String PARTY_LEAVING_TEXT = "You will leave your party?";

	public static final String MESSAGE_TITLE = "Message";
	public static final String MESSAGE_FROM = "from %s";

}
