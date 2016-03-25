package com.tencao.saoui.social.friends;

import com.tencao.saoui.events.FriendsHandler;
import com.tencao.saoui.screens.ParentElement;
import com.tencao.saoui.screens.buttons.ButtonGUI;
import com.tencao.saoui.screens.menu.Categories;
import com.tencao.saoui.social.StaticPlayerHelper;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

@SideOnly(Side.CLIENT)
public class FriendCore extends ButtonGUI {

    private EntityPlayer friend;

    public FriendCore(ParentElement gui, int xPos, int yPos, int w, int h, String name) {
        super(gui, Categories.FRIEND, xPos, yPos, w, h, name, IconCore.NONE);
        enabled = false;
    }

    public FriendCore(ParentElement gui, int xPos, int yPos, int w, String name) {
        this(gui, xPos, yPos, w, 20, name);
    }

    public FriendCore(ParentElement gui, int xPos, int yPos, String name) {
        this(gui, xPos, yPos, 100, name);
    }

    public void update(Minecraft mc) {
        final EntityPlayer player = getPlayer(mc);
        enabled = (player != null);

        if (enabled && FriendsHandler.instance().isFriend(player)) {
            highlight = true;
            icon = IconCore.NONE;
        } else {
            highlight = false;
            icon = IconCore.INVITE;
        }

        super.update(mc);
    }

    public final EntityPlayer getPlayer(Minecraft mc) {
        if (friend == null || friend.isDead || !friend.isEntityAlive()) friend = findPlayer(mc);

        return friend;
    }

    public final EntityPlayer findPlayer(Minecraft mc) {
        final List<EntityPlayer> players = StaticPlayerHelper.listOnlinePlayers(mc);

        for (final EntityPlayer player : players) {
            if (StaticPlayerHelper.getName(player).equals(caption)) {
                return player;
            }
        }

        return null;
    }

}
