package com.tencao.saoui.screens.window;

import com.tencao.saoui.events.FriendsHandler;
import com.tencao.saoui.screens.*;
import com.tencao.saoui.screens.menu.Categories;
import com.tencao.saoui.screens.menu.Panel;
import com.tencao.saoui.screens.window.ui.CharacterView;
import com.tencao.saoui.screens.window.ui.MapView;
import com.tencao.saoui.screens.window.ui.AchievementList;
import com.tencao.saoui.social.StaticPlayerHelper;
import com.tencao.saoui.social.party.PartyHelper;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.StatCollector;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class SubWindow {

    private SubWindow() {
    }

    private static MenuGUI resetSub(Minecraft mc, MenuGUI sub) {
        sub.elements.clear();

        sub.elements.add(new LabelGUI(sub, 0, 0, sub.width, StaticPlayerHelper.getName(mc), WindowAlign.CENTER));
        sub.elements.add(new VLine(sub, 0, 0, 150));

        return sub;
    }

    private static MenuGUI createSub(Minecraft mc, Elements element, int x, int y) {
        return resetSub(mc, new Panel(element, x, y, 175, 240));
    }

    public static MenuGUI createSocialSub(Minecraft mc, Elements element, int x, int y) {
        final MenuGUI sub = createSub(mc, element, x, y);
        final String[] party = PartyHelper.instance().listMembers();

        return party != null ? setPartySub(mc, sub) : setFriendsSub(mc, sub);
    }

    public static MenuGUI createNavigationSub(Minecraft mc, Elements element, int x, int y) {
        return setQuestsSub(mc, createSub(mc, element, x, y), mc.thePlayer);
    }

    public static MenuGUI createMainProfileSub(Minecraft mc, Elements element, int x, int y) {
        return resetProfileSub(mc, new Panel(element, x, y, 175, 240), mc.thePlayer);
    }

    public static MenuGUI resetFriendsSub(Minecraft mc, MenuGUI sub) {
        return setFriendsSub(mc, resetSub(mc, sub));
    }

    public static MenuGUI resetPartySub(Minecraft mc, MenuGUI sub) {
        return setPartySub(mc, resetSub(mc, sub));
    }

    public static MenuGUI resetProfileSub(Minecraft mc, MenuGUI sub, EntityPlayer player) {
        sub.elements.clear();

        sub.elements.add(new LabelGUI(sub, 0, 0, sub.width, StaticPlayerHelper.getName(player), WindowAlign.CENTER));
        sub.elements.add(new VLine(sub, 0, 0, 150));

        return setProfileSub(mc, sub, player);
    }

    public static MenuGUI resetCheckPositionSub(Minecraft mc, MenuGUI sub, EntityPlayer player, int zoom, String title) {
        sub.elements.clear();

        sub.elements.add(new LabelGUI(sub, 0, 0, sub.width, StaticPlayerHelper.getName(player), WindowAlign.CENTER));
        sub.elements.add(new VLine(sub, 0, 0, 150));

        return setCheckPositionSub(mc, sub, player, zoom, title);
    }

    public static MenuGUI resetQuestsSub(Minecraft mc, MenuGUI sub, EntityPlayer player) {
        sub.elements.clear();

        sub.elements.add(new LabelGUI(sub, 0, 0, sub.width, StaticPlayerHelper.getName(player), WindowAlign.CENTER));
        sub.elements.add(new VLine(sub, 0, 0, 150));

        return setQuestsSub(mc, sub, player);
    }

    private static MenuGUI setEmptySub(Minecraft mc, MenuGUI sub) {
        sub.elements.add(new TextGUI(sub, 0, 0, new String[4]));

        final IconGUI icon = new IconGUI(sub, Categories.NONE, sub.width / 2 - 10, 0, IconCore.NONE);
        icon.bgColor = ColorUtil.DEFAULT_FONT_COLOR;
        icon.disabledMask = ColorUtil.DEFAULT_COLOR;
        icon.enabled = false;

        sub.elements.add(icon);
        sub.elements.add(new TextGUI(sub, 0, 0, new String[4]));

        return sub;
    }

    private static MenuGUI setFriendsSub(Minecraft mc, MenuGUI sub) {
        final String[] friends = FriendsHandler.instance().listFriends();
        final boolean[] online = StaticPlayerHelper.isOnline(mc, friends);

        int onlineCount = 0;

        for (final boolean value : online) if (value) onlineCount++;

        if (onlineCount > 0) {
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < friends.length; i++)
                if (online[i]) builder.append(" - ").append(friends[i]).append('\n');

            sub.elements.add(new LabelGUI(sub, 0, 0, '-' + StatCollector.translateToLocal("guiFriends") + '-', WindowAlign.CENTER));
            sub.elements.add(new TextGUI(sub, 0, 0, builder.toString()));
        } else setEmptySub(mc, sub);

        return sub;
    }

    private static MenuGUI setPartySub(Minecraft mc, MenuGUI sub) {
        final String[] party = PartyHelper.instance().listMembers();

        if (party != null) {
            final boolean[] online = StaticPlayerHelper.isOnline(mc, party);
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < party.length; i++) if (online[i]) builder.append(" - ").append(party[i]).append('\n');

            sub.elements.add(new LabelGUI(sub, 0, 0, '-' + StatCollector.translateToLocal("guiParty") + '-', WindowAlign.CENTER));
            sub.elements.add(new TextGUI(sub, 0, 0, builder.toString()));
        } else setEmptySub(mc, sub);

        return sub;
    }

    public static MenuGUI setProfileSub(Minecraft mc, MenuGUI sub, EntityPlayer player) {
        if (player != null) sub.elements.add(new CharacterView(sub, 0, 0, sub.width, 150, player));
        else setEmptySub(mc, sub);

        return sub;
    }

    private static MenuGUI setCheckPositionSub(Minecraft mc, MenuGUI sub, EntityPlayer player, int zoom, String title) {
        if (player != null) {
            final MapView map = new MapView(sub, 0, 0, 4, player);
            map.zoom = zoom;

            if (title != null) sub.elements.add(new LabelGUI(sub, 0, 0, sub.width, title, WindowAlign.CENTER));

            sub.elements.add(map);
        } else setEmptySub(mc, sub);

        return sub;
    }

    private static MenuGUI setQuestsSub(Minecraft mc, MenuGUI sub, EntityPlayer player) {
        sub.elements.add(new LabelGUI(sub, 0, 0, sub.width, '-' + StatCollector.translateToLocal("guiQuestList") + '-', WindowAlign.CENTER));

        final MenuGUI questList = new MenuGUI(sub, 0, 0, sub.width, 150);
        questList.innerMenu = true;

        final StatFileWriter stats = mc.thePlayer.getStatFileWriter();

        if (stats != null) {
            @SuppressWarnings("unchecked") final List<Object> ach = net.minecraft.stats.AchievementList.achievementList;
            ach.stream()
                    .filter(obj0 -> obj0 instanceof Achievement).map(obj0 -> (Achievement) obj0)
                    .filter(ach0 -> ach0.isAchievement() && !stats.hasAchievementUnlocked(ach0) && stats.canUnlockAchievement(ach0))
                    .forEach(ach0 -> questList.elements.add(new AchievementList(questList, 0, 0, questList.width, ach0)));
        }

        sub.elements.add(questList);
        return sub;
    }

    public static Panel addInfo(MenuGUI sub) {
        final Panel info = new Panel(sub, 0, 0, sub.width, 0);
        info.bgColor = ColorUtil.DEFAULT_BOX_COLOR;
        info.innerMenu = true;

        sub.elements.add(info);
        return info;
    }

    public static Strings[] addProfileContent(Minecraft mc) {
        return addProfileContent(mc.thePlayer);
    }

    public static Strings[] addProfileContent(EntityPlayer player) {
        return new Strings[]{
                new JString(StatCollector.translateToLocal("guiProfile")), new PlayerString(player)
        };
    }

    public static Strings[] addPositionContent(EntityPlayer player, EntityPlayer search) {
        final StringBuilder floor = new StringBuilder(StatCollector.translateToLocal("guiFloor") + ' ');
        final StringBuilder builder = new StringBuilder();

        if (player != null) {
            floor.append(1 - player.dimension);

            builder.append("X: ").append((int) player.posX).append(", ");
            builder.append("Y: ").append((int) player.posY).append(", ");
            builder.append("Z: ").append((int) player.posZ).append('\n');

            if (player != search) {
                builder.append(StatCollector.translateToLocal("guiDistance")).append(' ');
                builder.append((double) ((int) (Math.sqrt(player.getDistanceSqToEntity(search)) * 1000)) / 1000);
                builder.append('\n');
            }
        } else floor.append("0");

        return new Strings[]{
                new JString(floor.toString()), new JString(builder.toString())
        };
    }

}