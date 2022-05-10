package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ViewFrustum {
    protected final RenderGlobal renderGlobal;
    protected final World world;
    protected int countChunksY;
    protected int countChunksX;
    protected int countChunksZ;
    public RenderChunk[] renderChunks;

    public ViewFrustum(World worldIn, int renderDistanceChunks, RenderGlobal renderGlobalIn, IRenderChunkFactory renderChunkFactory) {
        this.renderGlobal = renderGlobalIn;
        this.world = worldIn;
        this.setCountChunksXYZ(renderDistanceChunks);
        this.createRenderChunks(renderChunkFactory);
    }

    protected void createRenderChunks(IRenderChunkFactory renderChunkFactory) {
        int i2 = this.countChunksX * this.countChunksY * this.countChunksZ;
        this.renderChunks = new RenderChunk[i2];
        int j2 = 0;
        for (int k2 = 0; k2 < this.countChunksX; ++k2) {
            for (int l2 = 0; l2 < this.countChunksY; ++l2) {
                for (int i1 = 0; i1 < this.countChunksZ; ++i1) {
                    int j1 = (i1 * this.countChunksY + l2) * this.countChunksX + k2;
                    this.renderChunks[j1] = renderChunkFactory.create(this.world, this.renderGlobal, j2++);
                    this.renderChunks[j1].setPosition(k2 * 16, l2 * 16, i1 * 16);
                }
            }
        }
    }

    public void deleteGlResources() {
        RenderChunk[] arrrenderChunk = this.renderChunks;
        int n2 = this.renderChunks.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            RenderChunk renderchunk = arrrenderChunk[i2];
            renderchunk.deleteGlResources();
        }
    }

    protected void setCountChunksXYZ(int renderDistanceChunks) {
        int i2;
        this.countChunksX = i2 = renderDistanceChunks * 2 + 1;
        this.countChunksY = 16;
        this.countChunksZ = i2;
    }

    public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
        int i2 = MathHelper.floor(viewEntityX) - 8;
        int j2 = MathHelper.floor(viewEntityZ) - 8;
        int k2 = this.countChunksX * 16;
        for (int l2 = 0; l2 < this.countChunksX; ++l2) {
            int i1 = this.getBaseCoordinate(i2, k2, l2);
            for (int j1 = 0; j1 < this.countChunksZ; ++j1) {
                int k1 = this.getBaseCoordinate(j2, k2, j1);
                for (int l1 = 0; l1 < this.countChunksY; ++l1) {
                    int i22 = l1 * 16;
                    RenderChunk renderchunk = this.renderChunks[(j1 * this.countChunksY + l1) * this.countChunksX + l2];
                    renderchunk.setPosition(i1, i22, k1);
                }
            }
        }
    }

    private int getBaseCoordinate(int p_178157_1_, int p_178157_2_, int p_178157_3_) {
        int i2 = p_178157_3_ * 16;
        int j2 = i2 - p_178157_1_ + p_178157_2_ / 2;
        if (j2 < 0) {
            j2 -= p_178157_2_ - 1;
        }
        return i2 - j2 / p_178157_2_ * p_178157_2_;
    }

    public void markBlocksForUpdate(int p_187474_1_, int p_187474_2_, int p_187474_3_, int p_187474_4_, int p_187474_5_, int p_187474_6_, boolean p_187474_7_) {
        int i2 = MathHelper.intFloorDiv(p_187474_1_, 16);
        int j2 = MathHelper.intFloorDiv(p_187474_2_, 16);
        int k2 = MathHelper.intFloorDiv(p_187474_3_, 16);
        int l2 = MathHelper.intFloorDiv(p_187474_4_, 16);
        int i1 = MathHelper.intFloorDiv(p_187474_5_, 16);
        int j1 = MathHelper.intFloorDiv(p_187474_6_, 16);
        for (int k1 = i2; k1 <= l2; ++k1) {
            int l1 = k1 % this.countChunksX;
            if (l1 < 0) {
                l1 += this.countChunksX;
            }
            for (int i22 = j2; i22 <= i1; ++i22) {
                int j22 = i22 % this.countChunksY;
                if (j22 < 0) {
                    j22 += this.countChunksY;
                }
                for (int k22 = k2; k22 <= j1; ++k22) {
                    int l22 = k22 % this.countChunksZ;
                    if (l22 < 0) {
                        l22 += this.countChunksZ;
                    }
                    int i3 = (l22 * this.countChunksY + j22) * this.countChunksX + l1;
                    RenderChunk renderchunk = this.renderChunks[i3];
                    renderchunk.setNeedsUpdate(p_187474_7_);
                }
            }
        }
    }

    @Nullable
    public RenderChunk getRenderChunk(BlockPos pos) {
        int i2 = pos.getX() >> 4;
        int j2 = pos.getY() >> 4;
        int k2 = pos.getZ() >> 4;
        if (j2 >= 0 && j2 < this.countChunksY) {
            if ((i2 %= this.countChunksX) < 0) {
                i2 += this.countChunksX;
            }
            if ((k2 %= this.countChunksZ) < 0) {
                k2 += this.countChunksZ;
            }
            int l2 = (k2 * this.countChunksY + j2) * this.countChunksX + i2;
            return this.renderChunks[l2];
        }
        return null;
    }
}

