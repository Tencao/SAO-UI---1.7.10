package com.tencao.saoui;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.AIR;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ARMOR;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.EXPERIENCE;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.FOOD;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTH;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTHMOUNT;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HOTBAR;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.JUMPBAR;

import java.util.List;

import com.tencao.saoui.util.SAOEffect;
import com.tencao.saoui.util.SAOGL;
import com.tencao.saoui.util.SAOHealthStep;
import com.tencao.saoui.util.SAOOption;
import com.tencao.saoui.util.SAOResources;

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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class SAOIngameGUI extends GuiIngameForge {
    
    private FontRenderer fontRenderer = null;
    private RenderGameOverlayEvent eventParent;
    private static final ResourceLocation WIDGITS      = new ResourceLocation("textures/gui/widgets.png");

    private ScaledResolution res = null;
    private float time;

    
    public SAOIngameGUI(Minecraft mc) {
        super(mc);
    }

	@Override
    public void renderGameOverlay(float partialTicks, boolean hasScreen, int mouseX, int mouseY) {
		res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        eventParent = new RenderGameOverlayEvent(partialTicks, res, mouseX, mouseY);
        fontRenderer = mc.fontRenderer;
        int width = res.getScaledWidth();
    	int height = res.getScaledHeight();

        time = partialTicks;

        SAOGL.glBlend(true);
        super.renderGameOverlay(partialTicks, hasScreen, mouseX, mouseY);
        
        if (SAOOption.FORCE_HUD.value && !this.mc.playerController.shouldDrawHUD() && this.mc.renderViewEntity instanceof EntityPlayer) {
        	if (renderHealth) renderHealth(width, height);
        	if (renderArmor)  renderArmor(width, height);
        	if (renderFood)   renderFood(width, height);
        	if (renderHealthMount) renderHealthMount(width, height);
        	if (renderAir)    renderAir(width, height);
            renderJumpBar = false;
            renderArmor = false;
            renderHealthMount = false;
            renderExperiance = false;
            mc.entityRenderer.setupOverlayRendering();
        } // Basically adding what super doesn't render by default
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderCrosshairs(int width, int height) {
        SAOGL.glBlend(true);
        if (SAOOption.CROSS_HAIR.value) super.renderCrosshairs(width, height);
    }

    
    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderArmor(int width, int height) {
    	// Overrides and cancels any event registering what we want to, before we do, then forcing ours to the highest priority.
		if(eventParent.type == ARMOR) {
			if(eventParent.isCancelable()) {
				eventParent.setCanceled(true);
				pre(ARMOR);
				return;
			}
		}    
        // Nothing happens here
        post(ARMOR);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderHotbar(int width, int height, float partialTicks) {
		if(eventParent.type == HOTBAR) {
			if(eventParent.isCancelable()) {
				eventParent.setCanceled(true);
				pre(HOTBAR);
				return;
			}
		}    

        {
            SAOGL.glAlpha(true);
            SAOGL.glBlend(true);
            SAOGL.glColor(1, 1, 1, 1);

            final InventoryPlayer inv = mc.thePlayer.inventory;
            final int slotCount = 9;
            if (SAOOption.DEFAULT_HOTBAR.value){
                mc.renderEngine.bindTexture(WIDGITS);
                SAOGL.glTexturedRect(width / 2 - 91, height - 22, 0, 0, 182, 22);
                SAOGL.glTexturedRect(width / 2 - 91 - 1 + inv.currentItem * 20, height - 22 - 1, 0, 22, 24, 22);

                SAOGL.glBlend(false);
                SAOGL.glRescaleNormal(true);
                RenderHelper.enableGUIStandardItemLighting();

                for (int i = 0; i < 9; ++i)
                {
                    int x = width / 2 - 90 + i * 20 + 2;
                    int z = height - 16 - 3;
                    renderInventorySlot(i, x, z, partialTicks);
                }            	
            }
            else if (SAOOption.ALT_HOTBAR.value) {
                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

                for (int i = 0; i < slotCount; i++) {
                    SAOGL.glColorRGBA(i == inv.currentItem ? 0xE0BE62FF : 0xCDCDCDFF);
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
            }
            else {
                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);
                final int slotsY = (height - (slotCount * 22)) / 2;

                for (int i = 0; i < slotCount; i++) {
                    SAOGL.glColorRGBA(i == inv.currentItem ? 0xE0BE62FF : 0xCDCDCDFF);
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
        }

        post(HOTBAR);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderAir(int width, int height) {
    	if(eventParent.type == AIR) {
    		if(eventParent.isCancelable()) {
    			eventParent.setCanceled(true);
    			pre(AIR);
    			return;
    		}
    	}
        // Linked to renderHealth
        post(AIR);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHealth(int width, int height) {
    	if(eventParent.type == HEALTH) {
    		if(eventParent.isCancelable()) {
    			eventParent.setCanceled(true);
    			pre(HEALTH);
    			return;
    		}
    	}
	    mc.mcProfiler.startSection("health");
	
        final String username = mc.thePlayer.getDisplayName();
        int maxNameWidth = fontRenderer.getStringWidth(username);

        SAOGL.glAlpha(true);
        SAOGL.glBlend(true);

        SAOGL.glColor(1, 1, 1, 1);
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);
        SAOGL.glTexturedRect(2, 2, zLevel, 0, 0, 16, 15);

        final int usernameBoxes = 1 + (maxNameWidth + 4) / 5;

        SAOGL.glTexturedRect(18, 2, zLevel, usernameBoxes * 5, 15, 16, 0, 5, 15);
        SAOGL.glString(fontRenderer, username, 18, 3 + (15 - fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);

        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);
        SAOGL.glColor(1, 1, 1, 1);

        final int offsetUsername = 18 + usernameBoxes * 5;
        final int healthBarWidth = 234;

        SAOGL.glTexturedRect(offsetUsername, 2, zLevel, 21, 0, healthBarWidth, 15);

        final int healthWidth = 216;
        final int healthHeight = SAOOption.ORIGINAL_UI.value? 9 : 4;

        final int healthValue = (int) (SAOMod.getHealth(mc, mc.thePlayer, time) / SAOMod.getMaxHealth(mc.thePlayer) * healthWidth);
        SAOHealthStep.getStep(mc, mc.thePlayer, time).glColor();

        int stepOne = (int) (healthWidth / 3.0F - 3);
        int stepTwo = (int) (healthWidth / 3.0F * 2.0F - 3);
        int stepThree = healthWidth - 3;

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

        }

        mc.mcProfiler.endSection();
        post(HEALTH);

        renderFood(healthWidth, healthHeight, offsetUsername, stepOne, stepTwo, stepThree);

        int healthBoxes;

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
	
        SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);

        mc.mcProfiler.startSection("effects");

        final int offsetForEffects = offsetUsername + healthBarWidth - 4;
        final List<SAOEffect> effects = SAOEffect.getEffects(mc.thePlayer);

        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value? SAOResources.gui: SAOResources.guiCustom);

        for (int i = 0; i < effects.size(); i++) {
            effects.get(i).glDraw(offsetForEffects + i * 11, 2, zLevel);
        }

        mc.mcProfiler.endSection();
        /*
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

                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value ? SAOResources.gui : SAOResources.guiCustom);

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
        }*/
   	}

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderFood(int width, int height) {
        // See below, called by renderHealth
    }

    private void renderFood(int healthWidth, int healthHeight, int offsetUsername, int stepOne, int stepTwo, int stepThree) {
    	if(eventParent.type == FOOD) {
    		if(eventParent.isCancelable()) {
    			eventParent.setCanceled(true);
    			pre(FOOD);
    			return;
    		}
    	}
        mc.mcProfiler.startSection("food");
        final int foodValue = (int) (SAOMod.getHungerFract(mc, mc.thePlayer, time) * healthWidth);
        int h = foodValue < 12? 12 - foodValue: 0;
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

        mc.mcProfiler.endSection();
        post(FOOD);
    }

    
    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderExperience(int width, int height) {
    	if(eventParent.type == EXPERIENCE) {
    		if(eventParent.isCancelable()) {
    			eventParent.setCanceled(true);
    			pre(EXPERIENCE);
    			return;
    		}
    	}
        // Nothing happens here
        post(EXPERIENCE);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected void renderJumpBar(int width, int height) {
		if(eventParent.type == JUMPBAR) {
			if(eventParent.isCancelable()) {
				eventParent.setCanceled(true);
				pre(JUMPBAR);
				return;
			}
		}    
        // Nothing happens here (not implemented yet)
        post(JUMPBAR);
    }

    /*@Override
    protected void renderChat(int width, int height) {
        super.renderChat(width, height);
        // TODO: change this if need to use a custom chat system again!
    }*/

    @Override
    protected void renderHealthMount(int width, int height) {
        //EntityPlayer player = (EntityPlayer)mc.getRenderViewEntity();
    	EntityPlayer player = (EntityPlayer)mc.renderViewEntity;
        Entity tmp = player.ridingEntity;
        if (!(tmp instanceof EntityLivingBase)) return;

		if(eventParent.type == HEALTHMOUNT) {
			if(eventParent.isCancelable()) {
				eventParent.setCanceled(true);
				pre(HEALTHMOUNT);
				return;
			}
		}    
        // Not implemented yet
        post(HEALTHMOUNT);
    }

    // c/p from GuiIngameForge
    
    
    private boolean pre(ElementType type)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }
    
    private void post(ElementType type)
    {
    	
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }

    public boolean backgroundClicked(int cursorX, int cursorY, int button) {
        return !SAOOption.DEFAULT_UI.value;
    }
}
