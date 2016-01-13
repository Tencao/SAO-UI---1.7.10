package com.tencao.saoui.util;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import java.util.stream.Stream;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class ConfigHandler {
    public static String _PARTY_DISSOLVING_TITLE; // TODO: Change it! This is not pretty. AT ALL!
    public static String _PARTY_DISSOLVING_TEXT;
    public static String _PARTY_LEAVING_TITLE;
    public static String _PARTY_LEAVING_TEXT;
    public static String _MESSAGE_TITLE;
    public static String _MESSAGE_FROM;
    public static String _FRIEND_REQUEST_TITLE;
    public static String _FRIEND_REQUEST_TEXT;
    public static String _PARTY_INVITATION_TITLE;
    public static String _PARTY_INVITATION_TEXT;
    public static String _DEAD_ALERT;
    public static boolean DEBUG = false;
    private static Configuration config;

    public static void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        DEBUG = config.get(Configuration.CATEGORY_GENERAL, "debug", DEBUG).getBoolean();

        _FRIEND_REQUEST_TITLE = config.get(Configuration.CATEGORY_GENERAL, "friend.request.title", SAOResources.FRIEND_REQUEST_TITLE).getString();
        _FRIEND_REQUEST_TEXT = config.get(Configuration.CATEGORY_GENERAL, "friend.request.text", SAOResources.FRIEND_REQUEST_TEXT).getString();

        _PARTY_INVITATION_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.invitation.title", SAOResources.PARTY_INVITATION_TITLE).getString();
        _PARTY_INVITATION_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.invitation.text", SAOResources.PARTY_INVITATION_TEXT).getString();

        _PARTY_DISSOLVING_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.dissolving.title", SAOResources.PARTY_DISSOLVING_TITLE).getString();
        _PARTY_DISSOLVING_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.dissolving.text", SAOResources.PARTY_DISSOLVING_TEXT).getString();

        _PARTY_LEAVING_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.leaving.title", SAOResources.PARTY_LEAVING_TITLE).getString();
        _PARTY_LEAVING_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.leaving.text", SAOResources.PARTY_LEAVING_TEXT).getString();

        _MESSAGE_TITLE = config.get(Configuration.CATEGORY_GENERAL, "message.title", SAOResources.MESSAGE_TITLE).getString();
        _MESSAGE_FROM = config.get(Configuration.CATEGORY_GENERAL, "message.from", SAOResources.MESSAGE_FROM).getString();

        _DEAD_ALERT = config.get(Configuration.CATEGORY_GENERAL, "dead.alert", SAOResources.DEAD_ALERT).getString();

        Stream.of(SAOOption.values()).forEach(option -> {
            if (config.get(Configuration.CATEGORY_GENERAL, "option." + option.name().toLowerCase(), option.getValue()).getBoolean())
                option.enable();
            else option.disable();
        });

        config.save();
    }

    static void setOption(SAOOption option) {
        config.get(Configuration.CATEGORY_GENERAL, "option." + option.name().toLowerCase(), option.getValue()).set(option.getValue());
        saveAllOptions();
    }

    private static void saveAllOptions() {
        config.save();
    }
}