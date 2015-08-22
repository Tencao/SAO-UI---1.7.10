package com.thejackimonster.sao.util;

public enum SAOOption {

	DEFAULT_UI("Default UI", false),
	DEFAULT_INVENTORY("Default Inventory", false),
	CROSS_HAIR("Cross Hair", false),
	SMOOTH_HEALTH("Smooth Health", true),
	CLIENT_CHAT_PACKETS("Client Chat Packets", true),
	HEALTH_BARS("Health Bars", true),
	PARTICLES("Particles", true),
	LOGOUT("Can Logout?", false);

	public final String name;
	public boolean value;

	private SAOOption(String optionName, boolean defaultValue) {
		name = optionName;
		value = defaultValue;
	}

	public final String toString() {
		return name;
	}

}
