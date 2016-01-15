package com.tencao.saoui;

import com.tencao.saoui.util.SAOResources;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SAOEntityPiecesFX extends EntityAuraFX {

    float ParticleScale;

    public SAOEntityPiecesFX(World world, double xCoord, double yCoord, double zCoord, float redValue, float greenValue, float blueValue) {
        this(world, xCoord, yCoord, zCoord, redValue, greenValue, blueValue, 1.0F);
    }

    private SAOEntityPiecesFX(World world, double xCoord, double yCoord, double zCoord, float redValue, float greenValue, float blueVale, float scale) {
        super(world, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.particleRed = redValue;
        this.particleGreen = greenValue;
        this.particleBlue = blueVale;
        this.particleScale *= 0.75F;
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

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

        this.particleScale = this.ParticleScale * particle;

        renderParticles(tessellator, time, x, y, z, f0, f1);
    }

    private void renderParticles(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SAOResources.particleLarge);

        float scale = 0.1F * this.particleScale;
        float xPos = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) par2 - interpPosX);
        float yPos = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) par2 - interpPosY);
        float zPos = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) par2 - interpPosZ);
        float colorIntensity = 1.0F;
        par1Tessellator.setColorOpaque_F(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity);//, 1.0F);

        par1Tessellator.addVertexWithUV((double) (xPos - par3 * scale - par6 * scale), (double) (yPos - par4 * scale), (double) (zPos - par5 * scale - par7 * scale), 0D, 1D);
        par1Tessellator.addVertexWithUV((double) (xPos - par3 * scale + par6 * scale), (double) (yPos + par4 * scale), (double) (zPos - par5 * scale + par7 * scale), 1D, 1D);
        par1Tessellator.addVertexWithUV((double) (xPos + par3 * scale + par6 * scale), (double) (yPos + par4 * scale), (double) (zPos + par5 * scale + par7 * scale), 1D, 0D);
        par1Tessellator.addVertexWithUV((double) (xPos + par3 * scale - par6 * scale), (double) (yPos - par4 * scale), (double) (zPos + par5 * scale - par7 * scale), 0D, 0D);
    }


    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) this.setDead();

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.motionY -= 0.04D * (double) this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }
        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

}
