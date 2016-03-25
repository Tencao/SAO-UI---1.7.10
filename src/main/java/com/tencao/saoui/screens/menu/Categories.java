package com.tencao.saoui.screens.menu;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum Categories {

    NONE(false),

    PROFILE(true),
    SOCIAL(true),
    MESSAGE(false),
    NAVIGATION(true),
    SETTINGS(true),

    EQUIPMENT(PROFILE, true),
    ITEMS(PROFILE, true),
    SKILLS(PROFILE, true),

    GUILD(SOCIAL, false),
    PARTY(SOCIAL, true),
    FRIENDS(SOCIAL, true),

    QUESTS(NAVIGATION, true),
    FIELD_MAP(NAVIGATION, true),
    DUNGEON_MAP(NAVIGATION, true),

    OPTIONS(SETTINGS, true),
    MENU(SETTINGS, false),
    LOGOUT(SETTINGS, false),

    TOOLS(EQUIPMENT, true),
    ARMOR(EQUIPMENT, true),
    CONSUMABLES(EQUIPMENT, true),
    ACCESSORY(EQUIPMENT, true),

    WEAPONS(TOOLS, true),
    BOWS(TOOLS, true),
    PICKAXE(TOOLS, true),
    AXE(TOOLS, true),
    SHOVEL(TOOLS, true),

    INVITE_LIST(PARTY, true), INVITE_PLAYER(INVITE_LIST, false),
    DISSOLVE(PARTY, false),

    SLOT(false), FRIEND(FRIENDS, true), QUEST(false),

    MESSAGE_BOX(FRIEND, false),
    POSITION_CHECK(FRIEND, true),
    OTHER_PROFILE(FRIEND, true),

    CONFIRM(false), CANCEL(false),

    SKILL(SKILLS, false),
    OPTION(OPTIONS, false),
    OPT_CAT(OPTIONS, true),

    ALERT(false);

    public final Categories parent;
    public final boolean menuFlag;

    Categories(Categories parentID, boolean menu) {
        parent = parentID;
        menuFlag = menu;
    }

    Categories(boolean menu) {
        this(null, menu);
    }

    public boolean hasParent(Categories id) {
        return parent == id || parent != null && parent.hasParent(id);
    }

}
