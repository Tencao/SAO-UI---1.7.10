package com.saomc.events;

import com.saomc.screens.menu.Categories;
import com.saomc.screens.window.WindowView;
import com.saomc.social.StaticPlayerHelper;
import com.saomc.social.friends.FriendRequest;
import com.saomc.commands.Command;
import com.saomc.commands.CommandType;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FriendsHandler {


    private static FriendsHandler instance;
    private final File friendsFile;
    private final List<FriendRequest> friendRequests = new ArrayList<>();
    private String[] friends;

    private FriendsHandler(FMLPreInitializationEvent event) {
        this.friendsFile = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/saouifriends");
        if (!friendsFile.exists()) writeFriends(friends);
        friends = loadFriends();
    }

    public static FriendsHandler instance() {
        return instance;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        instance = new FriendsHandler(event);
    }

    public String[] loadFriends() {
        try {
            final FileInputStream stream = new FileInputStream(friendsFile);
            final String[] friends;

            if (stream.available() != 0) {
                final int count = (stream.read() & 0xFF);

                friends = new String[count];

                for (int i = 0; i < count; i++) {
                    final int length = (stream.read() & 0xFF);
                    final byte[] bytes = new byte[length];

                    stream.read(bytes, 0, length);

                    friends[i] = new String(bytes);
                }
            } else friends = new String[0];

            stream.close();

            return friends;
        } catch (IOException e) {
            return new String[0];
        }
    }

    public String[] listFriends() {
        if (friends == null) friends = loadFriends();

        return friends;
    }

    public void addFriendRequests(Minecraft mc, String... names) {
        synchronized (friendRequests) {
            for (final String name : names)
                if (!friendRequests.contains(new FriendRequest(name, 10000)) && !isFriend(name)) {
                    friendRequests.add(new FriendRequest(name, 10000));
                    new Command(CommandType.ADD_FRIEND_REQUEST, name).send(mc);
                }
        }
    }

    public boolean addFriends(String... names) {
        friends = listFriends();
        final ArrayList<String> newNames = new ArrayList<>();

        Stream.of(names).forEach(name -> {
            if (Stream.of(friends).noneMatch(friend -> friend.equals(name))) newNames.add(name);
        });

        String[] bb = new String[newNames.size()];
        System.arraycopy(newNames.toArray(), 0, bb, 0, bb.length);
        return newNames.size() <= 0 || addRawFriends(bb);
    }

    public boolean isFriend(String name) {
        return Stream.of(listFriends()).anyMatch(friend -> friend.equals(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return isFriend(StaticPlayerHelper.getName(player));
    }

    public boolean addRawFriends(String[] names) {
        friends = listFriends();

        final String[] resized = new String[friends.length + names.length];

        System.arraycopy(friends, 0, resized, 0, friends.length);
        System.arraycopy(names, 0, resized, friends.length, names.length);

        if (writeFriends(resized)) {
            friends = resized;
            return true;
        } else return false;
    }

    public boolean writeFriends(String[] friends) {
        final String[] data = friends == null ? new String[0] : friends;

        synchronized (friendsFile) {
            try (FileOutputStream stream = new FileOutputStream(friendsFile)) {
                final int count = (data.length % 0x100);
                stream.write(count);

                for (int i = 0; i < count; i++) {
                    final byte[] bytes = data[i].getBytes();
                    final int length = (bytes.length % 0x100);

                    stream.write(length);
                    stream.write(bytes, 0, length);
                }

                stream.flush();
                stream.close();

                return true;
            } catch (IOException e) {
                if (ConfigHandler.DEBUG) e.printStackTrace();

                return false;
            }
        }
    }

    public void acceptAddFriend(String username) {
        synchronized (friendRequests) {
            int index = -1;

            for (int i = 0; i < friendRequests.size(); i++)
                if (friendRequests.get(i).equals(username)) {
                    index = i;
                    break;
                }

            if (index >= 0 && (isFriend(username) || addFriends(username))) friendRequests.remove(index);
        }
    }

    public void cancelAddFriend(String username) {
        synchronized (friendRequests) {
            int index = -1;

            for (int i = 0; i < friendRequests.size(); i++)
                if (friendRequests.get(i).equals(username)) {
                    index = i;
                    break;
                }

            if (index >= 0) friendRequests.remove(index);
        }
    }

    public void addFriendRequest(Minecraft mc, String username) {
        if (!FriendsHandler.instance().isFriend(username)) {
            final GuiScreen keepScreen = mc.currentScreen;
            final boolean ingameFocus = mc.inGameHasFocus;

            final String text = StatCollector.translateToLocalFormatted(ConfigHandler._FRIEND_REQUEST_TEXT, username);

            mc.displayGuiScreen(WindowView.viewConfirm(ConfigHandler._FRIEND_REQUEST_TITLE, text, (element, action, data) -> {
                final Categories id = element.ID();

                if (id == Categories.CONFIRM && (FriendsHandler.instance().isFriend(username) || FriendsHandler.instance().addFriends(username)))
                    new Command(CommandType.ACCEPT_ADD_FRIEND, username).send(mc);
                else new Command(CommandType.CANCEL_ADD_FRIEND, username).send(mc);

                mc.displayGuiScreen(keepScreen);

                if (ingameFocus) mc.setIngameFocus();
                else mc.setIngameNotInFocus();
            }));

            if (ingameFocus) mc.setIngameNotInFocus();
        } else new Command(CommandType.ACCEPT_ADD_FRIEND, username).send(mc);
    }
}
