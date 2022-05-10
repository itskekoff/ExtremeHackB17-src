package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TileEntityNote
extends TileEntity {
    public byte note;
    public boolean previousRedstoneState;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setByte("note", this.note);
        compound.setBoolean("powered", this.previousRedstoneState);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.note = compound.getByte("note");
        this.note = (byte)MathHelper.clamp(this.note, 0, 24);
        this.previousRedstoneState = compound.getBoolean("powered");
    }

    public void changePitch() {
        this.note = (byte)((this.note + 1) % 25);
        this.markDirty();
    }

    public void triggerNote(World worldIn, BlockPos posIn) {
        if (worldIn.getBlockState(posIn.up()).getMaterial() == Material.AIR) {
            Block block;
            IBlockState iblockstate = worldIn.getBlockState(posIn.down());
            Material material = iblockstate.getMaterial();
            int i2 = 0;
            if (material == Material.ROCK) {
                i2 = 1;
            }
            if (material == Material.SAND) {
                i2 = 2;
            }
            if (material == Material.GLASS) {
                i2 = 3;
            }
            if (material == Material.WOOD) {
                i2 = 4;
            }
            if ((block = iblockstate.getBlock()) == Blocks.CLAY) {
                i2 = 5;
            }
            if (block == Blocks.GOLD_BLOCK) {
                i2 = 6;
            }
            if (block == Blocks.WOOL) {
                i2 = 7;
            }
            if (block == Blocks.PACKED_ICE) {
                i2 = 8;
            }
            if (block == Blocks.BONE_BLOCK) {
                i2 = 9;
            }
            worldIn.addBlockEvent(posIn, Blocks.NOTEBLOCK, i2, this.note);
        }
    }
}

