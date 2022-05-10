package javax.vecmath;

class VecMathUtil {
    private VecMathUtil() {
    }

    static final long hashLongBits(long hash, long l2) {
        return (hash *= 31L) + l2;
    }

    static final long hashFloatBits(long hash, float f2) {
        hash *= 31L;
        if (f2 == 0.0f) {
            return hash;
        }
        return hash + (long)Float.floatToIntBits(f2);
    }

    static final long hashDoubleBits(long hash, double d2) {
        hash *= 31L;
        if (d2 == 0.0) {
            return hash;
        }
        return hash + Double.doubleToLongBits(d2);
    }

    static final int hashFinish(long hash) {
        return (int)(hash ^ hash >> 32);
    }
}

