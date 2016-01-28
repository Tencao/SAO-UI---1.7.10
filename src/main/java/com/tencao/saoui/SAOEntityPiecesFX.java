package com.tencao.saoui;

import com.tencao.saoui.util.SAOResources;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;


@SideOnly(Side.CLIENT)
public class SAOEntityPiecesFX extends EntityFX {

    float ParticleScale;

    public SAOEntityPiecesFX(World world, double xCoord, double yCoord, double zCoord, float redValue, float greenValue, float blueValue) {
        this(world, xCoord, yCoord, zCoord, redValue, greenValue, blueValue, 1.0F);
    }

    private SAOEntityPiecesFX(World world, double xCoord, double yCoord, double zCoord, float redValue, float greenValue, float blueVale, float scale) {
        super(world, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
        this.motionX = (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionY = (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionZ = (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.particleRed = redValue;
        this.particleGreen = greenValue;
        this.particleBlue = blueVale;
        this.particleScale *= scale;
        this.ParticleScale = this.particleScale;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) ((float) this.particleMaxAge * scale);
        this.noClip = false;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float time, float x, float y, float z, float f0, float f1) {
        float particle = ((float) this.particleAge + time) / (float) this.particleMaxAge * 32.0F;

        if (particle < 0.0F) {
            particle = 0.0F;
        }

        if (particle > 1.0F) {
            particle = 1.0F;
        }

        this.particleScale = this.ParticleScale * particle;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        renderParticles(tessellator, time, x, y, z, f0, f1);
    }

    private void renderParticles(Tessellator tessellator, float time, float x, float y, float z, float f0, float f1) {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SAOResources.particleLarge);

        float scale = 0.1F * this.particleScale;
        float xPos = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) time - interpPosX);
        float yPos = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) time - interpPosY);
        float zPos = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) time - interpPosZ);
        float colorIntensity = 1.0F;
        tessellator.setColorOpaque_F(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity);//, 1.0F);

        tessellator.addVertexWithUV((double) (xPos - x * scale - f0 * scale), (double) (yPos - y * scale), (double) (zPos - z * scale - f1 * scale), 0D, 1D);
        tessellator.addVertexWithUV((double) (xPos - x * scale + f0 * scale), (double) (yPos + y * scale), (double) (zPos - z * scale + f1 * scale), 1D, 1D);
        tessellator.addVertexWithUV((double) (xPos + x * scale + f0 * scale), (double) (yPos + y * scale), (double) (zPos + z * scale + f1 * scale), 1D, 0D);
        tessellator.addVertexWithUV((double) (xPos + x * scale - f0 * scale), (double) (yPos - y * scale), (double) (zPos + z * scale - f1 * scale), 0D, 0D);
    }


    /**
     * Called to update the entity's position/logic.
     */

    @Override
    public EntityFX multiplyVelocity(float speed)
    {
        this.motionX *= (double)speed;
        this.motionY = (this.motionY - 0.10000000149011612D) * (double)speed + 0.10000000149011612D;
        this.motionZ *= (double)speed;
        return this;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.motionY += 0.004D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8999999761581421D;
        this.motionY *= 0.8999999761581421D;
        this.motionZ *= 0.8999999761581421D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

}
