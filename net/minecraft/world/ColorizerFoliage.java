package net.minecraft.world;

public class ColorizerFoliage {
    private static int[] foliageBuffer = new int[65536];

    public static void setFoliageBiomeColorizer(int[] foliageBufferIn) {
        foliageBuffer = foliageBufferIn;
    }

    public static int getFoliageColor(double temperature, double humidity) {
        int i2 = (int)((1.0 - temperature) * 255.0);
        int j2 = (int)((1.0 - (humidity *= temperature)) * 255.0);
        return foliageBuffer[j2 << 8 | i2];
    }

    public static int getFoliageColorPine() {
        return 0x619961;
    }

    public static int getFoliageColorBirch() {
        return 8431445;
    }

    public static int getFoliageColorBasic() {
        return 4764952;
    }
}

