package com.tencao.saoui.social.friends;

public final class FriendRequest {

    public final String friendName;
    public int ticks;

    public FriendRequest(String name, int maxTicks) {
        friendName = name;
        ticks = maxTicks;
    }

    public final boolean equals(FriendRequest request) {
        return equals(request == null ? null : request.friendName);
    }

    public final boolean equals(String name) {
        return friendName.equals(name);
    }

    public final boolean equals(Object object) {
        return object instanceof FriendRequest ? equals((FriendRequest) object) : equals(String.valueOf(object));
    }

}
