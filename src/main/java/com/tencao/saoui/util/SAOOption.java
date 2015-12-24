package com.tencao.saoui.util;

import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SAOOption {

    UI(StatCollector.translateToLocal("optCatUI"), false, true, null),
    RENDERER(StatCollector.translateToLocal("optCatRend"), false, true, null),
    INTERFACES(StatCollector.translateToLocal("optCatInterf"), false, true, null),
    DEFAULT_UI(StatCollector.translateToLocal("optionDefaultUI"), false, false, UI),
    //DEFAULT_HEALTH(StatCollector.translateToLocal("optionDefaultHealth"), false, false, UI),
    DEFAULT_INVENTORY(StatCollector.translateToLocal("optionDefaultInv"), true, false, INTERFACES),
    DEFAULT_DEATH_SCREEN(StatCollector.translateToLocal("optionDefaultDeath"), false, false, UI),
    DEFAULT_HOTBAR(StatCollector.translateToLocal("optionDefaultHotbar"), false, false, UI),
    ALT_HOTBAR(StatCollector.translateToLocal("optionAltHotbar"), false, false, UI),
    CROSS_HAIR(StatCollector.translateToLocal("optionCrossHair"), false, false, UI),
    HEALTH_BARS(StatCollector.translateToLocal("optionHealthBars"), true, false, RENDERER),
    SMOOTH_HEALTH(StatCollector.translateToLocal("optionSmoothHealth"), true, false, UI),
    COLOR_CURSOR(StatCollector.translateToLocal("optionColorCursor"), true, false, RENDERER),
    PARTICLES(StatCollector.translateToLocal("optionParticles"), true, false, RENDERER),
    CURSOR_MOVEMENT(StatCollector.translateToLocal("optionCursorMov"), true, false, INTERFACES),
    CLIENT_CHAT_PACKETS(StatCollector.translateToLocal("optionCliChatPacks"), true, false, null),
    SOUND_EFFECTS(StatCollector.translateToLocal("optionSounds"), true, false, null),
    LOGOUT(StatCollector.translateToLocal("optionLogout"), false, false, INTERFACES),
    ORIGINAL_UI(StatCollector.translateToLocal("optionOrigUI"), true, false, UI),
    LESS_VISUALS(StatCollector.translateToLocal("optionLessVis"), false, false, RENDERER),
    SPINNING_CRYSTALS(StatCollector.translateToLocal("optionSpinning"), true, false, RENDERER),
    FORCE_HUD(StatCollector.translateToLocal("optionForceHud"), false, false, UI),
    REMOVE_HPXP(StatCollector.translateToLocal("optionLightHud"), false, false, UI),
    ALT_ABSORB_POS(StatCollector.translateToLocal("optionAltAbsorbPos"), false, false, UI);

    public final String name;
    public boolean value;
    public final boolean isCategory;
    public final SAOOption category;

    SAOOption(String optionName, boolean defaultValue, boolean isCat, SAOOption category) {
        name = optionName;
        value = defaultValue;
        isCategory = isCat;
        this.category= category;
    }

    @Override
    public final String toString() {
        return name;
    }

    public static SAOOption fromString(String str) {
        for (final SAOOption option: values()) if (option.toString().equals(str)) return option;
        return null;
    }

}
