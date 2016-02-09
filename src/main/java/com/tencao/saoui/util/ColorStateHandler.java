package com.tencao.saoui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ColorStateHandler {
    private static ColorStateHandler instance = new ColorStateHandler();

    private ColorStateHandler() {

    }

    public static ColorStateHandler instance() {
        return instance;
    }

    public static final ConcurrentMap<UUID, SAOColorState> colorStates = new ConcurrentHashMap<>();
    public static final ConcurrentMap<Class, SAOColorState> defaultStates = new ConcurrentHashMap<>();
    public static final ConcurrentMap<UUID, Integer> stateKeeper =  new ConcurrentHashMap<>();

    public static SAOColorState getCurrent(EntityLivingBase entity) {
        if (colorStates.containsKey(entity.getUniqueID()))
            return colorStates.get(entity.getUniqueID());
        else return getSavedState(Minecraft.getMinecraft(), entity);
    }

    public static SAOColorState getDefault(EntityLivingBase entity) {
        return defaultStates.get(entity.getClass());
    }

    public static boolean isValid(EntityLivingBase entity) {
        return entity != null && colorStates.containsKey(entity.getUniqueID());
    }

    public static void remove(EntityLivingBase entity) {
        colorStates.remove(entity.getUniqueID());
        stateKeeper.remove(entity.getUniqueID());
    }

    public static void reset(UUID uuid) {
        colorStates.remove(uuid);
        stateKeeper.remove(uuid);
    }


    /**
     * This is used to dynamically update the Entities State.
     * @Param entity = Entity you wish to update
     * @Param newState = The State in which will be set
     * @Param event = if this is done via an event or not.
     * Event is temporary, and depending if it has being changed from the default already, it will persist for either 300 ticks, to 600 ticks.
     * If you pass a false for event, this will permanently update the state for the entire Entity class.
     */
    public static void set(EntityLivingBase entity, SAOColorState newState, boolean event) {
        final UUID uuid = entity.getUniqueID();
        if (event){
            SAOColorState defaultState = defaultStates.get(entity.getClass());
            SAOColorState currentState = colorStates.containsKey(entity.getUniqueID()) ? colorStates.get(entity.getUniqueID()) : defaultState;
            if (currentState != newState) {
                if (defaultState != currentState) {
                    stateKeeper.put(uuid, 12000);
                    colorStates.put(uuid, newState);
                    System.out.print(uuid + "---" + entity.getCommandSenderName() + "\n" + " updated state 1. Changed state from " + currentState.name() + " to " + newState.name() + "\n" + "\n");
                } else {
                    stateKeeper.put(uuid, 6000);
                    colorStates.put(uuid, newState);
                    System.out.print(uuid + "---" + entity.getCommandSenderName() + "\n" + " updated state 2. Changed state from " + currentState.name() + " to " + newState.name() + "\n" + "\n");
                }
            }
        } else {
            if (defaultStates.containsKey(entity.getClass())) {
            defaultStates.put(entity.getClass(), newState);
            } else defaultStates.put(entity.getClass(), newState);
        }
    }


    public static SAOColorState getSavedState(Minecraft mc, Entity entity){
        if (!(colorStates.containsKey(entity.getUniqueID()))){
            if (defaultStates.containsKey(entity.getClass())){
                SAOColorState state = defaultStates.get(entity.getClass());
                colorStates.put(entity.getUniqueID(), state);
                return state;
            } else {
                SAOColorState state = SAOColorState.getColorState(mc, entity);
                defaultStates.put(entity.getClass(), state);
                colorStates.put(entity.getUniqueID(), state);
                if (SAOOption.DEBUG_MODE.getValue())
                    System.out.print(entity.getCommandSenderName() + " added to map" + "\n");
                return state;
            }
        } else {
            return colorStates.get(entity.getUniqueID());
        }

    }
}
