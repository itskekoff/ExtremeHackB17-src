package com.jcraft.jorbis;

import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Residue0;

class Residue2
extends Residue0 {
    Residue2() {
    }

    int inverse(Block vb2, Object vl2, float[][] in2, int[] nonzero, int ch2) {
        int i2 = 0;
        for (i2 = 0; i2 < ch2 && nonzero[i2] == 0; ++i2) {
        }
        if (i2 == ch2) {
            return 0;
        }
        return Residue2._2inverse(vb2, vl2, in2, ch2);
    }
}

