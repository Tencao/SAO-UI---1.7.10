package com.tencao.saoui.util;

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.UUID;

public class ColorStateHandler {
    private static ColorStateHandler instance = new ColorStateHandler();
    private ColorStatesMap colorStates = new ColorStatesMap();

    private ColorStateHandler() {

    }

    public static ColorStateHandler instance() {
        return instance;
    }

    public SAOColorState get(final EntityPlayer entity) {
        final UUID uuid = entity.getUniqueID();

        if (!colorStates.containsKey(uuid)) colorStates.put(uuid, new SAOColorCursor());

        return colorStates.get(uuid).get();
    }

    public void set(final EntityPlayer entity, SAOColorState newState) {
        final UUID uuid = entity.getUniqueID();
        colorStates.put(uuid, new SAOColorCursor(newState, true));
    }

    public void update() { // TODO: this needs some optimization
        colorStates.values().stream().forEach(SAOColorCursor::update);
    }

    private class ColorStatesMap extends HashMap<UUID, SAOColorCursor> {
        @Override
        public SAOColorCursor put(UUID key, SAOColorCursor value) {
            if (super.containsKey(key)) {
                super.get(key).set(value.get());
                return null;
            }
            return super.put(key, value);
        }
    }
}
