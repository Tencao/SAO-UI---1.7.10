package com.tencao.saoui.util;

import com.tencao.saoui.ui.SAOCharacterView;
import com.tencao.saoui.ui.SAOMenuGUI;
import com.tencao.saoui.ui.SAOQuestGUI;
import com.tencao.saoui.SAOMod;
import com.tencao.saoui.ui.*;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class SAOSub {

    private SAOSub() {
    }

    private static SAOMenuGUI resetSub(Minecraft mc, SAOMenuGUI sub) {
        sub.elements.clear();

        sub.elements.add(new SAOLabelGUI(sub, 0, 0, sub.width, SAOMod.getName(mc), SAOAlign.CENTER));
        sub.elements.add(new SAOVLineGUI(sub, 0, 0, 150));

        return sub;
    }

    private static SAOMenuGUI createSub(Minecraft mc, SAOElementGUI element, int x, int y) {
        return resetSub(mc, new SAOPanelGUI(element, x, y, 175, 240));
    }

    public static SAOMenuGUI createSocialSub(Minecraft mc, SAOElementGUI element, int x, int y) {
        final SAOMenuGUI sub = createSub(mc, element, x, y);
        final String[] party = SAOMod.listPartyMembers();

        return party != null ? setPartySub(mc, sub) : setFriendsSub(mc, sub);
    }

    public static SAOMenuGUI createNavigationSub(Minecraft mc, SAOElementGUI element, int x, int y) {
        return setQuestsSub(mc, createSub(mc, element, x, y), mc.thePlayer);
    }

    public static SAOMenuGUI createMainProfileSub(Minecraft mc, SAOElementGUI element, int x, int y) {
        return resetProfileSub(mc, new SAOPanelGUI(element, x, y, 175, 240), mc.thePlayer);
    }

    public static SAOMenuGUI resetFriendsSub(Minecraft mc, SAOMenuGUI sub) {
        return setFriendsSub(mc, resetSub(mc, sub));
    }

    public static SAOMenuGUI resetPartySub(Minecraft mc, SAOMenuGUI sub) {
        return setPartySub(mc, resetSub(mc, sub));
    }

    public static SAOMenuGUI resetProfileSub(Minecraft mc, SAOMenuGUI sub, EntityPlayer player) {
        sub.elements.clear();

        sub.elements.add(new SAOLabelGUI(sub, 0, 0, sub.width, SAOMod.getName(player), SAOAlign.CENTER));
        sub.elements.add(new SAOVLineGUI(sub, 0, 0, 150));

        return setProfileSub(mc, sub, player);
    }

    public static SAOMenuGUI resetCheckPositionSub(Minecraft mc, SAOMenuGUI sub, EntityPlayer player, int zoom, String title) {
        sub.elements.clear();

        sub.elements.add(new SAOLabelGUI(sub, 0, 0, sub.width, SAOMod.getName(player), SAOAlign.CENTER));
        sub.elements.add(new SAOVLineGUI(sub, 0, 0, 150));

        return setCheckPositionSub(mc, sub, player, zoom, title);
    }

    public static SAOMenuGUI resetQuestsSub(Minecraft mc, SAOMenuGUI sub, EntityPlayer player) {
        sub.elements.clear();

        sub.elements.add(new SAOLabelGUI(sub, 0, 0, sub.width, SAOMod.getName(player), SAOAlign.CENTER));
        sub.elements.add(new SAOVLineGUI(sub, 0, 0, 150));

        return setQuestsSub(mc, sub, player);
    }

    private static SAOMenuGUI setEmptySub(Minecraft mc, SAOMenuGUI sub) {
        sub.elements.add(new SAOTextGUI(sub, 0, 0, new String[4]));

        final SAOIconGUI icon = new SAOIconGUI(sub, SAOID.NONE, sub.width / 2 - 10, 0, SAOIcon.NONE);
        icon.bgColor = SAOColor.DEFAULT_FONT_COLOR;
        icon.disabledMask = SAOColor.DEFAULT_COLOR;
        icon.enabled = false;

        sub.elements.add(icon);
        sub.elements.add(new SAOTextGUI(sub, 0, 0, new String[4]));

        return sub;
    }

    private static SAOMenuGUI setFriendsSub(Minecraft mc, SAOMenuGUI sub) {
        final String[] friends = SAOMod.listFriends();
        final boolean[] online = SAOMod.isOnline(mc, friends);

        int onlineCount = 0;

        for (final boolean value : online) if (value) onlineCount++;

        if (onlineCount > 0) {
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < friends.length; i++)
                if (online[i]) builder.append(" - ").append(friends[i]).append('\n');

            sub.elements.add(new SAOLabelGUI(sub, 0, 0, '-' + StatCollector.translateToLocal("guiFriends") + '-', SAOAlign.CENTER));
            sub.elements.add(new SAOTextGUI(sub, 0, 0, builder.toString()));
        } else setEmptySub(mc, sub);

        return sub;
    }

    private static SAOMenuGUI setPartySub(Minecraft mc, SAOMenuGUI sub) {
        final String[] party = SAOMod.listPartyMembers();

        if (party != null) {
            final boolean[] online = SAOMod.isOnline(mc, party);
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < party.length; i++) if (online[i]) builder.append(" - ").append(party[i]).append('\n');

            sub.elements.add(new SAOLabelGUI(sub, 0, 0, '-' + StatCollector.translateToLocal("guiParty") + '-', SAOAlign.CENTER));
            sub.elements.add(new SAOTextGUI(sub, 0, 0, builder.toString()));
        } else setEmptySub(mc, sub);

        return sub;
    }

    public static SAOMenuGUI setProfileSub(Minecraft mc, SAOMenuGUI sub, EntityPlayer player) {
        if (player != null) sub.elements.add(new SAOCharacterView(sub, 0, 0, sub.width, 150, player));
        else setEmptySub(mc, sub);
		
		return sub;
	}

    private static SAOMenuGUI setCheckPositionSub(Minecraft mc, SAOMenuGUI sub, EntityPlayer player, int zoom, String title) {
        if (player != null) {
            final SAOMapGUI map = new SAOMapGUI(sub, 0, 0, 4, player);
            map.zoom = zoom;

            if (title != null) sub.elements.add(new SAOLabelGUI(sub, 0, 0, sub.width, title, SAOAlign.CENTER));

            sub.elements.add(map);
        } else setEmptySub(mc, sub);

        return sub;
    }

    private static SAOMenuGUI setQuestsSub(Minecraft mc, SAOMenuGUI sub, EntityPlayer player) {
        sub.elements.add(new SAOLabelGUI(sub, 0, 0, sub.width, '-' + StatCollector.translateToLocal("guiQuestList") + '-', SAOAlign.CENTER));

        final SAOMenuGUI questList = new SAOMenuGUI(sub, 0, 0, sub.width, 150);
        questList.innerMenu = true;

        final StatFileWriter stats = mc.thePlayer.getStatFileWriter();

        if (stats != null) {
            @SuppressWarnings("unchecked") final List<Object> ach = AchievementList.achievementList;
                ach.stream()
                        .filter(obj0 -> obj0 instanceof Achievement).map(obj0 -> (Achievement) obj0)
                        .filter(ach0 -> ach0.isAchievement() && !stats.hasAchievementUnlocked(ach0) && stats.canUnlockAchievement(ach0))
                        .forEach(ach0 -> questList.elements.add(new SAOQuestGUI(questList, 0, 0, questList.width, ach0)));
        }

        sub.elements.add(questList);
        return sub;
    }

    public static SAOPanelGUI addInfo(SAOMenuGUI sub) {
        final SAOPanelGUI info = new SAOPanelGUI(sub, 0, 0, sub.width, 0);
        info.bgColor = SAOColor.DEFAULT_BOX_COLOR;
        info.innerMenu = true;

        sub.elements.add(info);
        return info;
    }

    public static SAOString[] addProfileContent(Minecraft mc) {
        return addProfileContent(mc.thePlayer);
    }

    public static SAOString[] addProfileContent(EntityPlayer player) {
        return new SAOString[]{
                new SAOJString(StatCollector.translateToLocal("guiProfile")), new SAOPlayerString(player)
        };
    }

    public static SAOString[] addPositionContent(EntityPlayer player, EntityPlayer search) {
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

        return new SAOString[]{
                new SAOJString(floor.toString()), new SAOJString(builder.toString())
        };
    }

}