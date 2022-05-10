package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer
implements IChunkProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Set<Long> droppedChunksSet = Sets.newHashSet();
    private final IChunkGenerator chunkGenerator;
    private final IChunkLoader chunkLoader;
    private final Long2ObjectMap<Chunk> id2ChunkMap = new Long2ObjectOpenHashMap<Chunk>(8192);
    private final WorldServer worldObj;

    public ChunkProviderServer(WorldServer worldObjIn, IChunkLoader chunkLoaderIn, IChunkGenerator chunkGeneratorIn) {
        this.worldObj = worldObjIn;
        this.chunkLoader = chunkLoaderIn;
        this.chunkGenerator = chunkGeneratorIn;
    }

    public Collection<Chunk> getLoadedChunks() {
        return this.id2ChunkMap.values();
    }

    public void unload(Chunk chunkIn) {
        if (this.worldObj.provider.canDropChunk(chunkIn.xPosition, chunkIn.zPosition)) {
            this.droppedChunksSet.add(ChunkPos.asLong(chunkIn.xPosition, chunkIn.zPosition));
            chunkIn.unloaded = true;
        }
    }

    public void unloadAllChunks() {
        for (Chunk chunk : this.id2ChunkMap.values()) {
            this.unload(chunk);
        }
    }

    @Override
    @Nullable
    public Chunk getLoadedChunk(int x2, int z2) {
        long i2 = ChunkPos.asLong(x2, z2);
        Chunk chunk = (Chunk)this.id2ChunkMap.get(i2);
        if (chunk != null) {
            chunk.unloaded = false;
        }
        return chunk;
    }

    @Nullable
    public Chunk loadChunk(int x2, int z2) {
        Chunk chunk = this.getLoadedChunk(x2, z2);
        if (chunk == null && (chunk = this.loadChunkFromFile(x2, z2)) != null) {
            this.id2ChunkMap.put(ChunkPos.asLong(x2, z2), chunk);
            chunk.onChunkLoad();
            chunk.populateChunk(this, this.chunkGenerator);
        }
        return chunk;
    }

    @Override
    public Chunk provideChunk(int x2, int z2) {
        Chunk chunk = this.loadChunk(x2, z2);
        if (chunk == null) {
            long i2 = ChunkPos.asLong(x2, z2);
            try {
                chunk = this.chunkGenerator.provideChunk(x2, z2);
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                crashreportcategory.addCrashSection("Location", String.format("%d,%d", x2, z2));
                crashreportcategory.addCrashSection("Position hash", i2);
                crashreportcategory.addCrashSection("Generator", this.chunkGenerator);
                throw new ReportedException(crashreport);
            }
            this.id2ChunkMap.put(i2, chunk);
            chunk.onChunkLoad();
            chunk.populateChunk(this, this.chunkGenerator);
        }
        return chunk;
    }

    @Nullable
    private Chunk loadChunkFromFile(int x2, int z2) {
        try {
            Chunk chunk = this.chunkLoader.loadChunk(this.worldObj, x2, z2);
            if (chunk != null) {
                chunk.setLastSaveTime(this.worldObj.getTotalWorldTime());
                this.chunkGenerator.recreateStructures(chunk, x2, z2);
            }
            return chunk;
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load chunk", (Throwable)exception);
            return null;
        }
    }

    private void saveChunkExtraData(Chunk chunkIn) {
        try {
            this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't save entities", (Throwable)exception);
        }
    }

    private void saveChunkData(Chunk chunkIn) {
        try {
            chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
            this.chunkLoader.saveChunk(this.worldObj, chunkIn);
        }
        catch (IOException ioexception) {
            LOGGER.error("Couldn't save chunk", (Throwable)ioexception);
        }
        catch (MinecraftException minecraftexception) {
            LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)minecraftexception);
        }
    }

    public boolean saveChunks(boolean p_186027_1_) {
        int i2 = 0;
        ArrayList<Chunk> list = Lists.newArrayList(this.id2ChunkMap.values());
        for (int j2 = 0; j2 < list.size(); ++j2) {
            Chunk chunk = (Chunk)list.get(j2);
            if (p_186027_1_) {
                this.saveChunkExtraData(chunk);
            }
            if (!chunk.needsSaving(p_186027_1_)) continue;
            this.saveChunkData(chunk);
            chunk.setModified(false);
            if (++i2 != 24 || p_186027_1_) continue;
            return false;
        }
        return true;
    }

    public void saveExtraData() {
        this.chunkLoader.saveExtraData();
    }

    @Override
    public boolean unloadQueuedChunks() {
        if (!this.worldObj.disableLevelSaving) {
            if (!this.droppedChunksSet.isEmpty()) {
                Iterator<Long> iterator = this.droppedChunksSet.iterator();
                int i2 = 0;
                while (i2 < 100 && iterator.hasNext()) {
                    Long olong = iterator.next();
                    Chunk chunk = (Chunk)this.id2ChunkMap.get(olong);
                    if (chunk != null && chunk.unloaded) {
                        chunk.onChunkUnload();
                        this.saveChunkData(chunk);
                        this.saveChunkExtraData(chunk);
                        this.id2ChunkMap.remove(olong);
                        ++i2;
                    }
                    iterator.remove();
                }
            }
            this.chunkLoader.chunkTick();
        }
        return false;
    }

    public boolean canSave() {
        return !this.worldObj.disableLevelSaving;
    }

    @Override
    public String makeString() {
        return "ServerChunkCache: " + this.id2ChunkMap.size() + " Drop: " + this.droppedChunksSet.size();
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return this.chunkGenerator.getPossibleCreatures(creatureType, pos);
    }

    @Nullable
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_) {
        return this.chunkGenerator.getStrongholdGen(worldIn, structureName, position, p_180513_4_);
    }

    public boolean func_193413_a(World p_193413_1_, String p_193413_2_, BlockPos p_193413_3_) {
        return this.chunkGenerator.func_193414_a(p_193413_1_, p_193413_2_, p_193413_3_);
    }

    public int getLoadedChunkCount() {
        return this.id2ChunkMap.size();
    }

    public boolean chunkExists(int x2, int z2) {
        return this.id2ChunkMap.containsKey(ChunkPos.asLong(x2, z2));
    }

    @Override
    public boolean func_191062_e(int p_191062_1_, int p_191062_2_) {
        return this.id2ChunkMap.containsKey(ChunkPos.asLong(p_191062_1_, p_191062_2_)) || this.chunkLoader.func_191063_a(p_191062_1_, p_191062_2_);
    }
}

