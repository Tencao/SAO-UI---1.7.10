package com.tencao.saoui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class SAORenderPlayer extends RenderPlayer {

    private SAORenderBase trueRenderer;

    public SAORenderPlayer(Render render) {
        this.trueRenderer = new SAORenderBase(render);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f0, float f1) {
        this.trueRenderer.doRender(entity, x, y, z, f0, f1);
    }

    @Override
    public void bindTexture(ResourceLocation location) {
        this.trueRenderer.bindTexture(location);
    }

    @Override
    public void doRenderShadowAndFire(Entity p_76979_1_, double p_76979_2_, double p_76979_4_, double p_76979_6_, float p_76979_8_, float p_76979_9_) {
        this.trueRenderer.doRenderShadowAndFire(p_76979_1_, p_76979_2_, p_76979_4_, p_76979_6_, p_76979_8_, p_76979_9_);
    }

    @Override
    public FontRenderer getFontRendererFromRenderManager() {
        return this.trueRenderer.getFontRendererFromRenderManager();
    }

    @Override
    public void setRenderManager(RenderManager render) {
        super.renderManager = render;
    }
}