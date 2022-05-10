package optifine;

import com.google.common.collect.Lists;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import optifine.Config;
import optifine.ConnectedProperties;
import optifine.MatchBlock;
import optifine.RangeInt;
import optifine.RangeListInt;

public class ConnectedParser {
    private String context = null;

    public ConnectedParser(String p_i26_1_) {
        this.context = p_i26_1_;
    }

    public String parseName(String p_parseName_1_) {
        int j2;
        String s2 = p_parseName_1_;
        int i2 = p_parseName_1_.lastIndexOf(47);
        if (i2 >= 0) {
            s2 = p_parseName_1_.substring(i2 + 1);
        }
        if ((j2 = s2.lastIndexOf(46)) >= 0) {
            s2 = s2.substring(0, j2);
        }
        return s2;
    }

    public String parseBasePath(String p_parseBasePath_1_) {
        int i2 = p_parseBasePath_1_.lastIndexOf(47);
        return i2 < 0 ? "" : p_parseBasePath_1_.substring(0, i2);
    }

    public MatchBlock[] parseMatchBlocks(String p_parseMatchBlocks_1_) {
        if (p_parseMatchBlocks_1_ == null) {
            return null;
        }
        ArrayList<MatchBlock> list = new ArrayList<MatchBlock>();
        String[] astring = Config.tokenize(p_parseMatchBlocks_1_, " ");
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2 = astring[i2];
            MatchBlock[] amatchblock = this.parseMatchBlock(s2);
            if (amatchblock == null) continue;
            list.addAll(Arrays.asList(amatchblock));
        }
        MatchBlock[] amatchblock1 = list.toArray(new MatchBlock[list.size()]);
        return amatchblock1;
    }

    public IBlockState parseBlockState(String p_parseBlockState_1_, IBlockState p_parseBlockState_2_) {
        MatchBlock[] amatchblock = this.parseMatchBlock(p_parseBlockState_1_);
        if (amatchblock == null) {
            return p_parseBlockState_2_;
        }
        if (amatchblock.length != 1) {
            return p_parseBlockState_2_;
        }
        MatchBlock matchblock = amatchblock[0];
        int i2 = matchblock.getBlockId();
        Block block = Block.getBlockById(i2);
        return block.getDefaultState();
    }

    public MatchBlock[] parseMatchBlock(String p_parseMatchBlock_1_) {
        if (p_parseMatchBlock_1_ == null) {
            return null;
        }
        if ((p_parseMatchBlock_1_ = p_parseMatchBlock_1_.trim()).length() <= 0) {
            return null;
        }
        String[] astring = Config.tokenize(p_parseMatchBlock_1_, ":");
        String s2 = "minecraft";
        int i2 = 0;
        if (astring.length > 1 && this.isFullBlockName(astring)) {
            s2 = astring[0];
            i2 = 1;
        } else {
            s2 = "minecraft";
            i2 = 0;
        }
        String s1 = astring[i2];
        String[] astring1 = Arrays.copyOfRange(astring, i2 + 1, astring.length);
        Block[] ablock = this.parseBlockPart(s2, s1);
        if (ablock == null) {
            return null;
        }
        MatchBlock[] amatchblock = new MatchBlock[ablock.length];
        for (int j2 = 0; j2 < ablock.length; ++j2) {
            MatchBlock matchblock;
            Block block = ablock[j2];
            int k2 = Block.getIdFromBlock(block);
            int[] aint = null;
            if (astring1.length > 0 && (aint = this.parseBlockMetadatas(block, astring1)) == null) {
                return null;
            }
            amatchblock[j2] = matchblock = new MatchBlock(k2, aint);
        }
        return amatchblock;
    }

    public boolean isFullBlockName(String[] p_isFullBlockName_1_) {
        if (p_isFullBlockName_1_.length < 2) {
            return false;
        }
        String s2 = p_isFullBlockName_1_[1];
        if (s2.length() < 1) {
            return false;
        }
        if (this.startsWithDigit(s2)) {
            return false;
        }
        return !s2.contains("=");
    }

    public boolean startsWithDigit(String p_startsWithDigit_1_) {
        if (p_startsWithDigit_1_ == null) {
            return false;
        }
        if (p_startsWithDigit_1_.length() < 1) {
            return false;
        }
        char c0 = p_startsWithDigit_1_.charAt(0);
        return Character.isDigit(c0);
    }

    public Block[] parseBlockPart(String p_parseBlockPart_1_, String p_parseBlockPart_2_) {
        if (this.startsWithDigit(p_parseBlockPart_2_)) {
            int[] aint = this.parseIntList(p_parseBlockPart_2_);
            if (aint == null) {
                return null;
            }
            Block[] ablock1 = new Block[aint.length];
            for (int j2 = 0; j2 < aint.length; ++j2) {
                int i2 = aint[j2];
                Block block1 = Block.getBlockById(i2);
                if (block1 == null) {
                    this.warn("Block not found for id: " + i2);
                    return null;
                }
                ablock1[j2] = block1;
            }
            return ablock1;
        }
        String s2 = String.valueOf(p_parseBlockPart_1_) + ":" + p_parseBlockPart_2_;
        Block block = Block.getBlockFromName(s2);
        if (block == null) {
            this.warn("Block not found for name: " + s2);
            return null;
        }
        Block[] ablock = new Block[]{block};
        return ablock;
    }

    public int[] parseBlockMetadatas(Block p_parseBlockMetadatas_1_, String[] p_parseBlockMetadatas_2_) {
        if (p_parseBlockMetadatas_2_.length <= 0) {
            return null;
        }
        String s2 = p_parseBlockMetadatas_2_[0];
        if (this.startsWithDigit(s2)) {
            int[] aint = this.parseIntList(s2);
            return aint;
        }
        IBlockState iblockstate = p_parseBlockMetadatas_1_.getDefaultState();
        Collection<IProperty<?>> collection = iblockstate.getPropertyNames();
        HashMap<IProperty, List<Comparable>> map = new HashMap<IProperty, List<Comparable>>();
        for (int i2 = 0; i2 < p_parseBlockMetadatas_2_.length; ++i2) {
            String s1 = p_parseBlockMetadatas_2_[i2];
            if (s1.length() <= 0) continue;
            String[] astring = Config.tokenize(s1, "=");
            if (astring.length != 2) {
                this.warn("Invalid block property: " + s1);
                return null;
            }
            String s22 = astring[0];
            String s3 = astring[1];
            IProperty iproperty = ConnectedProperties.getProperty(s22, collection);
            if (iproperty == null) {
                this.warn("Property not found: " + s22 + ", block: " + p_parseBlockMetadatas_1_);
                return null;
            }
            ArrayList<Comparable> list = (ArrayList<Comparable>)map.get(s22);
            if (list == null) {
                list = new ArrayList<Comparable>();
                map.put(iproperty, list);
            }
            String[] astring1 = Config.tokenize(s3, ",");
            for (int j2 = 0; j2 < astring1.length; ++j2) {
                String s4 = astring1[j2];
                Comparable comparable = ConnectedParser.parsePropertyValue(iproperty, s4);
                if (comparable == null) {
                    this.warn("Property value not found: " + s4 + ", property: " + s22 + ", block: " + p_parseBlockMetadatas_1_);
                    return null;
                }
                list.add(comparable);
            }
        }
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Integer> list1 = new ArrayList<Integer>();
        for (int k2 = 0; k2 < 16; ++k2) {
            int l2 = k2;
            try {
                IBlockState iblockstate1 = this.getStateFromMeta(p_parseBlockMetadatas_1_, l2);
                if (!this.matchState(iblockstate1, map)) continue;
                list1.add(l2);
                continue;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        if (list1.size() == 16) {
            return null;
        }
        int[] aint1 = new int[list1.size()];
        for (int i1 = 0; i1 < aint1.length; ++i1) {
            aint1[i1] = (Integer)list1.get(i1);
        }
        return aint1;
    }

    private IBlockState getStateFromMeta(Block p_getStateFromMeta_1_, int p_getStateFromMeta_2_) {
        try {
            IBlockState iblockstate = p_getStateFromMeta_1_.getStateFromMeta(p_getStateFromMeta_2_);
            if (p_getStateFromMeta_1_ == Blocks.DOUBLE_PLANT && p_getStateFromMeta_2_ > 7) {
                IBlockState iblockstate1 = p_getStateFromMeta_1_.getStateFromMeta(p_getStateFromMeta_2_ & 7);
                iblockstate = iblockstate.withProperty(BlockDoublePlant.VARIANT, iblockstate1.getValue(BlockDoublePlant.VARIANT));
            }
            if (p_getStateFromMeta_1_ == Blocks.field_190976_dk && (p_getStateFromMeta_2_ & 8) != 0) {
                iblockstate = iblockstate.withProperty(BlockObserver.field_190963_a, true);
            }
            return iblockstate;
        }
        catch (IllegalArgumentException var5) {
            return p_getStateFromMeta_1_.getDefaultState();
        }
    }

    public static Comparable parsePropertyValue(IProperty p_parsePropertyValue_0_, String p_parsePropertyValue_1_) {
        Class oclass = p_parsePropertyValue_0_.getValueClass();
        Comparable comparable = ConnectedParser.parseValue(p_parsePropertyValue_1_, oclass);
        if (comparable == null) {
            Collection collection = p_parsePropertyValue_0_.getAllowedValues();
            comparable = ConnectedParser.getPropertyValue(p_parsePropertyValue_1_, collection);
        }
        return comparable;
    }

    public static Comparable getPropertyValue(String p_getPropertyValue_0_, Collection p_getPropertyValue_1_) {
        for (Object comparable : p_getPropertyValue_1_) {
            if (!ConnectedParser.getValueName((Comparable)comparable).equals(p_getPropertyValue_0_)) continue;
            return (Comparable)comparable;
        }
        return null;
    }

    private static Object getValueName(Comparable p_getValueName_0_) {
        if (p_getValueName_0_ instanceof IStringSerializable) {
            IStringSerializable istringserializable = (IStringSerializable)((Object)p_getValueName_0_);
            return istringserializable.getName();
        }
        return p_getValueName_0_.toString();
    }

    public static Comparable parseValue(String p_parseValue_0_, Class p_parseValue_1_) {
        if (p_parseValue_1_ == String.class) {
            return p_parseValue_0_;
        }
        if (p_parseValue_1_ == Boolean.class) {
            return Boolean.valueOf(p_parseValue_0_);
        }
        if (p_parseValue_1_ == Float.class) {
            return Float.valueOf(p_parseValue_0_);
        }
        if (p_parseValue_1_ == Double.class) {
            return Double.valueOf(p_parseValue_0_);
        }
        if (p_parseValue_1_ == Integer.class) {
            return Integer.valueOf(p_parseValue_0_);
        }
        return p_parseValue_1_ == Long.class ? Long.valueOf(p_parseValue_0_) : null;
    }

    public boolean matchState(IBlockState p_matchState_1_, Map<IProperty, List<Comparable>> p_matchState_2_) {
        for (IProperty iproperty : p_matchState_2_.keySet()) {
            List<Comparable> list = p_matchState_2_.get(iproperty);
            Object comparable = p_matchState_1_.getValue(iproperty);
            if (comparable == null) {
                return false;
            }
            if (list.contains(comparable)) continue;
            return false;
        }
        return true;
    }

    public Biome[] parseBiomes(String p_parseBiomes_1_) {
        if (p_parseBiomes_1_ == null) {
            return null;
        }
        p_parseBiomes_1_ = p_parseBiomes_1_.trim();
        boolean flag = false;
        if (p_parseBiomes_1_.startsWith("!")) {
            flag = true;
            p_parseBiomes_1_ = p_parseBiomes_1_.substring(1);
        }
        String[] astring = Config.tokenize(p_parseBiomes_1_, " ");
        ArrayList<Biome> list = new ArrayList<Biome>();
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2 = astring[i2];
            Biome biome = this.findBiome(s2);
            if (biome == null) {
                this.warn("Biome not found: " + s2);
                continue;
            }
            list.add(biome);
        }
        if (flag) {
            ArrayList<Biome> list1 = Lists.newArrayList(Biome.REGISTRY.iterator());
            list1.removeAll(list);
            list = list1;
        }
        Biome[] abiome = list.toArray(new Biome[list.size()]);
        return abiome;
    }

    public Biome findBiome(String p_findBiome_1_) {
        if ((p_findBiome_1_ = p_findBiome_1_.toLowerCase()).equals("nether")) {
            return Biomes.HELL;
        }
        for (ResourceLocation resourcelocation : Biome.REGISTRY.getKeys()) {
            String s2;
            Biome biome = Biome.REGISTRY.getObject(resourcelocation);
            if (biome == null || !(s2 = biome.getBiomeName().replace(" ", "").toLowerCase()).equals(p_findBiome_1_)) continue;
            return biome;
        }
        return null;
    }

    public int parseInt(String p_parseInt_1_) {
        if (p_parseInt_1_ == null) {
            return -1;
        }
        int i2 = Config.parseInt(p_parseInt_1_ = p_parseInt_1_.trim(), -1);
        if (i2 < 0) {
            this.warn("Invalid number: " + p_parseInt_1_);
        }
        return i2;
    }

    public int parseInt(String p_parseInt_1_, int p_parseInt_2_) {
        if (p_parseInt_1_ == null) {
            return p_parseInt_2_;
        }
        int i2 = Config.parseInt(p_parseInt_1_ = p_parseInt_1_.trim(), -1);
        if (i2 < 0) {
            this.warn("Invalid number: " + p_parseInt_1_);
            return p_parseInt_2_;
        }
        return i2;
    }

    public int[] parseIntList(String p_parseIntList_1_) {
        if (p_parseIntList_1_ == null) {
            return null;
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        String[] astring = Config.tokenize(p_parseIntList_1_, " ,");
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2 = astring[i2];
            if (s2.contains("-")) {
                String[] astring1 = Config.tokenize(s2, "-");
                if (astring1.length != 2) {
                    this.warn("Invalid interval: " + s2 + ", when parsing: " + p_parseIntList_1_);
                    continue;
                }
                int k2 = Config.parseInt(astring1[0], -1);
                int l2 = Config.parseInt(astring1[1], -1);
                if (k2 >= 0 && l2 >= 0 && k2 <= l2) {
                    for (int i1 = k2; i1 <= l2; ++i1) {
                        list.add(i1);
                    }
                    continue;
                }
                this.warn("Invalid interval: " + s2 + ", when parsing: " + p_parseIntList_1_);
                continue;
            }
            int j2 = Config.parseInt(s2, -1);
            if (j2 < 0) {
                this.warn("Invalid number: " + s2 + ", when parsing: " + p_parseIntList_1_);
                continue;
            }
            list.add(j2);
        }
        int[] aint = new int[list.size()];
        for (int j1 = 0; j1 < aint.length; ++j1) {
            aint[j1] = (Integer)list.get(j1);
        }
        return aint;
    }

    public boolean[] parseFaces(String p_parseFaces_1_, boolean[] p_parseFaces_2_) {
        if (p_parseFaces_1_ == null) {
            return p_parseFaces_2_;
        }
        EnumSet<EnumFacing> enumset = EnumSet.allOf(EnumFacing.class);
        String[] astring = Config.tokenize(p_parseFaces_1_, " ,");
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2 = astring[i2];
            if (s2.equals("sides")) {
                enumset.add(EnumFacing.NORTH);
                enumset.add(EnumFacing.SOUTH);
                enumset.add(EnumFacing.WEST);
                enumset.add(EnumFacing.EAST);
                continue;
            }
            if (s2.equals("all")) {
                enumset.addAll(Arrays.asList(EnumFacing.VALUES));
                continue;
            }
            EnumFacing enumfacing = this.parseFace(s2);
            if (enumfacing == null) continue;
            enumset.add(enumfacing);
        }
        boolean[] aboolean = new boolean[EnumFacing.VALUES.length];
        for (int j2 = 0; j2 < aboolean.length; ++j2) {
            aboolean[j2] = enumset.contains(EnumFacing.VALUES[j2]);
        }
        return aboolean;
    }

    public EnumFacing parseFace(String p_parseFace_1_) {
        if (!(p_parseFace_1_ = p_parseFace_1_.toLowerCase()).equals("bottom") && !p_parseFace_1_.equals("down")) {
            if (!p_parseFace_1_.equals("top") && !p_parseFace_1_.equals("up")) {
                if (p_parseFace_1_.equals("north")) {
                    return EnumFacing.NORTH;
                }
                if (p_parseFace_1_.equals("south")) {
                    return EnumFacing.SOUTH;
                }
                if (p_parseFace_1_.equals("east")) {
                    return EnumFacing.EAST;
                }
                if (p_parseFace_1_.equals("west")) {
                    return EnumFacing.WEST;
                }
                Config.warn("Unknown face: " + p_parseFace_1_);
                return null;
            }
            return EnumFacing.UP;
        }
        return EnumFacing.DOWN;
    }

    public void dbg(String p_dbg_1_) {
        Config.dbg(this.context + ": " + p_dbg_1_);
    }

    public void warn(String p_warn_1_) {
        Config.warn(this.context + ": " + p_warn_1_);
    }

    public RangeListInt parseRangeListInt(String p_parseRangeListInt_1_) {
        if (p_parseRangeListInt_1_ == null) {
            return null;
        }
        RangeListInt rangelistint = new RangeListInt();
        String[] astring = Config.tokenize(p_parseRangeListInt_1_, " ,");
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2 = astring[i2];
            RangeInt rangeint = this.parseRangeInt(s2);
            if (rangeint == null) {
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
        if (p_parseRangeInt_1_.indexOf(45) >= 0) {
            String[] astring = Config.tokenize(p_parseRangeInt_1_, "-");
            if (astring.length != 2) {
                this.warn("Invalid range: " + p_parseRangeInt_1_);
                return null;
            }
            int j2 = Config.parseInt(astring[0], -1);
            int k2 = Config.parseInt(astring[1], -1);
            if (j2 >= 0 && k2 >= 0) {
                return new RangeInt(j2, k2);
            }
            this.warn("Invalid range: " + p_parseRangeInt_1_);
            return null;
        }
        int i2 = Config.parseInt(p_parseRangeInt_1_, -1);
        if (i2 < 0) {
            this.warn("Invalid integer: " + p_parseRangeInt_1_);
            return null;
        }
        return new RangeInt(i2, i2);
    }

    public static boolean parseBoolean(String p_parseBoolean_0_) {
        return p_parseBoolean_0_ == null ? false : p_parseBoolean_0_.trim().toLowerCase().equals("true");
    }

    public Boolean parseBooleanObject(String p_parseBooleanObject_1_) {
        if (p_parseBooleanObject_1_ == null) {
            return null;
        }
        String s2 = p_parseBooleanObject_1_.toLowerCase().trim();
        if (s2.equals("true")) {
            return Boolean.TRUE;
        }
        if (s2.equals("false")) {
            return Boolean.FALSE;
        }
        this.warn("Invalid boolean: " + p_parseBooleanObject_1_);
        return null;
    }

    public static int parseColor(String p_parseColor_0_, int p_parseColor_1_) {
        if (p_parseColor_0_ == null) {
            return p_parseColor_1_;
        }
        p_parseColor_0_ = p_parseColor_0_.trim();
        try {
            int i2 = Integer.parseInt(p_parseColor_0_, 16) & 0xFFFFFF;
            return i2;
        }
        catch (NumberFormatException var3) {
            return p_parseColor_1_;
        }
    }

    public static int parseColor4(String p_parseColor4_0_, int p_parseColor4_1_) {
        if (p_parseColor4_0_ == null) {
            return p_parseColor4_1_;
        }
        p_parseColor4_0_ = p_parseColor4_0_.trim();
        try {
            int i2 = (int)(Long.parseLong(p_parseColor4_0_, 16) & 0xFFFFFFFFFFFFFFFFL);
            return i2;
        }
        catch (NumberFormatException var3) {
            return p_parseColor4_1_;
        }
    }

    public BlockRenderLayer parseBlockRenderLayer(String p_parseBlockRenderLayer_1_, BlockRenderLayer p_parseBlockRenderLayer_2_) {
        if (p_parseBlockRenderLayer_1_ == null) {
            return p_parseBlockRenderLayer_2_;
        }
        p_parseBlockRenderLayer_1_ = p_parseBlockRenderLayer_1_.toLowerCase().trim();
        BlockRenderLayer[] ablockrenderlayer = BlockRenderLayer.values();
        for (int i2 = 0; i2 < ablockrenderlayer.length; ++i2) {
            BlockRenderLayer blockrenderlayer = ablockrenderlayer[i2];
            if (!p_parseBlockRenderLayer_1_.equals(blockrenderlayer.name().toLowerCase())) continue;
            return blockrenderlayer;
        }
        return p_parseBlockRenderLayer_2_;
    }

    public Enum parseEnum(String p_parseEnum_1_, Enum[] p_parseEnum_2_, String p_parseEnum_3_) {
        if (p_parseEnum_1_ == null) {
            return null;
        }
        String s2 = p_parseEnum_1_.toLowerCase().trim();
        for (int i2 = 0; i2 < p_parseEnum_2_.length; ++i2) {
            Enum oenum = p_parseEnum_2_[i2];
            if (!oenum.name().toLowerCase().equals(s2)) continue;
            return oenum;
        }
        this.warn("Invalid " + p_parseEnum_3_ + ": " + p_parseEnum_1_);
        return null;
    }

    public Enum[] parseEnums(String p_parseEnums_1_, Enum[] p_parseEnums_2_, String p_parseEnums_3_, Enum[] p_parseEnums_4_) {
        if (p_parseEnums_1_ == null) {
            return null;
        }
        p_parseEnums_1_ = p_parseEnums_1_.toLowerCase().trim();
        String[] astring = Config.tokenize(p_parseEnums_1_, " ");
        Enum[] aenum = (Enum[])Array.newInstance(p_parseEnums_2_.getClass().getComponentType(), astring.length);
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2 = astring[i2];
            Enum oenum = this.parseEnum(s2, p_parseEnums_2_, p_parseEnums_3_);
            if (oenum == null) {
                return p_parseEnums_4_;
            }
            aenum[i2] = oenum;
        }
        return aenum;
    }
}

