package com.tencao.sao;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.tencao.sao.ui.SAOElementGUI;
import com.tencao.sao.ui.SAOIconGUI;
import com.tencao.sao.ui.SAOMessageGUI;
import com.tencao.sao.util.SAOAction;
import com.tencao.sao.util.SAOColor;
import com.tencao.sao.util.SAOEffect;
import com.tencao.sao.util.SAOGL;
import com.tencao.sao.util.SAOHealthStep;
import com.tencao.sao.util.SAOID;
import com.tencao.sao.util.SAOIcon;
import com.tencao.sao.util.SAOOption;
import com.tencao.sao.util.SAOParentGUI;
import com.tencao.sao.util.SAOResources;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

@SideOnly(Side.CLIENT)
public class SAOIngameGUI extends GuiIngame {

	private final SAONewChatGUI chatLine;
	private final Queue<String[]> messages;
	public int hpBarOffset;
	//private final SAOIconGUI receivedMessage;

	private boolean openedMessage;

	public SAOIngameGUI(Minecraft mc) {
		super(mc);
		chatLine = new SAONewChatGUI(this, mc, persistantChatGUI);
		messages = new ArrayDeque<String[]>();
		//receivedMessage = new SAOIconGUI(null, SAOID.MESSAGE, 0, 0, SAOIcon.MESSAGE_RECEIVED);
		openedMessage = false;
		
		//receivedMessage.visibility = 0;
		//receivedMessage.highlight = true;
	}

	public void renderGameOverlay(float time, boolean p_73830_2_, int cursorX, int cursorY) {
		if (SAOOption.DEFAULT_UI.value) {
			super.renderGameOverlay(time, p_73830_2_, cursorX, cursorY);
			return;
		}
	}
	

    private void drawOverlay(float time) {
        final ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        final int width = resolution.getScaledWidth();
        final int height = resolution.getScaledHeight();

        final FontRenderer fontRenderer = mc.fontRenderer;

        mc.entityRenderer.setupOverlayRendering();

        SAOGL.glBlend(true);

        if (Minecraft.isFancyGraphicsEnabled()) {
        	renderVignette(mc.thePlayer.getBrightness(time), width, height);
        } else {
            SAOGL.tryBlendFuncSeparate(770, 771, 1, 0);
        }

        final ItemStack helmet = mc.thePlayer.inventory.armorItemInSlot(3);

        if (mc.gameSettings.thirdPersonView == 0 && helmet != null && helmet.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
        	renderPumpkinBlur(width, height);
        }

        if (!mc.thePlayer.isPotionActive(Potion.confusion)) {
            final float portalEffect = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * time;

            if (portalEffect > 0.0F) {
            	func_130015_b(portalEffect, width, height);
            }
        }

        SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);
        SAOGL.glBindTexture(icons);
        SAOGL.glBlend(true);

        if (SAOOption.CROSS_HAIR.value) {
            mc.mcProfiler.startSection("cross-hair");

            mc.getTextureManager().bindTexture(icons);
	        GL11.glEnable(GL11.GL_BLEND);
	        OpenGlHelper.glBlendFunc(775, 769, 1, 0);

            drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);

            OpenGlHelper.glBlendFunc(770, 771, 1, 0);

            mc.mcProfiler.endSection();
        }

        SAOGL.glStartUI(mc);

        if (mc.playerController.shouldDrawHUD() || SAOOption.FORCE_HUD.value) {
            drawHUD(time, fontRenderer);
        }

        SAOGL.glBlend(false);
        float f2;
        int k;
        int j1;

        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");

            SAOGL.glDepth(false);
            SAOGL.glAlpha(false);

            j1 = this.mc.thePlayer.getSleepTimer();
            f2 = (float) j1 / 100.0F;

            if (f2 > 1.0F) {
                f2 = 1.0F - (float) (j1 - 100) / 10.0F;
            }

            k = (int) (220.0F * f2) << 24 | 1052704;
            drawRect(0, 0, width, height, k);

            SAOGL.glAlpha(true);
            SAOGL.glDepth(true);

            this.mc.mcProfiler.endSection();
        }

        SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);

        mc.mcProfiler.startSection("inventorySlots");

        SAOGL.glAlpha(true);

        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);
        SAOGL.glColor(1, 1, 1, 1);

        final InventoryPlayer inv = mc.thePlayer.inventory;
        final int slotCount = 9;
        final int slotsY = (height - (slotCount * 22)) / 2;

        for (int i = 0; i < slotCount; i++) {
            SAOGL.glColorRGBA(i == inv.currentItem ? 0xE0BE62FF : 0xCDCDCDFF);
            SAOGL.glTexturedRect(width - 24, slotsY + (22 * i), zLevel, 0, 25, 20, 20);
        }

        SAOGL.glColor(1, 1, 1, 1);

        SAOGL.glRescaleNormal(true);

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < slotCount; i++) {
            super.renderInventorySlot(i, width - 22, slotsY + 2 + (22 * i), time);
        }

        RenderHelper.disableStandardItemLighting();

        SAOGL.glRescaleNormal(false);
        SAOGL.glBlend(false);

        mc.mcProfiler.endSection();

        SAOGL.glColor(1, 1, 1, 1);

        if (mc.gameSettings.heldItemTooltips) {
            drawTooltips(fontRenderer, width, height);
        }

        if (mc.gameSettings.showDebugInfo) {
            mc.mcProfiler.startSection("debug");
            
            GL11.glPushMatrix();
            
            fontRenderer.drawStringWithShadow("Minecraft " + SAOMod.NAME + " " + SAOMod.VERSION + " (" + this.mc.debug + ")", 2, 2 + hpBarOffset, 16777215);
            fontRenderer.drawStringWithShadow(mc.debugInfoRenders(), 2, 12 + hpBarOffset, 16777215);
            fontRenderer.drawStringWithShadow(mc.getEntityDebug(), 2, 22 + hpBarOffset, 16777215);
            fontRenderer.drawStringWithShadow(mc.debugInfoEntities(), 2, 32 + hpBarOffset, 16777215);
            fontRenderer.drawStringWithShadow(mc.getWorldProviderName(), 2, 42 + hpBarOffset, 16777215);
            
            long i5 = Runtime.getRuntime().maxMemory();
            long j5 = Runtime.getRuntime().totalMemory();
            long k5 = Runtime.getRuntime().freeMemory();
            long l5 = j5 - k5;
            
            String s = "Used memory: " + l5 * 100L / i5 + "% (" + l5 / 1024L / 1024L + "MB) of " + i5 / 1024L / 1024L + "MB";
            int i3 = 14737632;
            
            drawString(fontRenderer, s, width - fontRenderer.getStringWidth(s) - 2, 2, 14737632);
            
            s = "Allocated memory: " + j5 * 100L / i5 + "% (" + j5 / 1024L / 1024L + "MB)";
            
            drawString(fontRenderer, s, width - fontRenderer.getStringWidth(s) - 2, 12, 14737632);
            
            int offset = 22;
            
            for (final String brd : FMLCommonHandler.instance().getBrandings(false)) {
                drawString(fontRenderer, brd, width - fontRenderer.getStringWidth(brd) - 2, offset+=10, 14737632);
            }
            
            int j3 = MathHelper.floor_double(mc.thePlayer.posX);
            int k3 = MathHelper.floor_double(mc.thePlayer.posY);
            int l3 = MathHelper.floor_double(mc.thePlayer.posZ);
            
            drawString(fontRenderer, String.format("x: %.5f (%d) // c: %d (%d)", new Object[] {Double.valueOf(mc.thePlayer.posX), Integer.valueOf(j3), Integer.valueOf(j3 >> 4), Integer.valueOf(j3 & 15)}), 2, 64 + hpBarOffset, 14737632);
            drawString(fontRenderer, String.format("y: %.3f (feet pos, %.3f eyes pos)", new Object[] {Double.valueOf(mc.thePlayer.boundingBox.minY), Double.valueOf(mc.thePlayer.posY)}), 2, 72 + hpBarOffset, 14737632);
            drawString(fontRenderer, String.format("z: %.5f (%d) // c: %d (%d)", new Object[] {Double.valueOf(mc.thePlayer.posZ), Integer.valueOf(l3), Integer.valueOf(l3 >> 4), Integer.valueOf(l3 & 15)}), 2, 80 + hpBarOffset, 14737632);
            
            int i4 = MathHelper.floor_double((double)(mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            
            drawString(fontRenderer, "f: " + i4 + " (" + Direction.directions[i4] + ") / " + MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw), 2, 88 + hpBarOffset, 14737632);

            if (mc.theWorld != null && mc.theWorld.blockExists(j3, k3, l3)) {
                final Chunk chunk = mc.theWorld.getChunkFromBlockCoords(j3, l3);
                
                drawString(fontRenderer, "lc: " + (chunk.getTopFilledSegment() + 15) + " b: " + chunk.getBiomeGenForWorldCoords(j3 & 15, l3 & 15, mc.theWorld.getWorldChunkManager()).biomeName + " bl: " + chunk.getSavedLightValue(EnumSkyBlock.Block, j3 & 15, k3, l3 & 15) + " sl: " + chunk.getSavedLightValue(EnumSkyBlock.Sky, j3 & 15, k3, l3 & 15) + " rl: " + chunk.getBlockLightValue(j3 & 15, k3, l3 & 15, 0), 2, 96 + hpBarOffset, 14737632);
            }

            drawString(fontRenderer, String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", new Object[] {Float.valueOf(this.mc.thePlayer.capabilities.getWalkSpeed()), Float.valueOf(mc.thePlayer.capabilities.getFlySpeed()), Boolean.valueOf(mc.thePlayer.onGround), Integer.valueOf(mc.theWorld.getHeightValue(j3, l3))}), 2, 104 + hpBarOffset, 14737632);

            if (mc.entityRenderer != null && mc.entityRenderer.isShaderActive()) {
                drawString(fontRenderer, String.format("shader: %s", new Object[] {mc.entityRenderer.getShaderGroup().getShaderGroupName()}), 2, 112 + hpBarOffset, 14737632);
            }

            GL11.glPopMatrix();
            
            mc.mcProfiler.endSection();
        }

        int l;


        ScoreObjective scoreobjective = mc.theWorld.getScoreboard().func_96539_a(1);

        if (scoreobjective != null) {
            GL11.glTranslatef(-30, 0, 0);

            func_96136_a(scoreobjective, height, width - 30, fontRenderer);

            GL11.glTranslatef(30, 0, 0);
        }

        SAOGL.glBlend(true);
        SAOGL.tryBlendFuncSeparate(770, 771, 1, 0);
        SAOGL.glAlpha(false);

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, (float) (height - 48), 0.0F);

        mc.mcProfiler.startSection("chat");
        chatLine.drawChat(updateCounter);
        mc.mcProfiler.endSection();

        GL11.glPopMatrix();

        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        scoreobjective = scoreboard.func_96539_a(0);

        if (this.mc.gameSettings.keyBindPlayerList.getIsKeyPressed() && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.playerInfoList.size() > 1 || scoreobjective != null)) {
			int i2, j2, k2, i3, j3, k3;
			
            this.mc.mcProfiler.startSection("playerList");
            NetHandlerPlayClient nethandlerplayclient = this.mc.thePlayer.sendQueue;
            List list = nethandlerplayclient.playerInfoList;
            i2 = nethandlerplayclient.currentServerMaxPlayers;
            j2 = i2;

            for (k2 = 1; j2 > 20; j2 = (i2 + k2 - 1) / k2)
            {
                ++k2;
            }

            int i6 = 300 / k2;

            if (i6 > 150)
            {
                i6 = 150;
            }

            int l2 = (width - k2 * i6) / 2;
            int b0 = (height - 10 - 9 * j2);
            drawRect(l2 - 1, b0 - 1, l2 + i6 * k2, b0 + 9 * j2, Integer.MIN_VALUE);

            for (i3 = 0; i3 < i2; ++i3)
            {
                j3 = l2 + i3 % k2 * i6;
                k3 = b0 + i3 / k2 * 9;
                drawRect(j3, k3, j3 + i6 - 1, k3 + 8, 553648127);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                if (i3 < list.size())
                {
                    GuiPlayerInfo guiplayerinfo = (GuiPlayerInfo)list.get(i3);
                    ScorePlayerTeam scoreplayerteam = this.mc.theWorld.getScoreboard().getPlayersTeam(guiplayerinfo.name);
                    String s4 = ScorePlayerTeam.formatPlayerName(scoreplayerteam, guiplayerinfo.name);
                    fontRenderer.drawStringWithShadow(s4, j3, k3, 16777215);

                    if (scoreobjective != null)
                    {
                        int j4 = j3 + fontRenderer.getStringWidth(s4) + 5;
                        int k4 = j3 + i6 - 12 - 5;

                        if (k4 - j4 > 5)
                        {
                            Score score = scoreobjective.getScoreboard().func_96529_a(guiplayerinfo.name, scoreobjective);
                            String s1 = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                            fontRenderer.drawStringWithShadow(s1, k4 - fontRenderer.getStringWidth(s1), k3, 16777215);
                        }
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.mc.getTextureManager().bindTexture(icons);
                    byte b1 = 0;
                    boolean flag3 = false;
                    byte b2;

                    if (guiplayerinfo.responseTime < 0)
                    {
                        b2 = 5;
                    }
                    else if (guiplayerinfo.responseTime < 150)
                    {
                        b2 = 0;
                    }
                    else if (guiplayerinfo.responseTime < 300)
                    {
                        b2 = 1;
                    }
                    else if (guiplayerinfo.responseTime < 600)
                    {
                        b2 = 2;
                    }
                    else if (guiplayerinfo.responseTime < 1000)
                    {
                        b2 = 3;
                    }
                    else
                    {
                        b2 = 4;
                    }

                    this.zLevel += 100.0F;
                    this.drawTexturedModalRect(j3 + i6 - 12, k3, 0 + b1 * 10, 176 + b2 * 8, 10, 8);
                    this.zLevel -= 100.0F;
                }
            }
        }

        SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glDisable(GL11.GL_LIGHTING);

        SAOGL.glAlpha(true);
/*
        if (!messages.isEmpty()) {
            receivedMessage.x = 8;
            receivedMessage.y = height * 3 / 4;
            receivedMessage.visibility = 1;
        } else {
            receivedMessage.visibility = 0;
        }
        if (receivedMessage.visibility > 0) {
            receivedMessage.update(mc);
            receivedMessage.draw(mc, Mouse.getX(), Mouse.getY());
            final String numberString = String.valueOf(messages.size());
            SAOGL.glString(numberString, receivedMessage.getX(false), receivedMessage.getY(false), SAOColor.HOVER_FONT_COLOR, true);
        }*/

        SAOGL.glEndUI(mc);
    }


    private void drawHUD(float time, FontRenderer fontRenderer) {
        final String username = mc.thePlayer.getDisplayName();
        int maxNameWidth = fontRenderer.getStringWidth(username);

        mc.mcProfiler.startSection("username");

        SAOGL.glColor(1, 1, 1, 1);
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

        //SAOGL.glTexturedRect(2, 2, zLevel, 0, 0, 10, 15); // I'll leave these old ones in there
        //SAOGL.glTexturedRect(13, 2, zLevel, 10, 0, 5, 15);
        SAOGL.glTexturedRect(2, 2, zLevel, 0, 0, 16, 15);

        final int usernameBoxes = 1 + (maxNameWidth + 4) / 5;

        SAOGL.glTexturedRect(18, 2, zLevel, usernameBoxes * 5, 15, 16, 0, 5, 15);
        SAOGL.glString(fontRenderer, username, 18, 3 + (15 - fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);

        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);
        SAOGL.glColor(1, 1, 1, 1);

        mc.mcProfiler.endSection();

        mc.mcProfiler.startSection("healthBar");

        final int offsetUsername = 18 + usernameBoxes * 5;
        final int healthBarWidth = 234;

        SAOGL.glTexturedRect(offsetUsername, 2, zLevel, 21, 0, healthBarWidth, 15);

        final int healthWidth = 216;
        final int healthHeight = SAOOption.ORIGINAL_UI.value? 9 : 4;

        final int healthValue = (int) (SAOMod.getHealth(mc, mc.thePlayer, time) / SAOMod.getMaxHealth(mc.thePlayer) * healthWidth);
        SAOHealthStep.getStep(mc, mc.thePlayer, time).glColor();

        if (SAOOption.ORIGINAL_UI.value) {
            int h = healthHeight;
            for (int i = 0; i < healthValue; i++) {
                SAOGL.glTexturedRect(offsetUsername + 1 + i, 5, zLevel, (healthHeight - h), 15, 1, h);

                if (((i >= 105) && (i <= 110)) || (i >= healthValue - h)) {
                    h--;

                    if (h <= 0) {
                        break;
                    }
                }
            }
        } else {
            int h = healthValue <= 12? 12 - healthValue: 0;
            int o = healthHeight;
            int stepOne = (int) (healthWidth / 3.0F - 3);
            int stepTwo = (int) (healthWidth / 3.0F * 2.0F - 3);
            int stepThree = healthWidth - 3;
            for (int i = 0; i < healthValue; i++) {
                SAOGL.glTexturedRect(offsetUsername + 4 + i, 6 + (healthHeight - o), zLevel, h, 236 + (healthHeight - o), 1, o);
                if (healthValue < healthWidth && i >= healthValue - 3) o--;

                if (healthValue <= 12) {
                    h++;
                    if (h > 12) break;
                } else if ((i >= stepOne && i <= stepOne + 3) || (i >= stepTwo && i <= stepTwo + 3) || (i >= stepThree)) {
                    h++;

                    if (h > 12) {
                        break;
                    }
                }
            }

            if (healthValue >= stepTwo && healthValue < stepThree)
                SAOGL.glTexturedRect(offsetUsername + healthValue, 6, zLevel, 11, 245, 7, 4);
            if (healthValue >= stepOne && healthValue < stepTwo + 4)
                SAOGL.glTexturedRect(offsetUsername + healthValue, 6, zLevel, 4, 245, 7, 4);
            if (healthValue < stepOne + 4 && healthValue > 0) {
                SAOGL.glTexturedRect(offsetUsername + healthValue + 2, 6, zLevel, 0, 245, 4, 4);
                for (int i = 0; i < healthValue - 2; i++) SAOGL.glTexturedRect(offsetUsername + i  + 4, 6, zLevel, 0, 245, 4, 4);
            }

            final int foodValue = (int) (SAOMod.getHungerFract(mc, mc.thePlayer, time) * healthWidth);
            h = foodValue < 12? 12 - foodValue: 0;
            o = healthHeight;
            SAOGL.glColorRGBA(0x8EE1E8);
            for (int i = 0; i < foodValue; i++) {
                SAOGL.glTexturedRect(offsetUsername + i + 4, 9, zLevel, h, 240, 1, o);
                if (foodValue < healthWidth && i >= foodValue - 3) o--;

                if (foodValue <= 12) {
                    h++;
                    if (h > 12) break;
                } else if ((i >= stepOne && i <= stepOne + 3) || (i >= stepTwo && i <= stepTwo + 3) || (i >= stepThree)) {
                    h++;

                    if (h > 12) {
                        break;
                    }
                }
            }

            if (foodValue >= stepTwo && foodValue < stepThree)
                SAOGL.glTexturedRect(offsetUsername + foodValue, 9, zLevel, 11, 249, 7, 4);
            if (foodValue >= stepOne && foodValue < stepTwo + 4)
                SAOGL.glTexturedRect(offsetUsername + foodValue, 9, zLevel, 4, 249, 7, 4);
            if (foodValue < stepOne + 4 && foodValue > 0) {
                SAOGL.glTexturedRect(offsetUsername + foodValue + 2, 9, zLevel, 0, 249, 4, 4);
                for (int i = 0; i < foodValue - 2; i++) SAOGL.glTexturedRect(offsetUsername + i  + 4, 9, zLevel, 0, 249, 4, 4);
            }
        }


        int healthBoxes = 0;

        if (!SAOOption.REMOVE_HPXP.value) {
            String absorb = SAOOption.ALT_ABSORB_POS.value? "":" ";
            if (mc.thePlayer.getAbsorptionAmount() > 0) {
                absorb += "(+" + (int) Math.ceil(mc.thePlayer.getAbsorptionAmount());
                absorb += ')';
                absorb += SAOOption.ALT_ABSORB_POS.value? ' ':"";
            }

            final String healthStr = String.valueOf((SAOOption.ALT_ABSORB_POS.value? absorb:"") + (int) Math.ceil(SAOMod.getHealth(mc, mc.thePlayer, time))) + (SAOOption.ALT_ABSORB_POS.value? "":absorb) + " / " + String.valueOf((int) Math.ceil(SAOMod.getMaxHealth(mc.thePlayer)));
            final int healthStrWidth = fontRenderer.getStringWidth(healthStr);

            final int absStart = healthStr.indexOf('(');
            String[] strs;
            if (absStart >= 0) strs = new String[]{
                    healthStr.substring(0, absStart),
                    healthStr.substring(absStart, healthStr.indexOf(')') + 1),
                    healthStr.substring(healthStr.indexOf(')') + 1)
            };
            else strs = new String[] {"", "", healthStr};

            healthBoxes = (healthStrWidth + 4) / 5;

            SAOGL.glColor(1, 1, 1, 1);
            SAOGL.glTexturedRect(offsetUsername + 113, 13, zLevel, 60, 15, 5, 13);
            SAOGL.glTexturedRect(offsetUsername + 118, 13, zLevel, healthBoxes * 5, 13, 65, 15, 5, 13);
            SAOGL.glTexturedRect(offsetUsername + 118 + healthBoxes * 5, 13, zLevel, 70, 15, 5, 13);

            SAOGL.glString(strs[0], offsetUsername + 118, 16, 0xFFFFFFFF);
            SAOGL.glString(strs[1], offsetUsername + 118 + fontRenderer.getStringWidth(strs[0]), 16, 0xFF55FFFF);
            SAOGL.glString(strs[2], offsetUsername + 118 + fontRenderer.getStringWidth(strs[0] + strs[1]), 16, 0xFFFFFFFF);
        }

        mc.mcProfiler.endSection();

        if (!mc.thePlayer.capabilities.isCreativeMode) {
            SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);

            mc.mcProfiler.startSection("effects");

            final int offsetForEffects = offsetUsername + healthBarWidth - 4;
            final List<SAOEffect> effects = SAOEffect.getEffects(mc.thePlayer);

            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

            for (int i = 0; i < effects.size(); i++) {
                effects.get(i).glDraw(offsetForEffects + i * 11, 2, zLevel);
            }

            mc.mcProfiler.endSection();
        }

        int hpBarOffset = 26;

        if (SAOMod.isPartyMember(username)) {
            mc.mcProfiler.startSection("party");

            final List<EntityPlayer> players = SAOMod.listOnlinePlayers(mc);

            if (players.contains(mc.thePlayer)) {
                players.remove(mc.thePlayer);
            }

            int index = 0;
            for (final EntityPlayer player : players) {
                final String playerName = player.getDisplayName();

                if (!SAOMod.isPartyMember(playerName)) {
                    continue;
                }

                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

                SAOGL.glTexturedRect(2, 19 + index * 15, zLevel, 85, 15, 10, 13);
                SAOGL.glTexturedRect(13, 19 + index * 15, zLevel, 80, 15, 5, 13);

                final int nameWidth = fontRenderer.getStringWidth(playerName);
                final int nameBoxes = (nameWidth + 4) / 5 + 1;

                if (nameWidth > maxNameWidth) {
                    maxNameWidth = nameWidth;
                }

                SAOGL.glTexturedRect(18, 19 + index * 15, zLevel, nameBoxes * 5, 13, 65, 15, 5, 13);

                int offset = 18 + nameBoxes * 5;

                SAOGL.glTexturedRect(offset, 19 + index * 15, zLevel, 40, 28, 100, 13);

                final int hpWidth = 97;
                final int hpHeight = 3;

                final int hpValue = (int) (SAOMod.getHealth(mc, player, time) / SAOMod.getMaxHealth(player) * hpWidth);
                SAOHealthStep.getStep(mc, player, time).glColor();

                int hp = hpHeight;
                for (int j = 0; j < hpValue; j++) {
                    SAOGL.glTexturedRect(offset + 1 + j, 24 + index * 15, zLevel, (hpHeight - hp), 15, 1, hp);

                    if (j >= hpValue - hp) {
                        hp--;

                        if (hp <= 0) {
                            break;
                        }
                    }
                }

                offset += 100;

                SAOGL.glColor(1, 1, 1, 1);
                SAOGL.glTexturedRect(offset, 19 + index * 15, zLevel, 70, 15, 5, 13);
                SAOGL.glString(playerName, 18, 20 + index * 15 + (13 - fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);

                index++;
            }

            mc.mcProfiler.endSection();

            hpBarOffset += (index * 15);
        }

        if (!SAOOption.REMOVE_HPXP.value) {
            mc.mcProfiler.startSection("expLevel");

            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

            final int offsetHealth = offsetUsername + 113 + (healthBoxes + 2) * 5;

            final String levelStr = StatCollector.translateToLocal("displayLvShort") + ": " + String.valueOf(mc.thePlayer.experienceLevel);
            final int levelStrWidth = fontRenderer.getStringWidth(levelStr);

            final int levelBoxes = (levelStrWidth + 4) / 5;

            SAOGL.glTexturedRect(offsetHealth, 13, zLevel, 60, 15, 5, 13);
            SAOGL.glTexturedRect(offsetHealth + 5, 13, zLevel, levelBoxes * 5, 13, 65, 15, 5, 13);
            SAOGL.glTexturedRect(offsetHealth + (1 + levelBoxes) * 5, 13, zLevel, 75, 15, 5, 13);

            SAOGL.glString(levelStr, offsetHealth + 5, 16, 0xFFFFFFFF);
            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

            mc.mcProfiler.endSection();
        }

    }
    
    private void drawTooltips(FontRenderer fontRenderer, int width, int height) {
        mc.mcProfiler.startSection("toolHighlight");

        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
            String name = this.highlightingItemStack.getDisplayName();

            if (this.highlightingItemStack.hasDisplayName()) {
                name = EnumChatFormatting.ITALIC + name;
            }

            final int x = (width - 4) - fontRenderer.getStringWidth(name);
            final int y = (height - (6 + fontRenderer.FONT_HEIGHT)) - fontRenderer.FONT_HEIGHT;

            int alpha = (int) ((float) this.remainingHighlightTicks * 256.0F / 10.0F);

            if (alpha > 0xFF) {
                alpha = 0xFF;
            }

            if (alpha > 0x00) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);

                SAOGL.glString(name, x, y, 0xFFFFFF00 | (alpha), true);

                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }

        mc.mcProfiler.endSection();
    }

	public GuiNewChat getChatGUI() {
		return chatLine;
	}

	public void onMessage(String username, String message) {
        if (messages.isEmpty()) {
            SAOSound.play(Minecraft.getMinecraft(), SAOSound.MESSAGE);
        }
		messages.add(new String[] { username, message });
	}


    public boolean backgroundClicked(int cursorX, int cursorY, int button) {
        return !SAOOption.DEFAULT_UI.value; //&& (receivedMessage.mouseOver(cursorX, cursorY, button)) && (receivedMessage.mouseReleased(mc, cursorX, cursorY, button)) && openMessage();

    }

    public boolean viewMessageAuto() {
        return messages.size() != 0 && openMessage();

    }
    
    private boolean openMessage() {
        SAOSound.play(Minecraft.getMinecraft(), SAOSound.MENU_POPUP);

        final String[] message = messages.poll();
        mc.displayGuiScreen(SAOWindowViewGUI.viewMessage(message[0], message[1]));
        return true;
    }

}

