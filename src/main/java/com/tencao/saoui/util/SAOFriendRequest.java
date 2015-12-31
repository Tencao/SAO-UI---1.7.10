package com.tencao.saoui.util;

public final class SAOFriendRequest {

	public final String friendName;
	public int ticks;

	public SAOFriendRequest(String name, int maxTicks) {
		friendName = name;
		ticks = maxTicks;
	}

	public final boolean equals(SAOFriendRequest request) {
		return equals(request == null? null : request.friendName);
	}

	public final boolean equals(String name) {
		return friendName.equals(name);
	}

	public final boolean equals(Object object) {
		return object instanceof SAOFriendRequest ? equals((SAOFriendRequest) object) : equals(String.valueOf(object));
	}

}
