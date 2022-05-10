package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;

public abstract class StructureComponent {
    protected StructureBoundingBox boundingBox;
    @Nullable
    private EnumFacing coordBaseMode;
    private Mirror mirror;
    private Rotation rotation;
    protected int componentType;

    public StructureComponent() {
    }

    protected StructureComponent(int type) {
        this.componentType = type;
    }

    public final NBTTagCompound createStructureBaseNBT() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", MapGenStructureIO.getStructureComponentName(this));
        nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
        EnumFacing enumfacing = this.getCoordBaseMode();
        nbttagcompound.setInteger("O", enumfacing == null ? -1 : enumfacing.getHorizontalIndex());
        nbttagcompound.setInteger("GD", this.componentType);
        this.writeStructureToNBT(nbttagcompound);
        return nbttagcompound;
    }

    protected abstract void writeStructureToNBT(NBTTagCompound var1);

    public void readStructureBaseNBT(World worldIn, NBTTagCompound tagCompound) {
        int i2;
        if (tagCompound.hasKey("BB")) {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }
        this.setCoordBaseMode((i2 = tagCompound.getInteger("O")) == -1 ? null : EnumFacing.getHorizontal(i2));
        this.componentType = tagCompound.getInteger("GD");
        this.readStructureFromNBT(tagCompound, worldIn.getSaveHandler().getStructureTemplateManager());
    }

    protected abstract void readStructureFromNBT(NBTTagCompound var1, TemplateManager var2);

    public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
    }

    public abstract boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3);

    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getComponentType() {
        return this.componentType;
    }

    public static StructureComponent findIntersecting(List<StructureComponent> listIn, StructureBoundingBox boundingboxIn) {
        for (StructureComponent structurecomponent : listIn) {
            if (structurecomponent.getBoundingBox() == null || !structurecomponent.getBoundingBox().intersectsWith(boundingboxIn)) continue;
            return structurecomponent;
        }
        return null;
    }

    protected boolean isLiquidInStructureBoundingBox(World worldIn, StructureBoundingBox boundingboxIn) {
        int i2 = Math.max(this.boundingBox.minX - 1, boundingboxIn.minX);
        int j2 = Math.max(this.boundingBox.minY - 1, boundingboxIn.minY);
        int k2 = Math.max(this.boundingBox.minZ - 1, boundingboxIn.minZ);
        int l2 = Math.min(this.boundingBox.maxX + 1, boundingboxIn.maxX);
        int i1 = Math.min(this.boundingBox.maxY + 1, boundingboxIn.maxY);
        int j1 = Math.min(this.boundingBox.maxZ + 1, boundingboxIn.maxZ);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int k1 = i2; k1 <= l2; ++k1) {
            for (int l1 = k2; l1 <= j1; ++l1) {
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(k1, j2, l1)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!worldIn.getBlockState(blockpos$mutableblockpos.setPos(k1, i1, l1)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        for (int i22 = i2; i22 <= l2; ++i22) {
            for (int k22 = j2; k22 <= i1; ++k22) {
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(i22, k22, k2)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!worldIn.getBlockState(blockpos$mutableblockpos.setPos(i22, k22, j1)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        for (int j22 = k2; j22 <= j1; ++j22) {
            for (int l22 = j2; l22 <= i1; ++l22) {
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(i2, l22, j22)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!worldIn.getBlockState(blockpos$mutableblockpos.setPos(l2, l22, j22)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        return false;
    }

    protected int getXWithOffset(int x2, int z2) {
        EnumFacing enumfacing = this.getCoordBaseMode();
        if (enumfacing == null) {
            return x2;
        }
        switch (enumfacing) {
            case NORTH: 
            case SOUTH: {
                return this.boundingBox.minX + x2;
            }
            case WEST: {
                return this.boundingBox.maxX - z2;
            }
            case EAST: {
                return this.boundingBox.minX + z2;
            }
        }
        return x2;
    }

    protected int getYWithOffset(int y2) {
        return this.getCoordBaseMode() == null ? y2 : y2 + this.boundingBox.minY;
    }

    protected int getZWithOffset(int x2, int z2) {
        EnumFacing enumfacing = this.getCoordBaseMode();
        if (enumfacing == null) {
            return z2;
        }
        switch (enumfacing) {
            case NORTH: {
                return this.boundingBox.maxZ - z2;
            }
            case SOUTH: {
                return this.boundingBox.minZ + z2;
            }
            case WEST: 
            case EAST: {
                return this.boundingBox.minZ + x2;
            }
        }
        return z2;
    }

    protected void setBlockState(World worldIn, IBlockState blockstateIn, int x2, int y2, int z2, StructureBoundingBox boundingboxIn) {
        BlockPos blockpos = new BlockPos(this.getXWithOffset(x2, z2), this.getYWithOffset(y2), this.getZWithOffset(x2, z2));
        if (boundingboxIn.isVecInside(blockpos)) {
            if (this.mirror != Mirror.NONE) {
                blockstateIn = blockstateIn.withMirror(this.mirror);
            }
            if (this.rotation != Rotation.NONE) {
                blockstateIn = blockstateIn.withRotation(this.rotation);
            }
            worldIn.setBlockState(blockpos, blockstateIn, 2);
        }
    }

    protected IBlockState getBlockStateFromPos(World worldIn, int x2, int y2, int z2, StructureBoundingBox boundingboxIn) {
        int k2;
        int j2;
        int i2 = this.getXWithOffset(x2, z2);
        BlockPos blockpos = new BlockPos(i2, j2 = this.getYWithOffset(y2), k2 = this.getZWithOffset(x2, z2));
        return !boundingboxIn.isVecInside(blockpos) ? Blocks.AIR.getDefaultState() : worldIn.getBlockState(blockpos);
    }

    protected int func_189916_b(World p_189916_1_, int p_189916_2_, int p_189916_3_, int p_189916_4_, StructureBoundingBox p_189916_5_) {
        int k2;
        int j2;
        int i2 = this.getXWithOffset(p_189916_2_, p_189916_4_);
        BlockPos blockpos = new BlockPos(i2, j2 = this.getYWithOffset(p_189916_3_ + 1), k2 = this.getZWithOffset(p_189916_2_, p_189916_4_));
        return !p_189916_5_.isVecInside(blockpos) ? EnumSkyBlock.SKY.defaultLightValue : p_189916_1_.getLightFor(EnumSkyBlock.SKY, blockpos);
    }

    protected void fillWithAir(World worldIn, StructureBoundingBox structurebb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int i2 = minY; i2 <= maxY; ++i2) {
            for (int j2 = minX; j2 <= maxX; ++j2) {
                for (int k2 = minZ; k2 <= maxZ; ++k2) {
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), j2, i2, k2, structurebb);
                }
            }
        }
    }

    protected void fillWithBlocks(World worldIn, StructureBoundingBox boundingboxIn, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, IBlockState boundaryBlockState, IBlockState insideBlockState, boolean existingOnly) {
        for (int i2 = yMin; i2 <= yMax; ++i2) {
            for (int j2 = xMin; j2 <= xMax; ++j2) {
                for (int k2 = zMin; k2 <= zMax; ++k2) {
                    if (existingOnly && this.getBlockStateFromPos(worldIn, j2, i2, k2, boundingboxIn).getMaterial() == Material.AIR) continue;
                    if (i2 != yMin && i2 != yMax && j2 != xMin && j2 != xMax && k2 != zMin && k2 != zMax) {
                        this.setBlockState(worldIn, insideBlockState, j2, i2, k2, boundingboxIn);
                        continue;
                    }
                    this.setBlockState(worldIn, boundaryBlockState, j2, i2, k2, boundingboxIn);
                }
            }
        }
    }

    protected void fillWithRandomizedBlocks(World worldIn, StructureBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean alwaysReplace, Random rand, BlockSelector blockselector) {
        for (int i2 = minY; i2 <= maxY; ++i2) {
            for (int j2 = minX; j2 <= maxX; ++j2) {
                for (int k2 = minZ; k2 <= maxZ; ++k2) {
                    if (alwaysReplace && this.getBlockStateFromPos(worldIn, j2, i2, k2, boundingboxIn).getMaterial() == Material.AIR) continue;
                    blockselector.selectBlocks(rand, j2, i2, k2, i2 == minY || i2 == maxY || j2 == minX || j2 == maxX || k2 == minZ || k2 == maxZ);
                    this.setBlockState(worldIn, blockselector.getBlockState(), j2, i2, k2, boundingboxIn);
                }
            }
        }
    }

    protected void func_189914_a(World p_189914_1_, StructureBoundingBox p_189914_2_, Random p_189914_3_, float p_189914_4_, int p_189914_5_, int p_189914_6_, int p_189914_7_, int p_189914_8_, int p_189914_9_, int p_189914_10_, IBlockState p_189914_11_, IBlockState p_189914_12_, boolean p_189914_13_, int p_189914_14_) {
        for (int i2 = p_189914_6_; i2 <= p_189914_9_; ++i2) {
            for (int j2 = p_189914_5_; j2 <= p_189914_8_; ++j2) {
                for (int k2 = p_189914_7_; k2 <= p_189914_10_; ++k2) {
                    if (!(p_189914_3_.nextFloat() <= p_189914_4_) || p_189914_13_ && this.getBlockStateFromPos(p_189914_1_, j2, i2, k2, p_189914_2_).getMaterial() == Material.AIR || p_189914_14_ > 0 && this.func_189916_b(p_189914_1_, j2, i2, k2, p_189914_2_) >= p_189914_14_) continue;
                    if (i2 != p_189914_6_ && i2 != p_189914_9_ && j2 != p_189914_5_ && j2 != p_189914_8_ && k2 != p_189914_7_ && k2 != p_189914_10_) {
                        this.setBlockState(p_189914_1_, p_189914_12_, j2, i2, k2, p_189914_2_);
                        continue;
                    }
                    this.setBlockState(p_189914_1_, p_189914_11_, j2, i2, k2, p_189914_2_);
                }
            }
        }
    }

    protected void randomlyPlaceBlock(World worldIn, StructureBoundingBox boundingboxIn, Random rand, float chance, int x2, int y2, int z2, IBlockState blockstateIn) {
        if (rand.nextFloat() < chance) {
            this.setBlockState(worldIn, blockstateIn, x2, y2, z2, boundingboxIn);
        }
    }

    protected void randomlyRareFillWithBlocks(World worldIn, StructureBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockstateIn, boolean excludeAir) {
        float f2 = maxX - minX + 1;
        float f1 = maxY - minY + 1;
        float f22 = maxZ - minZ + 1;
        float f3 = (float)minX + f2 / 2.0f;
        float f4 = (float)minZ + f22 / 2.0f;
        for (int i2 = minY; i2 <= maxY; ++i2) {
            float f5 = (float)(i2 - minY) / f1;
            for (int j2 = minX; j2 <= maxX; ++j2) {
                float f6 = ((float)j2 - f3) / (f2 * 0.5f);
                for (int k2 = minZ; k2 <= maxZ; ++k2) {
                    float f8;
                    float f7 = ((float)k2 - f4) / (f22 * 0.5f);
                    if (excludeAir && this.getBlockStateFromPos(worldIn, j2, i2, k2, boundingboxIn).getMaterial() == Material.AIR || !((f8 = f6 * f6 + f5 * f5 + f7 * f7) <= 1.05f)) continue;
                    this.setBlockState(worldIn, blockstateIn, j2, i2, k2, boundingboxIn);
                }
            }
        }
    }

    protected void clearCurrentPositionBlocksUpwards(World worldIn, int x2, int y2, int z2, StructureBoundingBox structurebb) {
        BlockPos blockpos = new BlockPos(this.getXWithOffset(x2, z2), this.getYWithOffset(y2), this.getZWithOffset(x2, z2));
        if (structurebb.isVecInside(blockpos)) {
            while (!worldIn.isAirBlock(blockpos) && blockpos.getY() < 255) {
                worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
                blockpos = blockpos.up();
            }
        }
    }

    protected void replaceAirAndLiquidDownwards(World worldIn, IBlockState blockstateIn, int x2, int y2, int z2, StructureBoundingBox boundingboxIn) {
        int k2;
        int j2;
        int i2 = this.getXWithOffset(x2, z2);
        if (boundingboxIn.isVecInside(new BlockPos(i2, j2 = this.getYWithOffset(y2), k2 = this.getZWithOffset(x2, z2)))) {
            while ((worldIn.isAirBlock(new BlockPos(i2, j2, k2)) || worldIn.getBlockState(new BlockPos(i2, j2, k2)).getMaterial().isLiquid()) && j2 > 1) {
                worldIn.setBlockState(new BlockPos(i2, j2, k2), blockstateIn, 2);
                --j2;
            }
        }
    }

    protected boolean generateChest(World worldIn, StructureBoundingBox structurebb, Random randomIn, int x2, int y2, int z2, ResourceLocation loot) {
        BlockPos blockpos = new BlockPos(this.getXWithOffset(x2, z2), this.getYWithOffset(y2), this.getZWithOffset(x2, z2));
        return this.func_191080_a(worldIn, structurebb, randomIn, blockpos, loot, null);
    }

    protected boolean func_191080_a(World p_191080_1_, StructureBoundingBox p_191080_2_, Random p_191080_3_, BlockPos p_191080_4_, ResourceLocation p_191080_5_, @Nullable IBlockState p_191080_6_) {
        if (p_191080_2_.isVecInside(p_191080_4_) && p_191080_1_.getBlockState(p_191080_4_).getBlock() != Blocks.CHEST) {
            if (p_191080_6_ == null) {
                p_191080_6_ = Blocks.CHEST.correctFacing(p_191080_1_, p_191080_4_, Blocks.CHEST.getDefaultState());
            }
            p_191080_1_.setBlockState(p_191080_4_, p_191080_6_, 2);
            TileEntity tileentity = p_191080_1_.getTileEntity(p_191080_4_);
            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest)tileentity).setLootTable(p_191080_5_, p_191080_3_.nextLong());
            }
            return true;
        }
        return false;
    }

    protected boolean createDispenser(World p_189419_1_, StructureBoundingBox p_189419_2_, Random p_189419_3_, int p_189419_4_, int p_189419_5_, int p_189419_6_, EnumFacing p_189419_7_, ResourceLocation p_189419_8_) {
        BlockPos blockpos = new BlockPos(this.getXWithOffset(p_189419_4_, p_189419_6_), this.getYWithOffset(p_189419_5_), this.getZWithOffset(p_189419_4_, p_189419_6_));
        if (p_189419_2_.isVecInside(blockpos) && p_189419_1_.getBlockState(blockpos).getBlock() != Blocks.DISPENSER) {
            this.setBlockState(p_189419_1_, Blocks.DISPENSER.getDefaultState().withProperty(BlockDispenser.FACING, p_189419_7_), p_189419_4_, p_189419_5_, p_189419_6_, p_189419_2_);
            TileEntity tileentity = p_189419_1_.getTileEntity(blockpos);
            if (tileentity instanceof TileEntityDispenser) {
                ((TileEntityDispenser)tileentity).setLootTable(p_189419_8_, p_189419_3_.nextLong());
            }
            return true;
        }
        return false;
    }

    protected void func_189915_a(World p_189915_1_, StructureBoundingBox p_189915_2_, Random p_189915_3_, int p_189915_4_, int p_189915_5_, int p_189915_6_, EnumFacing p_189915_7_, BlockDoor p_189915_8_) {
        this.setBlockState(p_189915_1_, p_189915_8_.getDefaultState().withProperty(BlockDoor.FACING, p_189915_7_), p_189915_4_, p_189915_5_, p_189915_6_, p_189915_2_);
        this.setBlockState(p_189915_1_, p_189915_8_.getDefaultState().withProperty(BlockDoor.FACING, p_189915_7_).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), p_189915_4_, p_189915_5_ + 1, p_189915_6_, p_189915_2_);
    }

    public void offset(int x2, int y2, int z2) {
        this.boundingBox.offset(x2, y2, z2);
    }

    @Nullable
    public EnumFacing getCoordBaseMode() {
        return this.coordBaseMode;
    }

    public void setCoordBaseMode(@Nullable EnumFacing facing) {
        this.coordBaseMode = facing;
        if (facing == null) {
            this.rotation = Rotation.NONE;
            this.mirror = Mirror.NONE;
        } else {
            switch (facing) {
                case SOUTH: {
                    this.mirror = Mirror.LEFT_RIGHT;
                    this.rotation = Rotation.NONE;
                    break;
                }
                case WEST: {
                    this.mirror = Mirror.LEFT_RIGHT;
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                case EAST: {
                    this.mirror = Mirror.NONE;
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                default: {
                    this.mirror = Mirror.NONE;
                    this.rotation = Rotation.NONE;
                }
            }
        }
    }

    public static abstract class BlockSelector {
        protected IBlockState blockstate = Blocks.AIR.getDefaultState();

        public abstract void selectBlocks(Random var1, int var2, int var3, int var4, boolean var5);

        public IBlockState getBlockState() {
            return this.blockstate;
        }
    }
}

