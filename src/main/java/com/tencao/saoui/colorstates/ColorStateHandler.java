package com.tencao.saoui.colorstates;

import com.tencao.saoui.util.OptionCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SideOnly(Side.CLIENT)
public class ColorStateHandler {

    private ColorStateHandler()
    {
        // nill
    }

    @SideOnly(Side.CLIENT)
    public static synchronized ColorStateHandler getInstance()
    {
        if (ref == null)
            // Only return one instance
            ref = new ColorStateHandler();
        return ref;
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
        // Cloning not supported
    }

    private static ColorStateHandler ref;

    ConcurrentMap<Class, ColorState> defaultStates = new ConcurrentHashMap<>();
    ConcurrentMap<Integer, ColorState> colorStates = new ConcurrentHashMap<>();
    ConcurrentMap<UUID, ColorState> playerStates = new ConcurrentHashMap<>();
    ConcurrentMap<Integer, Integer> stateKeeper = new ConcurrentHashMap<>();
    ConcurrentMap<UUID, Integer> playerKeeper = new ConcurrentHashMap<>();

    public synchronized ColorState getDefault(EntityLivingBase entity) {
        return defaultStates.get(entity.getClass());
    }

    // For debug only
    public synchronized boolean isValid(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) return (playerStates.get(entity.getUniqueID()) != null);
        else return (colorStates.get(entity.getEntityId()) != null);
    }

    public synchronized void remove(EntityLivingBase entity) {
        colorStates.remove(entity.getEntityId());
        stateKeeper.remove(entity.getEntityId());
    }

    public synchronized void remove(EntityPlayer entity) {
        playerStates.remove(entity.getUniqueID());
        playerKeeper.remove(entity.getUniqueID());
    }

    public synchronized boolean isEmpty(){
        if (colorStates.isEmpty() && stateKeeper.isEmpty() && playerKeeper.isEmpty() && playerStates.isEmpty()) return true;
        else return false;
    }

    public boolean isChanged(EntityLivingBase entity) {
        return (getSavedState(entity) != getDefault(entity));
    }

    public void reset(int entity) {
        colorStates.replace(entity, ColorState.INNOCENT);
        stateKeeper.remove(entity);
    }

    public void reset(UUID entity) {
        playerStates.replace(entity, ColorState.INNOCENT);
        playerKeeper.remove(entity);
    }

    public void clean() {
        stateKeeper.clear();
        colorStates.clear();
        playerKeeper.clear();
        playerStates.clear();
        defaultStates.clear();
    }

    public synchronized void stateColor(EntityLivingBase entity) {
        if (!OptionCore.AGGRO_SYSTEM.getValue()) defaultStates.get(entity.getClass()).glColor();
        if (entity instanceof EntityPlayer){
            if (playerStates.get(entity.getUniqueID()) != null) {
                ColorState state = playerStates.get(entity.getUniqueID());
                state.glColor();
            } else genPlayerStates((EntityPlayer)entity);
        } else {
            if (colorStates.get(entity.getEntityId()) != null) {
                ColorState state = colorStates.get(entity.getEntityId());
                state.glColor();
            } else genColorStates(entity);
        }
        //else defaultStates.get(entity.getClass()).glColor();
    }


    /**
     * This is used to dynamically update the Entities State.
     * @Param entity = Entity you wish to update
     * @Param newState = The State in which will be set
     * @Param event = if this is done via an event or not.
     * Event is temporary, and depending if it has being changed from the default already, it will persist for either 300 ticks, to 600 ticks.
     * If you pass a false for event, this will permanently update the state for the entire Entity class.
     */
    public synchronized void set(EntityLivingBase entity, ColorState newState, boolean event) {
        ColorState defaultState = defaultStates.get(entity.getClass());
        if (!event) {
            if (defaultStates.get(entity.getClass()) != null) {
                defaultStates.replace(entity.getClass(), defaultState, newState);
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print("WARNING - DEFAULT STATE WAS CHANGED" + "\n");
            } else {
                defaultStates.putIfAbsent(entity.getClass(), newState);
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print("WARNING - DEFAULT STATE WAS CHANGED" + "\n");
            }
        } else if (!(entity instanceof EntityPlayer)) {
            if (colorStates.get(entity.getEntityId()) != null) {
                ColorState currentState = colorStates.get(entity.getEntityId());
                if (currentState != newState)
                    if (defaultState != currentState) {
                        stateKeeper.put(entity.getEntityId(), 12000);
                        colorStates.replace(entity.getEntityId(), currentState, newState);
                        if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity + "---" + entity.getCommandSenderName() + "\n" + " updated state 1. Changed state from " + currentState.name() + " to " + newState.name() + "\n" + "\n");
                    } else {
                        stateKeeper.put(entity.getEntityId(), 6000);
                        colorStates.replace(entity.getEntityId(), currentState, newState);
                        if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity + "---" + entity.getCommandSenderName() + "\n" + " updated state 2. Changed state from " + currentState.name() + " to " + newState.name() + "\n" + "\n");
                    }
                else if (newState == ColorState.KILLER) {
                    stateKeeper.put(entity.getEntityId(), 12000);
                } else if (newState == ColorState.VIOLENT){
                    stateKeeper.put(entity.getEntityId(), 6000);
                }
            } else if (OptionCore.DEBUG_MODE.getValue())
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print("WARNING - ENTITY RETURNED INVALID STATE FROM SET - " + entity.getCommandSenderName() + " " + entity.getEntityId() + "\n");
        } else {
            if (playerStates.get(entity.getUniqueID()) == null) genPlayerStates((EntityPlayer)entity);
            else {
                ColorState currentState = playerStates.get(entity.getUniqueID());
                if (currentState != newState) {
                    if (defaultState != currentState) {
                        playerKeeper.put(entity.getUniqueID(), 12000);
                        playerStates.replace(entity.getUniqueID(), currentState, newState);
                        if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity + "---" + entity.getCommandSenderName() + "\n" + " updated state 3. Changed state from " + currentState.name() + " to " + newState.name() + "\n" + "\n");
                    } else {
                        playerKeeper.put(entity.getUniqueID(), 6000);
                        playerStates.replace(entity.getUniqueID(), currentState, newState);
                        if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity + "---" + entity.getCommandSenderName() + "\n" + " updated state 4. Changed state from " + currentState.name() + " to " + newState.name() + "\n" + "\n");
                    }
                } else if (newState == ColorState.KILLER) {
                    playerKeeper.put(entity.getUniqueID(), 12000);
                } else if (newState == ColorState.VIOLENT){
                    playerKeeper.put(entity.getUniqueID(), 6000);
                }
            }
        }
    }

    public synchronized void genPlayerStates(EntityPlayer entity){
        if (playerStates.get(entity.getUniqueID()) == null) {
            if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity.getCommandSenderName() + " " + entity.getUniqueID() + " adding to Color Maps" + "\n");
            ColorState state = defaultStates.get(entity.getClass());
            if (state == null) genDefaultState(entity);
            else {
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print(state + " pulled from defaultStates" + "\n");
                colorStates.put(entity.getEntityId(), state);
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print(state + " stored in colorStates" + "\n");
            }
        }
    }

    public synchronized void genColorStates(EntityLivingBase entity){
        if (colorStates.get(entity.getEntityId()) == null) {
            if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity.getCommandSenderName() + " " + entity.getEntityId() + " adding to Color Maps" + "\n");
            ColorState state = defaultStates.get(entity.getClass());
            if (state == null) genDefaultState(entity);
            else {
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print(state + " pulled from defaultStates" + "\n");
                colorStates.put(entity.getEntityId(), state);
                if (OptionCore.DEBUG_MODE.getValue()) System.out.print(state + " stored in colorStates" + "\n");
            }
        }
    }

    public synchronized void genDefaultState(EntityLivingBase entity){
        if (getDefault(entity) == null){
            Minecraft mc = Minecraft.getMinecraft();
            ColorState state = ColorState.getColorState(mc, entity);
            defaultStates.put(entity.getClass(), state);
            if (entity instanceof EntityPlayer) {
                playerStates.putIfAbsent(entity.getUniqueID(), state);
                if (OptionCore.DEBUG_MODE.getValue())
                    if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity.getCommandSenderName() + " added to map" + "\n");
            } else {
                colorStates.putIfAbsent(entity.getEntityId(), state);
                if (OptionCore.DEBUG_MODE.getValue())
                    if (OptionCore.DEBUG_MODE.getValue()) System.out.print(entity.getCommandSenderName() + " added to map" + "\n");
            }
        }
    }

    public synchronized ColorState getSavedState(EntityLivingBase entity){
        if (entity instanceof EntityPlayer){
            if (playerKeeper.get(entity.getUniqueID()) != null)
                return playerStates.get(entity.getUniqueID());
            else {
                return defaultStates.get(entity.getClass());
            }
        } else {
            if (stateKeeper.get(entity.getEntityId()) != null)
                return colorStates.get(entity.getEntityId());
            else {
                return defaultStates.get(entity.getClass());
            }
        }
    }

    public synchronized void updateKeeper(){
        if (!OptionCore.AGGRO_SYSTEM.getValue()){
            clean();
        }
        if (!stateKeeper.isEmpty())
            stateKeeper.forEach((uuid, ticks) -> {
                --ticks;
                if (ticks == 0) {
                    reset(uuid);
                } else {
                    stateKeeper.put(uuid, ticks);
                }
            });
        if (!playerKeeper.isEmpty())
            playerKeeper.forEach((uuid, ticks) -> {
                --ticks;
                if (ticks == 0) {
                    reset(uuid);
                } else {
                    playerKeeper.put(uuid, ticks);
                }
            });
    }
}
