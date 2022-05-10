package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticlePortal
extends Particle {
    private final float portalParticleScale;
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;

    protected ParticlePortal(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.posX = xCoordIn;
        this.posY = yCoordIn;
        this.posZ = zCoordIn;
        this.portalPosX = this.posX;
        this.portalPosY = this.posY;
        this.portalPosZ = this.posZ;
        float f2 = this.rand.nextFloat() * 0.6f + 0.4f;
        this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2f + 0.5f;
        this.particleRed = f2 * 0.9f;
        this.particleGreen = f2 * 0.3f;
        this.particleBlue = f2;
        this.particleMaxAge = (int)(Math.random() * 10.0) + 40;
        this.setParticleTextureIndex((int)(Math.random() * 8.0));
    }

    @Override
    public void moveEntity(double x2, double y2, double z2) {
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x2, y2, z2));
        this.resetPositionToBB();
    }

    @Override
    public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f2 = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
        f2 = 1.0f - f2;
        f2 *= f2;
        f2 = 1.0f - f2;
        this.particleScale = this.portalParticleScale * f2;
        super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public int getBrightnessForRender(float p_189214_1_) {
        int i2 = super.getBrightnessForRender(p_189214_1_);
        float f2 = (float)this.particleAge / (float)this.particleMaxAge;
        f2 *= f2;
        f2 *= f2;
        int j2 = i2 & 0xFF;
        int k2 = i2 >> 16 & 0xFF;
        if ((k2 += (int)(f2 * 15.0f * 16.0f)) > 240) {
            k2 = 240;
        }
        return j2 | k2 << 16;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float f2 = (float)this.particleAge / (float)this.particleMaxAge;
        float f1 = -f2 + f2 * f2 * 2.0f;
        float f22 = 1.0f - f1;
        this.posX = this.portalPosX + this.motionX * (double)f22;
        this.posY = this.portalPosY + this.motionY * (double)f22 + (double)(1.0f - f2);
        this.posZ = this.portalPosZ + this.motionZ * (double)f22;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int ... p_178902_15_) {
            return new ParticlePortal(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}

