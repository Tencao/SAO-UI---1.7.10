package com.tencao.saoui;

import com.tencao.saoui.util.*;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

@SideOnly(Side.CLIENT)
public class SAOIngameGUI extends GuiIngameForge {

    private final int HPXP_OFFSET_ORIG_R = 3; // Used to fine-tune UI elements positioning
    private final int HPXP_OFFSET_ORIG_D = 1;
    private final int HPXP_OFFSET_ALO_R = 0;
    private final int HPXP_OFFSET_ALO_D = 6;
    private FontRenderer fontRenderer;
    private RenderGameOverlayEvent eventParent;
    private String username;
    private int maxNameWidth;
    private int usernameBoxes;
    private int offsetUsername;
    private ScaledResolution res = null;
    private float time;
    private int healthBoxes;

    public SAOIngameGUI(Minecraft mc) {
        super(mc);
    }

    @Override
    public void renderGameOverlay(float partialTicks, boolean hasScreen, int mouseX, int mouseY) {
        fontRenderer = mc.fontRenderer;
        username = mc.thePlayer.getDisplayName();
        maxNameWidth = fontRenderer.getStringWidth(username);
        usernameBoxes = 1 + (maxNameWidth + 4) / 5;
        offsetUsername = 18 + usernameBoxes * 5;
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        eventParent = new RenderGameOverlayEvent(partialTicks, res, mouseX, mouseY);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        time = partialTicks;

        SAOGL.glBlend(true);
        super.renderGameOverlay(partialTicks, hasScreen, mouseX, mouseY);

        if (SAOOption.FORCE_HUD.getValue() && !this.mc.playerController.shouldDrawHUD() && this.mc.renderViewEntity instanceof EntityPlayer) {
            if (renderHealth) renderHealth(width, height);
            if (renderArmor) renderArmor(width, height);
            if (renderFood) renderFood(width, height);
            if (renderHealthMount) renderHealthMount(width, height);
            if (renderAir) renderAir(width, height);
            renderJumpBar = false;
            renderArmor = false;
            renderHealthMount = false;
            mc.entityRenderer.setupOverlayRendering();
        } // Basically adding what super doesn't render by default
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderCrosshairs(int width, int height) {
        SAOGL.glBlend(true);
        if (SAOOption.CROSS_HAIR.getValue()) super.renderCrosshairs(width, height);
    }


    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderArmor(int width, int height) {
        if (replaceEvent(ARMOR)) return;
        // Nothing happens here
        post(ARMOR);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderHotbar(int width, int height, float partialTicks) {
        if (replaceEvent(HOTBAR)) return;

        SAOGL.glAlpha(true);
        SAOGL.glBlend(true);
        SAOGL.glColor(1, 1, 1, 1);

        final InventoryPlayer inv = mc.thePlayer.inventory;
        final int slotCount = 9;
        if (SAOOption.DEFAULT_HOTBAR.getValue()) super.renderHotbar(width, height, partialTicks);
        else if (SAOOption.ALT_HOTBAR.getValue()) {
            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);

            for (int i = 0; i < slotCount; i++) {
                SAOGL.glColorRGBA(i == inv.currentItem ? 0xE0BE62AA : 0xCDCDCDAA);
                SAOGL.glTexturedRect(width / 2 - 91 - 1 + i * 20, height - 22 - 1, zLevel, 0, 25, 20, 20);
            }

            SAOGL.glColor(1, 1, 1, 1);

            SAOGL.glBlend(false);
            SAOGL.glRescaleNormal(true);

            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < slotCount; i++) {
                int x = width / 2 - 92 + i * 20 + 2;
                int z = height - 17 - 3;
                //super.renderHotbarItem(i, res.getScaledWidth() - 22, slotsY + 2 + (22 * i), partialTicks, mc.thePlayer);
                super.renderInventorySlot(i, x, z, partialTicks);
            }
        } else {
            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);
            final int slotsY = (height - (slotCount * 22)) / 2;

            for (int i = 0; i < slotCount; i++) {
                SAOGL.glColorRGBA(i == inv.currentItem ? 0xE0BE62AA : 0xCDCDCDAA);
                SAOGL.glTexturedRect(width - 24, slotsY + (22 * i), zLevel, 0, 25, 20, 20);
            }

            SAOGL.glColor(1, 1, 1, 1);

            SAOGL.glBlend(false);
            SAOGL.glRescaleNormal(true);

            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < slotCount; i++) {
                //super.renderHotbarItem(i, res.getScaledWidth() - 22, slotsY + 2 + (22 * i), partialTicks, mc.thePlayer);
                super.renderInventorySlot(i, width - 22, slotsY + 2 + (22 * i), partialTicks);
            }
        }

        RenderHelper.disableStandardItemLighting();

        SAOGL.glRescaleNormal(false);

        post(HOTBAR);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderAir(int width, int height) {
        if (replaceEvent(AIR)) return;
        // Linked to renderHealth
        post(AIR);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHealth(int width, int height) {
        if (replaceEvent(HEALTH)) return;
        mc.mcProfiler.startSection("health");

        SAOGL.glAlpha(true);
        SAOGL.glBlend(true);

        SAOGL.glColor(1, 1, 1, 0.75F);
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);
        SAOGL.glTexturedRect(2, 2, zLevel, 0, 0, 16, 15);

        SAOGL.glTexturedRect(18, 2, zLevel, usernameBoxes * 5, 15, 16, 0, 5, 15);
        SAOGL.glString(fontRenderer, username, 18, 3 + (15 - fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);

        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);
        SAOGL.glColor(1, 1, 1, 1);

        final int healthBarWidth = 234;

        SAOGL.glTexturedRect(offsetUsername, 2, zLevel, 21, 0, healthBarWidth, 15);

        final int healthWidth = 216;
        final int healthHeight = SAOOption.ORIGINAL_UI.getValue() ? 9 : 4;

        final int healthValue = (int) (StaticPlayerHelper.getHealth(mc, mc.thePlayer, time) / StaticPlayerHelper.getMaxHealth(mc.thePlayer) * healthWidth);
        SAOHealthStep.getStep(mc, mc.thePlayer, time).glColor();

        int stepOne = (int) (healthWidth / 3.0F - 3);
        int stepTwo = (int) (healthWidth / 3.0F * 2.0F - 3);
        int stepThree = healthWidth - 3;

        if (SAOOption.ORIGINAL_UI.getValue()) {
            int h = healthHeight;
            for (int i = 0; i < healthValue; i++) {
                SAOGL.glTexturedRect(offsetUsername + 1 + i, 5, zLevel, (healthHeight - h), 15, 1, h);

                if (((i >= 105) && (i <= 110)) || (i >= healthValue - h)) {
                    h--;

                    if (h <= 0) break;
                }
            }
        } else {
            int h = healthValue <= 12 ? 12 - healthValue : 0;
            int o = healthHeight;
            for (int i = 0; i < healthValue; i++) {
                SAOGL.glTexturedRect(offsetUsername + 4 + i, 6 + (healthHeight - o), zLevel, h, 236 + (healthHeight - o), 1, o);
                if (healthValue < healthWidth && i >= healthValue - 3) o--;

                if (healthValue <= 12) {
                    h++;
                    if (h > 12) break;
                } else if ((i >= stepOne && i <= stepOne + 3) || (i >= stepTwo && i <= stepTwo + 3) || (i >= stepThree)) {
                    h++;

                    if (h > 12) break;
                }
            }

            if (healthValue >= stepTwo && healthValue < stepThree)
                SAOGL.glTexturedRect(offsetUsername + healthValue, 6, zLevel, 11, 245, 7, 4);
            if (healthValue >= stepOne && healthValue < stepTwo + 4)
                SAOGL.glTexturedRect(offsetUsername + healthValue, 6, zLevel, 4, 245, 7, 4);
            if (healthValue < stepOne + 4 && healthValue > 0) {
                SAOGL.glTexturedRect(offsetUsername + healthValue + 2, 6, zLevel, 0, 245, 4, 4);
                for (int i = 0; i < healthValue - 2; i++)
                    SAOGL.glTexturedRect(offsetUsername + i + 4, 6, zLevel, 0, 245, 4, 4);
            }

        }

        mc.mcProfiler.endSection();
        post(HEALTH);

        renderFood(healthWidth, healthHeight, offsetUsername, stepOne, stepTwo, stepThree);

        if (!SAOOption.REMOVE_HPXP.getValue()) {
            String absorb = SAOOption.ALT_ABSORB_POS.getValue() ? "" : " ";
            if (mc.thePlayer.getAbsorptionAmount() > 0) {
                absorb += "(+" + (int) Math.ceil(mc.thePlayer.getAbsorptionAmount());
                absorb += ')';
                absorb += SAOOption.ALT_ABSORB_POS.getValue() ? ' ' : "";
            }

            final String healthStr = String.valueOf((SAOOption.ALT_ABSORB_POS.getValue() ? absorb : "") + (int) Math.ceil(StaticPlayerHelper.getHealth(mc, mc.thePlayer, time))) + (SAOOption.ALT_ABSORB_POS.getValue() ? "" : absorb) + " / " + String.valueOf((int) Math.ceil(StaticPlayerHelper.getMaxHealth(mc.thePlayer)));
            final int healthStrWidth = fontRenderer.getStringWidth(healthStr);

            final int absStart = healthStr.indexOf('(');
            String[] strs;
            if (absStart >= 0) strs = new String[]{
                    healthStr.substring(0, absStart),
                    healthStr.substring(absStart, healthStr.indexOf(')') + 1),
                    healthStr.substring(healthStr.indexOf(')') + 1)
            };
            else strs = new String[]{"", "", healthStr};

            healthBoxes = (healthStrWidth + 4) / 5;

            final int offsetR = SAOOption.ORIGINAL_UI.getValue() ? HPXP_OFFSET_ORIG_R : HPXP_OFFSET_ALO_R;
            final int offsetD = SAOOption.ORIGINAL_UI.getValue() ? HPXP_OFFSET_ORIG_D : HPXP_OFFSET_ALO_D;
            SAOGL.glColor(1, 1, 1, 0.95F);
            SAOGL.glTexturedRect(offsetUsername + 113 + offsetR, 13 + offsetD, zLevel, 60, 15, 5, 13);
            SAOGL.glTexturedRect(offsetUsername + 118 + offsetR, 13 + offsetD, zLevel, healthBoxes * 5, 13, 66, 15, 5, 13);
            SAOGL.glTexturedRect(offsetUsername + 118 + offsetR + healthBoxes * 5, 13 + +offsetD, zLevel, 70, 15, 5, 13);

            SAOGL.glString(strs[0], offsetUsername + 118 + offsetR, 16 + offsetD, 0xFFFFFFFF);
            SAOGL.glString(strs[1], offsetUsername + 118 + offsetR + fontRenderer.getStringWidth(strs[0]), 16 + offsetD, 0xFF55FFFF);
            SAOGL.glString(strs[2], offsetUsername + 118 + offsetR + fontRenderer.getStringWidth(strs[0] + strs[1]), 16 + offsetD, 0xFFFFFFFF);
        }

        SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);

        mc.mcProfiler.startSection("effects");

        final int offsetForEffects = offsetUsername + healthBarWidth - 4;
        final List<SAOEffect> effects = SAOEffect.getEffects(mc.thePlayer);

        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);

        for (int i = 0; i < effects.size(); i++) {
            effects.get(i).glDraw(offsetForEffects + i * 11, 2, zLevel);
        }

        mc.mcProfiler.endSection();

        if (PartyHelper.instance().isEffective()) {
            mc.mcProfiler.startSection("party");

            final List<EntityPlayer> players = StaticPlayerHelper.listOnlinePlayers(mc);

            if (players.contains(mc.thePlayer)) players.remove(mc.thePlayer);

            SAOGL.glAlpha(true);
            SAOGL.glBlend(true);

            int index = 0;
            final int baseY = 35;
            final int h = 15;
            for (final EntityPlayer player : players) {
                final String playerName = player.getDisplayName();

                if (!PartyHelper.instance().isMember(playerName)) continue;

                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);

                SAOGL.glTexturedRect(2, baseY + index * h, zLevel, 85, 15, 10, 13);
                SAOGL.glTexturedRect(13, baseY + index * h, zLevel, 80, 15, 5, 13);

                final int nameWidth = fontRenderer.getStringWidth(playerName);
                final int nameBoxes = (nameWidth + 4) / 5 + 1;

                if (nameWidth > maxNameWidth) maxNameWidth = nameWidth;

                SAOGL.glTexturedRect(18, baseY + index * h, zLevel, nameBoxes * 5, 13, 65, 15, 5, 13);

                int offset = 18 + nameBoxes * 5;

                SAOGL.glTexturedRect(offset, baseY + index * h, zLevel, 40, 28, 100, 13);

                final int hpWidth = 97;
                final int hpHeight = 3;

                final int hpValue = (int) (StaticPlayerHelper.getHealth(mc, player, time) / StaticPlayerHelper.getMaxHealth(player) * hpWidth);
                SAOHealthStep.getStep(mc, player, time).glColor();

                int hp = hpHeight;
                for (int j = 0; j < hpValue; j++) {
                    SAOGL.glTexturedRect(offset + 1 + j, baseY + 5 + index * h, zLevel, (hpHeight - hp), 15, 1, hp);

                    if (j >= hpValue - hp) {
                        hp--;

                        if (hp <= 0) break;
                    }
                }

                offset += 100;

                SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);
                SAOGL.glTexturedRect(offset, baseY + index * h, zLevel, 70, 15, 5, 13);
                SAOGL.glString(playerName, 18, baseY + 1 + index * h + (13 - fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);

                index++;
            }

            mc.mcProfiler.endSection();
        }
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderFood(int width, int height) {
        // See below, called by renderHealth
    }

    private void renderFood(int healthWidth, int healthHeight, int offsetUsername, int stepOne, int stepTwo, int stepThree) {
        if (replaceEvent(FOOD)) return;
        mc.mcProfiler.startSection("food");
        final int foodValue = (int) (StaticPlayerHelper.getHungerFract(mc, mc.thePlayer, time) * healthWidth);
        int h = foodValue < 12 ? 12 - foodValue : 0;
        int o = healthHeight;
        SAOGL.glColorRGBA(0x8EE1E8);
        for (int i = 0; i < foodValue; i++) {
            SAOGL.glTexturedRect(offsetUsername + i + 4, 9, zLevel, h, 240, 1, o);
            if (foodValue < healthWidth && i >= foodValue - 3) o--;

            if (foodValue <= 12) {
                h++;
                if (h > 12) break;
            } else if ((i >= stepOne && i <= stepOne + 3) || (i >= stepTwo && i <= stepTwo + 3) || (i >= stepThree)) {
                h++;

                if (h > 12) break;
            }
        }

        if (foodValue >= stepTwo && foodValue < stepThree)
            SAOGL.glTexturedRect(offsetUsername + foodValue, 9, zLevel, 11, 249, 7, 4);
        if (foodValue >= stepOne && foodValue < stepTwo + 4)
            SAOGL.glTexturedRect(offsetUsername + foodValue, 9, zLevel, 4, 249, 7, 4);
        if (foodValue < stepOne + 4 && foodValue > 0) {
            SAOGL.glTexturedRect(offsetUsername + foodValue + 2, 9, zLevel, 0, 249, 4, 4);
            for (int i = 0; i < foodValue - 2; i++)
                SAOGL.glTexturedRect(offsetUsername + i + 4, 9, zLevel, 0, 249, 4, 4);
        }

        mc.mcProfiler.endSection();
        post(FOOD);
    }


    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderExperience(int width, int height) {
        if (SAOOption.REMOVE_HPXP.getValue() || replaceEvent(EXPERIENCE)) return;
        if (!SAOOption.FORCE_HUD.getValue() && !this.mc.playerController.shouldDrawHUD()) return;
        mc.mcProfiler.startSection("expLevel");

        final int offsetR = SAOOption.ORIGINAL_UI.getValue() ? HPXP_OFFSET_ORIG_R : HPXP_OFFSET_ALO_R;
        final int offsetD = SAOOption.ORIGINAL_UI.getValue() ? HPXP_OFFSET_ORIG_D : HPXP_OFFSET_ALO_D;
        final int offsetHealth = offsetUsername + 113 + (healthBoxes + 2) * 5 + offsetR;
        final String levelStr = StatCollector.translateToLocal("displayLvShort") + ": " + String.valueOf(mc.thePlayer.experienceLevel);
        final int levelStrWidth = fontRenderer.getStringWidth(levelStr);
        final int levelBoxes = (levelStrWidth + 4) / 5;

        //SAOGL.glAlpha(true);
        SAOGL.glBlend(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.gui : SAOResources.guiCustom);
        SAOGL.glTexturedRect(offsetHealth, 13 + offsetD, zLevel, 5, 13, 66, 15, 2, 13);
        SAOGL.glTexturedRect(offsetHealth + 5, 13 + offsetD, zLevel, levelBoxes * 5, 13, 66, 15, 5, 13);
        SAOGL.glTexturedRect(offsetHealth + (1 + levelBoxes) * 5, 13 + offsetD, zLevel, 5, 13, 78, 15, 3, 13);
        SAOGL.glString(levelStr, offsetHealth + 5, 16 + offsetD, 0xFFFFFFFF);

        mc.mcProfiler.endSection();
        post(EXPERIENCE);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderJumpBar(int width, int height) {
        if (replaceEvent(JUMPBAR)) return;
        // Nothing happens here (not implemented yet)
        post(JUMPBAR);
    }

    /**
     * Overrides and cancels any event registering what we
     * want to, before we do, then forcing ours to the highest priority.
     *
     * @param el the element type to override
     * @return whether caller should return
     */

    private boolean replaceEvent(ElementType el) {
        if (eventParent.type == el && eventParent.isCancelable()) {
            eventParent.setCanceled(true);
            pre(el);
            return true;
        }
        return false;
    }

    @Override
    protected void renderHealthMount(int width, int height) {
        //EntityPlayer player = (EntityPlayer)mc.getRenderViewEntity();
        EntityPlayer player = (EntityPlayer) mc.renderViewEntity;
        Entity tmp = player.ridingEntity;
        if (!(tmp instanceof EntityLivingBase)) return;

        if (replaceEvent(HEALTHMOUNT)) return;
        // Not implemented yet
        post(HEALTHMOUNT);
    }

    // c/p from GuiIngameForge


    private void pre(ElementType type) {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }

    private void post(ElementType type) {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }

    public boolean backgroundClicked(int cursorX, int cursorY, int button) {
        return SAOOption.DEFAULT_UI.getValue();
    }

    public void viewMessageAuto() {
        // TODO: implement method
        System.out.println("SAOIngameGUI#viewMessageAuto()");
    }
}
