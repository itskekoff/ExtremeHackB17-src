package optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import optifine.Blender;
import optifine.Config;
import optifine.CustomSkyLayer;
import optifine.TextureUtils;

public class CustomSky {
    private static CustomSkyLayer[][] worldSkyLayers = null;

    public static void reset() {
        worldSkyLayers = null;
    }

    public static void update() {
        CustomSky.reset();
        if (Config.isCustomSky()) {
            worldSkyLayers = CustomSky.readCustomSkies();
        }
    }

    private static CustomSkyLayer[][] readCustomSkies() {
        CustomSkyLayer[][] acustomskylayer = new CustomSkyLayer[10][0];
        String s2 = "mcpatcher/sky/world";
        int i2 = -1;
        for (int j2 = 0; j2 < acustomskylayer.length; ++j2) {
            String s1 = String.valueOf(s2) + j2 + "/sky";
            ArrayList<CustomSkyLayer> list = new ArrayList<CustomSkyLayer>();
            for (int k2 = 1; k2 < 1000; ++k2) {
                String s22 = String.valueOf(s1) + k2 + ".properties";
                try {
                    ResourceLocation resourcelocation = new ResourceLocation(s22);
                    InputStream inputstream = Config.getResourceStream(resourcelocation);
                    if (inputstream == null) break;
                    Properties properties = new Properties();
                    properties.load(inputstream);
                    inputstream.close();
                    Config.dbg("CustomSky properties: " + s22);
                    String s3 = String.valueOf(s1) + k2 + ".png";
                    CustomSkyLayer customskylayer = new CustomSkyLayer(properties, s3);
                    if (!customskylayer.isValid(s22)) continue;
                    ResourceLocation resourcelocation1 = new ResourceLocation(customskylayer.source);
                    ITextureObject itextureobject = TextureUtils.getTexture(resourcelocation1);
                    if (itextureobject == null) {
                        Config.log("CustomSky: Texture not found: " + resourcelocation1);
                        continue;
                    }
                    customskylayer.textureId = itextureobject.getGlTextureId();
                    list.add(customskylayer);
                    inputstream.close();
                    continue;
                }
                catch (FileNotFoundException var15) {
                    break;
                }
                catch (IOException ioexception) {
                    ioexception.printStackTrace();
                }
            }
            if (list.size() <= 0) continue;
            CustomSkyLayer[] acustomskylayer2 = list.toArray(new CustomSkyLayer[list.size()]);
            acustomskylayer[j2] = acustomskylayer2;
            i2 = j2;
        }
        if (i2 < 0) {
            return null;
        }
        int l2 = i2 + 1;
        CustomSkyLayer[][] acustomskylayer1 = new CustomSkyLayer[l2][0];
        for (int i1 = 0; i1 < acustomskylayer1.length; ++i1) {
            acustomskylayer1[i1] = acustomskylayer[i1];
        }
        return acustomskylayer1;
    }

    public static void renderSky(World p_renderSky_0_, TextureManager p_renderSky_1_, float p_renderSky_2_) {
        CustomSkyLayer[] acustomskylayer;
        int i2;
        if (worldSkyLayers != null && (i2 = p_renderSky_0_.provider.getDimensionType().getId()) >= 0 && i2 < worldSkyLayers.length && (acustomskylayer = worldSkyLayers[i2]) != null) {
            long j2 = p_renderSky_0_.getWorldTime();
            int k2 = (int)(j2 % 24000L);
            float f2 = p_renderSky_0_.getCelestialAngle(p_renderSky_2_);
            float f1 = p_renderSky_0_.getRainStrength(p_renderSky_2_);
            float f22 = p_renderSky_0_.getThunderStrength(p_renderSky_2_);
            if (f1 > 0.0f) {
                f22 /= f1;
            }
            for (int l2 = 0; l2 < acustomskylayer.length; ++l2) {
                CustomSkyLayer customskylayer = acustomskylayer[l2];
                if (!customskylayer.isActive(p_renderSky_0_, k2)) continue;
                customskylayer.render(k2, f2, f1, f22);
            }
            float f3 = 1.0f - f1;
            Blender.clearBlend(f3);
        }
    }

    public static boolean hasSkyLayers(World p_hasSkyLayers_0_) {
        if (worldSkyLayers == null) {
            return false;
        }
        if (Config.getGameSettings().renderDistanceChunks < 8) {
            return false;
        }
        int i2 = p_hasSkyLayers_0_.provider.getDimensionType().getId();
        if (i2 >= 0 && i2 < worldSkyLayers.length) {
            CustomSkyLayer[] acustomskylayer = worldSkyLayers[i2];
            if (acustomskylayer == null) {
                return false;
            }
            return acustomskylayer.length > 0;
        }
        return false;
    }
}

