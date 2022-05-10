package net.minecraft.world.chunk.storage;

public class NibbleArrayReader {
    public final byte[] data;
    private final int depthBits;
    private final int depthBitsPlusFour;

    public NibbleArrayReader(byte[] dataIn, int depthBitsIn) {
        this.data = dataIn;
        this.depthBits = depthBitsIn;
        this.depthBitsPlusFour = depthBitsIn + 4;
    }

    public int get(int x2, int y2, int z2) {
        int i2 = x2 << this.depthBitsPlusFour | z2 << this.depthBits | y2;
        int j2 = i2 >> 1;
        int k2 = i2 & 1;
        return k2 == 0 ? this.data[j2] & 0xF : this.data[j2] >> 4 & 0xF;
    }
}

