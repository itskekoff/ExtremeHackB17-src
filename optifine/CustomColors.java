package optifine;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import optifine.BlockPosM;
import optifine.Config;
import optifine.ConnectedParser;
import optifine.CustomColorFader;
import optifine.CustomColormap;
import optifine.EntityUtils;
import optifine.MatchBlock;
import optifine.Reflector;
import optifine.RenderEnv;
import optifine.ResUtils;
import optifine.StrUtils;
import optifine.TextureUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomColors {
    private static String paletteFormatDefault = "vanilla";
    private static CustomColormap waterColors = null;
    private static CustomColormap foliagePineColors = null;
    private static CustomColormap foliageBirchColors = null;
    private static CustomColormap swampFoliageColors = null;
    private static CustomColormap swampGrassColors = null;
    private static CustomColormap[] colorsBlockColormaps = null;
    private static CustomColormap[][] blockColormaps = null;
    private static CustomColormap skyColors = null;
    private static CustomColorFader skyColorFader = new CustomColorFader();
    private static CustomColormap fogColors = null;
    private static CustomColorFader fogColorFader = new CustomColorFader();
    private static CustomColormap underwaterColors = null;
    private static CustomColorFader underwaterColorFader = new CustomColorFader();
    private static CustomColormap underlavaColors = null;
    private static CustomColorFader underlavaColorFader = new CustomColorFader();
    private static CustomColormap[] lightMapsColorsRgb = null;
    private static int lightmapMinDimensionId = 0;
    private static float[][] sunRgbs = new float[16][3];
    private static float[][] torchRgbs = new float[16][3];
    private static CustomColormap redstoneColors = null;
    private static CustomColormap xpOrbColors = null;
    private static int xpOrbTime = -1;
    private static CustomColormap durabilityColors = null;
    private static CustomColormap stemColors = null;
    private static CustomColormap stemMelonColors = null;
    private static CustomColormap stemPumpkinColors = null;
    private static CustomColormap myceliumParticleColors = null;
    private static boolean useDefaultGrassFoliageColors = true;
    private static int particleWaterColor = -1;
    private static int particlePortalColor = -1;
    private static int lilyPadColor = -1;
    private static int expBarTextColor = -1;
    private static int bossTextColor = -1;
    private static int signTextColor = -1;
    private static Vec3d fogColorNether = null;
    private static Vec3d fogColorEnd = null;
    private static Vec3d skyColorEnd = null;
    private static int[] spawnEggPrimaryColors = null;
    private static int[] spawnEggSecondaryColors = null;
    private static float[][] wolfCollarColors = null;
    private static float[][] sheepColors = null;
    private static int[] textColors = null;
    private static int[] mapColorsOriginal = null;
    private static int[] potionColors = null;
    private static final IBlockState BLOCK_STATE_DIRT = Blocks.DIRT.getDefaultState();
    private static final IBlockState BLOCK_STATE_WATER = Blocks.WATER.getDefaultState();
    public static Random random = new Random();
    private static final IColorizer COLORIZER_GRASS = new IColorizer(){

        @Override
        public int getColor(IBlockState p_getColor_1_, IBlockAccess p_getColor_2_, BlockPos p_getColor_3_) {
            Biome biome = CustomColors.getColorBiome(p_getColor_2_, p_getColor_3_);
            return swampGrassColors != null && biome == Biomes.SWAMPLAND ? swampGrassColors.getColor(biome, p_getColor_3_) : biome.getGrassColorAtPos(p_getColor_3_);
        }

        @Override
        public boolean isColorConstant() {
            return false;
        }
    };
    private static final IColorizer COLORIZER_FOLIAGE = new IColorizer(){

        @Override
        public int getColor(IBlockState p_getColor_1_, IBlockAccess p_getColor_2_, BlockPos p_getColor_3_) {
            Biome biome = CustomColors.getColorBiome(p_getColor_2_, p_getColor_3_);
            return swampFoliageColors != null && biome == Biomes.SWAMPLAND ? swampFoliageColors.getColor(biome, p_getColor_3_) : biome.getFoliageColorAtPos(p_getColor_3_);
        }

        @Override
        public boolean isColorConstant() {
            return false;
        }
    };
    private static final IColorizer COLORIZER_FOLIAGE_PINE = new IColorizer(){

        @Override
        public int getColor(IBlockState p_getColor_1_, IBlockAccess p_getColor_2_, BlockPos p_getColor_3_) {
            return foliagePineColors != null ? foliagePineColors.getColor(p_getColor_2_, p_getColor_3_) : ColorizerFoliage.getFoliageColorPine();
        }

        @Override
        public boolean isColorConstant() {
            return foliagePineColors == null;
        }
    };
    private static final IColorizer COLORIZER_FOLIAGE_BIRCH = new IColorizer(){

        @Override
        public int getColor(IBlockState p_getColor_1_, IBlockAccess p_getColor_2_, BlockPos p_getColor_3_) {
            return foliageBirchColors != null ? foliageBirchColors.getColor(p_getColor_2_, p_getColor_3_) : ColorizerFoliage.getFoliageColorBirch();
        }

        @Override
        public boolean isColorConstant() {
            return foliageBirchColors == null;
        }
    };
    private static final IColorizer COLORIZER_WATER = new IColorizer(){

        @Override
        public int getColor(IBlockState p_getColor_1_, IBlockAccess p_getColor_2_, BlockPos p_getColor_3_) {
            Biome biome = CustomColors.getColorBiome(p_getColor_2_, p_getColor_3_);
            if (waterColors != null) {
                return waterColors.getColor(biome, p_getColor_3_);
            }
            return Reflector.ForgeBiome_getWaterColorMultiplier.exists() ? Reflector.callInt(biome, Reflector.ForgeBiome_getWaterColorMultiplier, new Object[0]) : biome.getWaterColor();
        }

        @Override
        public boolean isColorConstant() {
            return false;
        }
    };

    public static void update() {
        paletteFormatDefault = "vanilla";
        waterColors = null;
        foliageBirchColors = null;
        foliagePineColors = null;
        swampGrassColors = null;
        swampFoliageColors = null;
        skyColors = null;
        fogColors = null;
        underwaterColors = null;
        underlavaColors = null;
        redstoneColors = null;
        xpOrbColors = null;
        xpOrbTime = -1;
        durabilityColors = null;
        stemColors = null;
        myceliumParticleColors = null;
        lightMapsColorsRgb = null;
        particleWaterColor = -1;
        particlePortalColor = -1;
        lilyPadColor = -1;
        expBarTextColor = -1;
        bossTextColor = -1;
        signTextColor = -1;
        fogColorNether = null;
        fogColorEnd = null;
        skyColorEnd = null;
        colorsBlockColormaps = null;
        blockColormaps = null;
        useDefaultGrassFoliageColors = true;
        spawnEggPrimaryColors = null;
        spawnEggSecondaryColors = null;
        wolfCollarColors = null;
        sheepColors = null;
        textColors = null;
        CustomColors.setMapColors(mapColorsOriginal);
        potionColors = null;
        paletteFormatDefault = CustomColors.getValidProperty("mcpatcher/color.properties", "palette.format", CustomColormap.FORMAT_STRINGS, "vanilla");
        String s2 = "mcpatcher/colormap/";
        String[] astring = new String[]{"water.png", "watercolorX.png"};
        waterColors = CustomColors.getCustomColors(s2, astring, 256, 256);
        CustomColors.updateUseDefaultGrassFoliageColors();
        if (Config.isCustomColors()) {
            String[] astring1 = new String[]{"pine.png", "pinecolor.png"};
            foliagePineColors = CustomColors.getCustomColors(s2, astring1, 256, 256);
            String[] astring2 = new String[]{"birch.png", "birchcolor.png"};
            foliageBirchColors = CustomColors.getCustomColors(s2, astring2, 256, 256);
            String[] astring3 = new String[]{"swampgrass.png", "swampgrasscolor.png"};
            swampGrassColors = CustomColors.getCustomColors(s2, astring3, 256, 256);
            String[] astring4 = new String[]{"swampfoliage.png", "swampfoliagecolor.png"};
            swampFoliageColors = CustomColors.getCustomColors(s2, astring4, 256, 256);
            String[] astring5 = new String[]{"sky0.png", "skycolor0.png"};
            skyColors = CustomColors.getCustomColors(s2, astring5, 256, 256);
            String[] astring6 = new String[]{"fog0.png", "fogcolor0.png"};
            fogColors = CustomColors.getCustomColors(s2, astring6, 256, 256);
            String[] astring7 = new String[]{"underwater.png", "underwatercolor.png"};
            underwaterColors = CustomColors.getCustomColors(s2, astring7, 256, 256);
            String[] astring8 = new String[]{"underlava.png", "underlavacolor.png"};
            underlavaColors = CustomColors.getCustomColors(s2, astring8, 256, 256);
            String[] astring9 = new String[]{"redstone.png", "redstonecolor.png"};
            redstoneColors = CustomColors.getCustomColors(s2, astring9, 16, 1);
            xpOrbColors = CustomColors.getCustomColors(String.valueOf(s2) + "xporb.png", -1, -1);
            durabilityColors = CustomColors.getCustomColors(String.valueOf(s2) + "durability.png", -1, -1);
            String[] astring10 = new String[]{"stem.png", "stemcolor.png"};
            stemColors = CustomColors.getCustomColors(s2, astring10, 8, 1);
            stemPumpkinColors = CustomColors.getCustomColors(String.valueOf(s2) + "pumpkinstem.png", 8, 1);
            stemMelonColors = CustomColors.getCustomColors(String.valueOf(s2) + "melonstem.png", 8, 1);
            String[] astring11 = new String[]{"myceliumparticle.png", "myceliumparticlecolor.png"};
            myceliumParticleColors = CustomColors.getCustomColors(s2, astring11, -1, -1);
            Pair<CustomColormap[], Integer> pair = CustomColors.parseLightmapsRgb();
            lightMapsColorsRgb = pair.getLeft();
            lightmapMinDimensionId = pair.getRight();
            CustomColors.readColorProperties("mcpatcher/color.properties");
            blockColormaps = CustomColors.readBlockColormaps(new String[]{String.valueOf(s2) + "custom/", String.valueOf(s2) + "blocks/"}, colorsBlockColormaps, 256, 256);
            CustomColors.updateUseDefaultGrassFoliageColors();
        }
    }

    private static String getValidProperty(String p_getValidProperty_0_, String p_getValidProperty_1_, String[] p_getValidProperty_2_, String p_getValidProperty_3_) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation(p_getValidProperty_0_);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return p_getValidProperty_3_;
            }
            Properties properties = new Properties();
            properties.load(inputstream);
            inputstream.close();
            String s2 = properties.getProperty(p_getValidProperty_1_);
            if (s2 == null) {
                return p_getValidProperty_3_;
            }
            List<String> list = Arrays.asList(p_getValidProperty_2_);
            if (!list.contains(s2)) {
                CustomColors.warn("Invalid value: " + p_getValidProperty_1_ + "=" + s2);
                CustomColors.warn("Expected values: " + Config.arrayToString(p_getValidProperty_2_));
                return p_getValidProperty_3_;
            }
            CustomColors.dbg(p_getValidProperty_1_ + "=" + s2);
            return s2;
        }
        catch (FileNotFoundException var9) {
            return p_getValidProperty_3_;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
            return p_getValidProperty_3_;
        }
    }

    private static Pair<CustomColormap[], Integer> parseLightmapsRgb() {
        String s2 = "mcpatcher/lightmap/world";
        String s1 = ".png";
        String[] astring = ResUtils.collectFiles(s2, s1);
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s22 = astring[i2];
            String s3 = StrUtils.removePrefixSuffix(s22, s2, s1);
            int j2 = Config.parseInt(s3, Integer.MIN_VALUE);
            if (j2 == Integer.MIN_VALUE) {
                CustomColors.warn("Invalid dimension ID: " + s3 + ", path: " + s22);
                continue;
            }
            map.put(j2, s22);
        }
        Set set = map.keySet();
        Object[] ainteger = set.toArray(new Integer[set.size()]);
        Arrays.sort(ainteger);
        if (ainteger.length <= 0) {
            return new ImmutablePair<Object, Integer>(null, 0);
        }
        int j1 = (Integer)ainteger[0];
        int k1 = (Integer)ainteger[ainteger.length - 1];
        int k2 = k1 - j1 + 1;
        CustomColormap[] acustomcolormap = new CustomColormap[k2];
        for (int l2 = 0; l2 < ainteger.length; ++l2) {
            Object integer = ainteger[l2];
            String s4 = (String)map.get(integer);
            CustomColormap customcolormap = CustomColors.getCustomColors(s4, -1, -1);
            if (customcolormap == null) continue;
            if (customcolormap.getWidth() < 16) {
                CustomColors.warn("Invalid lightmap width: " + customcolormap.getWidth() + ", path: " + s4);
                continue;
            }
            int i1 = (Integer)integer - j1;
            acustomcolormap[i1] = customcolormap;
        }
        return new ImmutablePair<CustomColormap[], Integer>(acustomcolormap, j1);
    }

    private static int getTextureHeight(String p_getTextureHeight_0_, int p_getTextureHeight_1_) {
        try {
            InputStream inputstream = Config.getResourceStream(new ResourceLocation(p_getTextureHeight_0_));
            if (inputstream == null) {
                return p_getTextureHeight_1_;
            }
            BufferedImage bufferedimage = ImageIO.read(inputstream);
            inputstream.close();
            return bufferedimage == null ? p_getTextureHeight_1_ : bufferedimage.getHeight();
        }
        catch (IOException var4) {
            return p_getTextureHeight_1_;
        }
    }

    private static void readColorProperties(String p_readColorProperties_0_) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation(p_readColorProperties_0_);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            CustomColors.dbg("Loading " + p_readColorProperties_0_);
            Properties properties = new Properties();
            properties.load(inputstream);
            inputstream.close();
            particleWaterColor = CustomColors.readColor(properties, new String[]{"particle.water", "drop.water"});
            particlePortalColor = CustomColors.readColor(properties, "particle.portal");
            lilyPadColor = CustomColors.readColor(properties, "lilypad");
            expBarTextColor = CustomColors.readColor(properties, "text.xpbar");
            bossTextColor = CustomColors.readColor(properties, "text.boss");
            signTextColor = CustomColors.readColor(properties, "text.sign");
            fogColorNether = CustomColors.readColorVec3(properties, "fog.nether");
            fogColorEnd = CustomColors.readColorVec3(properties, "fog.end");
            skyColorEnd = CustomColors.readColorVec3(properties, "sky.end");
            colorsBlockColormaps = CustomColors.readCustomColormaps(properties, p_readColorProperties_0_);
            spawnEggPrimaryColors = CustomColors.readSpawnEggColors(properties, p_readColorProperties_0_, "egg.shell.", "Spawn egg shell");
            spawnEggSecondaryColors = CustomColors.readSpawnEggColors(properties, p_readColorProperties_0_, "egg.spots.", "Spawn egg spot");
            wolfCollarColors = CustomColors.readDyeColors(properties, p_readColorProperties_0_, "collar.", "Wolf collar");
            sheepColors = CustomColors.readDyeColors(properties, p_readColorProperties_0_, "sheep.", "Sheep");
            textColors = CustomColors.readTextColors(properties, p_readColorProperties_0_, "text.code.", "Text");
            int[] aint = CustomColors.readMapColors(properties, p_readColorProperties_0_, "map.", "Map");
            if (aint != null) {
                if (mapColorsOriginal == null) {
                    mapColorsOriginal = CustomColors.getMapColors();
                }
                CustomColors.setMapColors(aint);
            }
            potionColors = CustomColors.readPotionColors(properties, p_readColorProperties_0_, "potion.", "Potion");
            xpOrbTime = Config.parseInt(properties.getProperty("xporb.time"), -1);
        }
        catch (FileNotFoundException var5) {
            return;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private static CustomColormap[] readCustomColormaps(Properties p_readCustomColormaps_0_, String p_readCustomColormaps_1_) {
        ArrayList<CustomColormap> list = new ArrayList<CustomColormap>();
        String s2 = "palette.block.";
        HashMap map = new HashMap();
        for (Object s1 : p_readCustomColormaps_0_.keySet()) {
            String s22 = p_readCustomColormaps_0_.getProperty((String)s1);
            if (!((String)s1).startsWith(s2)) continue;
            map.put(s1, s22);
        }
        String[] astring = map.keySet().toArray(new String[map.size()]);
        for (int j2 = 0; j2 < astring.length; ++j2) {
            String s6 = astring[j2];
            String s3 = p_readCustomColormaps_0_.getProperty(s6);
            CustomColors.dbg("Block palette: " + s6 + " = " + s3);
            String s4 = s6.substring(s2.length());
            String s5 = TextureUtils.getBasePath(p_readCustomColormaps_1_);
            s4 = TextureUtils.fixResourcePath(s4, s5);
            CustomColormap customcolormap = CustomColors.getCustomColors(s4, 256, 256);
            if (customcolormap == null) {
                CustomColors.warn("Colormap not found: " + s4);
                continue;
            }
            ConnectedParser connectedparser = new ConnectedParser("CustomColors");
            MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s3);
            if (amatchblock != null && amatchblock.length > 0) {
                for (int i2 = 0; i2 < amatchblock.length; ++i2) {
                    MatchBlock matchblock = amatchblock[i2];
                    customcolormap.addMatchBlock(matchblock);
                }
                list.add(customcolormap);
                continue;
            }
            CustomColors.warn("Invalid match blocks: " + s3);
        }
        if (list.size() <= 0) {
            return null;
        }
        CustomColormap[] acustomcolormap = list.toArray(new CustomColormap[list.size()]);
        return acustomcolormap;
    }

    private static CustomColormap[][] readBlockColormaps(String[] p_readBlockColormaps_0_, CustomColormap[] p_readBlockColormaps_1_, int p_readBlockColormaps_2_, int p_readBlockColormaps_3_) {
        Object[] astring = ResUtils.collectFiles(p_readBlockColormaps_0_, new String[]{".properties"});
        Arrays.sort(astring);
        ArrayList list = new ArrayList();
        for (int i2 = 0; i2 < astring.length; ++i2) {
            Object s2 = astring[i2];
            CustomColors.dbg("Block colormap: " + (String)s2);
            try {
                ResourceLocation resourcelocation = new ResourceLocation("minecraft", (String)s2);
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                if (inputstream == null) {
                    CustomColors.warn("File not found: " + (String)s2);
                    continue;
                }
                Properties properties = new Properties();
                properties.load(inputstream);
                CustomColormap customcolormap = new CustomColormap(properties, (String)s2, p_readBlockColormaps_2_, p_readBlockColormaps_3_, paletteFormatDefault);
                if (!customcolormap.isValid((String)s2) || !customcolormap.isValidMatchBlocks((String)s2)) continue;
                CustomColors.addToBlockList(customcolormap, list);
                continue;
            }
            catch (FileNotFoundException var12) {
                CustomColors.warn("File not found: " + (String)s2);
                continue;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (p_readBlockColormaps_1_ != null) {
            for (int j2 = 0; j2 < p_readBlockColormaps_1_.length; ++j2) {
                CustomColormap customcolormap1 = p_readBlockColormaps_1_[j2];
                CustomColors.addToBlockList(customcolormap1, list);
            }
        }
        if (list.size() <= 0) {
            return null;
        }
        CustomColormap[][] acustomcolormap = CustomColors.blockListToArray(list);
        return acustomcolormap;
    }

    private static void addToBlockList(CustomColormap p_addToBlockList_0_, List p_addToBlockList_1_) {
        int[] aint = p_addToBlockList_0_.getMatchBlockIds();
        if (aint != null && aint.length > 0) {
            for (int i2 = 0; i2 < aint.length; ++i2) {
                int j2 = aint[i2];
                if (j2 < 0) {
                    CustomColors.warn("Invalid block ID: " + j2);
                    continue;
                }
                CustomColors.addToList(p_addToBlockList_0_, p_addToBlockList_1_, j2);
            }
        } else {
            CustomColors.warn("No match blocks: " + Config.arrayToString(aint));
        }
    }

    private static void addToList(CustomColormap p_addToList_0_, List p_addToList_1_, int p_addToList_2_) {
        while (p_addToList_2_ >= p_addToList_1_.size()) {
            p_addToList_1_.add(null);
        }
        ArrayList<CustomColormap> list = (ArrayList<CustomColormap>)p_addToList_1_.get(p_addToList_2_);
        if (list == null) {
            list = new ArrayList<CustomColormap>();
            p_addToList_1_.set(p_addToList_2_, list);
        }
        list.add(p_addToList_0_);
    }

    private static CustomColormap[][] blockListToArray(List p_blockListToArray_0_) {
        CustomColormap[][] acustomcolormap = new CustomColormap[p_blockListToArray_0_.size()][];
        for (int i2 = 0; i2 < p_blockListToArray_0_.size(); ++i2) {
            List list = (List)p_blockListToArray_0_.get(i2);
            if (list == null) continue;
            CustomColormap[] acustomcolormap1 = list.toArray(new CustomColormap[list.size()]);
            acustomcolormap[i2] = acustomcolormap1;
        }
        return acustomcolormap;
    }

    private static int readColor(Properties p_readColor_0_, String[] p_readColor_1_) {
        for (int i2 = 0; i2 < p_readColor_1_.length; ++i2) {
            String s2 = p_readColor_1_[i2];
            int j2 = CustomColors.readColor(p_readColor_0_, s2);
            if (j2 < 0) continue;
            return j2;
        }
        return -1;
    }

    private static int readColor(Properties p_readColor_0_, String p_readColor_1_) {
        String s2 = p_readColor_0_.getProperty(p_readColor_1_);
        if (s2 == null) {
            return -1;
        }
        int i2 = CustomColors.parseColor(s2 = s2.trim());
        if (i2 < 0) {
            CustomColors.warn("Invalid color: " + p_readColor_1_ + " = " + s2);
            return i2;
        }
        CustomColors.dbg(String.valueOf(p_readColor_1_) + " = " + s2);
        return i2;
    }

    private static int parseColor(String p_parseColor_0_) {
        if (p_parseColor_0_ == null) {
            return -1;
        }
        p_parseColor_0_ = p_parseColor_0_.trim();
        try {
            int i2 = Integer.parseInt(p_parseColor_0_, 16) & 0xFFFFFF;
            return i2;
        }
        catch (NumberFormatException var2) {
            return -1;
        }
    }

    private static Vec3d readColorVec3(Properties p_readColorVec3_0_, String p_readColorVec3_1_) {
        int i2 = CustomColors.readColor(p_readColorVec3_0_, p_readColorVec3_1_);
        if (i2 < 0) {
            return null;
        }
        int j2 = i2 >> 16 & 0xFF;
        int k2 = i2 >> 8 & 0xFF;
        int l2 = i2 & 0xFF;
        float f2 = (float)j2 / 255.0f;
        float f1 = (float)k2 / 255.0f;
        float f22 = (float)l2 / 255.0f;
        return new Vec3d(f2, f1, f22);
    }

    private static CustomColormap getCustomColors(String p_getCustomColors_0_, String[] p_getCustomColors_1_, int p_getCustomColors_2_, int p_getCustomColors_3_) {
        for (int i2 = 0; i2 < p_getCustomColors_1_.length; ++i2) {
            String s2 = p_getCustomColors_1_[i2];
            s2 = String.valueOf(p_getCustomColors_0_) + s2;
            CustomColormap customcolormap = CustomColors.getCustomColors(s2, p_getCustomColors_2_, p_getCustomColors_3_);
            if (customcolormap == null) continue;
            return customcolormap;
        }
        return null;
    }

    public static CustomColormap getCustomColors(String p_getCustomColors_0_, int p_getCustomColors_1_, int p_getCustomColors_2_) {
        block5: {
            try {
                ResourceLocation resourcelocation = new ResourceLocation(p_getCustomColors_0_);
                if (Config.hasResource(resourcelocation)) break block5;
                return null;
            }
            catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
        CustomColors.dbg("Colormap " + p_getCustomColors_0_);
        Properties properties = new Properties();
        String s2 = StrUtils.replaceSuffix(p_getCustomColors_0_, ".png", ".properties");
        ResourceLocation resourcelocation1 = new ResourceLocation(s2);
        if (Config.hasResource(resourcelocation1)) {
            InputStream inputstream = Config.getResourceStream(resourcelocation1);
            properties.load(inputstream);
            inputstream.close();
            CustomColors.dbg("Colormap properties: " + s2);
        } else {
            properties.put("format", paletteFormatDefault);
            properties.put("source", p_getCustomColors_0_);
            s2 = p_getCustomColors_0_;
        }
        CustomColormap customcolormap = new CustomColormap(properties, s2, p_getCustomColors_1_, p_getCustomColors_2_, paletteFormatDefault);
        return !customcolormap.isValid(s2) ? null : customcolormap;
    }

    public static void updateUseDefaultGrassFoliageColors() {
        useDefaultGrassFoliageColors = foliageBirchColors == null && foliagePineColors == null && swampGrassColors == null && swampFoliageColors == null && Config.isSwampColors() && Config.isSmoothBiomes();
    }

    public static int getColorMultiplier(BakedQuad p_getColorMultiplier_0_, IBlockState p_getColorMultiplier_1_, IBlockAccess p_getColorMultiplier_2_, BlockPos p_getColorMultiplier_3_, RenderEnv p_getColorMultiplier_4_) {
        IColorizer customcolors$icolorizer;
        Block block = p_getColorMultiplier_1_.getBlock();
        IBlockState iblockstate = p_getColorMultiplier_4_.getBlockState();
        if (blockColormaps != null) {
            CustomColormap customcolormap;
            if (!p_getColorMultiplier_0_.hasTintIndex()) {
                if (block == Blocks.GRASS) {
                    iblockstate = BLOCK_STATE_DIRT;
                }
                if (block == Blocks.REDSTONE_WIRE) {
                    return -1;
                }
            }
            if (block == Blocks.DOUBLE_PLANT && p_getColorMultiplier_4_.getMetadata() >= 8) {
                p_getColorMultiplier_3_ = p_getColorMultiplier_3_.down();
                iblockstate = p_getColorMultiplier_2_.getBlockState(p_getColorMultiplier_3_);
            }
            if ((customcolormap = CustomColors.getBlockColormap(iblockstate)) != null) {
                if (Config.isSmoothBiomes() && !customcolormap.isColorConstant()) {
                    return CustomColors.getSmoothColorMultiplier(p_getColorMultiplier_1_, p_getColorMultiplier_2_, p_getColorMultiplier_3_, customcolormap, p_getColorMultiplier_4_.getColorizerBlockPosM());
                }
                return customcolormap.getColor(p_getColorMultiplier_2_, p_getColorMultiplier_3_);
            }
        }
        if (!p_getColorMultiplier_0_.hasTintIndex()) {
            return -1;
        }
        if (block == Blocks.WATERLILY) {
            return CustomColors.getLilypadColorMultiplier(p_getColorMultiplier_2_, p_getColorMultiplier_3_);
        }
        if (block == Blocks.REDSTONE_WIRE) {
            return CustomColors.getRedstoneColor(p_getColorMultiplier_4_.getBlockState());
        }
        if (block instanceof BlockStem) {
            return CustomColors.getStemColorMultiplier(block, p_getColorMultiplier_2_, p_getColorMultiplier_3_, p_getColorMultiplier_4_);
        }
        if (useDefaultGrassFoliageColors) {
            return -1;
        }
        int i2 = p_getColorMultiplier_4_.getMetadata();
        if (block != Blocks.GRASS && block != Blocks.TALLGRASS && block != Blocks.DOUBLE_PLANT) {
            if (block == Blocks.DOUBLE_PLANT) {
                customcolors$icolorizer = COLORIZER_GRASS;
                if (i2 >= 8) {
                    p_getColorMultiplier_3_ = p_getColorMultiplier_3_.down();
                }
            } else if (block == Blocks.LEAVES) {
                switch (i2 & 3) {
                    case 0: {
                        customcolors$icolorizer = COLORIZER_FOLIAGE;
                        break;
                    }
                    case 1: {
                        customcolors$icolorizer = COLORIZER_FOLIAGE_PINE;
                        break;
                    }
                    case 2: {
                        customcolors$icolorizer = COLORIZER_FOLIAGE_BIRCH;
                        break;
                    }
                    default: {
                        customcolors$icolorizer = COLORIZER_FOLIAGE;
                        break;
                    }
                }
            } else if (block == Blocks.LEAVES2) {
                customcolors$icolorizer = COLORIZER_FOLIAGE;
            } else {
                if (block != Blocks.VINE) {
                    return -1;
                }
                customcolors$icolorizer = COLORIZER_FOLIAGE;
            }
        } else {
            customcolors$icolorizer = COLORIZER_GRASS;
        }
        return Config.isSmoothBiomes() && !customcolors$icolorizer.isColorConstant() ? CustomColors.getSmoothColorMultiplier(p_getColorMultiplier_1_, p_getColorMultiplier_2_, p_getColorMultiplier_3_, customcolors$icolorizer, p_getColorMultiplier_4_.getColorizerBlockPosM()) : customcolors$icolorizer.getColor(iblockstate, p_getColorMultiplier_2_, p_getColorMultiplier_3_);
    }

    protected static Biome getColorBiome(IBlockAccess p_getColorBiome_0_, BlockPos p_getColorBiome_1_) {
        Biome biome = p_getColorBiome_0_.getBiome(p_getColorBiome_1_);
        if (biome == Biomes.SWAMPLAND && !Config.isSwampColors()) {
            biome = Biomes.PLAINS;
        }
        return biome;
    }

    private static CustomColormap getBlockColormap(IBlockState p_getBlockColormap_0_) {
        if (blockColormaps == null) {
            return null;
        }
        if (!(p_getBlockColormap_0_ instanceof BlockStateBase)) {
            return null;
        }
        BlockStateBase blockstatebase = (BlockStateBase)p_getBlockColormap_0_;
        int i2 = blockstatebase.getBlockId();
        if (i2 >= 0 && i2 < blockColormaps.length) {
            CustomColormap[] acustomcolormap = blockColormaps[i2];
            if (acustomcolormap == null) {
                return null;
            }
            for (int j2 = 0; j2 < acustomcolormap.length; ++j2) {
                CustomColormap customcolormap = acustomcolormap[j2];
                if (!customcolormap.matchesBlock(blockstatebase)) continue;
                return customcolormap;
            }
            return null;
        }
        return null;
    }

    private static int getSmoothColorMultiplier(IBlockState p_getSmoothColorMultiplier_0_, IBlockAccess p_getSmoothColorMultiplier_1_, BlockPos p_getSmoothColorMultiplier_2_, IColorizer p_getSmoothColorMultiplier_3_, BlockPosM p_getSmoothColorMultiplier_4_) {
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int l2 = p_getSmoothColorMultiplier_2_.getX();
        int i1 = p_getSmoothColorMultiplier_2_.getY();
        int j1 = p_getSmoothColorMultiplier_2_.getZ();
        BlockPosM blockposm = p_getSmoothColorMultiplier_4_;
        for (int k1 = l2 - 1; k1 <= l2 + 1; ++k1) {
            for (int l1 = j1 - 1; l1 <= j1 + 1; ++l1) {
                blockposm.setXyz(k1, i1, l1);
                int i22 = p_getSmoothColorMultiplier_3_.getColor(p_getSmoothColorMultiplier_0_, p_getSmoothColorMultiplier_1_, blockposm);
                i2 += i22 >> 16 & 0xFF;
                j2 += i22 >> 8 & 0xFF;
                k2 += i22 & 0xFF;
            }
        }
        int j22 = i2 / 9;
        int k22 = j2 / 9;
        int l22 = k2 / 9;
        return j22 << 16 | k22 << 8 | l22;
    }

    public static int getFluidColor(IBlockAccess p_getFluidColor_0_, IBlockState p_getFluidColor_1_, BlockPos p_getFluidColor_2_, RenderEnv p_getFluidColor_3_) {
        Block block = p_getFluidColor_1_.getBlock();
        IColorizer customcolors$icolorizer = CustomColors.getBlockColormap(p_getFluidColor_1_);
        if (customcolors$icolorizer == null && p_getFluidColor_1_.getMaterial() == Material.WATER) {
            customcolors$icolorizer = COLORIZER_WATER;
        }
        if (customcolors$icolorizer == null) {
            return CustomColors.getBlockColors().colorMultiplier(p_getFluidColor_1_, p_getFluidColor_0_, p_getFluidColor_2_, 0);
        }
        return Config.isSmoothBiomes() && !customcolors$icolorizer.isColorConstant() ? CustomColors.getSmoothColorMultiplier(p_getFluidColor_1_, p_getFluidColor_0_, p_getFluidColor_2_, customcolors$icolorizer, p_getFluidColor_3_.getColorizerBlockPosM()) : customcolors$icolorizer.getColor(p_getFluidColor_1_, p_getFluidColor_0_, p_getFluidColor_2_);
    }

    public static BlockColors getBlockColors() {
        return Minecraft.getMinecraft().getBlockColors();
    }

    public static void updatePortalFX(Particle p_updatePortalFX_0_) {
        if (particlePortalColor >= 0) {
            int i2 = particlePortalColor;
            int j2 = i2 >> 16 & 0xFF;
            int k2 = i2 >> 8 & 0xFF;
            int l2 = i2 & 0xFF;
            float f2 = (float)j2 / 255.0f;
            float f1 = (float)k2 / 255.0f;
            float f22 = (float)l2 / 255.0f;
            p_updatePortalFX_0_.setRBGColorF(f2, f1, f22);
        }
    }

    public static void updateMyceliumFX(Particle p_updateMyceliumFX_0_) {
        if (myceliumParticleColors != null) {
            int i2 = myceliumParticleColors.getColorRandom();
            int j2 = i2 >> 16 & 0xFF;
            int k2 = i2 >> 8 & 0xFF;
            int l2 = i2 & 0xFF;
            float f2 = (float)j2 / 255.0f;
            float f1 = (float)k2 / 255.0f;
            float f22 = (float)l2 / 255.0f;
            p_updateMyceliumFX_0_.setRBGColorF(f2, f1, f22);
        }
    }

    private static int getRedstoneColor(IBlockState p_getRedstoneColor_0_) {
        if (redstoneColors == null) {
            return -1;
        }
        int i2 = CustomColors.getRedstoneLevel(p_getRedstoneColor_0_, 15);
        int j2 = redstoneColors.getColor(i2);
        return j2;
    }

    public static void updateReddustFX(Particle p_updateReddustFX_0_, IBlockAccess p_updateReddustFX_1_, double p_updateReddustFX_2_, double p_updateReddustFX_4_, double p_updateReddustFX_6_) {
        if (redstoneColors != null) {
            IBlockState iblockstate = p_updateReddustFX_1_.getBlockState(new BlockPos(p_updateReddustFX_2_, p_updateReddustFX_4_, p_updateReddustFX_6_));
            int i2 = CustomColors.getRedstoneLevel(iblockstate, 15);
            int j2 = redstoneColors.getColor(i2);
            int k2 = j2 >> 16 & 0xFF;
            int l2 = j2 >> 8 & 0xFF;
            int i1 = j2 & 0xFF;
            float f2 = (float)k2 / 255.0f;
            float f1 = (float)l2 / 255.0f;
            float f22 = (float)i1 / 255.0f;
            p_updateReddustFX_0_.setRBGColorF(f2, f1, f22);
        }
    }

    private static int getRedstoneLevel(IBlockState p_getRedstoneLevel_0_, int p_getRedstoneLevel_1_) {
        Block block = p_getRedstoneLevel_0_.getBlock();
        if (!(block instanceof BlockRedstoneWire)) {
            return p_getRedstoneLevel_1_;
        }
        Integer object = p_getRedstoneLevel_0_.getValue(BlockRedstoneWire.POWER);
        if (!(object instanceof Integer)) {
            return p_getRedstoneLevel_1_;
        }
        Integer integer = object;
        return integer;
    }

    public static float getXpOrbTimer(float p_getXpOrbTimer_0_) {
        if (xpOrbTime <= 0) {
            return p_getXpOrbTimer_0_;
        }
        float f2 = 628.0f / (float)xpOrbTime;
        return p_getXpOrbTimer_0_ * f2;
    }

    public static int getXpOrbColor(float p_getXpOrbColor_0_) {
        if (xpOrbColors == null) {
            return -1;
        }
        int i2 = (int)Math.round((double)((MathHelper.sin(p_getXpOrbColor_0_) + 1.0f) * (float)(xpOrbColors.getLength() - 1)) / 2.0);
        int j2 = xpOrbColors.getColor(i2);
        return j2;
    }

    public static int getDurabilityColor(float p_getDurabilityColor_0_, int p_getDurabilityColor_1_) {
        if (durabilityColors == null) {
            return p_getDurabilityColor_1_;
        }
        int i2 = (int)(p_getDurabilityColor_0_ * (float)durabilityColors.getLength());
        int j2 = durabilityColors.getColor(i2);
        return j2;
    }

    public static void updateWaterFX(Particle p_updateWaterFX_0_, IBlockAccess p_updateWaterFX_1_, double p_updateWaterFX_2_, double p_updateWaterFX_4_, double p_updateWaterFX_6_, RenderEnv p_updateWaterFX_8_) {
        if (waterColors != null || blockColormaps != null) {
            BlockPos blockpos = new BlockPos(p_updateWaterFX_2_, p_updateWaterFX_4_, p_updateWaterFX_6_);
            p_updateWaterFX_8_.reset(p_updateWaterFX_1_, BLOCK_STATE_WATER, blockpos);
            int i2 = CustomColors.getFluidColor(p_updateWaterFX_1_, BLOCK_STATE_WATER, blockpos, p_updateWaterFX_8_);
            int j2 = i2 >> 16 & 0xFF;
            int k2 = i2 >> 8 & 0xFF;
            int l2 = i2 & 0xFF;
            float f2 = (float)j2 / 255.0f;
            float f1 = (float)k2 / 255.0f;
            float f22 = (float)l2 / 255.0f;
            if (particleWaterColor >= 0) {
                int i1 = particleWaterColor >> 16 & 0xFF;
                int j1 = particleWaterColor >> 8 & 0xFF;
                int k1 = particleWaterColor & 0xFF;
                f2 *= (float)i1 / 255.0f;
                f1 *= (float)j1 / 255.0f;
                f22 *= (float)k1 / 255.0f;
            }
            p_updateWaterFX_0_.setRBGColorF(f2, f1, f22);
        }
    }

    private static int getLilypadColorMultiplier(IBlockAccess p_getLilypadColorMultiplier_0_, BlockPos p_getLilypadColorMultiplier_1_) {
        return lilyPadColor < 0 ? CustomColors.getBlockColors().colorMultiplier(Blocks.WATERLILY.getDefaultState(), p_getLilypadColorMultiplier_0_, p_getLilypadColorMultiplier_1_, 0) : lilyPadColor;
    }

    private static Vec3d getFogColorNether(Vec3d p_getFogColorNether_0_) {
        return fogColorNether == null ? p_getFogColorNether_0_ : fogColorNether;
    }

    private static Vec3d getFogColorEnd(Vec3d p_getFogColorEnd_0_) {
        return fogColorEnd == null ? p_getFogColorEnd_0_ : fogColorEnd;
    }

    private static Vec3d getSkyColorEnd(Vec3d p_getSkyColorEnd_0_) {
        return skyColorEnd == null ? p_getSkyColorEnd_0_ : skyColorEnd;
    }

    public static Vec3d getSkyColor(Vec3d p_getSkyColor_0_, IBlockAccess p_getSkyColor_1_, double p_getSkyColor_2_, double p_getSkyColor_4_, double p_getSkyColor_6_) {
        if (skyColors == null) {
            return p_getSkyColor_0_;
        }
        int i2 = skyColors.getColorSmooth(p_getSkyColor_1_, p_getSkyColor_2_, p_getSkyColor_4_, p_getSkyColor_6_, 3);
        int j2 = i2 >> 16 & 0xFF;
        int k2 = i2 >> 8 & 0xFF;
        int l2 = i2 & 0xFF;
        float f2 = (float)j2 / 255.0f;
        float f1 = (float)k2 / 255.0f;
        float f22 = (float)l2 / 255.0f;
        float f3 = (float)p_getSkyColor_0_.xCoord / 0.5f;
        float f4 = (float)p_getSkyColor_0_.yCoord / 0.66275f;
        float f5 = (float)p_getSkyColor_0_.zCoord;
        Vec3d vec3d = skyColorFader.getColor(f2 *= f3, f1 *= f4, f22 *= f5);
        return vec3d;
    }

    private static Vec3d getFogColor(Vec3d p_getFogColor_0_, IBlockAccess p_getFogColor_1_, double p_getFogColor_2_, double p_getFogColor_4_, double p_getFogColor_6_) {
        if (fogColors == null) {
            return p_getFogColor_0_;
        }
        int i2 = fogColors.getColorSmooth(p_getFogColor_1_, p_getFogColor_2_, p_getFogColor_4_, p_getFogColor_6_, 3);
        int j2 = i2 >> 16 & 0xFF;
        int k2 = i2 >> 8 & 0xFF;
        int l2 = i2 & 0xFF;
        float f2 = (float)j2 / 255.0f;
        float f1 = (float)k2 / 255.0f;
        float f22 = (float)l2 / 255.0f;
        float f3 = (float)p_getFogColor_0_.xCoord / 0.753f;
        float f4 = (float)p_getFogColor_0_.yCoord / 0.8471f;
        float f5 = (float)p_getFogColor_0_.zCoord;
        Vec3d vec3d = fogColorFader.getColor(f2 *= f3, f1 *= f4, f22 *= f5);
        return vec3d;
    }

    public static Vec3d getUnderwaterColor(IBlockAccess p_getUnderwaterColor_0_, double p_getUnderwaterColor_1_, double p_getUnderwaterColor_3_, double p_getUnderwaterColor_5_) {
        return CustomColors.getUnderFluidColor(p_getUnderwaterColor_0_, p_getUnderwaterColor_1_, p_getUnderwaterColor_3_, p_getUnderwaterColor_5_, underwaterColors, underwaterColorFader);
    }

    public static Vec3d getUnderlavaColor(IBlockAccess p_getUnderlavaColor_0_, double p_getUnderlavaColor_1_, double p_getUnderlavaColor_3_, double p_getUnderlavaColor_5_) {
        return CustomColors.getUnderFluidColor(p_getUnderlavaColor_0_, p_getUnderlavaColor_1_, p_getUnderlavaColor_3_, p_getUnderlavaColor_5_, underlavaColors, underlavaColorFader);
    }

    public static Vec3d getUnderFluidColor(IBlockAccess p_getUnderFluidColor_0_, double p_getUnderFluidColor_1_, double p_getUnderFluidColor_3_, double p_getUnderFluidColor_5_, CustomColormap p_getUnderFluidColor_7_, CustomColorFader p_getUnderFluidColor_8_) {
        if (p_getUnderFluidColor_7_ == null) {
            return null;
        }
        int i2 = p_getUnderFluidColor_7_.getColorSmooth(p_getUnderFluidColor_0_, p_getUnderFluidColor_1_, p_getUnderFluidColor_3_, p_getUnderFluidColor_5_, 3);
        int j2 = i2 >> 16 & 0xFF;
        int k2 = i2 >> 8 & 0xFF;
        int l2 = i2 & 0xFF;
        float f2 = (float)j2 / 255.0f;
        float f1 = (float)k2 / 255.0f;
        float f22 = (float)l2 / 255.0f;
        Vec3d vec3d = p_getUnderFluidColor_8_.getColor(f2, f1, f22);
        return vec3d;
    }

    private static int getStemColorMultiplier(Block p_getStemColorMultiplier_0_, IBlockAccess p_getStemColorMultiplier_1_, BlockPos p_getStemColorMultiplier_2_, RenderEnv p_getStemColorMultiplier_3_) {
        CustomColormap customcolormap = stemColors;
        if (p_getStemColorMultiplier_0_ == Blocks.PUMPKIN_STEM && stemPumpkinColors != null) {
            customcolormap = stemPumpkinColors;
        }
        if (p_getStemColorMultiplier_0_ == Blocks.MELON_STEM && stemMelonColors != null) {
            customcolormap = stemMelonColors;
        }
        if (customcolormap == null) {
            return -1;
        }
        int i2 = p_getStemColorMultiplier_3_.getMetadata();
        return customcolormap.getColor(i2);
    }

    public static boolean updateLightmap(World p_updateLightmap_0_, float p_updateLightmap_1_, int[] p_updateLightmap_2_, boolean p_updateLightmap_3_) {
        if (p_updateLightmap_0_ == null) {
            return false;
        }
        if (lightMapsColorsRgb == null) {
            return false;
        }
        int i2 = p_updateLightmap_0_.provider.getDimensionType().getId();
        int j2 = i2 - lightmapMinDimensionId;
        if (j2 >= 0 && j2 < lightMapsColorsRgb.length) {
            CustomColormap customcolormap = lightMapsColorsRgb[j2];
            if (customcolormap == null) {
                return false;
            }
            int k2 = customcolormap.getHeight();
            if (p_updateLightmap_3_ && k2 < 64) {
                return false;
            }
            int l2 = customcolormap.getWidth();
            if (l2 < 16) {
                CustomColors.warn("Invalid lightmap width: " + l2 + " for dimension: " + i2);
                CustomColors.lightMapsColorsRgb[j2] = null;
                return false;
            }
            int i1 = 0;
            if (p_updateLightmap_3_) {
                i1 = l2 * 16 * 2;
            }
            float f2 = 1.1666666f * (p_updateLightmap_0_.getSunBrightness(1.0f) - 0.2f);
            if (p_updateLightmap_0_.getLastLightningBolt() > 0) {
                f2 = 1.0f;
            }
            f2 = Config.limitTo1(f2);
            float f1 = f2 * (float)(l2 - 1);
            float f22 = Config.limitTo1(p_updateLightmap_1_ + 0.5f) * (float)(l2 - 1);
            float f3 = Config.limitTo1(Config.getGameSettings().gammaSetting);
            boolean flag = f3 > 1.0E-4f;
            float[][] afloat = customcolormap.getColorsRgb();
            CustomColors.getLightMapColumn(afloat, f1, i1, l2, sunRgbs);
            CustomColors.getLightMapColumn(afloat, f22, i1 + 16 * l2, l2, torchRgbs);
            float[] afloat1 = new float[3];
            for (int j1 = 0; j1 < 16; ++j1) {
                for (int k1 = 0; k1 < 16; ++k1) {
                    for (int l1 = 0; l1 < 3; ++l1) {
                        float f4 = Config.limitTo1(sunRgbs[j1][l1] + torchRgbs[k1][l1]);
                        if (flag) {
                            float f5 = 1.0f - f4;
                            f5 = 1.0f - f5 * f5 * f5 * f5;
                            f4 = f3 * f5 + (1.0f - f3) * f4;
                        }
                        afloat1[l1] = f4;
                    }
                    int i22 = (int)(afloat1[0] * 255.0f);
                    int j22 = (int)(afloat1[1] * 255.0f);
                    int k22 = (int)(afloat1[2] * 255.0f);
                    p_updateLightmap_2_[j1 * 16 + k1] = 0xFF000000 | i22 << 16 | j22 << 8 | k22;
                }
            }
            return true;
        }
        return false;
    }

    private static void getLightMapColumn(float[][] p_getLightMapColumn_0_, float p_getLightMapColumn_1_, int p_getLightMapColumn_2_, int p_getLightMapColumn_3_, float[][] p_getLightMapColumn_4_) {
        int j2;
        int i2 = (int)Math.floor(p_getLightMapColumn_1_);
        if (i2 == (j2 = (int)Math.ceil(p_getLightMapColumn_1_))) {
            for (int i1 = 0; i1 < 16; ++i1) {
                float[] afloat3 = p_getLightMapColumn_0_[p_getLightMapColumn_2_ + i1 * p_getLightMapColumn_3_ + i2];
                float[] afloat4 = p_getLightMapColumn_4_[i1];
                for (int j1 = 0; j1 < 3; ++j1) {
                    afloat4[j1] = afloat3[j1];
                }
            }
        } else {
            float f2 = 1.0f - (p_getLightMapColumn_1_ - (float)i2);
            float f1 = 1.0f - ((float)j2 - p_getLightMapColumn_1_);
            for (int k2 = 0; k2 < 16; ++k2) {
                float[] afloat = p_getLightMapColumn_0_[p_getLightMapColumn_2_ + k2 * p_getLightMapColumn_3_ + i2];
                float[] afloat1 = p_getLightMapColumn_0_[p_getLightMapColumn_2_ + k2 * p_getLightMapColumn_3_ + j2];
                float[] afloat2 = p_getLightMapColumn_4_[k2];
                for (int l2 = 0; l2 < 3; ++l2) {
                    afloat2[l2] = afloat[l2] * f2 + afloat1[l2] * f1;
                }
            }
        }
    }

    public static Vec3d getWorldFogColor(Vec3d p_getWorldFogColor_0_, World p_getWorldFogColor_1_, Entity p_getWorldFogColor_2_, float p_getWorldFogColor_3_) {
        DimensionType dimensiontype = p_getWorldFogColor_1_.provider.getDimensionType();
        switch (dimensiontype) {
            case NETHER: {
                p_getWorldFogColor_0_ = CustomColors.getFogColorNether(p_getWorldFogColor_0_);
                break;
            }
            case OVERWORLD: {
                Minecraft minecraft = Minecraft.getMinecraft();
                p_getWorldFogColor_0_ = CustomColors.getFogColor(p_getWorldFogColor_0_, minecraft.world, p_getWorldFogColor_2_.posX, p_getWorldFogColor_2_.posY + 1.0, p_getWorldFogColor_2_.posZ);
                break;
            }
            case THE_END: {
                p_getWorldFogColor_0_ = CustomColors.getFogColorEnd(p_getWorldFogColor_0_);
            }
        }
        return p_getWorldFogColor_0_;
    }

    public static Vec3d getWorldSkyColor(Vec3d p_getWorldSkyColor_0_, World p_getWorldSkyColor_1_, Entity p_getWorldSkyColor_2_, float p_getWorldSkyColor_3_) {
        DimensionType dimensiontype = p_getWorldSkyColor_1_.provider.getDimensionType();
        switch (dimensiontype) {
            case OVERWORLD: {
                Minecraft minecraft = Minecraft.getMinecraft();
                p_getWorldSkyColor_0_ = CustomColors.getSkyColor(p_getWorldSkyColor_0_, minecraft.world, p_getWorldSkyColor_2_.posX, p_getWorldSkyColor_2_.posY + 1.0, p_getWorldSkyColor_2_.posZ);
                break;
            }
            case THE_END: {
                p_getWorldSkyColor_0_ = CustomColors.getSkyColorEnd(p_getWorldSkyColor_0_);
            }
        }
        return p_getWorldSkyColor_0_;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private static int[] readSpawnEggColors(Properties p_readSpawnEggColors_0_, String p_readSpawnEggColors_1_, String p_readSpawnEggColors_2_, String p_readSpawnEggColors_3_) {
        list = new ArrayList<Integer>();
        set = p_readSpawnEggColors_0_.keySet();
        i = 0;
        for (K s : set) {
            s1 = p_readSpawnEggColors_0_.getProperty((String)s);
            if (!((String)s).startsWith(p_readSpawnEggColors_2_)) continue;
            s2 = StrUtils.removePrefix((String)s, p_readSpawnEggColors_2_);
            j = EntityUtils.getEntityIdByName(s2);
            if (j < 0) {
                j = EntityUtils.getEntityIdByLocation(new ResourceLocation(s2).toString());
            }
            if (j < 0) {
                CustomColors.warn("Invalid spawn egg name: " + s);
                continue;
            }
            k = CustomColors.parseColor(s1);
            if (k >= 0) ** GOTO lbl20
            CustomColors.warn("Invalid spawn egg color: " + s + " = " + s1);
            continue;
lbl-1000:
            // 1 sources

            {
                list.add(-1);
lbl20:
                // 2 sources

                ** while (list.size() <= j)
            }
lbl21:
            // 1 sources

            list.set(j, k);
            ++i;
        }
        if (i <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(p_readSpawnEggColors_3_) + " colors: " + i);
        aint = new int[list.size()];
        for (l = 0; l < aint.length; ++l) {
            aint[l] = (Integer)list.get(l);
        }
        return aint;
    }

    private static int getSpawnEggColor(ItemMonsterPlacer p_getSpawnEggColor_0_, ItemStack p_getSpawnEggColor_1_, int p_getSpawnEggColor_2_, int p_getSpawnEggColor_3_) {
        int[] aint;
        if (spawnEggPrimaryColors == null && spawnEggSecondaryColors == null) {
            return p_getSpawnEggColor_3_;
        }
        NBTTagCompound nbttagcompound = p_getSpawnEggColor_1_.getTagCompound();
        if (nbttagcompound == null) {
            return p_getSpawnEggColor_3_;
        }
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("EntityTag");
        if (nbttagcompound1 == null) {
            return p_getSpawnEggColor_3_;
        }
        String s2 = nbttagcompound1.getString("id");
        int i2 = EntityUtils.getEntityIdByLocation(s2);
        int[] arrn = aint = p_getSpawnEggColor_2_ == 0 ? spawnEggPrimaryColors : spawnEggSecondaryColors;
        if (aint == null) {
            return p_getSpawnEggColor_3_;
        }
        if (i2 >= 0 && i2 < aint.length) {
            int j2 = aint[i2];
            return j2 < 0 ? p_getSpawnEggColor_3_ : j2;
        }
        return p_getSpawnEggColor_3_;
    }

    public static int getColorFromItemStack(ItemStack p_getColorFromItemStack_0_, int p_getColorFromItemStack_1_, int p_getColorFromItemStack_2_) {
        if (p_getColorFromItemStack_0_ == null) {
            return p_getColorFromItemStack_2_;
        }
        Item item = p_getColorFromItemStack_0_.getItem();
        if (item == null) {
            return p_getColorFromItemStack_2_;
        }
        return item instanceof ItemMonsterPlacer ? CustomColors.getSpawnEggColor((ItemMonsterPlacer)item, p_getColorFromItemStack_0_, p_getColorFromItemStack_1_, p_getColorFromItemStack_2_) : p_getColorFromItemStack_2_;
    }

    private static float[][] readDyeColors(Properties p_readDyeColors_0_, String p_readDyeColors_1_, String p_readDyeColors_2_, String p_readDyeColors_3_) {
        EnumDyeColor[] aenumdyecolor = EnumDyeColor.values();
        HashMap<String, EnumDyeColor> map = new HashMap<String, EnumDyeColor>();
        for (int i2 = 0; i2 < aenumdyecolor.length; ++i2) {
            EnumDyeColor enumdyecolor = aenumdyecolor[i2];
            map.put(enumdyecolor.getName(), enumdyecolor);
        }
        float[][] afloat1 = new float[aenumdyecolor.length][];
        int k2 = 0;
        for (Object s2 : p_readDyeColors_0_.keySet()) {
            String s1 = p_readDyeColors_0_.getProperty((String)s2);
            if (!((String)s2).startsWith(p_readDyeColors_2_)) continue;
            String s22 = StrUtils.removePrefix((String)s2, p_readDyeColors_2_);
            if (s22.equals("lightBlue")) {
                s22 = "light_blue";
            }
            EnumDyeColor enumdyecolor1 = (EnumDyeColor)map.get(s22);
            int j2 = CustomColors.parseColor(s1);
            if (enumdyecolor1 != null && j2 >= 0) {
                float[] afloat = new float[]{(float)(j2 >> 16 & 0xFF) / 255.0f, (float)(j2 >> 8 & 0xFF) / 255.0f, (float)(j2 & 0xFF) / 255.0f};
                afloat1[enumdyecolor1.ordinal()] = afloat;
                ++k2;
                continue;
            }
            CustomColors.warn("Invalid color: " + s2 + " = " + s1);
        }
        if (k2 <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(p_readDyeColors_3_) + " colors: " + k2);
        return afloat1;
    }

    private static float[] getDyeColors(EnumDyeColor p_getDyeColors_0_, float[][] p_getDyeColors_1_, float[] p_getDyeColors_2_) {
        if (p_getDyeColors_1_ == null) {
            return p_getDyeColors_2_;
        }
        if (p_getDyeColors_0_ == null) {
            return p_getDyeColors_2_;
        }
        float[] afloat = p_getDyeColors_1_[p_getDyeColors_0_.ordinal()];
        return afloat == null ? p_getDyeColors_2_ : afloat;
    }

    public static float[] getWolfCollarColors(EnumDyeColor p_getWolfCollarColors_0_, float[] p_getWolfCollarColors_1_) {
        return CustomColors.getDyeColors(p_getWolfCollarColors_0_, wolfCollarColors, p_getWolfCollarColors_1_);
    }

    public static float[] getSheepColors(EnumDyeColor p_getSheepColors_0_, float[] p_getSheepColors_1_) {
        return CustomColors.getDyeColors(p_getSheepColors_0_, sheepColors, p_getSheepColors_1_);
    }

    private static int[] readTextColors(Properties p_readTextColors_0_, String p_readTextColors_1_, String p_readTextColors_2_, String p_readTextColors_3_) {
        int[] aint = new int[32];
        Arrays.fill(aint, -1);
        int i2 = 0;
        for (Object s2 : p_readTextColors_0_.keySet()) {
            String s1 = p_readTextColors_0_.getProperty((String)s2);
            if (!((String)s2).startsWith(p_readTextColors_2_)) continue;
            String s22 = StrUtils.removePrefix((String)s2, p_readTextColors_2_);
            int j2 = Config.parseInt(s22, -1);
            int k2 = CustomColors.parseColor(s1);
            if (j2 >= 0 && j2 < aint.length && k2 >= 0) {
                aint[j2] = k2;
                ++i2;
                continue;
            }
            CustomColors.warn("Invalid color: " + s2 + " = " + s1);
        }
        if (i2 <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(p_readTextColors_3_) + " colors: " + i2);
        return aint;
    }

    public static int getTextColor(int p_getTextColor_0_, int p_getTextColor_1_) {
        if (textColors == null) {
            return p_getTextColor_1_;
        }
        if (p_getTextColor_0_ >= 0 && p_getTextColor_0_ < textColors.length) {
            int i2 = textColors[p_getTextColor_0_];
            return i2 < 0 ? p_getTextColor_1_ : i2;
        }
        return p_getTextColor_1_;
    }

    private static int[] readMapColors(Properties p_readMapColors_0_, String p_readMapColors_1_, String p_readMapColors_2_, String p_readMapColors_3_) {
        int[] aint = new int[MapColor.COLORS.length];
        Arrays.fill(aint, -1);
        int i2 = 0;
        for (Object s2 : p_readMapColors_0_.keySet()) {
            String s1 = p_readMapColors_0_.getProperty((String)s2);
            if (!((String)s2).startsWith(p_readMapColors_2_)) continue;
            String s22 = StrUtils.removePrefix((String)s2, p_readMapColors_2_);
            int j2 = CustomColors.getMapColorIndex(s22);
            int k2 = CustomColors.parseColor(s1);
            if (j2 >= 0 && j2 < aint.length && k2 >= 0) {
                aint[j2] = k2;
                ++i2;
                continue;
            }
            CustomColors.warn("Invalid color: " + s2 + " = " + s1);
        }
        if (i2 <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(p_readMapColors_3_) + " colors: " + i2);
        return aint;
    }

    private static int[] readPotionColors(Properties p_readPotionColors_0_, String p_readPotionColors_1_, String p_readPotionColors_2_, String p_readPotionColors_3_) {
        int[] aint = new int[CustomColors.getMaxPotionId()];
        Arrays.fill(aint, -1);
        int i2 = 0;
        for (Object s2 : p_readPotionColors_0_.keySet()) {
            String s1 = p_readPotionColors_0_.getProperty((String)s2);
            if (!((String)s2).startsWith(p_readPotionColors_2_)) continue;
            int j2 = CustomColors.getPotionId((String)s2);
            int k2 = CustomColors.parseColor(s1);
            if (j2 >= 0 && j2 < aint.length && k2 >= 0) {
                aint[j2] = k2;
                ++i2;
                continue;
            }
            CustomColors.warn("Invalid color: " + s2 + " = " + s1);
        }
        if (i2 <= 0) {
            return null;
        }
        CustomColors.dbg(String.valueOf(p_readPotionColors_3_) + " colors: " + i2);
        return aint;
    }

    private static int getMaxPotionId() {
        int i2 = 0;
        for (ResourceLocation resourcelocation : Potion.REGISTRY.getKeys()) {
            Potion potion = Potion.REGISTRY.getObject(resourcelocation);
            int j2 = Potion.getIdFromPotion(potion);
            if (j2 <= i2) continue;
            i2 = j2;
        }
        return i2;
    }

    private static int getPotionId(String p_getPotionId_0_) {
        if (p_getPotionId_0_.equals("potion.water")) {
            return 0;
        }
        p_getPotionId_0_ = StrUtils.replacePrefix(p_getPotionId_0_, "potion.", "effect.");
        for (ResourceLocation resourcelocation : Potion.REGISTRY.getKeys()) {
            Potion potion = Potion.REGISTRY.getObject(resourcelocation);
            if (!potion.getName().equals(p_getPotionId_0_)) continue;
            return Potion.getIdFromPotion(potion);
        }
        return -1;
    }

    public static int getPotionColor(Potion p_getPotionColor_0_, int p_getPotionColor_1_) {
        int i2 = 0;
        if (p_getPotionColor_0_ != null) {
            i2 = Potion.getIdFromPotion(p_getPotionColor_0_);
        }
        return CustomColors.getPotionColor(i2, p_getPotionColor_1_);
    }

    public static int getPotionColor(int p_getPotionColor_0_, int p_getPotionColor_1_) {
        if (potionColors == null) {
            return p_getPotionColor_1_;
        }
        if (p_getPotionColor_0_ >= 0 && p_getPotionColor_0_ < potionColors.length) {
            int i2 = potionColors[p_getPotionColor_0_];
            return i2 < 0 ? p_getPotionColor_1_ : i2;
        }
        return p_getPotionColor_1_;
    }

    private static int getMapColorIndex(String p_getMapColorIndex_0_) {
        if (p_getMapColorIndex_0_ == null) {
            return -1;
        }
        if (p_getMapColorIndex_0_.equals("air")) {
            return MapColor.AIR.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("grass")) {
            return MapColor.GRASS.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("sand")) {
            return MapColor.SAND.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("cloth")) {
            return MapColor.CLOTH.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("tnt")) {
            return MapColor.TNT.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("ice")) {
            return MapColor.ICE.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("iron")) {
            return MapColor.IRON.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("foliage")) {
            return MapColor.FOLIAGE.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("clay")) {
            return MapColor.CLAY.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("dirt")) {
            return MapColor.DIRT.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("stone")) {
            return MapColor.STONE.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("water")) {
            return MapColor.WATER.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("wood")) {
            return MapColor.WOOD.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("quartz")) {
            return MapColor.QUARTZ.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("gold")) {
            return MapColor.GOLD.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("diamond")) {
            return MapColor.DIAMOND.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("lapis")) {
            return MapColor.LAPIS.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("emerald")) {
            return MapColor.EMERALD.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("podzol")) {
            return MapColor.OBSIDIAN.colorIndex;
        }
        if (p_getMapColorIndex_0_.equals("netherrack")) {
            return MapColor.NETHERRACK.colorIndex;
        }
        if (!p_getMapColorIndex_0_.equals("snow") && !p_getMapColorIndex_0_.equals("white")) {
            if (!p_getMapColorIndex_0_.equals("adobe") && !p_getMapColorIndex_0_.equals("orange")) {
                if (p_getMapColorIndex_0_.equals("magenta")) {
                    return MapColor.MAGENTA.colorIndex;
                }
                if (!p_getMapColorIndex_0_.equals("light_blue") && !p_getMapColorIndex_0_.equals("lightBlue")) {
                    if (p_getMapColorIndex_0_.equals("yellow")) {
                        return MapColor.YELLOW.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("lime")) {
                        return MapColor.LIME.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("pink")) {
                        return MapColor.PINK.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("gray")) {
                        return MapColor.GRAY.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("silver")) {
                        return MapColor.SILVER.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("cyan")) {
                        return MapColor.CYAN.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("purple")) {
                        return MapColor.PURPLE.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("blue")) {
                        return MapColor.BLUE.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("brown")) {
                        return MapColor.BROWN.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("green")) {
                        return MapColor.GREEN.colorIndex;
                    }
                    if (p_getMapColorIndex_0_.equals("red")) {
                        return MapColor.RED.colorIndex;
                    }
                    return p_getMapColorIndex_0_.equals("black") ? MapColor.BLACK.colorIndex : -1;
                }
                return MapColor.LIGHT_BLUE.colorIndex;
            }
            return MapColor.ADOBE.colorIndex;
        }
        return MapColor.SNOW.colorIndex;
    }

    private static int[] getMapColors() {
        MapColor[] amapcolor = MapColor.COLORS;
        int[] aint = new int[amapcolor.length];
        Arrays.fill(aint, -1);
        for (int i2 = 0; i2 < amapcolor.length && i2 < aint.length; ++i2) {
            MapColor mapcolor = amapcolor[i2];
            if (mapcolor == null) continue;
            aint[i2] = mapcolor.colorValue;
        }
        return aint;
    }

    private static void setMapColors(int[] p_setMapColors_0_) {
        if (p_setMapColors_0_ != null) {
            MapColor[] amapcolor = MapColor.COLORS;
            boolean flag = false;
            for (int i2 = 0; i2 < amapcolor.length && i2 < p_setMapColors_0_.length; ++i2) {
                int j2;
                MapColor mapcolor = amapcolor[i2];
                if (mapcolor == null || (j2 = p_setMapColors_0_[i2]) < 0 || mapcolor.colorValue == j2) continue;
                mapcolor.colorValue = j2;
                flag = true;
            }
            if (flag) {
                Minecraft.getMinecraft().getTextureManager().reloadBannerTextures();
            }
        }
    }

    private static void dbg(String p_dbg_0_) {
        Config.dbg("CustomColors: " + p_dbg_0_);
    }

    private static void warn(String p_warn_0_) {
        Config.warn("CustomColors: " + p_warn_0_);
    }

    public static int getExpBarTextColor(int p_getExpBarTextColor_0_) {
        return expBarTextColor < 0 ? p_getExpBarTextColor_0_ : expBarTextColor;
    }

    public static int getBossTextColor(int p_getBossTextColor_0_) {
        return bossTextColor < 0 ? p_getBossTextColor_0_ : bossTextColor;
    }

    public static int getSignTextColor(int p_getSignTextColor_0_) {
        return signTextColor < 0 ? p_getSignTextColor_0_ : signTextColor;
    }

    public static interface IColorizer {
        public int getColor(IBlockState var1, IBlockAccess var2, BlockPos var3);

        public boolean isColorConstant();
    }
}

