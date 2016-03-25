package com.tencao.saoui.social.friends;

import com.tencao.saoui.screens.ListGUI;
import com.tencao.saoui.screens.ParentElement;
import com.tencao.saoui.social.StaticPlayerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class FriendList extends ListGUI {

    public FriendList(Minecraft mc, ParentElement gui, int xPos, int yPos, int w, int h) {
        super(gui, xPos, yPos, w, h);
        init(mc);
    }

    private void init(Minecraft mc) {
        final List<EntityPlayer> list = StaticPlayerHelper.listOnlinePlayers(mc);

        if (list.contains(mc.thePlayer)) {
            list.remove(mc.thePlayer);
        }

        elements.addAll(list.stream().map(player -> new FriendCore(this, 0, 0, StaticPlayerHelper.getName(player))).collect(Collectors.toList()));
    }

}
