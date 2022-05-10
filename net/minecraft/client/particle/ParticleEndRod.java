package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.world.World;

public class ParticleEndRod
extends ParticleSimpleAnimated {
    public ParticleEndRod(World p_i46580_1_, double p_i46580_2_, double p_i46580_4_, double p_i46580_6_, double p_i46580_8_, double p_i46580_10_, double p_i46580_12_) {
        super(p_i46580_1_, p_i46580_2_, p_i46580_4_, p_i46580_6_, 176, 8, -5.0E-4f);
        this.motionX = p_i46580_8_;
        this.motionY = p_i46580_10_;
        this.motionZ = p_i46580_12_;
        this.particleScale *= 0.75f;
        this.particleMaxAge = 60 + this.rand.nextInt(12);
        this.setColorFade(15916745);
    }

    @Override
    public void moveEntity(double x2, double y2, double z2) {
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x2, y2, z2));
        this.resetPositionToBB();
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int ... p_178902_15_) {
            return new ParticleEndRod(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}

