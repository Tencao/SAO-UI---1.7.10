package com.saomc.screens.menu;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum Categories {

    NONE(false, true),

    PROFILE(true, true),
    SOCIAL(true, true),
    MESSAGE(false, true),
    NAVIGATION(true, true),
    SETTINGS(true, true),

    EQUIPMENT(PROFILE, true, false),
    ITEMS(PROFILE, true, false),
    SKILLS(PROFILE, true, false),

    GUILD(SOCIAL, false, false),
    PARTY(SOCIAL, true, false),
    FRIENDS(SOCIAL, true, false),

    QUESTS(NAVIGATION, true, false),
    FIELD_MAP(NAVIGATION, true, false),
    DUNGEON_MAP(NAVIGATION, true, false),

    OPTIONS(SETTINGS, true, false),
    MENU(SETTINGS, false, false),
    LOGOUT(SETTINGS, false, false),

    TOOLS(EQUIPMENT, true, false),
    ARMOR(EQUIPMENT, true, false),
    CONSUMABLES(EQUIPMENT, true, false),
    ACCESSORY(EQUIPMENT, true, false),

    WEAPONS(TOOLS, true, false),
    BOWS(TOOLS, true, false),
    PICKAXE(TOOLS, true, false),
    AXE(TOOLS, true, false),
    SHOVEL(TOOLS, true, false),

    INVITE_LIST(PARTY, true, false), INVITE_PLAYER(INVITE_LIST, false, false),
    DISSOLVE(PARTY, false, false),

    SLOT(false, false), FRIEND(FRIENDS, true, false), QUEST(false, false),

    MESSAGE_BOX(FRIEND, false, false),
    POSITION_CHECK(FRIEND, true, false),
    OTHER_PROFILE(FRIEND, true, false),

    CONFIRM(false, true), CANCEL(false, true),

    SKILL(SKILLS, false, false),
    OPTION(OPTIONS, false, false),
    OPT_CAT(OPTIONS, true, false),

    ALERT(false, true);

    private final Categories parent;
    private final boolean menuFlag;
    private final boolean isMandatory;

    Categories(Categories parentID, boolean menu, boolean isMain) {
        parent = parentID;
        menuFlag = menu;
        isMandatory = isMain;
    }

    Categories(boolean menu, boolean isMain) {
        this(null, menu, isMain);
    }

    public boolean hasParent(Categories id) {
        return parent == id || parent != null && parent.hasParent(id);
    }

    public Categories getParent(Categories id) {
        return id.parent;
    }

    public boolean isMandatory() {
        return this.isMandatory;
    }

    public boolean isMenu() {
        return this.menuFlag;
    }

    public boolean hasSecondParent(Categories id){
        return (parent != null && parent != id && parent.parent != null);
    }

    // Gets the parents parent
    public Categories getSecondParent(Categories id){
        System.out.print("getSecondParent, recieved " + id + " sent " + parent.parent + "\n");
        return parent.parent;
    }

    public boolean trimCheck(Categories id) {
        return (hasSecondParent(id) && getSecondParent(id) != id && !getSecondParent(id).isMandatory());
    }

}
