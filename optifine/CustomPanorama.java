package optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.CustomPanoramaProperties;
import optifine.MathUtils;

public class CustomPanorama {
    private static CustomPanoramaProperties customPanoramaProperties = null;
    private static final Random random = new Random();

    public static CustomPanoramaProperties getCustomPanoramaProperties() {
        return customPanoramaProperties;
    }

    public static void update() {
        customPanoramaProperties = null;
        String[] astring = CustomPanorama.getPanoramaFolders();
        if (astring.length > 1) {
            CustomPanoramaProperties custompanoramaproperties;
            Properties[] aproperties = CustomPanorama.getPanoramaProperties(astring);
            int[] aint = CustomPanorama.getWeights(aproperties);
            int i2 = CustomPanorama.getRandomIndex(aint);
            String s2 = astring[i2];
            Properties properties = aproperties[i2];
            if (properties == null) {
                properties = aproperties[0];
            }
            if (properties == null) {
                properties = new Properties();
            }
            customPanoramaProperties = custompanoramaproperties = new CustomPanoramaProperties(s2, properties);
        }
    }

    private static String[] getPanoramaFolders() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("textures/gui/title/background");
        for (int i2 = 0; i2 < 100; ++i2) {
            String s2 = "optifine/gui/background" + i2;
            String s1 = String.valueOf(s2) + "/panorama_0.png";
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            if (!Config.hasResource(resourcelocation)) continue;
            list.add(s2);
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }

    private static Properties[] getPanoramaProperties(String[] p_getPanoramaProperties_0_) {
        Properties[] aproperties = new Properties[p_getPanoramaProperties_0_.length];
        for (int i2 = 0; i2 < p_getPanoramaProperties_0_.length; ++i2) {
            String s2 = p_getPanoramaProperties_0_[i2];
            if (i2 == 0) {
                s2 = "optifine/gui";
            } else {
                Config.dbg("CustomPanorama: " + s2);
            }
            ResourceLocation resourcelocation = new ResourceLocation(String.valueOf(s2) + "/background.properties");
            try {
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                if (inputstream == null) continue;
                Properties properties = new Properties();
                properties.load(inputstream);
                Config.dbg("CustomPanorama: " + resourcelocation.getResourcePath());
                aproperties[i2] = properties;
                inputstream.close();
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return aproperties;
    }

    private static int[] getWeights(Properties[] p_getWeights_0_) {
        int[] aint = new int[p_getWeights_0_.length];
        for (int i2 = 0; i2 < aint.length; ++i2) {
            Properties properties = p_getWeights_0_[i2];
            if (properties == null) {
                properties = p_getWeights_0_[0];
            }
            if (properties == null) {
                aint[i2] = 1;
                continue;
            }
            String s2 = properties.getProperty("weight", null);
            aint[i2] = Config.parseInt(s2, 1);
        }
        return aint;
    }

    private static int getRandomIndex(int[] p_getRandomIndex_0_) {
        int i2 = MathUtils.getSum(p_getRandomIndex_0_);
        int j2 = random.nextInt(i2);
        int k2 = 0;
        for (int l2 = 0; l2 < p_getRandomIndex_0_.length; ++l2) {
            if ((k2 += p_getRandomIndex_0_[l2]) <= j2) continue;
            return l2;
        }
        return p_getRandomIndex_0_.length - 1;
    }
}

