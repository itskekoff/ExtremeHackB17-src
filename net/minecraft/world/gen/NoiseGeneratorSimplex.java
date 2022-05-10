package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorSimplex {
    private static final int[][] grad3;
    public static final double SQRT_3;
    private final int[] p = new int[512];
    public double xo;
    public double yo;
    public double zo;
    private static final double F2;
    private static final double G2;

    static {
        int[][] arrarrn = new int[12][];
        int[] arrn = new int[3];
        arrn[0] = 1;
        arrn[1] = 1;
        arrarrn[0] = arrn;
        int[] arrn2 = new int[3];
        arrn2[0] = -1;
        arrn2[1] = 1;
        arrarrn[1] = arrn2;
        int[] arrn3 = new int[3];
        arrn3[0] = 1;
        arrn3[1] = -1;
        arrarrn[2] = arrn3;
        int[] arrn4 = new int[3];
        arrn4[0] = -1;
        arrn4[1] = -1;
        arrarrn[3] = arrn4;
        int[] arrn5 = new int[3];
        arrn5[0] = 1;
        arrn5[2] = 1;
        arrarrn[4] = arrn5;
        int[] arrn6 = new int[3];
        arrn6[0] = -1;
        arrn6[2] = 1;
        arrarrn[5] = arrn6;
        int[] arrn7 = new int[3];
        arrn7[0] = 1;
        arrn7[2] = -1;
        arrarrn[6] = arrn7;
        int[] arrn8 = new int[3];
        arrn8[0] = -1;
        arrn8[2] = -1;
        arrarrn[7] = arrn8;
        int[] arrn9 = new int[3];
        arrn9[1] = 1;
        arrn9[2] = 1;
        arrarrn[8] = arrn9;
        int[] arrn10 = new int[3];
        arrn10[1] = -1;
        arrn10[2] = 1;
        arrarrn[9] = arrn10;
        int[] arrn11 = new int[3];
        arrn11[1] = 1;
        arrn11[2] = -1;
        arrarrn[10] = arrn11;
        int[] arrn12 = new int[3];
        arrn12[1] = -1;
        arrn12[2] = -1;
        arrarrn[11] = arrn12;
        grad3 = arrarrn;
        SQRT_3 = Math.sqrt(3.0);
        F2 = 0.5 * (SQRT_3 - 1.0);
        G2 = (3.0 - SQRT_3) / 6.0;
    }

    public NoiseGeneratorSimplex() {
        this(new Random());
    }

    public NoiseGeneratorSimplex(Random p_i45471_1_) {
        this.xo = p_i45471_1_.nextDouble() * 256.0;
        this.yo = p_i45471_1_.nextDouble() * 256.0;
        this.zo = p_i45471_1_.nextDouble() * 256.0;
        int i2 = 0;
        while (i2 < 256) {
            this.p[i2] = i2++;
        }
        for (int l2 = 0; l2 < 256; ++l2) {
            int j2 = p_i45471_1_.nextInt(256 - l2) + l2;
            int k2 = this.p[l2];
            this.p[l2] = this.p[j2];
            this.p[j2] = k2;
            this.p[l2 + 256] = this.p[l2];
        }
    }

    private static int fastFloor(double value) {
        return value > 0.0 ? (int)value : (int)value - 1;
    }

    private static double dot(int[] p_151604_0_, double p_151604_1_, double p_151604_3_) {
        return (double)p_151604_0_[0] * p_151604_1_ + (double)p_151604_0_[1] * p_151604_3_;
    }

    public double getValue(double p_151605_1_, double p_151605_3_) {
        double d2;
        double d1;
        double d0;
        int l2;
        int k2;
        double d8;
        double d10;
        double d5;
        int j2;
        double d6;
        double d3 = 0.5 * (SQRT_3 - 1.0);
        double d4 = (p_151605_1_ + p_151605_3_) * d3;
        int i2 = NoiseGeneratorSimplex.fastFloor(p_151605_1_ + d4);
        double d7 = (double)i2 - (d6 = (double)(i2 + (j2 = NoiseGeneratorSimplex.fastFloor(p_151605_3_ + d4))) * (d5 = (3.0 - SQRT_3) / 6.0));
        double d9 = p_151605_1_ - d7;
        if (d9 > (d10 = p_151605_3_ - (d8 = (double)j2 - d6))) {
            k2 = 1;
            l2 = 0;
        } else {
            k2 = 0;
            l2 = 1;
        }
        double d11 = d9 - (double)k2 + d5;
        double d12 = d10 - (double)l2 + d5;
        double d13 = d9 - 1.0 + 2.0 * d5;
        double d14 = d10 - 1.0 + 2.0 * d5;
        int i1 = i2 & 0xFF;
        int j1 = j2 & 0xFF;
        int k1 = this.p[i1 + this.p[j1]] % 12;
        int l1 = this.p[i1 + k2 + this.p[j1 + l2]] % 12;
        int i22 = this.p[i1 + 1 + this.p[j1 + 1]] % 12;
        double d15 = 0.5 - d9 * d9 - d10 * d10;
        if (d15 < 0.0) {
            d0 = 0.0;
        } else {
            d15 *= d15;
            d0 = d15 * d15 * NoiseGeneratorSimplex.dot(grad3[k1], d9, d10);
        }
        double d16 = 0.5 - d11 * d11 - d12 * d12;
        if (d16 < 0.0) {
            d1 = 0.0;
        } else {
            d16 *= d16;
            d1 = d16 * d16 * NoiseGeneratorSimplex.dot(grad3[l1], d11, d12);
        }
        double d17 = 0.5 - d13 * d13 - d14 * d14;
        if (d17 < 0.0) {
            d2 = 0.0;
        } else {
            d17 *= d17;
            d2 = d17 * d17 * NoiseGeneratorSimplex.dot(grad3[i22], d13, d14);
        }
        return 70.0 * (d0 + d1 + d2);
    }

    public void add(double[] p_151606_1_, double p_151606_2_, double p_151606_4_, int p_151606_6_, int p_151606_7_, double p_151606_8_, double p_151606_10_, double p_151606_12_) {
        int i2 = 0;
        for (int j2 = 0; j2 < p_151606_7_; ++j2) {
            double d0 = (p_151606_4_ + (double)j2) * p_151606_10_ + this.yo;
            for (int k2 = 0; k2 < p_151606_6_; ++k2) {
                int i3;
                double d4;
                double d3;
                double d2;
                int k1;
                int j1;
                double d8;
                double d10;
                int i1;
                double d6;
                double d1 = (p_151606_2_ + (double)k2) * p_151606_8_ + this.xo;
                double d5 = (d1 + d0) * F2;
                int l2 = NoiseGeneratorSimplex.fastFloor(d1 + d5);
                double d7 = (double)l2 - (d6 = (double)(l2 + (i1 = NoiseGeneratorSimplex.fastFloor(d0 + d5))) * G2);
                double d9 = d1 - d7;
                if (d9 > (d10 = d0 - (d8 = (double)i1 - d6))) {
                    j1 = 1;
                    k1 = 0;
                } else {
                    j1 = 0;
                    k1 = 1;
                }
                double d11 = d9 - (double)j1 + G2;
                double d12 = d10 - (double)k1 + G2;
                double d13 = d9 - 1.0 + 2.0 * G2;
                double d14 = d10 - 1.0 + 2.0 * G2;
                int l1 = l2 & 0xFF;
                int i22 = i1 & 0xFF;
                int j22 = this.p[l1 + this.p[i22]] % 12;
                int k22 = this.p[l1 + j1 + this.p[i22 + k1]] % 12;
                int l22 = this.p[l1 + 1 + this.p[i22 + 1]] % 12;
                double d15 = 0.5 - d9 * d9 - d10 * d10;
                if (d15 < 0.0) {
                    d2 = 0.0;
                } else {
                    d15 *= d15;
                    d2 = d15 * d15 * NoiseGeneratorSimplex.dot(grad3[j22], d9, d10);
                }
                double d16 = 0.5 - d11 * d11 - d12 * d12;
                if (d16 < 0.0) {
                    d3 = 0.0;
                } else {
                    d16 *= d16;
                    d3 = d16 * d16 * NoiseGeneratorSimplex.dot(grad3[k22], d11, d12);
                }
                double d17 = 0.5 - d13 * d13 - d14 * d14;
                if (d17 < 0.0) {
                    d4 = 0.0;
                } else {
                    d17 *= d17;
                    d4 = d17 * d17 * NoiseGeneratorSimplex.dot(grad3[l22], d13, d14);
                }
                int n2 = i3 = i2++;
                p_151606_1_[n2] = p_151606_1_[n2] + 70.0 * (d2 + d3 + d4) * p_151606_12_;
            }
        }
    }
}

