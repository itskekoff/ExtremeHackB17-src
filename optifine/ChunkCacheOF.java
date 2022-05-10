package optifine;

import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import optifine.ArrayCache;
import optifine.Config;
import optifine.DynamicLights;
import optifine.Reflector;

public class ChunkCacheOF
implements IBlockAccess {
    private ChunkCache chunkCache;
    private int posX;
    private int posY;
    private int posZ;
    private int[] combinedLights;
    private IBlockState[] blockStates;
    private static ArrayCache cacheCombinedLights = new ArrayCache(Integer.TYPE, 16);
    private static ArrayCache cacheBlockStates = new ArrayCache(IBlockState.class, 16);
    private static final int ARRAY_SIZE = 8000;

    public ChunkCacheOF(ChunkCache p_i22_1_, BlockPos p_i22_2_, int p_i22_3_) {
        this.chunkCache = p_i22_1_;
        this.posX = p_i22_2_.getX() - p_i22_3_;
        this.posY = p_i22_2_.getY() - p_i22_3_;
        this.posZ = p_i22_2_.getZ() - p_i22_3_;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        if (this.combinedLights == null) {
            int k2 = this.chunkCache.getCombinedLight(pos, lightValue);
            if (Config.isDynamicLights() && !this.getBlockState(pos).isOpaqueCube()) {
                k2 = DynamicLights.getCombinedLight(pos, k2);
            }
            return k2;
        }
        int i2 = this.getPositionIndex(pos);
        if (i2 >= 0 && i2 < this.combinedLights.length) {
            int j2 = this.combinedLights[i2];
            if (j2 == -1) {
                j2 = this.chunkCache.getCombinedLight(pos, lightValue);
                if (Config.isDynamicLights() && !this.getBlockState(pos).isOpaqueCube()) {
                    j2 = DynamicLights.getCombinedLight(pos, j2);
                }
                this.combinedLights[i2] = j2;
            }
            return j2;
        }
        return this.chunkCache.getCombinedLight(pos, lightValue);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (this.blockStates == null) {
            return this.chunkCache.getBlockState(pos);
        }
        int i2 = this.getPositionIndex(pos);
        if (i2 >= 0 && i2 < this.blockStates.length) {
            IBlockState iblockstate = this.blockStates[i2];
            if (iblockstate == null) {
                this.blockStates[i2] = iblockstate = this.chunkCache.getBlockState(pos);
            }
            return iblockstate;
        }
        return this.chunkCache.getBlockState(pos);
    }

    private int getPositionIndex(BlockPos p_getPositionIndex_1_) {
        int i2 = p_getPositionIndex_1_.getX() - this.posX;
        int j2 = p_getPositionIndex_1_.getY() - this.posY;
        int k2 = p_getPositionIndex_1_.getZ() - this.posZ;
        return i2 * 400 + k2 * 20 + j2;
    }

    public void renderStart() {
        if (this.combinedLights == null) {
            this.combinedLights = (int[])cacheCombinedLights.allocate(8000);
        }
        Arrays.fill(this.combinedLights, -1);
        if (this.blockStates == null) {
            this.blockStates = (IBlockState[])cacheBlockStates.allocate(8000);
        }
        Arrays.fill(this.blockStates, null);
    }

    public void renderFinish() {
        cacheCombinedLights.free(this.combinedLights);
        this.combinedLights = null;
        cacheBlockStates.free(this.blockStates);
        this.blockStates = null;
    }

    public boolean isEmpty() {
        return this.chunkCache.extendedLevelsInChunkCache();
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return this.chunkCache.getBiome(pos);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.chunkCache.getStrongPower(pos, direction);
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return this.chunkCache.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
    }

    public TileEntity getTileEntity(BlockPos p_getTileEntity_1_, Chunk.EnumCreateEntityType p_getTileEntity_2_) {
        return this.chunkCache.getTileEntity(p_getTileEntity_1_, p_getTileEntity_2_);
    }

    @Override
    public WorldType getWorldType() {
        return this.chunkCache.getWorldType();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.chunkCache.isAirBlock(pos);
    }

    public boolean isSideSolid(BlockPos p_isSideSolid_1_, EnumFacing p_isSideSolid_2_, boolean p_isSideSolid_3_) {
        return Reflector.callBoolean(this.chunkCache, Reflector.ForgeChunkCache_isSideSolid, p_isSideSolid_1_, p_isSideSolid_2_, p_isSideSolid_3_);
    }
}

