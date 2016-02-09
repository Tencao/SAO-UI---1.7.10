package com.tencao.saoui;

import com.tencao.saoui.util.SAOGL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.profiler.Profiler;
import org.lwjgl.opengl.GL11;

/**
 * This code was original created by <Vazkii> and has been modified to our needs
 * All credit goes to him
 */
@SideOnly(Side.CLIENT)
public final class SAORenderDispatcher {

    public static int particleFxCount = 0;

    public static void dispatch() {
        Tessellator tessellator = Tessellator.instance;

        Profiler profiler = Minecraft.getMinecraft().mcProfiler;

        //GL11.glPushAttrib(GL11.GL_LIGHTING);
        SAOGL.glBlend(true);
        SAOGL.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

        profiler.startSection("death particle");
        SAOEntityPiecesFX.dispatchQueuedRenders(tessellator);
        profiler.endSection();
        SAOGL.glBlend(false);
        //GL11.glPopAttrib();
    }
}