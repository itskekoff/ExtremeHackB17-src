package optifine;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import net.minecraft.block.state.IBlockState;
import optifine.Config;

public class CacheObjectArray {
    private static ArrayDeque<int[]> arrays = new ArrayDeque();
    private static int maxCacheSize = 10;

    private static synchronized int[] allocateArray(int p_allocateArray_0_) {
        int[] aint = arrays.pollLast();
        if (aint == null || aint.length < p_allocateArray_0_) {
            aint = new int[p_allocateArray_0_];
        }
        return aint;
    }

    public static synchronized void freeArray(int[] p_freeArray_0_) {
        if (arrays.size() < maxCacheSize) {
            arrays.add(p_freeArray_0_);
        }
    }

    public static void main(String[] p_main_0_) throws Exception {
        int i2 = 4096;
        int j2 = 500000;
        CacheObjectArray.testNew(i2, j2);
        CacheObjectArray.testClone(i2, j2);
        CacheObjectArray.testNewObj(i2, j2);
        CacheObjectArray.testCloneObj(i2, j2);
        CacheObjectArray.testNewObjDyn(IBlockState.class, i2, j2);
        long k2 = CacheObjectArray.testNew(i2, j2);
        long l2 = CacheObjectArray.testClone(i2, j2);
        long i1 = CacheObjectArray.testNewObj(i2, j2);
        long j1 = CacheObjectArray.testCloneObj(i2, j2);
        long k1 = CacheObjectArray.testNewObjDyn(IBlockState.class, i2, j2);
        Config.dbg("New: " + k2);
        Config.dbg("Clone: " + l2);
        Config.dbg("NewObj: " + i1);
        Config.dbg("CloneObj: " + j1);
        Config.dbg("NewObjDyn: " + k1);
    }

    private static long testClone(int p_testClone_0_, int p_testClone_1_) {
        long i2 = System.currentTimeMillis();
        int[] aint = new int[p_testClone_0_];
        for (int j2 = 0; j2 < p_testClone_1_; ++j2) {
            int[] arrn = (int[])aint.clone();
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }

    private static long testNew(int p_testNew_0_, int p_testNew_1_) {
        long i2 = System.currentTimeMillis();
        for (int j2 = 0; j2 < p_testNew_1_; ++j2) {
            int[] arrn = (int[])Array.newInstance(Integer.TYPE, p_testNew_0_);
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }

    private static long testCloneObj(int p_testCloneObj_0_, int p_testCloneObj_1_) {
        long i2 = System.currentTimeMillis();
        IBlockState[] aiblockstate = new IBlockState[p_testCloneObj_0_];
        for (int j2 = 0; j2 < p_testCloneObj_1_; ++j2) {
            IBlockState[] arriBlockState = (IBlockState[])aiblockstate.clone();
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }

    private static long testNewObj(int p_testNewObj_0_, int p_testNewObj_1_) {
        long i2 = System.currentTimeMillis();
        for (int j2 = 0; j2 < p_testNewObj_1_; ++j2) {
            IBlockState[] arriBlockState = new IBlockState[p_testNewObj_0_];
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }

    private static long testNewObjDyn(Class p_testNewObjDyn_0_, int p_testNewObjDyn_1_, int p_testNewObjDyn_2_) {
        long i2 = System.currentTimeMillis();
        for (int j2 = 0; j2 < p_testNewObjDyn_2_; ++j2) {
            Object[] arrobject = (Object[])Array.newInstance(p_testNewObjDyn_0_, p_testNewObjDyn_1_);
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }
}

