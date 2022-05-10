package net.minecraft.world.gen.feature;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSpikes
extends WorldGenerator {
    private boolean crystalInvulnerable;
    private EndSpike spike;
    private BlockPos beamTarget;

    public void setSpike(EndSpike p_186143_1_) {
        this.spike = p_186143_1_;
    }

    public void setCrystalInvulnerable(boolean p_186144_1_) {
        this.crystalInvulnerable = p_186144_1_;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (this.spike == null) {
            throw new IllegalStateException("Decoration requires priming with a spike");
        }
        int i2 = this.spike.getRadius();
        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(new BlockPos(position.getX() - i2, 0, position.getZ() - i2), new BlockPos(position.getX() + i2, this.spike.getHeight() + 10, position.getZ() + i2))) {
            if (blockpos$mutableblockpos.distanceSq(position.getX(), blockpos$mutableblockpos.getY(), position.getZ()) <= (double)(i2 * i2 + 1) && blockpos$mutableblockpos.getY() < this.spike.getHeight()) {
                this.setBlockAndNotifyAdequately(worldIn, blockpos$mutableblockpos, Blocks.OBSIDIAN.getDefaultState());
                continue;
            }
            if (blockpos$mutableblockpos.getY() <= 65) continue;
            this.setBlockAndNotifyAdequately(worldIn, blockpos$mutableblockpos, Blocks.AIR.getDefaultState());
        }
        if (this.spike.isGuarded()) {
            for (int j2 = -2; j2 <= 2; ++j2) {
                for (int k2 = -2; k2 <= 2; ++k2) {
                    if (MathHelper.abs(j2) == 2 || MathHelper.abs(k2) == 2) {
                        this.setBlockAndNotifyAdequately(worldIn, new BlockPos(position.getX() + j2, this.spike.getHeight(), position.getZ() + k2), Blocks.IRON_BARS.getDefaultState());
                        this.setBlockAndNotifyAdequately(worldIn, new BlockPos(position.getX() + j2, this.spike.getHeight() + 1, position.getZ() + k2), Blocks.IRON_BARS.getDefaultState());
                        this.setBlockAndNotifyAdequately(worldIn, new BlockPos(position.getX() + j2, this.spike.getHeight() + 2, position.getZ() + k2), Blocks.IRON_BARS.getDefaultState());
                    }
                    this.setBlockAndNotifyAdequately(worldIn, new BlockPos(position.getX() + j2, this.spike.getHeight() + 3, position.getZ() + k2), Blocks.IRON_BARS.getDefaultState());
                }
            }
        }
        EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(worldIn);
        entityendercrystal.setBeamTarget(this.beamTarget);
        entityendercrystal.setEntityInvulnerable(this.crystalInvulnerable);
        entityendercrystal.setLocationAndAngles((float)position.getX() + 0.5f, this.spike.getHeight() + 1, (float)position.getZ() + 0.5f, rand.nextFloat() * 360.0f, 0.0f);
        worldIn.spawnEntityInWorld(entityendercrystal);
        this.setBlockAndNotifyAdequately(worldIn, new BlockPos(position.getX(), this.spike.getHeight(), position.getZ()), Blocks.BEDROCK.getDefaultState());
        return true;
    }

    public void setBeamTarget(@Nullable BlockPos pos) {
        this.beamTarget = pos;
    }

    public static class EndSpike {
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final AxisAlignedBB topBoundingBox;

        public EndSpike(int p_i47020_1_, int p_i47020_2_, int p_i47020_3_, int p_i47020_4_, boolean p_i47020_5_) {
            this.centerX = p_i47020_1_;
            this.centerZ = p_i47020_2_;
            this.radius = p_i47020_3_;
            this.height = p_i47020_4_;
            this.guarded = p_i47020_5_;
            this.topBoundingBox = new AxisAlignedBB(p_i47020_1_ - p_i47020_3_, 0.0, p_i47020_2_ - p_i47020_3_, p_i47020_1_ + p_i47020_3_, 256.0, p_i47020_2_ + p_i47020_3_);
        }

        public boolean doesStartInChunk(BlockPos p_186154_1_) {
            int i2 = this.centerX - this.radius;
            int j2 = this.centerZ - this.radius;
            return p_186154_1_.getX() == (i2 & 0xFFFFFFF0) && p_186154_1_.getZ() == (j2 & 0xFFFFFFF0);
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        public AxisAlignedBB getTopBoundingBox() {
            return this.topBoundingBox;
        }
    }
}

