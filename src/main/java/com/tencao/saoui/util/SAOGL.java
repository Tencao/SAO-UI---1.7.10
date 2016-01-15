package com.tencao.saoui.util;

import com.tencao.saoui.SAOMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public final class SAOGL {

    private SAOGL() {
    }

    public static Minecraft glMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static FontRenderer glFont() {
        final Minecraft mc = glMinecraft();

        return mc != null ? mc.fontRenderer : null;
    }

    public static TextureManager glTextureManager() {
        final Minecraft mc = glMinecraft();

        return mc != null ? mc.getTextureManager() : null;
    }

    public static void glColor(float red, float green, float blue, float alpha) {
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColorRGBA(SAOColor color) {
        glColorRGBA(color.rgba);
    }

    public static void glColorRGBA(int rgba) {
        final float red = (float) ((rgba >> 24) & 0xFF) / 0xFF;
        final float green = (float) ((rgba >> 16) & 0xFF) / 0xFF;
        final float blue = (float) ((rgba >> 8) & 0xFF) / 0xFF;
        final float alpha = (float) ((rgba) & 0xFF) / 0xFF;

        glColor(red, green, blue, alpha);
    }

    public static int glFontColor(int rgba) {
        final int alpha = (rgba) & 0xFF;
        final int red = (rgba >> 24) & 0xFF;
        final int blue = (rgba >> 8) & 0xFF;
        final int green = (rgba >> 16) & 0xFF;

        return (alpha << 24) | (red << 16) | (blue << 8) | (green);
    }

    public static void glString(FontRenderer font, String string, int x, int y, int argb, boolean shadow) {
        if (font != null) font.drawString(string, x, y, glFontColor(argb), shadow);
    }

    public static void glString(FontRenderer font, String string, int x, int y, int argb) {
        glString(font, string, x, y, argb, false);
    }

    public static void glString(String string, int x, int y, int argb, boolean shadow) {
        glString(glFont(), string, x, y, argb, shadow);
    }

    public static void glString(String string, int x, int y, int argb) {
        glString(string, x, y, argb, false);
    }

    public static void setFont(Minecraft mc, boolean custom) {
        if (mc.fontRenderer == null) return;
        ResourceLocation fontLocation = custom ? new ResourceLocation(SAOMod.MODID, "textures/ascii.png") : new ResourceLocation("textures/font/ascii.png");
        GameSettings gs = mc.gameSettings;
        mc.fontRenderer = new FontRenderer(gs, fontLocation, mc.getTextureManager(), false);
        if (gs.language != null) {
            mc.fontRenderer.setUnicodeFlag(mc.func_152349_b()); // May need to be rechecked
            mc.fontRenderer.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
        }
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(mc.fontRenderer);
    }

    public static int glStringWidth(FontRenderer font, String string) {
        if (font != null) return font.getStringWidth(string);
        else return 0;
    }

    public static int glStringWidth(String string) {
        return glStringWidth(glFont(), string);
    }

    public static int glStringHeight(FontRenderer font) {
        if (font != null) return font.FONT_HEIGHT;
        else return 0;
    }

    public static int glStringHeight() {
        return glStringHeight(glFont());
    }

    public static void glBindTexture(TextureManager textureManager, ResourceLocation location) {
        if (textureManager != null) textureManager.bindTexture(location);
    }

    public static void glBindTexture(ResourceLocation location) {
        glBindTexture(glTextureManager(), location);
    }

    public static void glTexturedRect(int x, int y, float z, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (x), (double) (y + height), (double) z, (double) ((float) (srcX) * f), (double) ((float) (srcY + srcHeight) * f1));
        tessellator.addVertexWithUV((double) (x + width), (double) (y + height), (double) z, (double) ((float) (srcX + srcWidth) * f), (double) ((float) (srcY + srcHeight) * f1));
        tessellator.addVertexWithUV((double) (x + width), (double) (y), (double) z, (double) ((float) (srcX + srcWidth) * f), (double) ((float) (srcY) * f1));
        tessellator.addVertexWithUV((double) (x), (double) (y), (double) z, (double) ((float) (srcX) * f), (double) ((float) (srcY) * f1));
        tessellator.draw();
    }

    public static void glTexturedRect(int x, int y, float z, int srcX, int srcY, int width, int height) {
        glTexturedRect(x, y, z, width, height, srcX, srcY, width, height);
    }

    public static void glTexturedRect(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight) {
        glTexturedRect(x, y, 0, width, height, srcX, srcY, srcWidth, srcHeight);
    }

    public static void glTexturedRect(int x, int y, int srcX, int srcY, int width, int height) {
        glTexturedRect(x, y, 0, srcX, srcY, width, height);
    }

    public static void glRect(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) (x), (double) (y + height), 0.0D);
        tessellator.addVertex((double) (x + width), (double) (y + height), 0.0D);
        tessellator.addVertex((double) (x + width), (double) (y), 0.0D);
        tessellator.addVertex((double) (x), (double) (y), 0.0D);
        tessellator.draw();
    }

    public static void glAlpha(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_ALPHA_TEST);
        else GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void alphaFunc(int src, int dst) {
        GL11.glAlphaFunc(src, dst);
    }

    public static void glBlend(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
    }

    public static void blendFunc(int src, int dst) {
        GL11.glBlendFunc(src, dst);
    }

    public static void tryBlendFuncSeparate(int a, int b, int c, int d) {
        OpenGlHelper.glBlendFunc(a, b, c, d);
    }

    public static void glDepth(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_DEPTH);
        else GL11.glDisable(GL11.GL_DEPTH);
    }

    public static void depthMask(boolean flag) {
        GL11.glDepthMask(flag);
    }

    public static void glDepthTest(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public static void glRescaleNormal(boolean flag) {
        if (flag) GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        else GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public static void glTexture2D(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_TEXTURE_2D);
        else GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void glCullFace(boolean flag) {
        if (flag) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        } else {
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }

    public static void glStartUI(Minecraft mc) {
        mc.mcProfiler.startSection(SAOMod.MODID + "[ '" + SAOMod.NAME + "' ]");
    }

    public static void glEndUI(Minecraft mc) {
        mc.mcProfiler.endSection();
    }

}
