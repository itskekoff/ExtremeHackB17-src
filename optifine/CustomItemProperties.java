package optifine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import optifine.Blender;
import optifine.Config;
import optifine.IParserInt;
import optifine.NbtTagValue;
import optifine.ParserEnchantmentId;
import optifine.RangeInt;
import optifine.RangeListInt;
import optifine.Reflector;
import optifine.StrUtils;
import optifine.TextureUtils;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties {
    public String name = null;
    public String basePath = null;
    public int type = 1;
    public int[] items = null;
    public String texture = null;
    public Map<String, String> mapTextures = null;
    public String model = null;
    public Map<String, String> mapModels = null;
    public RangeListInt damage = null;
    public boolean damagePercent = false;
    public int damageMask = 0;
    public RangeListInt stackSize = null;
    public RangeListInt enchantmentIds = null;
    public RangeListInt enchantmentLevels = null;
    public NbtTagValue[] nbtTagValues = null;
    public int hand = 0;
    public int blend = 1;
    public float speed = 0.0f;
    public float rotation = 0.0f;
    public int layer = 0;
    public float duration = 1.0f;
    public int weight = 0;
    public ResourceLocation textureLocation = null;
    public Map mapTextureLocations = null;
    public TextureAtlasSprite sprite = null;
    public Map mapSprites = null;
    public IBakedModel bakedModelTexture = null;
    public Map<String, IBakedModel> mapBakedModelsTexture = null;
    public IBakedModel bakedModelFull = null;
    public Map<String, IBakedModel> mapBakedModelsFull = null;
    private int textureWidth = 0;
    private int textureHeight = 0;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_ENCHANTMENT = 2;
    public static final int TYPE_ARMOR = 3;
    public static final int TYPE_ELYTRA = 4;
    public static final int HAND_ANY = 0;
    public static final int HAND_MAIN = 1;
    public static final int HAND_OFF = 2;
    public static final String INVENTORY = "inventory";

    public CustomItemProperties(Properties p_i33_1_, String p_i33_2_) {
        this.name = CustomItemProperties.parseName(p_i33_2_);
        this.basePath = CustomItemProperties.parseBasePath(p_i33_2_);
        this.type = this.parseType(p_i33_1_.getProperty("type"));
        this.items = this.parseItems(p_i33_1_.getProperty("items"), p_i33_1_.getProperty("matchItems"));
        this.mapModels = CustomItemProperties.parseModels(p_i33_1_, this.basePath);
        this.model = CustomItemProperties.parseModel(p_i33_1_.getProperty("model"), p_i33_2_, this.basePath, this.type, this.mapModels);
        this.mapTextures = CustomItemProperties.parseTextures(p_i33_1_, this.basePath);
        boolean flag = this.mapModels == null && this.model == null;
        this.texture = CustomItemProperties.parseTexture(p_i33_1_.getProperty("texture"), p_i33_1_.getProperty("tile"), p_i33_1_.getProperty("source"), p_i33_2_, this.basePath, this.type, this.mapTextures, flag);
        String s2 = p_i33_1_.getProperty("damage");
        if (s2 != null) {
            this.damagePercent = s2.contains("%");
            s2 = s2.replace("%", "");
            this.damage = this.parseRangeListInt(s2);
            this.damageMask = this.parseInt(p_i33_1_.getProperty("damageMask"), 0);
        }
        this.stackSize = this.parseRangeListInt(p_i33_1_.getProperty("stackSize"));
        this.enchantmentIds = this.parseRangeListInt(p_i33_1_.getProperty("enchantmentIDs"), new ParserEnchantmentId());
        this.enchantmentLevels = this.parseRangeListInt(p_i33_1_.getProperty("enchantmentLevels"));
        this.nbtTagValues = this.parseNbtTagValues(p_i33_1_);
        this.hand = this.parseHand(p_i33_1_.getProperty("hand"));
        this.blend = Blender.parseBlend(p_i33_1_.getProperty("blend"));
        this.speed = this.parseFloat(p_i33_1_.getProperty("speed"), 0.0f);
        this.rotation = this.parseFloat(p_i33_1_.getProperty("rotation"), 0.0f);
        this.layer = this.parseInt(p_i33_1_.getProperty("layer"), 0);
        this.weight = this.parseInt(p_i33_1_.getProperty("weight"), 0);
        this.duration = this.parseFloat(p_i33_1_.getProperty("duration"), 1.0f);
    }

    private static String parseName(String p_parseName_0_) {
        int j2;
        String s2 = p_parseName_0_;
        int i2 = p_parseName_0_.lastIndexOf(47);
        if (i2 >= 0) {
            s2 = p_parseName_0_.substring(i2 + 1);
        }
        if ((j2 = s2.lastIndexOf(46)) >= 0) {
            s2 = s2.substring(0, j2);
        }
        return s2;
    }

    private static String parseBasePath(String p_parseBasePath_0_) {
        int i2 = p_parseBasePath_0_.lastIndexOf(47);
        return i2 < 0 ? "" : p_parseBasePath_0_.substring(0, i2);
    }

    private int parseType(String p_parseType_1_) {
        if (p_parseType_1_ == null) {
            return 1;
        }
        if (p_parseType_1_.equals("item")) {
            return 1;
        }
        if (p_parseType_1_.equals("enchantment")) {
            return 2;
        }
        if (p_parseType_1_.equals("armor")) {
            return 3;
        }
        if (p_parseType_1_.equals("elytra")) {
            return 4;
        }
        Config.warn("Unknown method: " + p_parseType_1_);
        return 0;
    }

    private int[] parseItems(String p_parseItems_1_, String p_parseItems_2_) {
        if (p_parseItems_1_ == null) {
            p_parseItems_1_ = p_parseItems_2_;
        }
        if (p_parseItems_1_ == null) {
            return null;
        }
        p_parseItems_1_ = p_parseItems_1_.trim();
        TreeSet<Integer> set = new TreeSet<Integer>();
        String[] astring = Config.tokenize(p_parseItems_1_, " ");
        for (int i2 = 0; i2 < astring.length; ++i2) {
            Item item;
            String[] astring1;
            String s2 = astring[i2];
            int j2 = Config.parseInt(s2, -1);
            if (j2 >= 0) {
                set.add(new Integer(j2));
                continue;
            }
            if (s2.contains("-") && (astring1 = Config.tokenize(s2, "-")).length == 2) {
                int k2 = Config.parseInt(astring1[0], -1);
                int l2 = Config.parseInt(astring1[1], -1);
                if (k2 >= 0 && l2 >= 0) {
                    int i1 = Math.min(k2, l2);
                    int j1 = Math.max(k2, l2);
                    for (int k1 = i1; k1 <= j1; ++k1) {
                        set.add(new Integer(k1));
                    }
                    continue;
                }
            }
            if ((item = Item.getByNameOrId(s2)) == null) {
                Config.warn("Item not found: " + s2);
                continue;
            }
            int i22 = Item.getIdFromItem(item);
            if (i22 <= 0) {
                Config.warn("Item not found: " + s2);
                continue;
            }
            set.add(new Integer(i22));
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = new int[ainteger.length];
        for (int l1 = 0; l1 < aint.length; ++l1) {
            aint[l1] = ainteger[l1];
        }
        return aint;
    }

    private static String parseTexture(String p_parseTexture_0_, String p_parseTexture_1_, String p_parseTexture_2_, String p_parseTexture_3_, String p_parseTexture_4_, int p_parseTexture_5_, Map<String, String> p_parseTexture_6_, boolean p_parseTexture_7_) {
        int j2;
        String s2;
        if (p_parseTexture_0_ == null) {
            p_parseTexture_0_ = p_parseTexture_1_;
        }
        if (p_parseTexture_0_ == null) {
            p_parseTexture_0_ = p_parseTexture_2_;
        }
        if (p_parseTexture_0_ != null) {
            String s22 = ".png";
            if (p_parseTexture_0_.endsWith(s22)) {
                p_parseTexture_0_ = p_parseTexture_0_.substring(0, p_parseTexture_0_.length() - s22.length());
            }
            p_parseTexture_0_ = CustomItemProperties.fixTextureName(p_parseTexture_0_, p_parseTexture_4_);
            return p_parseTexture_0_;
        }
        if (p_parseTexture_5_ == 3) {
            return null;
        }
        if (p_parseTexture_6_ != null && (s2 = p_parseTexture_6_.get("texture.bow_standby")) != null) {
            return s2;
        }
        if (!p_parseTexture_7_) {
            return null;
        }
        String s1 = p_parseTexture_3_;
        int i2 = p_parseTexture_3_.lastIndexOf(47);
        if (i2 >= 0) {
            s1 = p_parseTexture_3_.substring(i2 + 1);
        }
        if ((j2 = s1.lastIndexOf(46)) >= 0) {
            s1 = s1.substring(0, j2);
        }
        s1 = CustomItemProperties.fixTextureName(s1, p_parseTexture_4_);
        return s1;
    }

    private static Map parseTextures(Properties p_parseTextures_0_, String p_parseTextures_1_) {
        String s2 = "texture.";
        Map map = CustomItemProperties.getMatchingProperties(p_parseTextures_0_, s2);
        if (map.size() <= 0) {
            return null;
        }
        Set set = map.keySet();
        LinkedHashMap map1 = new LinkedHashMap();
        for (Object s1 : set) {
            String s22 = (String)map.get(s1);
            s22 = CustomItemProperties.fixTextureName(s22, p_parseTextures_1_);
            map1.put(s1, s22);
        }
        return map1;
    }

    private static String fixTextureName(String p_fixTextureName_0_, String p_fixTextureName_1_) {
        if (!((p_fixTextureName_0_ = TextureUtils.fixResourcePath(p_fixTextureName_0_, p_fixTextureName_1_)).startsWith(p_fixTextureName_1_) || p_fixTextureName_0_.startsWith("textures/") || p_fixTextureName_0_.startsWith("mcpatcher/"))) {
            p_fixTextureName_0_ = String.valueOf(p_fixTextureName_1_) + "/" + p_fixTextureName_0_;
        }
        if (p_fixTextureName_0_.endsWith(".png")) {
            p_fixTextureName_0_ = p_fixTextureName_0_.substring(0, p_fixTextureName_0_.length() - 4);
        }
        if (p_fixTextureName_0_.startsWith("/")) {
            p_fixTextureName_0_ = p_fixTextureName_0_.substring(1);
        }
        return p_fixTextureName_0_;
    }

    private static String parseModel(String p_parseModel_0_, String p_parseModel_1_, String p_parseModel_2_, int p_parseModel_3_, Map<String, String> p_parseModel_4_) {
        String s2;
        if (p_parseModel_0_ != null) {
            String s1 = ".json";
            if (p_parseModel_0_.endsWith(s1)) {
                p_parseModel_0_ = p_parseModel_0_.substring(0, p_parseModel_0_.length() - s1.length());
            }
            p_parseModel_0_ = CustomItemProperties.fixModelName(p_parseModel_0_, p_parseModel_2_);
            return p_parseModel_0_;
        }
        if (p_parseModel_3_ == 3) {
            return null;
        }
        if (p_parseModel_4_ != null && (s2 = p_parseModel_4_.get("model.bow_standby")) != null) {
            return s2;
        }
        return p_parseModel_0_;
    }

    private static Map parseModels(Properties p_parseModels_0_, String p_parseModels_1_) {
        String s2 = "model.";
        Map map = CustomItemProperties.getMatchingProperties(p_parseModels_0_, s2);
        if (map.size() <= 0) {
            return null;
        }
        Set set = map.keySet();
        LinkedHashMap map1 = new LinkedHashMap();
        for (Object s1 : set) {
            String s22 = (String)map.get(s1);
            s22 = CustomItemProperties.fixModelName(s22, p_parseModels_1_);
            map1.put(s1, s22);
        }
        return map1;
    }

    private static String fixModelName(String p_fixModelName_0_, String p_fixModelName_1_) {
        String s2;
        boolean flag;
        boolean bl2 = flag = (p_fixModelName_0_ = TextureUtils.fixResourcePath(p_fixModelName_0_, p_fixModelName_1_)).startsWith("block/") || p_fixModelName_0_.startsWith("item/");
        if (!(p_fixModelName_0_.startsWith(p_fixModelName_1_) || flag || p_fixModelName_0_.startsWith("mcpatcher/"))) {
            p_fixModelName_0_ = String.valueOf(p_fixModelName_1_) + "/" + p_fixModelName_0_;
        }
        if (p_fixModelName_0_.endsWith(s2 = ".json")) {
            p_fixModelName_0_ = p_fixModelName_0_.substring(0, p_fixModelName_0_.length() - s2.length());
        }
        if (p_fixModelName_0_.startsWith("/")) {
            p_fixModelName_0_ = p_fixModelName_0_.substring(1);
        }
        return p_fixModelName_0_;
    }

    private int parseInt(String p_parseInt_1_, int p_parseInt_2_) {
        if (p_parseInt_1_ == null) {
            return p_parseInt_2_;
        }
        int i2 = Config.parseInt(p_parseInt_1_ = p_parseInt_1_.trim(), Integer.MIN_VALUE);
        if (i2 == Integer.MIN_VALUE) {
            Config.warn("Invalid integer: " + p_parseInt_1_);
            return p_parseInt_2_;
        }
        return i2;
    }

    private float parseFloat(String p_parseFloat_1_, float p_parseFloat_2_) {
        if (p_parseFloat_1_ == null) {
            return p_parseFloat_2_;
        }
        float f2 = Config.parseFloat(p_parseFloat_1_ = p_parseFloat_1_.trim(), Float.MIN_VALUE);
        if (f2 == Float.MIN_VALUE) {
            Config.warn("Invalid float: " + p_parseFloat_1_);
            return p_parseFloat_2_;
        }
        return f2;
    }

    private RangeListInt parseRangeListInt(String p_parseRangeListInt_1_) {
        return this.parseRangeListInt(p_parseRangeListInt_1_, null);
    }

    private RangeListInt parseRangeListInt(String p_parseRangeListInt_1_, IParserInt p_parseRangeListInt_2_) {
        if (p_parseRangeListInt_1_ == null) {
            return null;
        }
        String[] astring = Config.tokenize(p_parseRangeListInt_1_, " ");
        RangeListInt rangelistint = new RangeListInt();
        for (int i2 = 0; i2 < astring.length; ++i2) {
            int j2;
            String s2 = astring[i2];
            if (p_parseRangeListInt_2_ != null && (j2 = p_parseRangeListInt_2_.parse(s2, Integer.MIN_VALUE)) != Integer.MIN_VALUE) {
                rangelistint.addRange(new RangeInt(j2, j2));
                continue;
            }
            RangeInt rangeint = this.parseRangeInt(s2);
            if (rangeint == null) {
                Config.warn("Invalid range list: " + p_parseRangeListInt_1_);
                return null;
            }
            rangelistint.addRange(rangeint);
        }
        return rangelistint;
    }

    private RangeInt parseRangeInt(String p_parseRangeInt_1_) {
        if (p_parseRangeInt_1_ == null) {
            return null;
        }
        int i2 = (p_parseRangeInt_1_ = p_parseRangeInt_1_.trim()).length() - p_parseRangeInt_1_.replace("-", "").length();
        if (i2 > 1) {
            Config.warn("Invalid range: " + p_parseRangeInt_1_);
            return null;
        }
        String[] astring = Config.tokenize(p_parseRangeInt_1_, "- ");
        int[] aint = new int[astring.length];
        for (int j2 = 0; j2 < astring.length; ++j2) {
            String s2 = astring[j2];
            int k2 = Config.parseInt(s2, -1);
            if (k2 < 0) {
                Config.warn("Invalid range: " + p_parseRangeInt_1_);
                return null;
            }
            aint[j2] = k2;
        }
        if (aint.length == 1) {
            int i1 = aint[0];
            if (p_parseRangeInt_1_.startsWith("-")) {
                return new RangeInt(0, i1);
            }
            if (p_parseRangeInt_1_.endsWith("-")) {
                return new RangeInt(i1, 65535);
            }
            return new RangeInt(i1, i1);
        }
        if (aint.length == 2) {
            int l2 = Math.min(aint[0], aint[1]);
            int j1 = Math.max(aint[0], aint[1]);
            return new RangeInt(l2, j1);
        }
        Config.warn("Invalid range: " + p_parseRangeInt_1_);
        return null;
    }

    private NbtTagValue[] parseNbtTagValues(Properties p_parseNbtTagValues_1_) {
        String s2 = "nbt.";
        Map map = CustomItemProperties.getMatchingProperties(p_parseNbtTagValues_1_, s2);
        if (map.size() <= 0) {
            return null;
        }
        ArrayList<NbtTagValue> list = new ArrayList<NbtTagValue>();
        for (Object s1 : map.keySet()) {
            String s22 = (String)map.get(s1);
            String s3 = ((String)s1).substring(s2.length());
            NbtTagValue nbttagvalue = new NbtTagValue(s3, s22);
            list.add(nbttagvalue);
        }
        NbtTagValue[] anbttagvalue = list.toArray(new NbtTagValue[list.size()]);
        return anbttagvalue;
    }

    private static Map getMatchingProperties(Properties p_getMatchingProperties_0_, String p_getMatchingProperties_1_) {
        LinkedHashMap map = new LinkedHashMap();
        for (Object s2 : p_getMatchingProperties_0_.keySet()) {
            String s1 = p_getMatchingProperties_0_.getProperty((String)s2);
            if (!((String)s2).startsWith(p_getMatchingProperties_1_)) continue;
            map.put(s2, s1);
        }
        return map;
    }

    private int parseHand(String p_parseHand_1_) {
        if (p_parseHand_1_ == null) {
            return 0;
        }
        if ((p_parseHand_1_ = p_parseHand_1_.toLowerCase()).equals("any")) {
            return 0;
        }
        if (p_parseHand_1_.equals("main")) {
            return 1;
        }
        if (p_parseHand_1_.equals("off")) {
            return 2;
        }
        Config.warn("Invalid hand: " + p_parseHand_1_);
        return 0;
    }

    public boolean isValid(String p_isValid_1_) {
        if (this.name != null && this.name.length() > 0) {
            if (this.basePath == null) {
                Config.warn("No base path found: " + p_isValid_1_);
                return false;
            }
            if (this.type == 0) {
                Config.warn("No type defined: " + p_isValid_1_);
                return false;
            }
            if (this.type == 4 && this.items == null) {
                this.items = new int[]{Item.getIdFromItem(Items.ELYTRA)};
            }
            if (this.type == 1 || this.type == 3 || this.type == 4) {
                if (this.items == null) {
                    this.items = this.detectItems();
                }
                if (this.items == null) {
                    Config.warn("No items defined: " + p_isValid_1_);
                    return false;
                }
            }
            if (this.texture == null && this.mapTextures == null && this.model == null && this.mapModels == null) {
                Config.warn("No texture or model specified: " + p_isValid_1_);
                return false;
            }
            if (this.type == 2 && this.enchantmentIds == null) {
                Config.warn("No enchantmentIDs specified: " + p_isValid_1_);
                return false;
            }
            return true;
        }
        Config.warn("No name found: " + p_isValid_1_);
        return false;
    }

    private int[] detectItems() {
        int[] arrn;
        Item item = Item.getByNameOrId(this.name);
        if (item == null) {
            return null;
        }
        int i2 = Item.getIdFromItem(item);
        if (i2 <= 0) {
            arrn = null;
        } else {
            int[] arrn2 = new int[1];
            arrn = arrn2;
            arrn2[0] = i2;
        }
        return arrn;
    }

    public void updateIcons(TextureMap p_updateIcons_1_) {
        if (this.texture != null) {
            this.textureLocation = this.getTextureLocation(this.texture);
            if (this.type == 1) {
                ResourceLocation resourcelocation = this.getSpriteLocation(this.textureLocation);
                this.sprite = p_updateIcons_1_.registerSprite(resourcelocation);
            }
        }
        if (this.mapTextures != null) {
            this.mapTextureLocations = new HashMap();
            this.mapSprites = new HashMap();
            for (String s2 : this.mapTextures.keySet()) {
                String s1 = this.mapTextures.get(s2);
                ResourceLocation resourcelocation1 = this.getTextureLocation(s1);
                this.mapTextureLocations.put(s2, resourcelocation1);
                if (this.type != 1) continue;
                ResourceLocation resourcelocation2 = this.getSpriteLocation(resourcelocation1);
                TextureAtlasSprite textureatlassprite = p_updateIcons_1_.registerSprite(resourcelocation2);
                this.mapSprites.put(s2, textureatlassprite);
            }
        }
    }

    private ResourceLocation getTextureLocation(String p_getTextureLocation_1_) {
        String s2;
        ResourceLocation resourcelocation1;
        boolean flag;
        if (p_getTextureLocation_1_ == null) {
            return null;
        }
        ResourceLocation resourcelocation = new ResourceLocation(p_getTextureLocation_1_);
        String s3 = resourcelocation.getResourceDomain();
        String s1 = resourcelocation.getResourcePath();
        if (!s1.contains("/")) {
            s1 = "textures/items/" + s1;
        }
        if (!(flag = Config.hasResource(resourcelocation1 = new ResourceLocation(s3, s2 = String.valueOf(s1) + ".png")))) {
            Config.warn("File not found: " + s2);
        }
        return resourcelocation1;
    }

    private ResourceLocation getSpriteLocation(ResourceLocation p_getSpriteLocation_1_) {
        String s2 = p_getSpriteLocation_1_.getResourcePath();
        s2 = StrUtils.removePrefix(s2, "textures/");
        s2 = StrUtils.removeSuffix(s2, ".png");
        ResourceLocation resourcelocation = new ResourceLocation(p_getSpriteLocation_1_.getResourceDomain(), s2);
        return resourcelocation;
    }

    public void updateModelTexture(TextureMap p_updateModelTexture_1_, ItemModelGenerator p_updateModelTexture_2_) {
        if (this.texture != null || this.mapTextures != null) {
            String[] astring = this.getModelTextures();
            boolean flag = this.isUseTint();
            this.bakedModelTexture = CustomItemProperties.makeBakedModel(p_updateModelTexture_1_, p_updateModelTexture_2_, astring, flag);
            if (this.type == 1 && this.mapTextures != null) {
                for (String s2 : this.mapTextures.keySet()) {
                    String s1 = this.mapTextures.get(s2);
                    String s22 = StrUtils.removePrefix(s2, "texture.");
                    if (!s22.startsWith("bow") && !s22.startsWith("fishing_rod")) continue;
                    String[] astring1 = new String[]{s1};
                    IBakedModel ibakedmodel = CustomItemProperties.makeBakedModel(p_updateModelTexture_1_, p_updateModelTexture_2_, astring1, flag);
                    if (this.mapBakedModelsTexture == null) {
                        this.mapBakedModelsTexture = new HashMap<String, IBakedModel>();
                    }
                    String s3 = "item/" + s22;
                    this.mapBakedModelsTexture.put(s3, ibakedmodel);
                }
            }
        }
    }

    private boolean isUseTint() {
        return true;
    }

    private static IBakedModel makeBakedModel(TextureMap p_makeBakedModel_0_, ItemModelGenerator p_makeBakedModel_1_, String[] p_makeBakedModel_2_, boolean p_makeBakedModel_3_) {
        String[] astring = new String[p_makeBakedModel_2_.length];
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2 = p_makeBakedModel_2_[i2];
            astring[i2] = StrUtils.removePrefix(s2, "textures/");
        }
        ModelBlock modelblock = CustomItemProperties.makeModelBlock(astring);
        ModelBlock modelblock1 = p_makeBakedModel_1_.makeItemModel(p_makeBakedModel_0_, modelblock);
        IBakedModel ibakedmodel = CustomItemProperties.bakeModel(p_makeBakedModel_0_, modelblock1, p_makeBakedModel_3_);
        return ibakedmodel;
    }

    private String[] getModelTextures() {
        if (this.type == 1 && this.items.length == 1) {
            ItemArmor itemarmor;
            boolean flag;
            Item item = Item.getItemById(this.items[0]);
            boolean bl2 = flag = item == Items.POTIONITEM || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
            if (flag && this.damage != null && this.damage.getCountRanges() > 0) {
                RangeInt rangeint = this.damage.getRange(0);
                int i2 = rangeint.getMin();
                boolean flag1 = (i2 & 0x4000) != 0;
                String s5 = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
                String s6 = null;
                s6 = flag1 ? this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash") : this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
                return new String[]{s5, s6};
            }
            if (item instanceof ItemArmor && (itemarmor = (ItemArmor)item).getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                String s2 = "leather";
                String s1 = "helmet";
                if (itemarmor.armorType == EntityEquipmentSlot.HEAD) {
                    s1 = "helmet";
                }
                if (itemarmor.armorType == EntityEquipmentSlot.CHEST) {
                    s1 = "chestplate";
                }
                if (itemarmor.armorType == EntityEquipmentSlot.LEGS) {
                    s1 = "leggings";
                }
                if (itemarmor.armorType == EntityEquipmentSlot.FEET) {
                    s1 = "boots";
                }
                String s22 = String.valueOf(s2) + "_" + s1;
                String s3 = this.getMapTexture(this.mapTextures, "texture." + s22, "items/" + s22);
                String s4 = this.getMapTexture(this.mapTextures, "texture." + s22 + "_overlay", "items/" + s22 + "_overlay");
                return new String[]{s3, s4};
            }
        }
        return new String[]{this.texture};
    }

    private String getMapTexture(Map<String, String> p_getMapTexture_1_, String p_getMapTexture_2_, String p_getMapTexture_3_) {
        if (p_getMapTexture_1_ == null) {
            return p_getMapTexture_3_;
        }
        String s2 = p_getMapTexture_1_.get(p_getMapTexture_2_);
        return s2 == null ? p_getMapTexture_3_ : s2;
    }

    private static ModelBlock makeModelBlock(String[] p_makeModelBlock_0_) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("{\"parent\": \"builtin/generated\",\"textures\": {");
        for (int i2 = 0; i2 < p_makeModelBlock_0_.length; ++i2) {
            String s2 = p_makeModelBlock_0_[i2];
            if (i2 > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append("\"layer" + i2 + "\": \"" + s2 + "\"");
        }
        stringbuffer.append("}}");
        String s1 = stringbuffer.toString();
        ModelBlock modelblock = ModelBlock.deserialize(s1);
        return modelblock;
    }

    private static IBakedModel bakeModel(TextureMap p_bakeModel_0_, ModelBlock p_bakeModel_1_, boolean p_bakeModel_2_) {
        ModelRotation modelrotation = ModelRotation.X0_Y0;
        boolean flag = false;
        String s2 = p_bakeModel_1_.resolveTextureName("particle");
        TextureAtlasSprite textureatlassprite = p_bakeModel_0_.getAtlasSprite(new ResourceLocation(s2).toString());
        SimpleBakedModel.Builder simplebakedmodel$builder = new SimpleBakedModel.Builder(p_bakeModel_1_, p_bakeModel_1_.createOverrides()).setTexture(textureatlassprite);
        for (BlockPart blockpart : p_bakeModel_1_.getElements()) {
            for (EnumFacing enumfacing : blockpart.mapFaces.keySet()) {
                BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
                if (!p_bakeModel_2_) {
                    blockpartface = new BlockPartFace(blockpartface.cullFace, -1, blockpartface.texture, blockpartface.blockFaceUV);
                }
                String s1 = p_bakeModel_1_.resolveTextureName(blockpartface.texture);
                TextureAtlasSprite textureatlassprite1 = p_bakeModel_0_.getAtlasSprite(new ResourceLocation(s1).toString());
                BakedQuad bakedquad = CustomItemProperties.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, modelrotation, flag);
                if (blockpartface.cullFace == null) {
                    simplebakedmodel$builder.addGeneralQuad(bakedquad);
                    continue;
                }
                simplebakedmodel$builder.addFaceQuad(modelrotation.rotateFace(blockpartface.cullFace), bakedquad);
            }
        }
        return simplebakedmodel$builder.makeBakedModel();
    }

    private static BakedQuad makeBakedQuad(BlockPart p_makeBakedQuad_0_, BlockPartFace p_makeBakedQuad_1_, TextureAtlasSprite p_makeBakedQuad_2_, EnumFacing p_makeBakedQuad_3_, ModelRotation p_makeBakedQuad_4_, boolean p_makeBakedQuad_5_) {
        FaceBakery facebakery = new FaceBakery();
        return facebakery.makeBakedQuad(p_makeBakedQuad_0_.positionFrom, p_makeBakedQuad_0_.positionTo, p_makeBakedQuad_1_, p_makeBakedQuad_2_, p_makeBakedQuad_3_, p_makeBakedQuad_4_, p_makeBakedQuad_0_.partRotation, p_makeBakedQuad_5_, p_makeBakedQuad_0_.shade);
    }

    public String toString() {
        return this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
    }

    public float getTextureWidth(TextureManager p_getTextureWidth_1_) {
        if (this.textureWidth <= 0) {
            if (this.textureLocation != null) {
                ITextureObject itextureobject = p_getTextureWidth_1_.getTexture(this.textureLocation);
                int i2 = itextureobject.getGlTextureId();
                int j2 = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i2);
                this.textureWidth = GL11.glGetTexLevelParameteri(3553, 0, 4096);
                GlStateManager.bindTexture(j2);
            }
            if (this.textureWidth <= 0) {
                this.textureWidth = 16;
            }
        }
        return this.textureWidth;
    }

    public float getTextureHeight(TextureManager p_getTextureHeight_1_) {
        if (this.textureHeight <= 0) {
            if (this.textureLocation != null) {
                ITextureObject itextureobject = p_getTextureHeight_1_.getTexture(this.textureLocation);
                int i2 = itextureobject.getGlTextureId();
                int j2 = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i2);
                this.textureHeight = GL11.glGetTexLevelParameteri(3553, 0, 4097);
                GlStateManager.bindTexture(j2);
            }
            if (this.textureHeight <= 0) {
                this.textureHeight = 16;
            }
        }
        return this.textureHeight;
    }

    public IBakedModel getBakedModel(ResourceLocation p_getBakedModel_1_, boolean p_getBakedModel_2_) {
        String s2;
        IBakedModel ibakedmodel1;
        Map<String, IBakedModel> map;
        IBakedModel ibakedmodel;
        if (p_getBakedModel_2_) {
            ibakedmodel = this.bakedModelFull;
            map = this.mapBakedModelsFull;
        } else {
            ibakedmodel = this.bakedModelTexture;
            map = this.mapBakedModelsTexture;
        }
        if (p_getBakedModel_1_ != null && map != null && (ibakedmodel1 = map.get(s2 = p_getBakedModel_1_.getResourcePath())) != null) {
            return ibakedmodel1;
        }
        return ibakedmodel;
    }

    public void loadModels(ModelBakery p_loadModels_1_) {
        if (this.model != null) {
            CustomItemProperties.loadItemModel(p_loadModels_1_, this.model);
        }
        if (this.type == 1 && this.mapModels != null) {
            for (String s2 : this.mapModels.keySet()) {
                String s1 = this.mapModels.get(s2);
                String s22 = StrUtils.removePrefix(s2, "model.");
                if (!s22.startsWith("bow") && !s22.startsWith("fishing_rod")) continue;
                CustomItemProperties.loadItemModel(p_loadModels_1_, s1);
            }
        }
    }

    public void updateModelsFull() {
        ModelManager modelmanager = Config.getModelManager();
        IBakedModel ibakedmodel = modelmanager.getMissingModel();
        if (this.model != null) {
            ResourceLocation resourcelocation = CustomItemProperties.getModelLocation(this.model);
            ModelResourceLocation modelresourcelocation = new ModelResourceLocation(resourcelocation, INVENTORY);
            this.bakedModelFull = modelmanager.getModel(modelresourcelocation);
            if (this.bakedModelFull == ibakedmodel) {
                Config.warn("Custom Items: Model not found " + modelresourcelocation.getResourcePath());
                this.bakedModelFull = null;
            }
        }
        if (this.type == 1 && this.mapModels != null) {
            for (String s2 : this.mapModels.keySet()) {
                String s1 = this.mapModels.get(s2);
                String s22 = StrUtils.removePrefix(s2, "model.");
                if (!s22.startsWith("bow") && !s22.startsWith("fishing_rod")) continue;
                ResourceLocation resourcelocation1 = CustomItemProperties.getModelLocation(s1);
                ModelResourceLocation modelresourcelocation1 = new ModelResourceLocation(resourcelocation1, INVENTORY);
                IBakedModel ibakedmodel1 = modelmanager.getModel(modelresourcelocation1);
                if (ibakedmodel1 == ibakedmodel) {
                    Config.warn("Custom Items: Model not found " + modelresourcelocation1.getResourcePath());
                    continue;
                }
                if (this.mapBakedModelsFull == null) {
                    this.mapBakedModelsFull = new HashMap<String, IBakedModel>();
                }
                String s3 = "item/" + s22;
                this.mapBakedModelsFull.put(s3, ibakedmodel1);
            }
        }
    }

    private static void loadItemModel(ModelBakery p_loadItemModel_0_, String p_loadItemModel_1_) {
        ResourceLocation resourcelocation = CustomItemProperties.getModelLocation(p_loadItemModel_1_);
        ModelResourceLocation modelresourcelocation = new ModelResourceLocation(resourcelocation, INVENTORY);
        if (Reflector.ModelLoader.exists()) {
            try {
                Object object = Reflector.ModelLoader_VanillaLoader_INSTANCE.getValue();
                CustomItemProperties.checkNull(object, "vanillaLoader is null");
                Object object1 = Reflector.call(object, Reflector.ModelLoader_VanillaLoader_loadModel, modelresourcelocation);
                CustomItemProperties.checkNull(object1, "iModel is null");
                Map map = (Map)Reflector.getFieldValue(p_loadItemModel_0_, Reflector.ModelLoader_stateModels);
                CustomItemProperties.checkNull(map, "stateModels is null");
                map.put(modelresourcelocation, object1);
                Set set = (Set)Reflector.ModelLoaderRegistry_textures.getValue();
                CustomItemProperties.checkNull(set, "registryTextures is null");
                Collection collection = (Collection)Reflector.call(object1, Reflector.IModel_getTextures, new Object[0]);
                CustomItemProperties.checkNull(collection, "modelTextures is null");
                set.addAll(collection);
            }
            catch (Exception exception) {
                Config.warn("Error registering model: " + modelresourcelocation + ", " + exception.getClass().getName() + ": " + exception.getMessage());
            }
        } else {
            p_loadItemModel_0_.loadItemModel(resourcelocation.toString(), modelresourcelocation, resourcelocation);
        }
    }

    private static void checkNull(Object p_checkNull_0_, String p_checkNull_1_) throws NullPointerException {
        if (p_checkNull_0_ == null) {
            throw new NullPointerException(p_checkNull_1_);
        }
    }

    private static ResourceLocation getModelLocation(String p_getModelLocation_0_) {
        return Reflector.ModelLoader.exists() && !p_getModelLocation_0_.startsWith("mcpatcher/") && !p_getModelLocation_0_.startsWith("optifine/") ? new ResourceLocation("models/" + p_getModelLocation_0_) : new ResourceLocation(p_getModelLocation_0_);
    }
}

