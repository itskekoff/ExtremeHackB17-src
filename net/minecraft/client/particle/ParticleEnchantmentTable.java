package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleEnchantmentTable
extends Particle {
    private final float oSize;
    private final double coordX;
    private final double coordY;
    private final double coordZ;

    protected ParticleEnchantmentTable(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.coordX = xCoordIn;
        this.coordY = yCoordIn;
        this.coordZ = zCoordIn;
        this.prevPosX = xCoordIn + xSpeedIn;
        this.prevPosY = yCoordIn + ySpeedIn;
        this.prevPosZ = zCoordIn + zSpeedIn;
        this.posX = this.prevPosX;
        this.posY = this.prevPosY;
        this.posZ = this.prevPosZ;
        float f2 = this.rand.nextFloat() * 0.6f + 0.4f;
        this.oSize = this.particleScale = this.rand.nextFloat() * 0.5f + 0.2f;
        this.particleRed = 0.9f * f2;
        this.particleGreen = 0.9f * f2;
        this.particleBlue = f2;
        this.particleMaxAge = (int)(Math.random() * 10.0) + 30;
        this.setParticleTextureIndex((int)(Math.random() * 26.0 + 1.0 + 224.0));
    }

    @Override
    public void moveEntity(double x2, double y2, double z2) {
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x2, y2, z2));
        this.resetPositionToBB();
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
        f2 = 1.0f - f2;
        float f1 = 1.0f - f2;
        f1 *= f1;
        f1 *= f1;
        this.posX = this.coordX + this.motionX * (double)f2;
        this.posY = this.coordY + this.motionY * (double)f2 - (double)(f1 * 1.2f);
        this.posZ = this.coordZ + this.motionZ * (double)f2;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    public static class EnchantmentTable
    implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int ... p_178902_15_) {
            return new ParticleEnchantmentTable(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}

