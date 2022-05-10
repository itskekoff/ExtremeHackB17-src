package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityExpBottle
extends EntityThrowable {
    public EntityExpBottle(World worldIn) {
        super(worldIn);
    }

    public EntityExpBottle(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityExpBottle(World worldIn, double x2, double y2, double z2) {
        super(worldIn, x2, y2, z2);
    }

    public static void registerFixesExpBottle(DataFixer fixer) {
        EntityThrowable.registerFixesThrowable(fixer, "ThrowableExpBottle");
    }

    @Override
    protected float getGravityVelocity() {
        return 0.07f;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            int j2;
            this.world.playEvent(2002, new BlockPos(this), PotionUtils.getPotionColor(PotionTypes.WATER));
            for (int i2 = 3 + this.world.rand.nextInt(5) + this.world.rand.nextInt(5); i2 > 0; i2 -= j2) {
                j2 = EntityXPOrb.getXPSplit(i2);
                this.world.spawnEntityInWorld(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j2));
            }
            this.setDead();
        }
    }
}

