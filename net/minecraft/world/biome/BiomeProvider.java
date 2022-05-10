package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Biomes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;

public class BiomeProvider {
    private ChunkGeneratorSettings field_190945_a;
    private GenLayer genBiomes;
    private GenLayer biomeIndexLayer;
    private final BiomeCache biomeCache = new BiomeCache(this);
    private final List<Biome> biomesToSpawnIn = Lists.newArrayList(Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.FOREST_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS);

    protected BiomeProvider() {
    }

    private BiomeProvider(long seed, WorldType worldTypeIn, String options) {
        this();
        if (worldTypeIn == WorldType.CUSTOMIZED && !options.isEmpty()) {
            this.field_190945_a = ChunkGeneratorSettings.Factory.jsonToFactory(options).build();
        }
        GenLayer[] agenlayer = GenLayer.initializeAllBiomeGenerators(seed, worldTypeIn, this.field_190945_a);
        this.genBiomes = agenlayer[0];
        this.biomeIndexLayer = agenlayer[1];
    }

    public BiomeProvider(WorldInfo info) {
        this(info.getSeed(), info.getTerrainType(), info.getGeneratorOptions());
    }

    public List<Biome> getBiomesToSpawnIn() {
        return this.biomesToSpawnIn;
    }

    public Biome getBiome(BlockPos pos) {
        return this.getBiome(pos, null);
    }

    public Biome getBiome(BlockPos pos, Biome defaultBiome) {
        return this.biomeCache.getBiome(pos.getX(), pos.getZ(), defaultBiome);
    }

    public float getTemperatureAtHeight(float p_76939_1_, int p_76939_2_) {
        return p_76939_1_;
    }

    public Biome[] getBiomesForGeneration(Biome[] biomes, int x2, int z2, int width, int height) {
        IntCache.resetIntCache();
        if (biomes == null || biomes.length < width * height) {
            biomes = new Biome[width * height];
        }
        int[] aint = this.genBiomes.getInts(x2, z2, width, height);
        try {
            for (int i2 = 0; i2 < width * height; ++i2) {
                biomes[i2] = Biome.getBiome(aint[i2], Biomes.DEFAULT);
            }
            return biomes;
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
            crashreportcategory.addCrashSection("biomes[] size", biomes.length);
            crashreportcategory.addCrashSection("x", x2);
            crashreportcategory.addCrashSection("z", z2);
            crashreportcategory.addCrashSection("w", width);
            crashreportcategory.addCrashSection("h", height);
            throw new ReportedException(crashreport);
        }
    }

    public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x2, int z2, int width, int depth) {
        return this.getBiomes(oldBiomeList, x2, z2, width, depth, true);
    }

    public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x2, int z2, int width, int length, boolean cacheFlag) {
        IntCache.resetIntCache();
        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new Biome[width * length];
        }
        if (cacheFlag && width == 16 && length == 16 && (x2 & 0xF) == 0 && (z2 & 0xF) == 0) {
            Biome[] abiome = this.biomeCache.getCachedBiomes(x2, z2);
            System.arraycopy(abiome, 0, listToReuse, 0, width * length);
            return listToReuse;
        }
        int[] aint = this.biomeIndexLayer.getInts(x2, z2, width, length);
        for (int i2 = 0; i2 < width * length; ++i2) {
            listToReuse[i2] = Biome.getBiome(aint[i2], Biomes.DEFAULT);
        }
        return listToReuse;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean areBiomesViable(int x2, int z2, int radius, List<Biome> allowed) {
        IntCache.resetIntCache();
        int i2 = x2 - radius >> 2;
        int j2 = z2 - radius >> 2;
        int k2 = x2 + radius >> 2;
        int l2 = z2 + radius >> 2;
        int i1 = k2 - i2 + 1;
        int j1 = l2 - j2 + 1;
        int[] aint = this.genBiomes.getInts(i2, j2, i1, j1);
        try {
            int k1 = 0;
            while (true) {
                if (k1 >= i1 * j1) {
                    return true;
                }
                Biome biome = Biome.getBiome(aint[k1]);
                if (!allowed.contains(biome)) {
                    return false;
                }
                ++k1;
            }
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
            crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
            crashreportcategory.addCrashSection("x", x2);
            crashreportcategory.addCrashSection("z", z2);
            crashreportcategory.addCrashSection("radius", radius);
            crashreportcategory.addCrashSection("allowed", allowed);
            throw new ReportedException(crashreport);
        }
    }

    @Nullable
    public BlockPos findBiomePosition(int x2, int z2, int range, List<Biome> biomes, Random random) {
        IntCache.resetIntCache();
        int i2 = x2 - range >> 2;
        int j2 = z2 - range >> 2;
        int k2 = x2 + range >> 2;
        int l2 = z2 + range >> 2;
        int i1 = k2 - i2 + 1;
        int j1 = l2 - j2 + 1;
        int[] aint = this.genBiomes.getInts(i2, j2, i1, j1);
        BlockPos blockpos = null;
        int k1 = 0;
        for (int l1 = 0; l1 < i1 * j1; ++l1) {
            int i22 = i2 + l1 % i1 << 2;
            int j22 = j2 + l1 / i1 << 2;
            Biome biome = Biome.getBiome(aint[l1]);
            if (!biomes.contains(biome) || blockpos != null && random.nextInt(k1 + 1) != 0) continue;
            blockpos = new BlockPos(i22, 0, j22);
            ++k1;
        }
        return blockpos;
    }

    public void cleanupCache() {
        this.biomeCache.cleanupCache();
    }

    public boolean func_190944_c() {
        return this.field_190945_a != null && this.field_190945_a.fixedBiome >= 0;
    }

    public Biome func_190943_d() {
        return this.field_190945_a != null && this.field_190945_a.fixedBiome >= 0 ? Biome.getBiomeForId(this.field_190945_a.fixedBiome) : null;
    }
}

