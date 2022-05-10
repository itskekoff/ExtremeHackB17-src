package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ChunkPrimer {
    private static final IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();
    private final char[] data = new char[65536];

    public IBlockState getBlockState(int x2, int y2, int z2) {
        IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(this.data[ChunkPrimer.getBlockIndex(x2, y2, z2)]);
        return iblockstate == null ? DEFAULT_STATE : iblockstate;
    }

    public void setBlockState(int x2, int y2, int z2, IBlockState state) {
        this.data[ChunkPrimer.getBlockIndex((int)x2, (int)y2, (int)z2)] = (char)Block.BLOCK_STATE_IDS.get(state);
    }

    private static int getBlockIndex(int x2, int y2, int z2) {
        return x2 << 12 | z2 << 8 | y2;
    }

    public int findGroundBlockIdx(int x2, int z2) {
        int i2 = (x2 << 12 | z2 << 8) + 256 - 1;
        for (int j2 = 255; j2 >= 0; --j2) {
            IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(this.data[i2 + j2]);
            if (iblockstate == null || iblockstate == DEFAULT_STATE) continue;
            return j2;
        }
        return 0;
    }
}

