package com.tencao.saoui;

import com.tencao.saoui.commands.Command;
import com.tencao.saoui.util.*;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class SAOEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean isPlaying = false;

    public static void onDamagePlayer(final EntityPlayer entity) {
        ColorStateHandler.instance().set(entity, SAOColorState.VIOLENT);
    }

    public static void onKillPlayer(final EntityPlayer entity) {
        ColorStateHandler.instance().set(entity, SAOColorState.KILLER);
    }

    @SubscribeEvent
    public void livingAttack(LivingAttackEvent e) {
        this.livingHit(e.entityLiving, e.source.getEntity());
    }

    @SubscribeEvent
    public void livingHurt(LivingHurtEvent e) {
        this.livingHit(e.entityLiving, e.source.getEntity());
    }

    private void livingHit(EntityLivingBase target, Entity source) {
        if (target instanceof EntityPlayer && source instanceof EntityPlayer) {
            if (target.getHealth() <= 0) {
                onKillPlayer((EntityPlayer) source);
            } else {
                onDamagePlayer((EntityPlayer) source);
            }
        }
    }

    @SubscribeEvent
    public void livingDeath(LivingDeathEvent e) {
        if (e.entityLiving instanceof EntityPlayer && e.source.getEntity() instanceof EntityPlayer)
            onKillPlayer((EntityPlayer) e.source.getEntity());
    }

    @SubscribeEvent
    public void livingDrop(LivingDropsEvent e) {
        if (e.entityLiving instanceof EntityPlayer && e.source.getEntity() instanceof EntityPlayer)
            onKillPlayer((EntityPlayer) e.source.getEntity());
    }

    @SubscribeEvent
    public void playerAttackEntity(AttackEntityEvent e) {
        if (e.target instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) e.target;

            if (player.getHealth() <= 0) onKillPlayer(e.entityPlayer);
            else onDamagePlayer(e.entityPlayer);
        }
    }

    @SubscribeEvent
    public void playerDrops(PlayerDropsEvent e) {
        if (e.source.getEntity() instanceof EntityPlayer) onKillPlayer((EntityPlayer) e.source.getEntity());
    }

    @SubscribeEvent
    public void abilityCheck(TickEvent.ClientTickEvent e) {
        if (mc.thePlayer == null) {
            SAOMod.IS_SPRINTING = false;
            SAOMod.IS_SNEAKING = false;
        } else if (mc.inGameHasFocus) {
            if (SAOMod.IS_SPRINTING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (SAOMod.IS_SNEAKING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void chatEvent(ClientChatReceivedEvent evt) {
        if (Command.processCommand(evt.message.getUnformattedText())) evt.setCanceled(true);// TODO: add pm feature and PT chat
    }

}
