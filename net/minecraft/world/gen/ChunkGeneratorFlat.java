package net.minecraft.world.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkGeneratorFlat
implements IChunkGenerator {
    private final World worldObj;
    private final Random random;
    private final IBlockState[] cachedBlockIDs = new IBlockState[256];
    private final FlatGeneratorInfo flatWorldGenInfo;
    private final Map<String, MapGenStructure> structureGenerators = new HashMap<String, MapGenStructure>();
    private final boolean hasDecoration;
    private final boolean hasDungeons;
    private WorldGenLakes waterLakeGenerator;
    private WorldGenLakes lavaLakeGenerator;

    public ChunkGeneratorFlat(World worldIn, long seed, boolean generateStructures, String flatGeneratorSettings) {
        this.worldObj = worldIn;
        this.random = new Random(seed);
        this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(flatGeneratorSettings);
        if (generateStructures) {
            Map<String, Map<String, String>> map = this.flatWorldGenInfo.getWorldFeatures();
            if (map.containsKey("village")) {
                Map<String, String> map1 = map.get("village");
                if (!map1.containsKey("size")) {
                    map1.put("size", "1");
                }
                this.structureGenerators.put("Village", new MapGenVillage(map1));
            }
            if (map.containsKey("biome_1")) {
                this.structureGenerators.put("Temple", new MapGenScatteredFeature(map.get("biome_1")));
            }
            if (map.containsKey("mineshaft")) {
                this.structureGenerators.put("Mineshaft", new MapGenMineshaft(map.get("mineshaft")));
            }
            if (map.containsKey("stronghold")) {
                this.structureGenerators.put("Stronghold", new MapGenStronghold(map.get("stronghold")));
            }
            if (map.containsKey("oceanmonument")) {
                this.structureGenerators.put("Monument", new StructureOceanMonument(map.get("oceanmonument")));
            }
        }
        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake")) {
            this.waterLakeGenerator = new WorldGenLakes(Blocks.WATER);
        }
        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake")) {
            this.lavaLakeGenerator = new WorldGenLakes(Blocks.LAVA);
        }
        this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
        int j2 = 0;
        int k2 = 0;
        boolean flag = true;
        for (FlatLayerInfo flatlayerinfo : this.flatWorldGenInfo.getFlatLayers()) {
            for (int i2 = flatlayerinfo.getMinY(); i2 < flatlayerinfo.getMinY() + flatlayerinfo.getLayerCount(); ++i2) {
                IBlockState iblockstate = flatlayerinfo.getLayerMaterial();
                if (iblockstate.getBlock() == Blocks.AIR) continue;
                flag = false;
                this.cachedBlockIDs[i2] = iblockstate;
            }
            if (flatlayerinfo.getLayerMaterial().getBlock() == Blocks.AIR) {
                k2 += flatlayerinfo.getLayerCount();
                continue;
            }
            j2 += flatlayerinfo.getLayerCount() + k2;
            k2 = 0;
        }
        worldIn.setSeaLevel(j2);
        this.hasDecoration = flag && this.flatWorldGenInfo.getBiome() != Biome.getIdForBiome(Biomes.VOID) ? false : this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public Chunk provideChunk(int x2, int z2) {
        void var4_5;
        ChunkPrimer chunkprimer = new ChunkPrimer();
        boolean bl2 = false;
        while (++var4_5 < this.cachedBlockIDs.length) {
            IBlockState iblockstate = this.cachedBlockIDs[var4_5];
            if (iblockstate == null) continue;
            for (int j2 = 0; j2 < 16; ++j2) {
                for (int k2 = 0; k2 < 16; ++k2) {
                    chunkprimer.setBlockState(j2, (int)var4_5, k2, iblockstate);
                }
            }
        }
        for (MapGenBase mapGenBase : this.structureGenerators.values()) {
            mapGenBase.generate(this.worldObj, x2, z2, chunkprimer);
        }
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x2, z2);
        Biome[] abiome = this.worldObj.getBiomeProvider().getBiomes(null, x2 * 16, z2 * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();
        for (int l2 = 0; l2 < abyte.length; ++l2) {
            abyte[l2] = (byte)Biome.getIdForBiome(abiome[l2]);
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x2, int z2) {
        BlockPos blockpos1;
        int i2 = x2 * 16;
        int j2 = z2 * 16;
        BlockPos blockpos = new BlockPos(i2, 0, j2);
        Biome biome = this.worldObj.getBiome(new BlockPos(i2 + 16, 0, j2 + 16));
        boolean flag = false;
        this.random.setSeed(this.worldObj.getSeed());
        long k2 = this.random.nextLong() / 2L * 2L + 1L;
        long l2 = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long)x2 * k2 + (long)z2 * l2 ^ this.worldObj.getSeed());
        ChunkPos chunkpos = new ChunkPos(x2, z2);
        for (MapGenStructure mapgenstructure : this.structureGenerators.values()) {
            boolean flag1 = mapgenstructure.generateStructure(this.worldObj, this.random, chunkpos);
            if (!(mapgenstructure instanceof MapGenVillage)) continue;
            flag |= flag1;
        }
        if (this.waterLakeGenerator != null && !flag && this.random.nextInt(4) == 0) {
            this.waterLakeGenerator.generate(this.worldObj, this.random, blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
        }
        if (!(this.lavaLakeGenerator == null || flag || this.random.nextInt(8) != 0 || (blockpos1 = blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8)).getY() >= this.worldObj.getSeaLevel() && this.random.nextInt(10) != 0)) {
            this.lavaLakeGenerator.generate(this.worldObj, this.random, blockpos1);
        }
        if (this.hasDungeons) {
            for (int i1 = 0; i1 < 8; ++i1) {
                new WorldGenDungeons().generate(this.worldObj, this.random, blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
            }
        }
        if (this.hasDecoration) {
            biome.decorate(this.worldObj, this.random, blockpos);
        }
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x2, int z2) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        Biome biome = this.worldObj.getBiome(pos);
        return biome.getSpawnableList(creatureType);
    }

    @Override
    @Nullable
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_) {
        MapGenStructure mapgenstructure = this.structureGenerators.get(structureName);
        return mapgenstructure != null ? mapgenstructure.getClosestStrongholdPos(worldIn, position, p_180513_4_) : null;
    }

    @Override
    public boolean func_193414_a(World p_193414_1_, String p_193414_2_, BlockPos p_193414_3_) {
        MapGenStructure mapgenstructure = this.structureGenerators.get(p_193414_2_);
        return mapgenstructure != null ? mapgenstructure.isInsideStructure(p_193414_3_) : false;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
        for (MapGenStructure mapgenstructure : this.structureGenerators.values()) {
            mapgenstructure.generate(this.worldObj, x2, z2, null);
        }
    }
}

