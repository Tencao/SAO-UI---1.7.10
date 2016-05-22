package com.saomc.util;

import com.saomc.GLCore;
import com.saomc.events.ConfigHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public enum OptionCore {

    //Main Categories
    VANILLA_OPTIONS(StatCollector.translateToLocal("guiOptions"), false, false, null, false),
    UI(StatCollector.translateToLocal("optCatUI"), false, true, null, false),
    THEME(StatCollector.translateToLocal("optTheme"), false, true, null, false),
    HEALTH_OPTIONS(StatCollector.translateToLocal("optCatHealth"), false, true, null, false),
    HOTBAR_OPTIONS(StatCollector.translateToLocal("optCatHotBar"), false, true, null, false),
    EFFECTS(StatCollector.translateToLocal("optCatEffects"), false, true, null, false),
    MISC(StatCollector.translateToLocal("optCatMisc"), false, true, null, false),
    //General UI Settings
    UI_ONLY(StatCollector.translateToLocal("optionUIOnly"), false, false, UI, false),
    DEFAULT_INVENTORY(StatCollector.translateToLocal("optionDefaultInv"), true, false, UI, false),
    DEFAULT_DEATH_SCREEN(StatCollector.translateToLocal("optionDefaultDeath"), false, false, UI, false),
    DEFAULT_DEBUG(StatCollector.translateToLocal("optionDefaultDebug"), false, false, UI, false),
    FORCE_HUD(StatCollector.translateToLocal("optionForceHud"), false, false, UI, false),
    LOGOUT(StatCollector.translateToLocal("optionLogout"), false, false, UI, false),
    GUI_PAUSE(StatCollector.translateToLocal("optionGuiPause"), true, false, UI, false),
    // Themes
    VANILLA_UI(StatCollector.translateToLocal("optionDefaultUI"), false, false, THEME, true),
    ALO_UI(StatCollector.translateToLocal("optionALOUI"), false, false, THEME, true),
    SAO_UI(StatCollector.translateToLocal("optionSAOUI"), true, false, THEME, true),
    // Health Options
    SMOOTH_HEALTH(StatCollector.translateToLocal("optionSmoothHealth"), true, false, HEALTH_OPTIONS, false),
    HEALTH_BARS(StatCollector.translateToLocal("optionHealthBars"), true, false, HEALTH_OPTIONS, false),
    REMOVE_HPXP(StatCollector.translateToLocal("optionLightHud"), false, false, HEALTH_OPTIONS, false),
    //DEFAULT_HEALTH(StatCollector.translateToLocal("optionDefaultHealth"), false, false, HEALTH_OPTIONS, false),
    ALT_ABSORB_POS(StatCollector.translateToLocal("optionAltAbsorbPos"), false, false, HEALTH_OPTIONS, false),
    //Hotbar
    DEFAULT_HOTBAR(StatCollector.translateToLocal("optionDefaultHotbar"), false, false, HOTBAR_OPTIONS, true),
    HOR_HOTBAR(StatCollector.translateToLocal("optionHorHotbar"), false, false, HOTBAR_OPTIONS, true),
    VER_HOTBAR(StatCollector.translateToLocal("optionVerHotbar"), false, false, HOTBAR_OPTIONS, true),
    //Effects
    CURSOR_TOGGLE(StatCollector.translateToLocal("optionCursorToggle"), true, false, EFFECTS, false),
    COLOR_CURSOR(StatCollector.translateToLocal("optionColorCursor"), true, false, EFFECTS, false),
    SPINNING_CRYSTALS(StatCollector.translateToLocal("optionSpinning"), true, false, EFFECTS, false),
    PARTICLES(StatCollector.translateToLocal("optionParticles"), true, false, EFFECTS, false),
    LESS_VISUALS(StatCollector.translateToLocal("optionLessVis"), false, false, EFFECTS, false),
    SOUND_EFFECTS(StatCollector.translateToLocal("optionSounds"), true, false, EFFECTS, false),
    //Misc
    CROSS_HAIR(StatCollector.translateToLocal("optionCrossHair"), false, false, MISC, false),
    AGGRO_SYSTEM(StatCollector.translateToLocal("optionAggro"), false, false, MISC, false),
    CLIENT_CHAT_PACKETS(StatCollector.translateToLocal("optionCliChatPacks"), true, false, MISC, false),
    MOUNT_STAT_VIEW(StatCollector.translateToLocal("optionMountStatView"), true, false, MISC, false),
    CUSTOM_FONT(StatCollector.translateToLocal("optionCustomFont"), false, false, MISC, false),
    DEBUG_MODE(StatCollector.translateToLocal("optionDebugMode"), false, false, MISC, false),
    COMPACT_INVENTORY(StatCollector.translateToLocal("optionCompatInv"), false, false, MISC, false),
    //Debug
    DISABLE_TICKS(StatCollector.translateToLocal("optionDisableTicks"), false, false, MISC, false);

    public final String name;
    public final boolean isCategory;
    public final OptionCore category;
    private boolean value;
    private boolean restricted;

    OptionCore(String optionName, boolean defaultValue, boolean isCat, OptionCore category, boolean onlyOne) {
        name = optionName;
        value = defaultValue;
        isCategory = isCat;
        this.category = category;
        restricted = onlyOne;
    }

    public static OptionCore fromString(String str) {
        return Stream.of(values()).filter(option -> option.toString().equals(str)).findAny().orElse(null);
    }

    @Override
    public final String toString() {
        return name;
    }

    public boolean flip() {
        this.value = !this.getValue();
        ConfigHandler.setOption(this);
        if (this == CUSTOM_FONT) GLCore.setFont(Minecraft.getMinecraft(), this.value);
        return this.value;
    }

    public boolean getValue() {
        return this.value;
    }

    public boolean isRestricted() {
        return this.restricted;
    }

    public OptionCore getCategory() {
        return this.category;
    }

    public void disable() {
        if (this.value) this.flip();
    }

    public void enable() {
        if (!this.value) this.flip();
    }

}
