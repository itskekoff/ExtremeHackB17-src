package net.minecraft.client.renderer.chunk;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;
import net.minecraft.util.math.BlockPos;

public class VisGraph {
    private static final int DX = (int)Math.pow(16.0, 0.0);
    private static final int DZ = (int)Math.pow(16.0, 1.0);
    private static final int DY = (int)Math.pow(16.0, 2.0);
    private final BitSet bitSet = new BitSet(4096);
    private static final int[] INDEX_OF_EDGES = new int[1352];
    private int empty = 4096;

    static {
        boolean i2 = false;
        int j2 = 15;
        int k2 = 0;
        for (int l2 = 0; l2 < 16; ++l2) {
            for (int i1 = 0; i1 < 16; ++i1) {
                for (int j1 = 0; j1 < 16; ++j1) {
                    if (l2 != 0 && l2 != 15 && i1 != 0 && i1 != 15 && j1 != 0 && j1 != 15) continue;
                    VisGraph.INDEX_OF_EDGES[k2++] = VisGraph.getIndex(l2, i1, j1);
                }
            }
        }
    }

    public void setOpaqueCube(BlockPos pos) {
        this.bitSet.set(VisGraph.getIndex(pos), true);
        --this.empty;
    }

    private static int getIndex(BlockPos pos) {
        return VisGraph.getIndex(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
    }

    private static int getIndex(int x2, int y2, int z2) {
        return x2 << 0 | y2 << 8 | z2 << 4;
    }

    public SetVisibility computeVisibility() {
        SetVisibility setvisibility = new SetVisibility();
        if (4096 - this.empty < 256) {
            setvisibility.setAllVisible(true);
        } else if (this.empty == 0) {
            setvisibility.setAllVisible(false);
        } else {
            int[] arrn = INDEX_OF_EDGES;
            int n2 = INDEX_OF_EDGES.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                int i3 = arrn[i2];
                if (this.bitSet.get(i3)) continue;
                setvisibility.setManyVisible(this.floodFill(i3));
            }
        }
        return setvisibility;
    }

    public Set<EnumFacing> getVisibleFacings(BlockPos pos) {
        return this.floodFill(VisGraph.getIndex(pos));
    }

    private Set<EnumFacing> floodFill(int p_178604_1_) {
        EnumSet<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);
        ArrayDeque<Integer> arraydeque = new ArrayDeque<Integer>(384);
        arraydeque.add(IntegerCache.getInteger(p_178604_1_));
        this.bitSet.set(p_178604_1_, true);
        while (!arraydeque.isEmpty()) {
            int i2 = (Integer)arraydeque.poll();
            this.addEdges(i2, set);
            EnumFacing[] arrenumFacing = EnumFacing.VALUES;
            int n2 = EnumFacing.VALUES.length;
            for (int i3 = 0; i3 < n2; ++i3) {
                EnumFacing enumfacing = arrenumFacing[i3];
                int j2 = this.getNeighborIndexAtFace(i2, enumfacing);
                if (j2 < 0 || this.bitSet.get(j2)) continue;
                this.bitSet.set(j2, true);
                arraydeque.add(IntegerCache.getInteger(j2));
            }
        }
        return set;
    }

    private void addEdges(int p_178610_1_, Set<EnumFacing> p_178610_2_) {
        int i2 = p_178610_1_ >> 0 & 0xF;
        if (i2 == 0) {
            p_178610_2_.add(EnumFacing.WEST);
        } else if (i2 == 15) {
            p_178610_2_.add(EnumFacing.EAST);
        }
        int j2 = p_178610_1_ >> 8 & 0xF;
        if (j2 == 0) {
            p_178610_2_.add(EnumFacing.DOWN);
        } else if (j2 == 15) {
            p_178610_2_.add(EnumFacing.UP);
        }
        int k2 = p_178610_1_ >> 4 & 0xF;
        if (k2 == 0) {
            p_178610_2_.add(EnumFacing.NORTH);
        } else if (k2 == 15) {
            p_178610_2_.add(EnumFacing.SOUTH);
        }
    }

    private int getNeighborIndexAtFace(int p_178603_1_, EnumFacing p_178603_2_) {
        switch (p_178603_2_) {
            case DOWN: {
                if ((p_178603_1_ >> 8 & 0xF) == 0) {
                    return -1;
                }
                return p_178603_1_ - DY;
            }
            case UP: {
                if ((p_178603_1_ >> 8 & 0xF) == 15) {
                    return -1;
                }
                return p_178603_1_ + DY;
            }
            case NORTH: {
                if ((p_178603_1_ >> 4 & 0xF) == 0) {
                    return -1;
                }
                return p_178603_1_ - DZ;
            }
            case SOUTH: {
                if ((p_178603_1_ >> 4 & 0xF) == 15) {
                    return -1;
                }
                return p_178603_1_ + DZ;
            }
            case WEST: {
                if ((p_178603_1_ >> 0 & 0xF) == 0) {
                    return -1;
                }
                return p_178603_1_ - DX;
            }
            case EAST: {
                if ((p_178603_1_ >> 0 & 0xF) == 15) {
                    return -1;
                }
                return p_178603_1_ + DX;
            }
        }
        return -1;
    }
}

