package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkGeneratorDebug;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ExtendedBlockStorage NULL_BLOCK_STORAGE = null;
    private final ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
    private final byte[] blockBiomeArray = new byte[256];
    private final int[] precipitationHeightMap = new int[256];
    private final boolean[] updateSkylightColumns = new boolean[256];
    private boolean isChunkLoaded;
    private final World worldObj;
    private final int[] heightMap;
    public final int xPosition;
    public final int zPosition;
    private boolean isGapLightingUpdated;
    private final Map<BlockPos, TileEntity> chunkTileEntityMap = Maps.newHashMap();
    private final ClassInheritanceMultiMap<Entity>[] entityLists;
    private boolean isTerrainPopulated;
    private boolean isLightPopulated;
    private boolean chunkTicked;
    private boolean isModified;
    private boolean hasEntities;
    private long lastSaveTime;
    private int heightMapMinimum;
    private long inhabitedTime;
    private int queuedLightChecks = 4096;
    private final ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue = Queues.newConcurrentLinkedQueue();
    public boolean unloaded;

    public Chunk(World worldIn, int x2, int z2) {
        this.entityLists = new ClassInheritanceMultiMap[16];
        this.worldObj = worldIn;
        this.xPosition = x2;
        this.zPosition = z2;
        this.heightMap = new int[256];
        for (int i2 = 0; i2 < this.entityLists.length; ++i2) {
            this.entityLists[i2] = new ClassInheritanceMultiMap<Entity>(Entity.class);
        }
        Arrays.fill(this.precipitationHeightMap, -999);
        Arrays.fill(this.getBlockBiomeArray(), (byte)-1);
    }

    public Chunk(World worldIn, ChunkPrimer primer, int x2, int z2) {
        this(worldIn, x2, z2);
        int i2 = 256;
        boolean flag = worldIn.provider.func_191066_m();
        for (int j2 = 0; j2 < 16; ++j2) {
            for (int k2 = 0; k2 < 16; ++k2) {
                for (int l2 = 0; l2 < 256; ++l2) {
                    IBlockState iblockstate = primer.getBlockState(j2, l2, k2);
                    if (iblockstate.getMaterial() == Material.AIR) continue;
                    int i1 = l2 >> 4;
                    if (this.storageArrays[i1] == NULL_BLOCK_STORAGE) {
                        this.storageArrays[i1] = new ExtendedBlockStorage(i1 << 4, flag);
                    }
                    this.storageArrays[i1].set(j2, l2 & 0xF, k2, iblockstate);
                }
            }
        }
    }

    public boolean isAtLocation(int x2, int z2) {
        return x2 == this.xPosition && z2 == this.zPosition;
    }

    public int getHeight(BlockPos pos) {
        return this.getHeightValue(pos.getX() & 0xF, pos.getZ() & 0xF);
    }

    public int getHeightValue(int x2, int z2) {
        return this.heightMap[z2 << 4 | x2];
    }

    @Nullable
    private ExtendedBlockStorage getLastExtendedBlockStorage() {
        for (int i2 = this.storageArrays.length - 1; i2 >= 0; --i2) {
            if (this.storageArrays[i2] == NULL_BLOCK_STORAGE) continue;
            return this.storageArrays[i2];
        }
        return null;
    }

    public int getTopFilledSegment() {
        ExtendedBlockStorage extendedblockstorage = this.getLastExtendedBlockStorage();
        return extendedblockstorage == null ? 0 : extendedblockstorage.getYLocation();
    }

    public ExtendedBlockStorage[] getBlockStorageArray() {
        return this.storageArrays;
    }

    protected void generateHeightMap() {
        int i2 = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;
        for (int j2 = 0; j2 < 16; ++j2) {
            block1: for (int k2 = 0; k2 < 16; ++k2) {
                this.precipitationHeightMap[j2 + (k2 << 4)] = -999;
                for (int l2 = i2 + 16; l2 > 0; --l2) {
                    IBlockState iblockstate = this.getBlockState(j2, l2 - 1, k2);
                    if (iblockstate.getLightOpacity() == 0) continue;
                    this.heightMap[k2 << 4 | j2] = l2;
                    if (l2 >= this.heightMapMinimum) continue block1;
                    this.heightMapMinimum = l2;
                    continue block1;
                }
            }
        }
        this.isModified = true;
    }

    public void generateSkylightMap() {
        int i2 = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;
        for (int j2 = 0; j2 < 16; ++j2) {
            for (int k2 = 0; k2 < 16; ++k2) {
                this.precipitationHeightMap[j2 + (k2 << 4)] = -999;
                for (int l2 = i2 + 16; l2 > 0; --l2) {
                    if (this.getBlockLightOpacity(j2, l2 - 1, k2) == 0) continue;
                    this.heightMap[k2 << 4 | j2] = l2;
                    if (l2 >= this.heightMapMinimum) break;
                    this.heightMapMinimum = l2;
                    break;
                }
                if (!this.worldObj.provider.func_191066_m()) continue;
                int k1 = 15;
                int i1 = i2 + 16 - 1;
                do {
                    ExtendedBlockStorage extendedblockstorage;
                    int j1;
                    if ((j1 = this.getBlockLightOpacity(j2, i1, k2)) == 0 && k1 != 15) {
                        j1 = 1;
                    }
                    if ((k1 -= j1) <= 0 || (extendedblockstorage = this.storageArrays[i1 >> 4]) == NULL_BLOCK_STORAGE) continue;
                    extendedblockstorage.setExtSkylightValue(j2, i1 & 0xF, k2, k1);
                    this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + j2, i1, (this.zPosition << 4) + k2));
                } while (--i1 > 0 && k1 > 0);
            }
        }
        this.isModified = true;
    }

    private void propagateSkylightOcclusion(int x2, int z2) {
        this.updateSkylightColumns[x2 + z2 * 16] = true;
        this.isGapLightingUpdated = true;
    }

    private void recheckGaps(boolean p_150803_1_) {
        this.worldObj.theProfiler.startSection("recheckGaps");
        if (this.worldObj.isAreaLoaded(new BlockPos(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8), 16)) {
            for (int i2 = 0; i2 < 16; ++i2) {
                for (int j2 = 0; j2 < 16; ++j2) {
                    if (!this.updateSkylightColumns[i2 + j2 * 16]) continue;
                    this.updateSkylightColumns[i2 + j2 * 16] = false;
                    int k2 = this.getHeightValue(i2, j2);
                    int l2 = this.xPosition * 16 + i2;
                    int i1 = this.zPosition * 16 + j2;
                    int j1 = Integer.MAX_VALUE;
                    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                        j1 = Math.min(j1, this.worldObj.getChunksLowestHorizon(l2 + enumfacing.getFrontOffsetX(), i1 + enumfacing.getFrontOffsetZ()));
                    }
                    this.checkSkylightNeighborHeight(l2, i1, j1);
                    for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                        this.checkSkylightNeighborHeight(l2 + enumfacing1.getFrontOffsetX(), i1 + enumfacing1.getFrontOffsetZ(), k2);
                    }
                    if (!p_150803_1_) continue;
                    this.worldObj.theProfiler.endSection();
                    return;
                }
            }
            this.isGapLightingUpdated = false;
        }
        this.worldObj.theProfiler.endSection();
    }

    private void checkSkylightNeighborHeight(int x2, int z2, int maxValue) {
        int i2 = this.worldObj.getHeight(new BlockPos(x2, 0, z2)).getY();
        if (i2 > maxValue) {
            this.updateSkylightNeighborHeight(x2, z2, maxValue, i2 + 1);
        } else if (i2 < maxValue) {
            this.updateSkylightNeighborHeight(x2, z2, i2, maxValue + 1);
        }
    }

    private void updateSkylightNeighborHeight(int x2, int z2, int startY, int endY) {
        if (endY > startY && this.worldObj.isAreaLoaded(new BlockPos(x2, 0, z2), 16)) {
            for (int i2 = startY; i2 < endY; ++i2) {
                this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x2, i2, z2));
            }
            this.isModified = true;
        }
    }

    private void relightBlock(int x2, int y2, int z2) {
        int i2;
        int j2 = i2 = this.heightMap[z2 << 4 | x2] & 0xFF;
        if (y2 > i2) {
            j2 = y2;
        }
        while (j2 > 0 && this.getBlockLightOpacity(x2, j2 - 1, z2) == 0) {
            --j2;
        }
        if (j2 != i2) {
            this.worldObj.markBlocksDirtyVertical(x2 + this.xPosition * 16, z2 + this.zPosition * 16, j2, i2);
            this.heightMap[z2 << 4 | x2] = j2;
            int k2 = this.xPosition * 16 + x2;
            int l2 = this.zPosition * 16 + z2;
            if (this.worldObj.provider.func_191066_m()) {
                if (j2 < i2) {
                    for (int j1 = j2; j1 < i2; ++j1) {
                        ExtendedBlockStorage extendedblockstorage2 = this.storageArrays[j1 >> 4];
                        if (extendedblockstorage2 == NULL_BLOCK_STORAGE) continue;
                        extendedblockstorage2.setExtSkylightValue(x2, j1 & 0xF, z2, 15);
                        this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x2, j1, (this.zPosition << 4) + z2));
                    }
                } else {
                    for (int i1 = i2; i1 < j2; ++i1) {
                        ExtendedBlockStorage extendedblockstorage = this.storageArrays[i1 >> 4];
                        if (extendedblockstorage == NULL_BLOCK_STORAGE) continue;
                        extendedblockstorage.setExtSkylightValue(x2, i1 & 0xF, z2, 0);
                        this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x2, i1, (this.zPosition << 4) + z2));
                    }
                }
                int k1 = 15;
                while (j2 > 0 && k1 > 0) {
                    ExtendedBlockStorage extendedblockstorage1;
                    int i22;
                    if ((i22 = this.getBlockLightOpacity(x2, --j2, z2)) == 0) {
                        i22 = 1;
                    }
                    if ((k1 -= i22) < 0) {
                        k1 = 0;
                    }
                    if ((extendedblockstorage1 = this.storageArrays[j2 >> 4]) == NULL_BLOCK_STORAGE) continue;
                    extendedblockstorage1.setExtSkylightValue(x2, j2 & 0xF, z2, k1);
                }
            }
            int l1 = this.heightMap[z2 << 4 | x2];
            int j22 = i2;
            int k22 = l1;
            if (l1 < i2) {
                j22 = l1;
                k22 = i2;
            }
            if (l1 < this.heightMapMinimum) {
                this.heightMapMinimum = l1;
            }
            if (this.worldObj.provider.func_191066_m()) {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    this.updateSkylightNeighborHeight(k2 + enumfacing.getFrontOffsetX(), l2 + enumfacing.getFrontOffsetZ(), j22, k22);
                }
                this.updateSkylightNeighborHeight(k2, l2, j22, k22);
            }
            this.isModified = true;
        }
    }

    public int getBlockLightOpacity(BlockPos pos) {
        return this.getBlockState(pos).getLightOpacity();
    }

    private int getBlockLightOpacity(int x2, int y2, int z2) {
        return this.getBlockState(x2, y2, z2).getLightOpacity();
    }

    public IBlockState getBlockState(BlockPos pos) {
        return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }

    public IBlockState getBlockState(final int x2, final int y2, final int z2) {
        if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD) {
            IBlockState iblockstate = null;
            if (y2 == 60) {
                iblockstate = Blocks.BARRIER.getDefaultState();
            }
            if (y2 == 70) {
                iblockstate = ChunkGeneratorDebug.getBlockStateFor(x2, z2);
            }
            return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
        }
        try {
            ExtendedBlockStorage extendedblockstorage;
            if (y2 >= 0 && y2 >> 4 < this.storageArrays.length && (extendedblockstorage = this.storageArrays[y2 >> 4]) != NULL_BLOCK_STORAGE) {
                return extendedblockstorage.get(x2 & 0xF, y2 & 0xF, z2 & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.setDetail("Location", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    return CrashReportCategory.getCoordinateInfo(x2, y2, z2);
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    @Nullable
    public IBlockState setBlockState(BlockPos pos, IBlockState state) {
        TileEntity tileentity;
        int k2;
        int l2;
        int i2 = pos.getX() & 0xF;
        int j2 = pos.getY();
        if (j2 >= this.precipitationHeightMap[l2 = (k2 = pos.getZ() & 0xF) << 4 | i2] - 1) {
            this.precipitationHeightMap[l2] = -999;
        }
        int i1 = this.heightMap[l2];
        IBlockState iblockstate = this.getBlockState(pos);
        if (iblockstate == state) {
            return null;
        }
        Block block = state.getBlock();
        Block block1 = iblockstate.getBlock();
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j2 >> 4];
        boolean flag = false;
        if (extendedblockstorage == NULL_BLOCK_STORAGE) {
            if (block == Blocks.AIR) {
                return null;
            }
            this.storageArrays[j2 >> 4] = extendedblockstorage = new ExtendedBlockStorage(j2 >> 4 << 4, this.worldObj.provider.func_191066_m());
            flag = j2 >= i1;
        }
        extendedblockstorage.set(i2, j2 & 0xF, k2, state);
        if (block1 != block) {
            if (!this.worldObj.isRemote) {
                block1.breakBlock(this.worldObj, pos, iblockstate);
            } else if (block1 instanceof ITileEntityProvider) {
                this.worldObj.removeTileEntity(pos);
            }
        }
        if (extendedblockstorage.get(i2, j2 & 0xF, k2).getBlock() != block) {
            return null;
        }
        if (flag) {
            this.generateSkylightMap();
        } else {
            int j1 = state.getLightOpacity();
            int k1 = iblockstate.getLightOpacity();
            if (j1 > 0) {
                if (j2 >= i1) {
                    this.relightBlock(i2, j2 + 1, k2);
                }
            } else if (j2 == i1 - 1) {
                this.relightBlock(i2, j2, k2);
            }
            if (j1 != k1 && (j1 < k1 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
                this.propagateSkylightOcclusion(i2, k2);
            }
        }
        if (block1 instanceof ITileEntityProvider && (tileentity = this.getTileEntity(pos, EnumCreateEntityType.CHECK)) != null) {
            tileentity.updateContainingBlockInfo();
        }
        if (!this.worldObj.isRemote && block1 != block) {
            block.onBlockAdded(this.worldObj, pos, state);
        }
        if (block instanceof ITileEntityProvider) {
            TileEntity tileentity1 = this.getTileEntity(pos, EnumCreateEntityType.CHECK);
            if (tileentity1 == null) {
                tileentity1 = ((ITileEntityProvider)((Object)block)).createNewTileEntity(this.worldObj, block.getMetaFromState(state));
                this.worldObj.setTileEntity(pos, tileentity1);
            }
            if (tileentity1 != null) {
                tileentity1.updateContainingBlockInfo();
            }
        }
        this.isModified = true;
        return iblockstate;
    }

    public int getLightFor(EnumSkyBlock p_177413_1_, BlockPos pos) {
        int i2 = pos.getX() & 0xF;
        int j2 = pos.getY();
        int k2 = pos.getZ() & 0xF;
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j2 >> 4];
        if (extendedblockstorage == NULL_BLOCK_STORAGE) {
            return this.canSeeSky(pos) ? p_177413_1_.defaultLightValue : 0;
        }
        if (p_177413_1_ == EnumSkyBlock.SKY) {
            return !this.worldObj.provider.func_191066_m() ? 0 : extendedblockstorage.getExtSkylightValue(i2, j2 & 0xF, k2);
        }
        return p_177413_1_ == EnumSkyBlock.BLOCK ? extendedblockstorage.getExtBlocklightValue(i2, j2 & 0xF, k2) : p_177413_1_.defaultLightValue;
    }

    public void setLightFor(EnumSkyBlock p_177431_1_, BlockPos pos, int value) {
        int i2 = pos.getX() & 0xF;
        int j2 = pos.getY();
        int k2 = pos.getZ() & 0xF;
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j2 >> 4];
        if (extendedblockstorage == NULL_BLOCK_STORAGE) {
            this.storageArrays[j2 >> 4] = extendedblockstorage = new ExtendedBlockStorage(j2 >> 4 << 4, this.worldObj.provider.func_191066_m());
            this.generateSkylightMap();
        }
        this.isModified = true;
        if (p_177431_1_ == EnumSkyBlock.SKY) {
            if (this.worldObj.provider.func_191066_m()) {
                extendedblockstorage.setExtSkylightValue(i2, j2 & 0xF, k2, value);
            }
        } else if (p_177431_1_ == EnumSkyBlock.BLOCK) {
            extendedblockstorage.setExtBlocklightValue(i2, j2 & 0xF, k2, value);
        }
    }

    public int getLightSubtracted(BlockPos pos, int amount) {
        int i2 = pos.getX() & 0xF;
        int j2 = pos.getY();
        int k2 = pos.getZ() & 0xF;
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j2 >> 4];
        if (extendedblockstorage == NULL_BLOCK_STORAGE) {
            return this.worldObj.provider.func_191066_m() && amount < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - amount : 0;
        }
        int l2 = !this.worldObj.provider.func_191066_m() ? 0 : extendedblockstorage.getExtSkylightValue(i2, j2 & 0xF, k2);
        int i1 = extendedblockstorage.getExtBlocklightValue(i2, j2 & 0xF, k2);
        if (i1 > (l2 -= amount)) {
            l2 = i1;
        }
        return l2;
    }

    public void addEntity(Entity entityIn) {
        int k2;
        this.hasEntities = true;
        int i2 = MathHelper.floor(entityIn.posX / 16.0);
        int j2 = MathHelper.floor(entityIn.posZ / 16.0);
        if (i2 != this.xPosition || j2 != this.zPosition) {
            LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", (Object)i2, (Object)j2, (Object)this.xPosition, (Object)this.zPosition, (Object)entityIn);
            entityIn.setDead();
        }
        if ((k2 = MathHelper.floor(entityIn.posY / 16.0)) < 0) {
            k2 = 0;
        }
        if (k2 >= this.entityLists.length) {
            k2 = this.entityLists.length - 1;
        }
        entityIn.addedToChunk = true;
        entityIn.chunkCoordX = this.xPosition;
        entityIn.chunkCoordY = k2;
        entityIn.chunkCoordZ = this.zPosition;
        this.entityLists[k2].add(entityIn);
    }

    public void removeEntity(Entity entityIn) {
        this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
    }

    public void removeEntityAtIndex(Entity entityIn, int index) {
        if (index < 0) {
            index = 0;
        }
        if (index >= this.entityLists.length) {
            index = this.entityLists.length - 1;
        }
        this.entityLists[index].remove(entityIn);
    }

    public boolean canSeeSky(BlockPos pos) {
        int k2;
        int i2 = pos.getX() & 0xF;
        int j2 = pos.getY();
        return j2 >= this.heightMap[(k2 = pos.getZ() & 0xF) << 4 | i2];
    }

    @Nullable
    private TileEntity createNewTileEntity(BlockPos pos) {
        IBlockState iblockstate = this.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return !block.hasTileEntity() ? null : ((ITileEntityProvider)((Object)block)).createNewTileEntity(this.worldObj, iblockstate.getBlock().getMetaFromState(iblockstate));
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos, EnumCreateEntityType p_177424_2_) {
        TileEntity tileentity = this.chunkTileEntityMap.get(pos);
        if (tileentity == null) {
            if (p_177424_2_ == EnumCreateEntityType.IMMEDIATE) {
                tileentity = this.createNewTileEntity(pos);
                this.worldObj.setTileEntity(pos, tileentity);
            } else if (p_177424_2_ == EnumCreateEntityType.QUEUED) {
                this.tileEntityPosQueue.add(pos);
            }
        } else if (tileentity.isInvalid()) {
            this.chunkTileEntityMap.remove(pos);
            return null;
        }
        return tileentity;
    }

    public void addTileEntity(TileEntity tileEntityIn) {
        this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
        if (this.isChunkLoaded) {
            this.worldObj.addTileEntity(tileEntityIn);
        }
    }

    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        tileEntityIn.setWorldObj(this.worldObj);
        tileEntityIn.setPos(pos);
        if (this.getBlockState(pos).getBlock() instanceof ITileEntityProvider) {
            if (this.chunkTileEntityMap.containsKey(pos)) {
                this.chunkTileEntityMap.get(pos).invalidate();
            }
            tileEntityIn.validate();
            this.chunkTileEntityMap.put(pos, tileEntityIn);
        }
    }

    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity;
        if (this.isChunkLoaded && (tileentity = this.chunkTileEntityMap.remove(pos)) != null) {
            tileentity.invalidate();
        }
    }

    public void onChunkLoad() {
        this.isChunkLoaded = true;
        this.worldObj.addTileEntities(this.chunkTileEntityMap.values());
        ClassInheritanceMultiMap<Entity>[] arrclassInheritanceMultiMap = this.entityLists;
        int n2 = this.entityLists.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            ClassInheritanceMultiMap<Entity> classinheritancemultimap = arrclassInheritanceMultiMap[i2];
            this.worldObj.loadEntities(classinheritancemultimap);
        }
    }

    public void onChunkUnload() {
        this.isChunkLoaded = false;
        for (TileEntity tileentity : this.chunkTileEntityMap.values()) {
            this.worldObj.markTileEntityForRemoval(tileentity);
        }
        ClassInheritanceMultiMap<Entity>[] arrclassInheritanceMultiMap = this.entityLists;
        int n2 = this.entityLists.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            ClassInheritanceMultiMap<Entity> classinheritancemultimap = arrclassInheritanceMultiMap[i2];
            this.worldObj.unloadEntities(classinheritancemultimap);
        }
    }

    public void setChunkModified() {
        this.isModified = true;
    }

    public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> p_177414_4_) {
        int i2 = MathHelper.floor((aabb.minY - 2.0) / 16.0);
        int j2 = MathHelper.floor((aabb.maxY + 2.0) / 16.0);
        i2 = MathHelper.clamp(i2, 0, this.entityLists.length - 1);
        j2 = MathHelper.clamp(j2, 0, this.entityLists.length - 1);
        for (int k2 = i2; k2 <= j2; ++k2) {
            if (this.entityLists[k2].isEmpty()) continue;
            for (Entity entity : this.entityLists[k2]) {
                Entity[] aentity;
                if (!entity.getEntityBoundingBox().intersectsWith(aabb) || entity == entityIn) continue;
                if (p_177414_4_ == null || p_177414_4_.apply(entity)) {
                    listToFill.add(entity);
                }
                if ((aentity = entity.getParts()) == null) continue;
                Entity[] arrentity = aentity;
                int n2 = aentity.length;
                for (int i3 = 0; i3 < n2; ++i3) {
                    Entity entity1 = arrentity[i3];
                    if (entity1 == entityIn || !entity1.getEntityBoundingBox().intersectsWith(aabb) || p_177414_4_ != null && !p_177414_4_.apply(entity1)) continue;
                    listToFill.add(entity1);
                }
            }
        }
    }

    public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter) {
        int i2 = MathHelper.floor((aabb.minY - 2.0) / 16.0);
        int j2 = MathHelper.floor((aabb.maxY + 2.0) / 16.0);
        i2 = MathHelper.clamp(i2, 0, this.entityLists.length - 1);
        j2 = MathHelper.clamp(j2, 0, this.entityLists.length - 1);
        for (int k2 = i2; k2 <= j2; ++k2) {
            for (Entity t2 : this.entityLists[k2].getByClass(entityClass)) {
                if (!t2.getEntityBoundingBox().intersectsWith(aabb) || filter != null && !filter.apply(t2)) continue;
                listToFill.add(t2);
            }
        }
    }

    public boolean needsSaving(boolean p_76601_1_) {
        if (p_76601_1_ ? this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified : this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L) {
            return true;
        }
        return this.isModified;
    }

    public Random getRandomWithSeed(long seed) {
        return new Random(this.worldObj.getSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ seed);
    }

    public boolean isEmpty() {
        return false;
    }

    public void populateChunk(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator) {
        Chunk chunk4;
        Chunk chunk = chunkProvider.getLoadedChunk(this.xPosition, this.zPosition - 1);
        Chunk chunk1 = chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition);
        Chunk chunk2 = chunkProvider.getLoadedChunk(this.xPosition, this.zPosition + 1);
        Chunk chunk3 = chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition);
        if (chunk1 != null && chunk2 != null && chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition + 1) != null) {
            this.populateChunk(chunkGenrator);
        }
        if (chunk3 != null && chunk2 != null && chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition + 1) != null) {
            chunk3.populateChunk(chunkGenrator);
        }
        if (chunk != null && chunk1 != null && chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition - 1) != null) {
            chunk.populateChunk(chunkGenrator);
        }
        if (chunk != null && chunk3 != null && (chunk4 = chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition - 1)) != null) {
            chunk4.populateChunk(chunkGenrator);
        }
    }

    protected void populateChunk(IChunkGenerator generator) {
        if (this.isTerrainPopulated()) {
            if (generator.generateStructures(this, this.xPosition, this.zPosition)) {
                this.setChunkModified();
            }
        } else {
            this.checkLight();
            generator.populate(this.xPosition, this.zPosition);
            this.setChunkModified();
        }
    }

    public BlockPos getPrecipitationHeight(BlockPos pos) {
        int i2 = pos.getX() & 0xF;
        int j2 = pos.getZ() & 0xF;
        int k2 = i2 | j2 << 4;
        BlockPos blockpos = new BlockPos(pos.getX(), this.precipitationHeightMap[k2], pos.getZ());
        if (blockpos.getY() == -999) {
            int l2 = this.getTopFilledSegment() + 15;
            blockpos = new BlockPos(pos.getX(), l2, pos.getZ());
            int i1 = -1;
            while (blockpos.getY() > 0 && i1 == -1) {
                IBlockState iblockstate = this.getBlockState(blockpos);
                Material material = iblockstate.getMaterial();
                if (!material.blocksMovement() && !material.isLiquid()) {
                    blockpos = blockpos.down();
                    continue;
                }
                i1 = blockpos.getY() + 1;
            }
            this.precipitationHeightMap[k2] = i1;
        }
        return new BlockPos(pos.getX(), this.precipitationHeightMap[k2], pos.getZ());
    }

    public void onTick(boolean p_150804_1_) {
        if (this.isGapLightingUpdated && this.worldObj.provider.func_191066_m() && !p_150804_1_) {
            this.recheckGaps(this.worldObj.isRemote);
        }
        this.chunkTicked = true;
        if (!this.isLightPopulated && this.isTerrainPopulated) {
            this.checkLight();
        }
        while (!this.tileEntityPosQueue.isEmpty()) {
            BlockPos blockpos = this.tileEntityPosQueue.poll();
            if (this.getTileEntity(blockpos, EnumCreateEntityType.CHECK) != null || !this.getBlockState(blockpos).getBlock().hasTileEntity()) continue;
            TileEntity tileentity = this.createNewTileEntity(blockpos);
            this.worldObj.setTileEntity(blockpos, tileentity);
            this.worldObj.markBlockRangeForRenderUpdate(blockpos, blockpos);
        }
    }

    public boolean isPopulated() {
        return this.chunkTicked && this.isTerrainPopulated && this.isLightPopulated;
    }

    public boolean isChunkTicked() {
        return this.chunkTicked;
    }

    public ChunkPos getChunkCoordIntPair() {
        return new ChunkPos(this.xPosition, this.zPosition);
    }

    public boolean getAreLevelsEmpty(int startY, int endY) {
        if (startY < 0) {
            startY = 0;
        }
        if (endY >= 256) {
            endY = 255;
        }
        for (int i2 = startY; i2 <= endY; i2 += 16) {
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[i2 >> 4];
            if (extendedblockstorage == NULL_BLOCK_STORAGE || extendedblockstorage.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public void setStorageArrays(ExtendedBlockStorage[] newStorageArrays) {
        if (this.storageArrays.length != newStorageArrays.length) {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)newStorageArrays.length, (Object)this.storageArrays.length);
        } else {
            System.arraycopy(newStorageArrays, 0, this.storageArrays, 0, this.storageArrays.length);
        }
    }

    public void fillChunk(PacketBuffer buf2, int p_186033_2_, boolean p_186033_3_) {
        boolean flag = this.worldObj.provider.func_191066_m();
        for (int i2 = 0; i2 < this.storageArrays.length; ++i2) {
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[i2];
            if ((p_186033_2_ & 1 << i2) == 0) {
                if (!p_186033_3_ || extendedblockstorage == NULL_BLOCK_STORAGE) continue;
                this.storageArrays[i2] = NULL_BLOCK_STORAGE;
                continue;
            }
            if (extendedblockstorage == NULL_BLOCK_STORAGE) {
                this.storageArrays[i2] = extendedblockstorage = new ExtendedBlockStorage(i2 << 4, flag);
            }
            extendedblockstorage.getData().read(buf2);
            buf2.readBytes(extendedblockstorage.getBlocklightArray().getData());
            if (!flag) continue;
            buf2.readBytes(extendedblockstorage.getSkylightArray().getData());
        }
        if (p_186033_3_) {
            buf2.readBytes(this.getBlockBiomeArray());
        }
        for (int j2 = 0; j2 < this.storageArrays.length; ++j2) {
            if (this.storageArrays[j2] == NULL_BLOCK_STORAGE || (p_186033_2_ & 1 << j2) == 0) continue;
            this.storageArrays[j2].removeInvalidBlocks();
        }
        this.isLightPopulated = true;
        this.isTerrainPopulated = true;
        this.generateHeightMap();
        for (TileEntity tileentity : this.chunkTileEntityMap.values()) {
            tileentity.updateContainingBlockInfo();
        }
    }

    public Biome getBiome(BlockPos pos, BiomeProvider provider) {
        Biome biome1;
        int i2 = pos.getX() & 0xF;
        int j2 = pos.getZ() & 0xF;
        int k2 = this.getBlockBiomeArray()[j2 << 4 | i2] & 0xFF;
        if (k2 == 255) {
            Biome biome = provider.getBiome(pos, Biomes.PLAINS);
            k2 = Biome.getIdForBiome(biome);
            this.getBlockBiomeArray()[j2 << 4 | i2] = (byte)(k2 & 0xFF);
        }
        return (biome1 = Biome.getBiome(k2)) == null ? Biomes.PLAINS : biome1;
    }

    public byte[] getBiomeArray() {
        return this.getBlockBiomeArray();
    }

    public void setBiomeArray(byte[] biomeArray) {
        if (this.getBlockBiomeArray().length != biomeArray.length) {
            LOGGER.warn("Could not set level chunk biomes, array length is {} instead of {}", (Object)biomeArray.length, (Object)this.getBlockBiomeArray().length);
        } else {
            System.arraycopy(biomeArray, 0, this.getBlockBiomeArray(), 0, this.getBlockBiomeArray().length);
        }
    }

    public void resetRelightChecks() {
        this.queuedLightChecks = 0;
    }

    public void enqueueRelightChecks() {
        if (this.queuedLightChecks < 4096) {
            BlockPos blockpos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);
            for (int i2 = 0; i2 < 8; ++i2) {
                if (this.queuedLightChecks >= 4096) {
                    return;
                }
                int j2 = this.queuedLightChecks % 16;
                int k2 = this.queuedLightChecks / 16 % 16;
                int l2 = this.queuedLightChecks / 256;
                ++this.queuedLightChecks;
                for (int i1 = 0; i1 < 16; ++i1) {
                    boolean flag;
                    BlockPos blockpos1 = blockpos.add(k2, (j2 << 4) + i1, l2);
                    boolean bl2 = flag = i1 == 0 || i1 == 15 || k2 == 0 || k2 == 15 || l2 == 0 || l2 == 15;
                    if ((this.storageArrays[j2] != NULL_BLOCK_STORAGE || !flag) && (this.storageArrays[j2] == NULL_BLOCK_STORAGE || this.storageArrays[j2].get(k2, i1, l2).getMaterial() != Material.AIR)) continue;
                    for (EnumFacing enumfacing : EnumFacing.values()) {
                        BlockPos blockpos2 = blockpos1.offset(enumfacing);
                        if (this.worldObj.getBlockState(blockpos2).getLightValue() <= 0) continue;
                        this.worldObj.checkLight(blockpos2);
                    }
                    this.worldObj.checkLight(blockpos1);
                }
            }
        }
    }

    public void checkLight() {
        this.isTerrainPopulated = true;
        this.isLightPopulated = true;
        BlockPos blockpos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);
        if (this.worldObj.provider.func_191066_m()) {
            if (this.worldObj.isAreaLoaded(blockpos.add(-1, 0, -1), blockpos.add(16, this.worldObj.getSeaLevel(), 16))) {
                block0: for (int i2 = 0; i2 < 16; ++i2) {
                    for (int j2 = 0; j2 < 16; ++j2) {
                        if (this.checkLight(i2, j2)) continue;
                        this.isLightPopulated = false;
                        break block0;
                    }
                }
                if (this.isLightPopulated) {
                    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                        int k2 = enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 16 : 1;
                        this.worldObj.getChunkFromBlockCoords(blockpos.offset(enumfacing, k2)).checkLightSide(enumfacing.getOpposite());
                    }
                    this.setSkylightUpdated();
                }
            } else {
                this.isLightPopulated = false;
            }
        }
    }

    private void setSkylightUpdated() {
        for (int i2 = 0; i2 < this.updateSkylightColumns.length; ++i2) {
            this.updateSkylightColumns[i2] = true;
        }
        this.recheckGaps(false);
    }

    private void checkLightSide(EnumFacing facing) {
        block4: {
            block7: {
                block6: {
                    block5: {
                        if (!this.isTerrainPopulated) break block4;
                        if (facing != EnumFacing.EAST) break block5;
                        for (int i2 = 0; i2 < 16; ++i2) {
                            this.checkLight(15, i2);
                        }
                        break block4;
                    }
                    if (facing != EnumFacing.WEST) break block6;
                    for (int j2 = 0; j2 < 16; ++j2) {
                        this.checkLight(0, j2);
                    }
                    break block4;
                }
                if (facing != EnumFacing.SOUTH) break block7;
                for (int k2 = 0; k2 < 16; ++k2) {
                    this.checkLight(k2, 15);
                }
                break block4;
            }
            if (facing != EnumFacing.NORTH) break block4;
            for (int l2 = 0; l2 < 16; ++l2) {
                this.checkLight(l2, 0);
            }
        }
    }

    private boolean checkLight(int x2, int z2) {
        int i2 = this.getTopFilledSegment();
        boolean flag = false;
        boolean flag1 = false;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos((this.xPosition << 4) + x2, 0, (this.zPosition << 4) + z2);
        for (int j2 = i2 + 16 - 1; j2 > this.worldObj.getSeaLevel() || j2 > 0 && !flag1; --j2) {
            blockpos$mutableblockpos.setPos(blockpos$mutableblockpos.getX(), j2, blockpos$mutableblockpos.getZ());
            int k2 = this.getBlockLightOpacity(blockpos$mutableblockpos);
            if (k2 == 255 && blockpos$mutableblockpos.getY() < this.worldObj.getSeaLevel()) {
                flag1 = true;
            }
            if (!flag && k2 > 0) {
                flag = true;
                continue;
            }
            if (!flag || k2 != 0 || this.worldObj.checkLight(blockpos$mutableblockpos)) continue;
            return false;
        }
        for (int l2 = blockpos$mutableblockpos.getY(); l2 > 0; --l2) {
            blockpos$mutableblockpos.setPos(blockpos$mutableblockpos.getX(), l2, blockpos$mutableblockpos.getZ());
            if (this.getBlockState(blockpos$mutableblockpos).getLightValue() <= 0) continue;
            this.worldObj.checkLight(blockpos$mutableblockpos);
        }
        return true;
    }

    public boolean isLoaded() {
        return this.isChunkLoaded;
    }

    public void setChunkLoaded(boolean loaded) {
        this.isChunkLoaded = loaded;
    }

    public World getWorld() {
        return this.worldObj;
    }

    public int[] getHeightMap() {
        return this.heightMap;
    }

    public void setHeightMap(int[] newHeightMap) {
        if (this.heightMap.length != newHeightMap.length) {
            LOGGER.warn("Could not set level chunk heightmap, array length is {} instead of {}", (Object)newHeightMap.length, (Object)this.heightMap.length);
        } else {
            System.arraycopy(newHeightMap, 0, this.heightMap, 0, this.heightMap.length);
        }
    }

    public Map<BlockPos, TileEntity> getTileEntityMap() {
        return this.chunkTileEntityMap;
    }

    public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
        return this.entityLists;
    }

    public boolean isTerrainPopulated() {
        return this.isTerrainPopulated;
    }

    public void setTerrainPopulated(boolean terrainPopulated) {
        this.isTerrainPopulated = terrainPopulated;
    }

    public boolean isLightPopulated() {
        return this.isLightPopulated;
    }

    public void setLightPopulated(boolean lightPopulated) {
        this.isLightPopulated = lightPopulated;
    }

    public void setModified(boolean modified) {
        this.isModified = modified;
    }

    public void setHasEntities(boolean hasEntitiesIn) {
        this.hasEntities = hasEntitiesIn;
    }

    public void setLastSaveTime(long saveTime) {
        this.lastSaveTime = saveTime;
    }

    public int getLowestHeight() {
        return this.heightMapMinimum;
    }

    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    public void setInhabitedTime(long newInhabitedTime) {
        this.inhabitedTime = newInhabitedTime;
    }

    public byte[] getBlockBiomeArray() {
        return this.blockBiomeArray;
    }

    public static enum EnumCreateEntityType {
        IMMEDIATE,
        QUEUED,
        CHECK;

    }
}

