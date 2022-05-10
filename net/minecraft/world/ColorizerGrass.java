package net.minecraft.world;

public class ColorizerGrass {
    private static int[] grassBuffer = new int[65536];

    public static void setGrassBiomeColorizer(int[] grassBufferIn) {
        grassBuffer = grassBufferIn;
    }

    public static int getGrassColor(double temperature, double humidity) {
        int j2 = (int)((1.0 - (humidity *= temperature)) * 255.0);
        int i2 = (int)((1.0 - temperature) * 255.0);
        int k2 = j2 << 8 | i2;
        return k2 > grassBuffer.length ? -65281 : grassBuffer[k2];
    }
}

