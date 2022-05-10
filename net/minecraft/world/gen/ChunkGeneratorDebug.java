package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkGeneratorDebug
implements IChunkGenerator {
    private static final List<IBlockState> ALL_VALID_STATES = Lists.newArrayList();
    private static final int GRID_WIDTH;
    private static final int GRID_HEIGHT;
    protected static final IBlockState AIR;
    protected static final IBlockState BARRIER;
    private final World world;

    static {
        AIR = Blocks.AIR.getDefaultState();
        BARRIER = Blocks.BARRIER.getDefaultState();
        for (Block block : Block.REGISTRY) {
            ALL_VALID_STATES.addAll(block.getBlockState().getValidStates());
        }
        GRID_WIDTH = MathHelper.ceil(MathHelper.sqrt(ALL_VALID_STATES.size()));
        GRID_HEIGHT = MathHelper.ceil((float)ALL_VALID_STATES.size() / (float)GRID_WIDTH);
    }

    public ChunkGeneratorDebug(World worldIn) {
        this.world = worldIn;
    }

    @Override
    public Chunk provideChunk(int x2, int z2) {
        ChunkPrimer chunkprimer = new ChunkPrimer();
        for (int i2 = 0; i2 < 16; ++i2) {
            for (int j2 = 0; j2 < 16; ++j2) {
                int k2 = x2 * 16 + i2;
                int l2 = z2 * 16 + j2;
                chunkprimer.setBlockState(i2, 60, j2, BARRIER);
                IBlockState iblockstate = ChunkGeneratorDebug.getBlockStateFor(k2, l2);
                if (iblockstate == null) continue;
                chunkprimer.setBlockState(i2, 70, j2, iblockstate);
            }
        }
        Chunk chunk = new Chunk(this.world, chunkprimer, x2, z2);
        chunk.generateSkylightMap();
        Biome[] abiome = this.world.getBiomeProvider().getBiomes(null, x2 * 16, z2 * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();
        for (int i1 = 0; i1 < abyte.length; ++i1) {
            abyte[i1] = (byte)Biome.getIdForBiome(abiome[i1]);
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    public static IBlockState getBlockStateFor(int p_177461_0_, int p_177461_1_) {
        int i2;
        IBlockState iblockstate = AIR;
        if (p_177461_0_ > 0 && p_177461_1_ > 0 && p_177461_0_ % 2 != 0 && p_177461_1_ % 2 != 0 && (p_177461_0_ /= 2) <= GRID_WIDTH && (p_177461_1_ /= 2) <= GRID_HEIGHT && (i2 = MathHelper.abs(p_177461_0_ * GRID_WIDTH + p_177461_1_)) < ALL_VALID_STATES.size()) {
            iblockstate = ALL_VALID_STATES.get(i2);
        }
        return iblockstate;
    }

    @Override
    public void populate(int x2, int z2) {
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x2, int z2) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        Biome biome = this.world.getBiome(pos);
        return biome.getSpawnableList(creatureType);
    }

    @Override
    @Nullable
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_) {
        return null;
    }

    @Override
    public boolean func_193414_a(World p_193414_1_, String p_193414_2_, BlockPos p_193414_3_) {
        return false;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
    }
}

