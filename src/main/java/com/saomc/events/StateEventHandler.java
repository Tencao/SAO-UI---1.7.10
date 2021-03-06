package com.saomc.events;

import com.saomc.colorstates.ColorState;
import com.saomc.colorstates.ColorStateHandler;
import com.saomc.util.OptionCore;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.Iterator;
import java.util.List;

import static com.saomc.events.EventCore.mc;

/**
 * This is purely for the ColorStateHandler
 */
public class StateEventHandler {

    private static int ticks = 0;

    static void checkTicks (TickEvent.RenderTickEvent e){
        if (!OptionCore.DISABLE_TICKS.getValue() && mc.theWorld != null && e.phase.equals(TickEvent.Phase.END)) {
            if (ticks >= 10) {
                checkRadius();
                resetState();
                ticks = 0;
            } else ++ticks;
        }
    }

    static void checkRadius (){
        List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.boundingBox.expand(20.0D, 20.0D, 20.0D));
        entities.removeIf(ent -> !(ent != null && ent instanceof EntityLivingBase && ent.worldObj.isRemote));
        entities.stream().filter(ent -> ((EntityLivingBase)ent).getHealth() <= 0 && OptionCore.PARTICLES.getValue()).forEach(ent -> RenderHandler.deadHandlers.add((EntityLivingBase)ent));
        entities.stream().filter(ent -> mc.thePlayer.canEntityBeSeen(ent) && ColorStateHandler.getInstance().getSavedState((EntityLivingBase) ent) == ColorState.VIOLENT).forEach(ent -> ColorStateHandler.getInstance().set((EntityLivingBase) ent, ColorState.KILLER, true));
    }

    static void resetState(){
        if (OptionCore.AGGRO_SYSTEM.getValue())ColorStateHandler.getInstance().updateKeeper();
        else if (!ColorStateHandler.getInstance().isEmpty())ColorStateHandler.getInstance().clean();
    }

    static void genStateMaps(EntityEvent.EntityConstructing e){
        if (e.entity instanceof EntityLivingBase)
            if (ColorStateHandler.getInstance().getDefault((EntityLivingBase)e.entity) == null && !(e.entity instanceof EntityPlayer))
                ColorStateHandler.getInstance().genDefaultState((EntityLivingBase)e.entity);
    }

    public static void getColor(EntityLivingBase entity){
        ColorStateHandler.getInstance().stateColor(entity);
    }

}
