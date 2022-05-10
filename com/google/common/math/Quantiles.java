package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Beta
@GwtIncompatible
public final class Quantiles {
    public static ScaleAndIndex median() {
        return Quantiles.scale(2).index(1);
    }

    public static Scale quartiles() {
        return Quantiles.scale(4);
    }

    public static Scale percentiles() {
        return Quantiles.scale(100);
    }

    public static Scale scale(int scale) {
        return new Scale(scale);
    }

    private static boolean containsNaN(double ... dataset) {
        for (double value : dataset) {
            if (!Double.isNaN(value)) continue;
            return true;
        }
        return false;
    }

    private static double interpolate(double lower, double upper, double remainder, double scale) {
        if (lower == Double.NEGATIVE_INFINITY) {
            if (upper == Double.POSITIVE_INFINITY) {
                return Double.NaN;
            }
            return Double.NEGATIVE_INFINITY;
        }
        if (upper == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        return lower + (upper - lower) * remainder / scale;
    }

    private static void checkIndex(int index, int scale) {
        if (index < 0 || index > scale) {
            throw new IllegalArgumentException("Quantile indexes must be between 0 and the scale, which is " + scale);
        }
    }

    private static double[] longsToDoubles(long[] longs) {
        int len = longs.length;
        double[] doubles = new double[len];
        for (int i2 = 0; i2 < len; ++i2) {
            doubles[i2] = longs[i2];
        }
        return doubles;
    }

    private static double[] intsToDoubles(int[] ints) {
        int len = ints.length;
        double[] doubles = new double[len];
        for (int i2 = 0; i2 < len; ++i2) {
            doubles[i2] = ints[i2];
        }
        return doubles;
    }

    private static void selectInPlace(int required, double[] array, int from, int to2) {
        if (required == from) {
            int min = from;
            for (int index = from + 1; index <= to2; ++index) {
                if (!(array[min] > array[index])) continue;
                min = index;
            }
            if (min != from) {
                Quantiles.swap(array, min, from);
            }
            return;
        }
        while (to2 > from) {
            int partitionPoint = Quantiles.partition(array, from, to2);
            if (partitionPoint >= required) {
                to2 = partitionPoint - 1;
            }
            if (partitionPoint > required) continue;
            from = partitionPoint + 1;
        }
    }

    private static int partition(double[] array, int from, int to2) {
        Quantiles.movePivotToStartOfSlice(array, from, to2);
        double pivot = array[from];
        int partitionPoint = to2;
        for (int i2 = to2; i2 > from; --i2) {
            if (!(array[i2] > pivot)) continue;
            Quantiles.swap(array, partitionPoint, i2);
            --partitionPoint;
        }
        Quantiles.swap(array, from, partitionPoint);
        return partitionPoint;
    }

    private static void movePivotToStartOfSlice(double[] array, int from, int to2) {
        boolean toLessThanFrom;
        int mid = from + to2 >>> 1;
        boolean toLessThanMid = array[to2] < array[mid];
        boolean midLessThanFrom = array[mid] < array[from];
        boolean bl2 = toLessThanFrom = array[to2] < array[from];
        if (toLessThanMid == midLessThanFrom) {
            Quantiles.swap(array, mid, from);
        } else if (toLessThanMid != toLessThanFrom) {
            Quantiles.swap(array, from, to2);
        }
    }

    private static void selectAllInPlace(int[] allRequired, int requiredFrom, int requiredTo, double[] array, int from, int to2) {
        int requiredAbove;
        int requiredBelow;
        int requiredChosen = Quantiles.chooseNextSelection(allRequired, requiredFrom, requiredTo, from, to2);
        int required = allRequired[requiredChosen];
        Quantiles.selectInPlace(required, array, from, to2);
        for (requiredBelow = requiredChosen - 1; requiredBelow >= requiredFrom && allRequired[requiredBelow] == required; --requiredBelow) {
        }
        if (requiredBelow >= requiredFrom) {
            Quantiles.selectAllInPlace(allRequired, requiredFrom, requiredBelow, array, from, required - 1);
        }
        for (requiredAbove = requiredChosen + 1; requiredAbove <= requiredTo && allRequired[requiredAbove] == required; ++requiredAbove) {
        }
        if (requiredAbove <= requiredTo) {
            Quantiles.selectAllInPlace(allRequired, requiredAbove, requiredTo, array, required + 1, to2);
        }
    }

    private static int chooseNextSelection(int[] allRequired, int requiredFrom, int requiredTo, int from, int to2) {
        if (requiredFrom == requiredTo) {
            return requiredFrom;
        }
        int centerFloor = from + to2 >>> 1;
        int low = requiredFrom;
        int high = requiredTo;
        while (high > low + 1) {
            int mid = low + high >>> 1;
            if (allRequired[mid] > centerFloor) {
                high = mid;
                continue;
            }
            if (allRequired[mid] < centerFloor) {
                low = mid;
                continue;
            }
            return mid;
        }
        if (from + to2 - allRequired[low] - allRequired[high] > 0) {
            return high;
        }
        return low;
    }

    private static void swap(double[] array, int i2, int j2) {
        double temp = array[i2];
        array[i2] = array[j2];
        array[j2] = temp;
    }

    public static final class ScaleAndIndexes {
        private final int scale;
        private final int[] indexes;

        private ScaleAndIndexes(int scale, int[] indexes) {
            for (int index : indexes) {
                Quantiles.checkIndex(index, scale);
            }
            this.scale = scale;
            this.indexes = indexes;
        }

        public Map<Integer, Double> compute(Collection<? extends Number> dataset) {
            return this.computeInPlace(Doubles.toArray(dataset));
        }

        public Map<Integer, Double> compute(double ... dataset) {
            return this.computeInPlace((double[])dataset.clone());
        }

        public Map<Integer, Double> compute(long ... dataset) {
            return this.computeInPlace(Quantiles.longsToDoubles(dataset));
        }

        public Map<Integer, Double> compute(int ... dataset) {
            return this.computeInPlace(Quantiles.intsToDoubles(dataset));
        }

        public Map<Integer, Double> computeInPlace(double ... dataset) {
            Preconditions.checkArgument(dataset.length > 0, "Cannot calculate quantiles of an empty dataset");
            if (Quantiles.containsNaN(dataset)) {
                HashMap<Integer, Double> nanMap = new HashMap<Integer, Double>();
                for (int index : this.indexes) {
                    nanMap.put(index, Double.NaN);
                }
                return Collections.unmodifiableMap(nanMap);
            }
            int[] quotients = new int[this.indexes.length];
            int[] remainders = new int[this.indexes.length];
            int[] requiredSelections = new int[this.indexes.length * 2];
            int requiredSelectionsCount = 0;
            for (int i2 = 0; i2 < this.indexes.length; ++i2) {
                long numerator = (long)this.indexes[i2] * (long)(dataset.length - 1);
                int quotient = (int)LongMath.divide(numerator, this.scale, RoundingMode.DOWN);
                int remainder = (int)(numerator - (long)quotient * (long)this.scale);
                quotients[i2] = quotient;
                remainders[i2] = remainder;
                requiredSelections[requiredSelectionsCount] = quotient;
                ++requiredSelectionsCount;
                if (remainder == 0) continue;
                requiredSelections[requiredSelectionsCount] = quotient + 1;
                ++requiredSelectionsCount;
            }
            Arrays.sort(requiredSelections, 0, requiredSelectionsCount);
            Quantiles.selectAllInPlace(requiredSelections, 0, requiredSelectionsCount - 1, dataset, 0, dataset.length - 1);
            HashMap<Integer, Double> ret = new HashMap<Integer, Double>();
            for (int i3 = 0; i3 < this.indexes.length; ++i3) {
                int quotient = quotients[i3];
                int remainder = remainders[i3];
                if (remainder == 0) {
                    ret.put(this.indexes[i3], dataset[quotient]);
                    continue;
                }
                ret.put(this.indexes[i3], Quantiles.interpolate(dataset[quotient], dataset[quotient + 1], remainder, this.scale));
            }
            return Collections.unmodifiableMap(ret);
        }
    }

    public static final class ScaleAndIndex {
        private final int scale;
        private final int index;

        private ScaleAndIndex(int scale, int index) {
            Quantiles.checkIndex(index, scale);
            this.scale = scale;
            this.index = index;
        }

        public double compute(Collection<? extends Number> dataset) {
            return this.computeInPlace(Doubles.toArray(dataset));
        }

        public double compute(double ... dataset) {
            return this.computeInPlace((double[])dataset.clone());
        }

        public double compute(long ... dataset) {
            return this.computeInPlace(Quantiles.longsToDoubles(dataset));
        }

        public double compute(int ... dataset) {
            return this.computeInPlace(Quantiles.intsToDoubles(dataset));
        }

        public double computeInPlace(double ... dataset) {
            Preconditions.checkArgument(dataset.length > 0, "Cannot calculate quantiles of an empty dataset");
            if (Quantiles.containsNaN(dataset)) {
                return Double.NaN;
            }
            long numerator = (long)this.index * (long)(dataset.length - 1);
            int quotient = (int)LongMath.divide(numerator, this.scale, RoundingMode.DOWN);
            int remainder = (int)(numerator - (long)quotient * (long)this.scale);
            Quantiles.selectInPlace(quotient, dataset, 0, dataset.length - 1);
            if (remainder == 0) {
                return dataset[quotient];
            }
            Quantiles.selectInPlace(quotient + 1, dataset, quotient + 1, dataset.length - 1);
            return Quantiles.interpolate(dataset[quotient], dataset[quotient + 1], remainder, this.scale);
        }
    }

    public static final class Scale {
        private final int scale;

        private Scale(int scale) {
            Preconditions.checkArgument(scale > 0, "Quantile scale must be positive");
            this.scale = scale;
        }

        public ScaleAndIndex index(int index) {
            return new ScaleAndIndex(this.scale, index);
        }

        public ScaleAndIndexes indexes(int ... indexes) {
            return new ScaleAndIndexes(this.scale, (int[])indexes.clone());
        }

        public ScaleAndIndexes indexes(Collection<Integer> indexes) {
            return new ScaleAndIndexes(this.scale, Ints.toArray(indexes));
        }
    }
}

