package com.tencao.saoui.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public enum SAOOption {

    //Main Categories
    VANILLA_OPTIONS(StatCollector.translateToLocal("guiOptions"), false, false, null),
    UI(StatCollector.translateToLocal("optCatUI"), false, true, null),
    HEALTH_OPTIONS(StatCollector.translateToLocal("optCatHealth"), false, true, null),
    HOTBAR_OPTIONS(StatCollector.translateToLocal("optCatHotBar"), false, true, null),
    EFFECTS(StatCollector.translateToLocal("optCatEffects"), false, true, null),
    MISC(StatCollector.translateToLocal("optCatMisc"), false, true, null),
    //General UI Settings
    DEFAULT_UI(StatCollector.translateToLocal("optionDefaultUI"), false, false, UI),
    DEFAULT_INVENTORY(StatCollector.translateToLocal("optionDefaultInv"), true, false, UI),
    DEFAULT_DEATH_SCREEN(StatCollector.translateToLocal("optionDefaultDeath"), false, false, UI),
    ORIGINAL_UI(StatCollector.translateToLocal("optionOrigUI"), true, false, UI),
    FORCE_HUD(StatCollector.translateToLocal("optionForceHud"), false, false, UI),
    LOGOUT(StatCollector.translateToLocal("optionLogout"), false, false, UI),
    GUI_PAUSE(StatCollector.translateToLocal("optionGuiPause"), true, false, UI),
    // Health Options
    SMOOTH_HEALTH(StatCollector.translateToLocal("optionSmoothHealth"), true, false, HEALTH_OPTIONS),
    HEALTH_BARS(StatCollector.translateToLocal("optionHealthBars"), true, false, HEALTH_OPTIONS),
    REMOVE_HPXP(StatCollector.translateToLocal("optionLightHud"), false, false, HEALTH_OPTIONS),
    //DEFAULT_HEALTH(StatCollector.translateToLocal("optionDefaultHealth"), false, false, HEALTH_OPTIONS),
    ALT_ABSORB_POS(StatCollector.translateToLocal("optionAltAbsorbPos"), false, false, HEALTH_OPTIONS),
    //Hotbar
    DEFAULT_HOTBAR(StatCollector.translateToLocal("optionDefaultHotbar"), false, false, HOTBAR_OPTIONS),
    ALT_HOTBAR(StatCollector.translateToLocal("optionAltHotbar"), false, false, HOTBAR_OPTIONS),
    //Effects
    COLOR_CURSOR(StatCollector.translateToLocal("optionColorCursor"), true, false, EFFECTS),
    CURSOR_MOVEMENT(StatCollector.translateToLocal("optionCursorMov"), true, false, EFFECTS),
    SPINNING_CRYSTALS(StatCollector.translateToLocal("optionSpinning"), true, false, EFFECTS),
    PARTICLES(StatCollector.translateToLocal("optionParticles"), true, false, EFFECTS),
    LESS_VISUALS(StatCollector.translateToLocal("optionLessVis"), false, false, EFFECTS),
    SOUND_EFFECTS(StatCollector.translateToLocal("optionSounds"), true, false, EFFECTS),
    //Misc
    CROSS_HAIR(StatCollector.translateToLocal("optionCrossHair"), false, false, MISC),
    CLIENT_CHAT_PACKETS(StatCollector.translateToLocal("optionCliChatPacks"), true, false, MISC),
    MOUNT_STAT_VIEW(StatCollector.translateToLocal("optionMountStatView"), true, false, MISC),
    CUSTOM_FONT(StatCollector.translateToLocal("optionCustomFont"), false, false, MISC);

    public final String name;
    public final boolean isCategory;
    public final SAOOption category;
    private boolean value;

    SAOOption(String optionName, boolean defaultValue, boolean isCat, SAOOption category) {
        name = optionName;
        value = defaultValue;
        isCategory = isCat;
        this.category = category;
    }

    public static SAOOption fromString(String str) {
        return Stream.of(values()).filter(option -> option.toString().equals(str)).findAny().orElse(null);
    }

    @Override
    public final String toString() {
        return name;
    }

    public boolean flip() {
        this.value = !this.getValue();
        ConfigHandler.setOption(this);
        if (this == CUSTOM_FONT) SAOGL.setFont(Minecraft.getMinecraft(), this.value);
        return this.value;
    }

    public boolean getValue() {
        return this.value;
    }

    public void disable() {
        if (this.value) this.flip();
    }

    public void enable() {
        if (!this.value) this.flip();
    }

}
