package optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;
import optifine.Config;

public class FontUtils {
    public static Properties readFontProperties(ResourceLocation p_readFontProperties_0_) {
        String s2 = p_readFontProperties_0_.getResourcePath();
        Properties properties = new Properties();
        String s1 = ".png";
        if (!s2.endsWith(s1)) {
            return properties;
        }
        String s22 = String.valueOf(s2.substring(0, s2.length() - s1.length())) + ".properties";
        try {
            ResourceLocation resourcelocation = new ResourceLocation(p_readFontProperties_0_.getResourceDomain(), s22);
            InputStream inputstream = Config.getResourceStream(Config.getResourceManager(), resourcelocation);
            if (inputstream == null) {
                return properties;
            }
            Config.log("Loading " + s22);
            properties.load(inputstream);
        }
        catch (FileNotFoundException resourcelocation) {
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return properties;
    }

    public static void readCustomCharWidths(Properties p_readCustomCharWidths_0_, float[] p_readCustomCharWidths_1_) {
        for (Object s2 : p_readCustomCharWidths_0_.keySet()) {
            String s3;
            float f2;
            String s22;
            int i2;
            String s1;
            if (!((String)s2).startsWith(s1 = "width.") || (i2 = Config.parseInt(s22 = ((String)s2).substring(s1.length()), -1)) < 0 || i2 >= p_readCustomCharWidths_1_.length || !((f2 = Config.parseFloat(s3 = p_readCustomCharWidths_0_.getProperty((String)s2), -1.0f)) >= 0.0f)) continue;
            p_readCustomCharWidths_1_[i2] = f2;
        }
    }

    public static float readFloat(Properties p_readFloat_0_, String p_readFloat_1_, float p_readFloat_2_) {
        String s2 = p_readFloat_0_.getProperty(p_readFloat_1_);
        if (s2 == null) {
            return p_readFloat_2_;
        }
        float f2 = Config.parseFloat(s2, Float.MIN_VALUE);
        if (f2 == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + p_readFloat_1_ + ": " + s2);
            return p_readFloat_2_;
        }
        return f2;
    }

    public static boolean readBoolean(Properties p_readBoolean_0_, String p_readBoolean_1_, boolean p_readBoolean_2_) {
        String s2 = p_readBoolean_0_.getProperty(p_readBoolean_1_);
        if (s2 == null) {
            return p_readBoolean_2_;
        }
        String s1 = s2.toLowerCase().trim();
        if (!s1.equals("true") && !s1.equals("on")) {
            if (!s1.equals("false") && !s1.equals("off")) {
                Config.warn("Invalid value for " + p_readBoolean_1_ + ": " + s2);
                return p_readBoolean_2_;
            }
            return false;
        }
        return true;
    }

    public static ResourceLocation getHdFontLocation(ResourceLocation p_getHdFontLocation_0_) {
        if (!Config.isCustomFonts()) {
            return p_getHdFontLocation_0_;
        }
        if (p_getHdFontLocation_0_ == null) {
            return p_getHdFontLocation_0_;
        }
        if (!Config.isMinecraftThread()) {
            return p_getHdFontLocation_0_;
        }
        String s2 = p_getHdFontLocation_0_.getResourcePath();
        String s1 = "textures/";
        String s22 = "mcpatcher/";
        if (!s2.startsWith(s1)) {
            return p_getHdFontLocation_0_;
        }
        s2 = s2.substring(s1.length());
        s2 = String.valueOf(s22) + s2;
        ResourceLocation resourcelocation = new ResourceLocation(p_getHdFontLocation_0_.getResourceDomain(), s2);
        return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : p_getHdFontLocation_0_;
    }
}

