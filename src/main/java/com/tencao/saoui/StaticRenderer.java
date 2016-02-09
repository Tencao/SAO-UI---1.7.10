package com.tencao.saoui;

import com.tencao.saoui.ui.SAOCharacterView;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@SideOnly(Side.CLIENT)
class StaticRenderer {

    private static final int HEALTH_COUNT = 32;
    private static final double HEALTH_ANGLE = 0.35F;
    private static final double HEALTH_RANGE = 0.975F;
    private static final float HEALTH_OFFSET = 0.75F;
    private static final double HEALTH_HEIGHT = 0.21F;

    public static void render(RenderManager renderManager, Entity entity, double x, double y, double z) {
        final Minecraft mc = Minecraft.getMinecraft();

        boolean dead = false, deadStart = false, deadExactly = false;

        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase living = (EntityLivingBase) entity;

            dead = StaticPlayerHelper.getHealth(mc, living, SAOMod.UNKNOWN_TIME_DELAY) <= 0;
            deadStart = (living.deathTime == 1);
            deadExactly = (living.deathTime >= 18);

            if (deadStart) {
                living.deathTime++;
            }

        } else if (entity instanceof EntityItem) {
            final EntityItem item = (EntityItem) entity;

            deadStart = (item.age + 16 >= item.lifespan);
            deadExactly = (item.age >= item.lifespan);
        }

        if (!SAOCharacterView.IS_VIEWING && !dead && !entity.isInvisibleToPlayer(mc.thePlayer) && entity != mc.thePlayer) {
            if (SAOOption.COLOR_CURSOR.getValue()) {
                if (!(SAOOption.DEBUG_MODE.getValue() && SAOColorState.checkValidState(entity))) {
                    doRenderColorCursor(renderManager, mc, (EntityLivingBase) entity, x, y, z, 64);
                } else if (SAOOption.DEBUG_MODE.getValue())
                    doRenderColorCursor(renderManager, mc, (EntityLivingBase) entity, x, y, z, 64);
            }
            if ((SAOOption.HEALTH_BARS.getValue()) && (!entity.equals(mc.thePlayer))) {
                if (!(SAOOption.DEBUG_MODE.getValue() && SAOColorState.checkValidState((EntityLivingBase) entity))) {
                    doRenderHealthBar(renderManager, mc, (EntityLivingBase) entity, x, y, z);
                } else if (SAOOption.DEBUG_MODE.getValue())
                    doRenderHealthBar(renderManager, mc, (EntityLivingBase) entity, x, y, z);
            }
        }

    }

    private static void doRenderColorCursor(RenderManager renderManager, Minecraft mc, EntityLivingBase entity, double x, double y, double z, int distance) {
        if (entity.riddenByEntity != null) return;
        if (SAOOption.LESS_VISUALS.getValue() && !(entity instanceof IMob || StaticPlayerHelper.getHealth(mc, entity, SAOMod.UNKNOWN_TIME_DELAY) != StaticPlayerHelper.getMaxHealth(entity)))
            return;

        double d3 = entity.getDistanceSqToEntity(renderManager.livingPlayer);

        if (d3 <= (double) (distance * distance)) {
            final float sizeMult = entity.isChild() && entity instanceof EntityMob ? 0.5F : 1.0F;

            float f = 1.6F;
            float f1 = 0.016666668F * f;

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.0F, (float) y + sizeMult * entity.height + sizeMult * 1.1F, (float) z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-(f1 * sizeMult), -(f1 * sizeMult), (f1 * sizeMult));
            GL11.glDisable(GL11.GL_LIGHTING);

            SAOGL.glDepthTest(true);

            SAOGL.glBlend(true);
            SAOGL.tryBlendFuncSeparate(770, 771, 1, 0);

            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.entities : SAOResources.entitiesCustom);

            Tessellator tessellator = Tessellator.instance;

            tessellator.startDrawingQuads();
            ColorStateHandler.getCurrent(entity).glColor();

            //System.out.print(state.name() + " assigned to " + entity.getCommandSenderName() + " " + entity.getUniqueID() + "\n");

            if (SAOOption.SPINNING_CRYSTALS.getValue()) {
                double a = (entity.worldObj.getTotalWorldTime() % 40) / 20.0D * Math.PI;
                double cos = Math.cos(a);//Math.PI / 3 * 2);
                double sin = Math.sin(a);//Math.PI / 3 * 2);

                if (a > Math.PI / 2 && a <= Math.PI * 3 / 2) {
                    tessellator.addVertexWithUV(9.0D * cos, -1, 9.0D * sin, 0.125F, 0.25F);
                    tessellator.addVertexWithUV(9.0D * cos, 17, 9.0D * sin, 0.125F, 0.375F);
                    tessellator.addVertexWithUV(-9.0D * cos, 17, -9.0D * sin, 0F, 0.375F);
                    tessellator.addVertexWithUV(-9.0D * cos, -1, -9.0D * sin, 0F, 0.25F);
                } else {
                    tessellator.addVertexWithUV(-9.0D * cos, -1, -9.0D * sin, 0F, 0.25F);
                    tessellator.addVertexWithUV(-9.0D * cos, 17, -9.0D * sin, 0F, 0.375F);
                    tessellator.addVertexWithUV(9.0D * cos, 17, 9.0D * sin, 0.125F, 0.375F);
                    tessellator.addVertexWithUV(9.0D * cos, -1, 9.0D * sin, 0.125F, 0.25F);
                }

                tessellator.draw();
                tessellator.startDrawingQuads();

                if (a < Math.PI) {
                    tessellator.addVertexWithUV(-9.0D * sin, -1, 9.0D * cos, 0.125F, 0.25F);
                    tessellator.addVertexWithUV(-9.0D * sin, 17, 9.0D * cos, 0.125F, 0.375F);
                    tessellator.addVertexWithUV(9.0D * sin, 17, -9.0D * cos, 0F, 0.375F);
                    tessellator.addVertexWithUV(9.0D * sin, -1, -9.0D * cos, 0F, 0.25F);
                } else {
                    tessellator.addVertexWithUV(9.0D * sin, -1, -9.0D * cos, 0F, 0.25F);
                    tessellator.addVertexWithUV(9.0D * sin, 17, -9.0D * cos, 0F, 0.375F);
                    tessellator.addVertexWithUV(-9.0D * sin, 17, 9.0D * cos, 0.125F, 0.375F);
                    tessellator.addVertexWithUV(-9.0D * sin, -1, 9.0D * cos, 0.125F, 0.25F);
                }

                tessellator.draw();
            } else {
                tessellator.addVertexWithUV(-9, -1, 0.0D, 0F, 0.25F);
                tessellator.addVertexWithUV(-9, 17, 0.0D, 0F, 0.375F);
                tessellator.addVertexWithUV(9, 17, 0.0D, 0.125F, 0.375F);
                tessellator.addVertexWithUV(9, -1, 0.0D, 0.125F, 0.25F);
                tessellator.draw();
            }


            SAOGL.glBlend(false);
            SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }

    private static void doRenderHealthBar(RenderManager renderManager, Minecraft mc, EntityLivingBase entity, double x, double y, double z) {
        if (entity.riddenByEntity != null && entity.riddenByEntity == mc.thePlayer) return;
        if (SAOOption.LESS_VISUALS.getValue() && !(entity instanceof IMob || StaticPlayerHelper.getHealth(mc, entity, SAOMod.UNKNOWN_TIME_DELAY) != StaticPlayerHelper.getMaxHealth(entity)))
            return;
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.entities : SAOResources.entitiesCustom);

        Tessellator tessellator = Tessellator.instance;
        SAOGL.glDepthTest(true);
        SAOGL.glCullFace(false);
        SAOGL.glBlend(true);

        SAOGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        final int hitPoints = (int) (getHealthFactor(mc, entity, SAOMod.UNKNOWN_TIME_DELAY) * HEALTH_COUNT);
        useColor(mc, entity, SAOMod.UNKNOWN_TIME_DELAY);
        if (entity instanceof IBossDisplayData){
            tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);
            final float sizeMult = ((EntityLivingBase)entity).isChild() && entity instanceof EntityMob ? 0.5F : 1.0F;
            for (int i = 0; i <= hitPoints; i++) {
                final double value = (double) (i + HEALTH_COUNT - hitPoints) / HEALTH_COUNT;
                final double rad = Math.toRadians(renderManager.playerViewY - 135) + (value - 0.5) * Math.PI * HEALTH_ANGLE;

                final double x0 = x + sizeMult * entity.width * HEALTH_RANGE * Math.cos(rad);
                final double y0 = y + sizeMult * entity.height * HEALTH_OFFSET;
                final double z0 = z + sizeMult * entity.width * HEALTH_RANGE * Math.sin(rad);

                final double uv_value = value - (double) (HEALTH_COUNT - hitPoints) / HEALTH_COUNT;

                tessellator.addVertexWithUV(x0, y0 + HEALTH_HEIGHT, z0, (1.0 - uv_value), 0);
                tessellator.addVertexWithUV(x0, y0, z0, (1.0 - uv_value), 0.125);
            }
            tessellator.draw();

            SAOGL.glColor(1, 1, 1, 1);
            tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);

            for (int i = 0; i <= HEALTH_COUNT; i++) {
                final double value = (double) i / HEALTH_COUNT;
                final double rad = Math.toRadians(renderManager.playerViewY - 135) + (value - 0.5) * Math.PI * HEALTH_ANGLE;

                final double x0 = x + sizeMult * entity.width * HEALTH_RANGE * Math.cos(rad);
                final double y0 = y + sizeMult * entity.height * HEALTH_OFFSET;
                final double z0 = z + sizeMult * entity.width * HEALTH_RANGE * Math.sin(rad);

                tessellator.addVertexWithUV(x0, y0 + HEALTH_HEIGHT, z0, (1.0 - value), 0.125);
                tessellator.addVertexWithUV(x0, y0, z0, (1.0 - value), 0.25);
            }

            tessellator.draw();

            SAOGL.glCullFace(true);
        }
        else {
            tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);

            final float sizeMult = ((EntityLivingBase)entity).isChild() && entity instanceof IAnimals ? 0.5F : 1.0F;

            for (int i = 0; i <= hitPoints; i++) {
                final double value = (double) (i + HEALTH_COUNT - hitPoints) / HEALTH_COUNT;
                final double rad = Math.toRadians(renderManager.playerViewY - 135) + (value - 0.5) * Math.PI * HEALTH_ANGLE;

                final double x0 = x + sizeMult * entity.width * HEALTH_RANGE * Math.cos(rad);
                final double y0 = y + sizeMult * entity.height * HEALTH_OFFSET;
                final double z0 = z + sizeMult * entity.width * HEALTH_RANGE * Math.sin(rad);

                final double uv_value = value - (double) (HEALTH_COUNT - hitPoints) / HEALTH_COUNT;

                tessellator.addVertexWithUV(x0, y0 + HEALTH_HEIGHT, z0, (1.0 - uv_value), 0);
                tessellator.addVertexWithUV(x0, y0, z0, (1.0 - uv_value), 0.125);
            }

            tessellator.draw();

            SAOGL.glColor(1, 1, 1, 1);
            tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);

            for (int i = 0; i <= HEALTH_COUNT; i++) {
                final double value = (double) i / HEALTH_COUNT;
                final double rad = Math.toRadians(renderManager.playerViewY - 135) + (value - 0.5) * Math.PI * HEALTH_ANGLE;

                final double x0 = x + sizeMult * entity.width * HEALTH_RANGE * Math.cos(rad);
                final double y0 = y + sizeMult * entity.height * HEALTH_OFFSET;
                final double z0 = z + sizeMult * entity.width * HEALTH_RANGE * Math.sin(rad);

                tessellator.addVertexWithUV(x0, y0 + HEALTH_HEIGHT, z0, (1.0 - value), 0.125);
                tessellator.addVertexWithUV(x0, y0, z0, (1.0 - value), 0.25);
            }

            tessellator.draw();
        }

    }

    public static void doSpawnDeathParticles(Minecraft mc, Entity entity) {
        final World world = entity.worldObj;

        if (entity.worldObj.isRemote) {
            final float[][] colors = {
                    {1F / 0xFF * 0x9A, 1F / 0xFF * 0xFE, 1F / 0xFF * 0x2E},
                    {1F / 0xFF * 0x01, 1F / 0xFF * 0xFF, 1F / 0xFF * 0xFF},
                    {1F / 0xFF * 0x08, 1F / 0xFF * 0x08, 1F / 0xFF * 0x8A}
            };

            final float size = entity.width * entity.height;
            final int pieces = (int) Math.max(Math.min((size * 64), 128), 8);

            for (int i = 0; i < pieces; i++) {
                final float[] color = colors[i % 3];

                final double x0 = entity.width * (Math.random() * 2 - 1) * 0.75;
                final double y0 = entity.height * (Math.random());
                final double z0 = entity.width * (Math.random() * 2 - 1) * 0.75;

                mc.effectRenderer.addEffect(new SAOEntityPiecesFX(
                        world,
                        entity.posX + x0, entity.posY + y0, entity.posZ + z0,
                        color[0], color[1], color[2]
                ));
            }
        }
    }

    private static void useColor(Minecraft mc, Entity entity, float time) {
        if (entity instanceof EntityLivingBase) {
            SAOHealthStep.getStep(mc, (EntityLivingBase) entity, time).glColor();
        } else {
            SAOHealthStep.GOOD.glColor();
        }
    }

    private static float getHealthFactor(Minecraft mc, Entity entity, float time) {
        final float normalFactor = StaticPlayerHelper.getHealth(mc, entity, time) / StaticPlayerHelper.getMaxHealth(entity);
        final float delta = 1.0F - normalFactor;

        return normalFactor + (delta * delta / 2) * normalFactor;
    }
}