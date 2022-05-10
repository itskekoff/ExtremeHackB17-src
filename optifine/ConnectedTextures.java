package optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import optifine.BetterGrass;
import optifine.BlockDir;
import optifine.BlockModelUtils;
import optifine.Config;
import optifine.ConnectedProperties;
import optifine.ConnectedTexturesCompact;
import optifine.ListQuadsOverlay;
import optifine.Matches;
import optifine.RenderEnv;
import optifine.ResUtils;

public class ConnectedTextures {
    private static Map[] spriteQuadMaps = null;
    private static Map[] spriteQuadFullMaps = null;
    private static Map[][] spriteQuadCompactMaps = null;
    private static ConnectedProperties[][] blockProperties = null;
    private static ConnectedProperties[][] tileProperties = null;
    private static boolean multipass = false;
    protected static final int UNKNOWN = -1;
    protected static final int Y_NEG_DOWN = 0;
    protected static final int Y_POS_UP = 1;
    protected static final int Z_NEG_NORTH = 2;
    protected static final int Z_POS_SOUTH = 3;
    protected static final int X_NEG_WEST = 4;
    protected static final int X_POS_EAST = 5;
    private static final int Y_AXIS = 0;
    private static final int Z_AXIS = 1;
    private static final int X_AXIS = 2;
    public static final IBlockState AIR_DEFAULT_STATE = Blocks.AIR.getDefaultState();
    private static TextureAtlasSprite emptySprite = null;
    private static final BlockDir[] SIDES_Y_NEG_DOWN = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.NORTH, BlockDir.SOUTH};
    private static final BlockDir[] SIDES_Y_POS_UP = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.SOUTH, BlockDir.NORTH};
    private static final BlockDir[] SIDES_Z_NEG_NORTH = new BlockDir[]{BlockDir.EAST, BlockDir.WEST, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_Z_POS_SOUTH = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_X_NEG_WEST = new BlockDir[]{BlockDir.NORTH, BlockDir.SOUTH, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_X_POS_EAST = new BlockDir[]{BlockDir.SOUTH, BlockDir.NORTH, BlockDir.DOWN, BlockDir.UP};
    private static final BlockDir[] SIDES_Z_NEG_NORTH_Z_AXIS = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.UP, BlockDir.DOWN};
    private static final BlockDir[] SIDES_X_POS_EAST_X_AXIS = new BlockDir[]{BlockDir.NORTH, BlockDir.SOUTH, BlockDir.UP, BlockDir.DOWN};
    private static final BlockDir[] EDGES_Y_NEG_DOWN = new BlockDir[]{BlockDir.NORTH_EAST, BlockDir.NORTH_WEST, BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST};
    private static final BlockDir[] EDGES_Y_POS_UP = new BlockDir[]{BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST, BlockDir.NORTH_EAST, BlockDir.NORTH_WEST};
    private static final BlockDir[] EDGES_Z_NEG_NORTH = new BlockDir[]{BlockDir.DOWN_WEST, BlockDir.DOWN_EAST, BlockDir.UP_WEST, BlockDir.UP_EAST};
    private static final BlockDir[] EDGES_Z_POS_SOUTH = new BlockDir[]{BlockDir.DOWN_EAST, BlockDir.DOWN_WEST, BlockDir.UP_EAST, BlockDir.UP_WEST};
    private static final BlockDir[] EDGES_X_NEG_WEST = new BlockDir[]{BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH, BlockDir.UP_SOUTH, BlockDir.UP_NORTH};
    private static final BlockDir[] EDGES_X_POS_EAST = new BlockDir[]{BlockDir.DOWN_NORTH, BlockDir.DOWN_SOUTH, BlockDir.UP_NORTH, BlockDir.UP_SOUTH};
    private static final BlockDir[] EDGES_Z_NEG_NORTH_Z_AXIS = new BlockDir[]{BlockDir.UP_EAST, BlockDir.UP_WEST, BlockDir.DOWN_EAST, BlockDir.DOWN_WEST};
    private static final BlockDir[] EDGES_X_POS_EAST_X_AXIS = new BlockDir[]{BlockDir.UP_SOUTH, BlockDir.UP_NORTH, BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH};

    public static synchronized BakedQuad[] getConnectedTexture(IBlockAccess p_getConnectedTexture_0_, IBlockState p_getConnectedTexture_1_, BlockPos p_getConnectedTexture_2_, BakedQuad p_getConnectedTexture_3_, RenderEnv p_getConnectedTexture_4_) {
        TextureAtlasSprite textureatlassprite = p_getConnectedTexture_3_.getSprite();
        if (textureatlassprite == null) {
            return p_getConnectedTexture_4_.getArrayQuadsCtm(p_getConnectedTexture_3_);
        }
        Block block = p_getConnectedTexture_1_.getBlock();
        if (ConnectedTextures.skipConnectedTexture(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, p_getConnectedTexture_4_)) {
            p_getConnectedTexture_3_ = ConnectedTextures.getQuad(emptySprite, p_getConnectedTexture_3_);
            return p_getConnectedTexture_4_.getArrayQuadsCtm(p_getConnectedTexture_3_);
        }
        EnumFacing enumfacing = p_getConnectedTexture_3_.getFace();
        BakedQuad[] abakedquad = ConnectedTextures.getConnectedTextureMultiPass(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, enumfacing, p_getConnectedTexture_3_, p_getConnectedTexture_4_);
        return abakedquad;
    }

    private static boolean skipConnectedTexture(IBlockAccess p_skipConnectedTexture_0_, IBlockState p_skipConnectedTexture_1_, BlockPos p_skipConnectedTexture_2_, BakedQuad p_skipConnectedTexture_3_, RenderEnv p_skipConnectedTexture_4_) {
        Block block = p_skipConnectedTexture_1_.getBlock();
        if (block instanceof BlockPane) {
            EnumFacing enumfacing = p_skipConnectedTexture_3_.getFace();
            if (enumfacing != EnumFacing.UP && enumfacing != EnumFacing.DOWN) {
                return false;
            }
            if (!p_skipConnectedTexture_3_.isFaceQuad()) {
                return false;
            }
            BlockPos blockpos = p_skipConnectedTexture_2_.offset(p_skipConnectedTexture_3_.getFace());
            IBlockState iblockstate = p_skipConnectedTexture_0_.getBlockState(blockpos);
            if (iblockstate.getBlock() != block) {
                return false;
            }
            if (block == Blocks.STAINED_GLASS_PANE && iblockstate.getValue(BlockStainedGlassPane.COLOR) != p_skipConnectedTexture_1_.getValue(BlockStainedGlassPane.COLOR)) {
                return false;
            }
            iblockstate = iblockstate.getActualState(p_skipConnectedTexture_0_, blockpos);
            double d0 = p_skipConnectedTexture_3_.getMidX();
            if (d0 < 0.4) {
                if (iblockstate.getValue(BlockPane.WEST).booleanValue()) {
                    return true;
                }
            } else if (d0 > 0.6) {
                if (iblockstate.getValue(BlockPane.EAST).booleanValue()) {
                    return true;
                }
            } else {
                double d1 = p_skipConnectedTexture_3_.getMidZ();
                if (d1 < 0.4) {
                    if (iblockstate.getValue(BlockPane.NORTH).booleanValue()) {
                        return true;
                    }
                } else {
                    if (d1 <= 0.6) {
                        return true;
                    }
                    if (iblockstate.getValue(BlockPane.SOUTH).booleanValue()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected static BakedQuad[] getQuads(TextureAtlasSprite p_getQuads_0_, BakedQuad p_getQuads_1_, RenderEnv p_getQuads_2_) {
        if (p_getQuads_0_ == null) {
            return null;
        }
        BakedQuad bakedquad = ConnectedTextures.getQuad(p_getQuads_0_, p_getQuads_1_);
        BakedQuad[] abakedquad = p_getQuads_2_.getArrayQuadsCtm(bakedquad);
        return abakedquad;
    }

    private static BakedQuad getQuad(TextureAtlasSprite p_getQuad_0_, BakedQuad p_getQuad_1_) {
        if (spriteQuadMaps == null) {
            return p_getQuad_1_;
        }
        int i2 = p_getQuad_0_.getIndexInMap();
        if (i2 >= 0 && i2 < spriteQuadMaps.length) {
            BakedQuad bakedquad;
            IdentityHashMap<BakedQuad, BakedQuad> map = spriteQuadMaps[i2];
            if (map == null) {
                ConnectedTextures.spriteQuadMaps[i2] = map = new IdentityHashMap<BakedQuad, BakedQuad>(1);
            }
            if ((bakedquad = (BakedQuad)map.get(p_getQuad_1_)) == null) {
                bakedquad = ConnectedTextures.makeSpriteQuad(p_getQuad_1_, p_getQuad_0_);
                map.put(p_getQuad_1_, bakedquad);
            }
            return bakedquad;
        }
        return p_getQuad_1_;
    }

    private static BakedQuad getQuadFull(TextureAtlasSprite p_getQuadFull_0_, BakedQuad p_getQuadFull_1_, int p_getQuadFull_2_) {
        if (spriteQuadFullMaps == null) {
            return p_getQuadFull_1_;
        }
        int i2 = p_getQuadFull_0_.getIndexInMap();
        if (i2 >= 0 && i2 < spriteQuadFullMaps.length) {
            EnumFacing enumfacing;
            BakedQuad bakedquad;
            EnumMap<EnumFacing, BakedQuad> map = spriteQuadFullMaps[i2];
            if (map == null) {
                ConnectedTextures.spriteQuadFullMaps[i2] = map = new EnumMap<EnumFacing, BakedQuad>(EnumFacing.class);
            }
            if ((bakedquad = (BakedQuad)map.get(enumfacing = p_getQuadFull_1_.getFace())) == null) {
                bakedquad = BlockModelUtils.makeBakedQuad(enumfacing, p_getQuadFull_0_, p_getQuadFull_2_);
                map.put(enumfacing, bakedquad);
            }
            return bakedquad;
        }
        return p_getQuadFull_1_;
    }

    private static BakedQuad makeSpriteQuad(BakedQuad p_makeSpriteQuad_0_, TextureAtlasSprite p_makeSpriteQuad_1_) {
        int[] aint = (int[])p_makeSpriteQuad_0_.getVertexData().clone();
        TextureAtlasSprite textureatlassprite = p_makeSpriteQuad_0_.getSprite();
        for (int i2 = 0; i2 < 4; ++i2) {
            ConnectedTextures.fixVertex(aint, i2, textureatlassprite, p_makeSpriteQuad_1_);
        }
        BakedQuad bakedquad = new BakedQuad(aint, p_makeSpriteQuad_0_.getTintIndex(), p_makeSpriteQuad_0_.getFace(), p_makeSpriteQuad_1_);
        return bakedquad;
    }

    private static void fixVertex(int[] p_fixVertex_0_, int p_fixVertex_1_, TextureAtlasSprite p_fixVertex_2_, TextureAtlasSprite p_fixVertex_3_) {
        int i2 = p_fixVertex_0_.length / 4;
        int j2 = i2 * p_fixVertex_1_;
        float f2 = Float.intBitsToFloat(p_fixVertex_0_[j2 + 4]);
        float f1 = Float.intBitsToFloat(p_fixVertex_0_[j2 + 4 + 1]);
        double d0 = p_fixVertex_2_.getSpriteU16(f2);
        double d1 = p_fixVertex_2_.getSpriteV16(f1);
        p_fixVertex_0_[j2 + 4] = Float.floatToRawIntBits(p_fixVertex_3_.getInterpolatedU(d0));
        p_fixVertex_0_[j2 + 4 + 1] = Float.floatToRawIntBits(p_fixVertex_3_.getInterpolatedV(d1));
    }

    private static BakedQuad[] getConnectedTextureMultiPass(IBlockAccess p_getConnectedTextureMultiPass_0_, IBlockState p_getConnectedTextureMultiPass_1_, BlockPos p_getConnectedTextureMultiPass_2_, EnumFacing p_getConnectedTextureMultiPass_3_, BakedQuad p_getConnectedTextureMultiPass_4_, RenderEnv p_getConnectedTextureMultiPass_5_) {
        BakedQuad[] abakedquad = ConnectedTextures.getConnectedTextureSingle(p_getConnectedTextureMultiPass_0_, p_getConnectedTextureMultiPass_1_, p_getConnectedTextureMultiPass_2_, p_getConnectedTextureMultiPass_3_, p_getConnectedTextureMultiPass_4_, true, 0, p_getConnectedTextureMultiPass_5_);
        if (!multipass) {
            return abakedquad;
        }
        if (abakedquad.length == 1 && abakedquad[0] == p_getConnectedTextureMultiPass_4_) {
            return abakedquad;
        }
        List<BakedQuad> list = p_getConnectedTextureMultiPass_5_.getListQuadsCtmMultipass(abakedquad);
        for (int i2 = 0; i2 < list.size(); ++i2) {
            BakedQuad bakedquad;
            BakedQuad bakedquad1 = bakedquad = list.get(i2);
            for (int j2 = 0; j2 < 3; ++j2) {
                BakedQuad[] abakedquad1 = ConnectedTextures.getConnectedTextureSingle(p_getConnectedTextureMultiPass_0_, p_getConnectedTextureMultiPass_1_, p_getConnectedTextureMultiPass_2_, p_getConnectedTextureMultiPass_3_, bakedquad1, false, j2 + 1, p_getConnectedTextureMultiPass_5_);
                if (abakedquad1.length != 1 || abakedquad1[0] == bakedquad1) break;
                bakedquad1 = abakedquad1[0];
            }
            list.set(i2, bakedquad1);
        }
        for (int k2 = 0; k2 < abakedquad.length; ++k2) {
            abakedquad[k2] = list.get(k2);
        }
        return abakedquad;
    }

    public static BakedQuad[] getConnectedTextureSingle(IBlockAccess p_getConnectedTextureSingle_0_, IBlockState p_getConnectedTextureSingle_1_, BlockPos p_getConnectedTextureSingle_2_, EnumFacing p_getConnectedTextureSingle_3_, BakedQuad p_getConnectedTextureSingle_4_, boolean p_getConnectedTextureSingle_5_, int p_getConnectedTextureSingle_6_, RenderEnv p_getConnectedTextureSingle_7_) {
        ConnectedProperties[] aconnectedproperties1;
        int l2;
        ConnectedProperties[] aconnectedproperties;
        int i2;
        Block block = p_getConnectedTextureSingle_1_.getBlock();
        if (!(p_getConnectedTextureSingle_1_ instanceof BlockStateBase)) {
            return p_getConnectedTextureSingle_7_.getArrayQuadsCtm(p_getConnectedTextureSingle_4_);
        }
        BlockStateBase blockstatebase = (BlockStateBase)p_getConnectedTextureSingle_1_;
        TextureAtlasSprite textureatlassprite = p_getConnectedTextureSingle_4_.getSprite();
        if (tileProperties != null && (i2 = textureatlassprite.getIndexInMap()) >= 0 && i2 < tileProperties.length && (aconnectedproperties = tileProperties[i2]) != null) {
            int j2 = ConnectedTextures.getSide(p_getConnectedTextureSingle_3_);
            for (int k2 = 0; k2 < aconnectedproperties.length; ++k2) {
                BakedQuad[] abakedquad;
                ConnectedProperties connectedproperties = aconnectedproperties[k2];
                if (connectedproperties == null || !connectedproperties.matchesBlockId(blockstatebase.getBlockId()) || (abakedquad = ConnectedTextures.getConnectedTexture(connectedproperties, p_getConnectedTextureSingle_0_, blockstatebase, p_getConnectedTextureSingle_2_, j2, p_getConnectedTextureSingle_4_, p_getConnectedTextureSingle_6_, p_getConnectedTextureSingle_7_)) == null) continue;
                return abakedquad;
            }
        }
        if (blockProperties != null && p_getConnectedTextureSingle_5_ && (l2 = p_getConnectedTextureSingle_7_.getBlockId()) >= 0 && l2 < blockProperties.length && (aconnectedproperties1 = blockProperties[l2]) != null) {
            int i1 = ConnectedTextures.getSide(p_getConnectedTextureSingle_3_);
            for (int j1 = 0; j1 < aconnectedproperties1.length; ++j1) {
                BakedQuad[] abakedquad1;
                ConnectedProperties connectedproperties1 = aconnectedproperties1[j1];
                if (connectedproperties1 == null || !connectedproperties1.matchesIcon(textureatlassprite) || (abakedquad1 = ConnectedTextures.getConnectedTexture(connectedproperties1, p_getConnectedTextureSingle_0_, blockstatebase, p_getConnectedTextureSingle_2_, i1, p_getConnectedTextureSingle_4_, p_getConnectedTextureSingle_6_, p_getConnectedTextureSingle_7_)) == null) continue;
                return abakedquad1;
            }
        }
        return p_getConnectedTextureSingle_7_.getArrayQuadsCtm(p_getConnectedTextureSingle_4_);
    }

    public static int getSide(EnumFacing p_getSide_0_) {
        if (p_getSide_0_ == null) {
            return -1;
        }
        switch (p_getSide_0_) {
            case DOWN: {
                return 0;
            }
            case UP: {
                return 1;
            }
            case EAST: {
                return 5;
            }
            case WEST: {
                return 4;
            }
            case NORTH: {
                return 2;
            }
            case SOUTH: {
                return 3;
            }
        }
        return -1;
    }

    private static EnumFacing getFacing(int p_getFacing_0_) {
        switch (p_getFacing_0_) {
            case 0: {
                return EnumFacing.DOWN;
            }
            case 1: {
                return EnumFacing.UP;
            }
            case 2: {
                return EnumFacing.NORTH;
            }
            case 3: {
                return EnumFacing.SOUTH;
            }
            case 4: {
                return EnumFacing.WEST;
            }
            case 5: {
                return EnumFacing.EAST;
            }
        }
        return EnumFacing.UP;
    }

    private static BakedQuad[] getConnectedTexture(ConnectedProperties p_getConnectedTexture_0_, IBlockAccess p_getConnectedTexture_1_, BlockStateBase p_getConnectedTexture_2_, BlockPos p_getConnectedTexture_3_, int p_getConnectedTexture_4_, BakedQuad p_getConnectedTexture_5_, int p_getConnectedTexture_6_, RenderEnv p_getConnectedTexture_7_) {
        int i1;
        int j2;
        int i2 = 0;
        int k2 = j2 = p_getConnectedTexture_2_.getMetadata();
        Block block = p_getConnectedTexture_2_.getBlock();
        if (block instanceof BlockRotatedPillar) {
            i2 = ConnectedTextures.getWoodAxis(p_getConnectedTexture_4_, j2);
            if (p_getConnectedTexture_0_.getMetadataMax() <= 3) {
                k2 = j2 & 3;
            }
        }
        if (block instanceof BlockQuartz) {
            i2 = ConnectedTextures.getQuartzAxis(p_getConnectedTexture_4_, j2);
            if (p_getConnectedTexture_0_.getMetadataMax() <= 2 && k2 > 2) {
                k2 = 2;
            }
        }
        if (!p_getConnectedTexture_0_.matchesBlock(p_getConnectedTexture_2_.getBlockId(), k2)) {
            return null;
        }
        if (p_getConnectedTexture_4_ >= 0 && p_getConnectedTexture_0_.faces != 63) {
            int l2 = p_getConnectedTexture_4_;
            if (i2 != 0) {
                l2 = ConnectedTextures.fixSideByAxis(p_getConnectedTexture_4_, i2);
            }
            if ((1 << l2 & p_getConnectedTexture_0_.faces) == 0) {
                return null;
            }
        }
        if ((i1 = p_getConnectedTexture_3_.getY()) >= p_getConnectedTexture_0_.minHeight && i1 <= p_getConnectedTexture_0_.maxHeight) {
            Biome biome;
            if (p_getConnectedTexture_0_.biomes != null && !p_getConnectedTexture_0_.matchesBiome(biome = p_getConnectedTexture_1_.getBiome(p_getConnectedTexture_3_))) {
                return null;
            }
            TextureAtlasSprite textureatlassprite = p_getConnectedTexture_5_.getSprite();
            switch (p_getConnectedTexture_0_.method) {
                case 1: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureCtm(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, i2, p_getConnectedTexture_4_, textureatlassprite, j2, p_getConnectedTexture_7_), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 2: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureHorizontal(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, i2, p_getConnectedTexture_4_, textureatlassprite, j2), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 3: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureTop(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, i2, p_getConnectedTexture_4_, textureatlassprite, j2), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 4: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureRandom(p_getConnectedTexture_0_, p_getConnectedTexture_3_, p_getConnectedTexture_4_), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 5: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureRepeat(p_getConnectedTexture_0_, p_getConnectedTexture_3_, p_getConnectedTexture_4_), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 6: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureVertical(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, i2, p_getConnectedTexture_4_, textureatlassprite, j2), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 7: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureFixed(p_getConnectedTexture_0_), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 8: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureHorizontalVertical(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, i2, p_getConnectedTexture_4_, textureatlassprite, j2), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 9: {
                    return ConnectedTextures.getQuads(ConnectedTextures.getConnectedTextureVerticalHorizontal(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, i2, p_getConnectedTexture_4_, textureatlassprite, j2), p_getConnectedTexture_5_, p_getConnectedTexture_7_);
                }
                case 10: {
                    if (p_getConnectedTexture_6_ == 0) {
                        return ConnectedTextures.getConnectedTextureCtmCompact(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, i2, p_getConnectedTexture_4_, p_getConnectedTexture_5_, j2, p_getConnectedTexture_7_);
                    }
                }
                default: {
                    return null;
                }
                case 11: 
            }
            return ConnectedTextures.getConnectedTextureOverlay(p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, i2, p_getConnectedTexture_4_, p_getConnectedTexture_5_, j2, p_getConnectedTexture_7_);
        }
        return null;
    }

    private static int fixSideByAxis(int p_fixSideByAxis_0_, int p_fixSideByAxis_1_) {
        switch (p_fixSideByAxis_1_) {
            case 0: {
                return p_fixSideByAxis_0_;
            }
            case 1: {
                switch (p_fixSideByAxis_0_) {
                    case 0: {
                        return 2;
                    }
                    case 1: {
                        return 3;
                    }
                    case 2: {
                        return 1;
                    }
                    case 3: {
                        return 0;
                    }
                }
                return p_fixSideByAxis_0_;
            }
            case 2: {
                switch (p_fixSideByAxis_0_) {
                    case 0: {
                        return 4;
                    }
                    case 1: {
                        return 5;
                    }
                    default: {
                        return p_fixSideByAxis_0_;
                    }
                    case 4: {
                        return 1;
                    }
                    case 5: 
                }
                return 0;
            }
        }
        return p_fixSideByAxis_0_;
    }

    private static int getWoodAxis(int p_getWoodAxis_0_, int p_getWoodAxis_1_) {
        int i2 = (p_getWoodAxis_1_ & 0xC) >> 2;
        switch (i2) {
            case 1: {
                return 2;
            }
            case 2: {
                return 1;
            }
        }
        return 0;
    }

    private static int getQuartzAxis(int p_getQuartzAxis_0_, int p_getQuartzAxis_1_) {
        switch (p_getQuartzAxis_1_) {
            case 3: {
                return 2;
            }
            case 4: {
                return 1;
            }
        }
        return 0;
    }

    private static TextureAtlasSprite getConnectedTextureRandom(ConnectedProperties p_getConnectedTextureRandom_0_, BlockPos p_getConnectedTextureRandom_1_, int p_getConnectedTextureRandom_2_) {
        if (p_getConnectedTextureRandom_0_.tileIcons.length == 1) {
            return p_getConnectedTextureRandom_0_.tileIcons[0];
        }
        int i2 = p_getConnectedTextureRandom_2_ / p_getConnectedTextureRandom_0_.symmetry * p_getConnectedTextureRandom_0_.symmetry;
        int j2 = Config.getRandom(p_getConnectedTextureRandom_1_, i2) & Integer.MAX_VALUE;
        int k2 = 0;
        if (p_getConnectedTextureRandom_0_.weights == null) {
            k2 = j2 % p_getConnectedTextureRandom_0_.tileIcons.length;
        } else {
            int l2 = j2 % p_getConnectedTextureRandom_0_.sumAllWeights;
            int[] aint = p_getConnectedTextureRandom_0_.sumWeights;
            for (int i1 = 0; i1 < aint.length; ++i1) {
                if (l2 >= aint[i1]) continue;
                k2 = i1;
                break;
            }
        }
        return p_getConnectedTextureRandom_0_.tileIcons[k2];
    }

    private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties p_getConnectedTextureFixed_0_) {
        return p_getConnectedTextureFixed_0_.tileIcons[0];
    }

    private static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties p_getConnectedTextureRepeat_0_, BlockPos p_getConnectedTextureRepeat_1_, int p_getConnectedTextureRepeat_2_) {
        if (p_getConnectedTextureRepeat_0_.tileIcons.length == 1) {
            return p_getConnectedTextureRepeat_0_.tileIcons[0];
        }
        int i2 = p_getConnectedTextureRepeat_1_.getX();
        int j2 = p_getConnectedTextureRepeat_1_.getY();
        int k2 = p_getConnectedTextureRepeat_1_.getZ();
        int l2 = 0;
        int i1 = 0;
        switch (p_getConnectedTextureRepeat_2_) {
            case 0: {
                l2 = i2;
                i1 = k2;
                break;
            }
            case 1: {
                l2 = i2;
                i1 = k2;
                break;
            }
            case 2: {
                l2 = -i2 - 1;
                i1 = -j2;
                break;
            }
            case 3: {
                l2 = i2;
                i1 = -j2;
                break;
            }
            case 4: {
                l2 = k2;
                i1 = -j2;
                break;
            }
            case 5: {
                l2 = -k2 - 1;
                i1 = -j2;
            }
        }
        i1 %= p_getConnectedTextureRepeat_0_.height;
        if ((l2 %= p_getConnectedTextureRepeat_0_.width) < 0) {
            l2 += p_getConnectedTextureRepeat_0_.width;
        }
        if (i1 < 0) {
            i1 += p_getConnectedTextureRepeat_0_.height;
        }
        int j1 = i1 * p_getConnectedTextureRepeat_0_.width + l2;
        return p_getConnectedTextureRepeat_0_.tileIcons[j1];
    }

    private static TextureAtlasSprite getConnectedTextureCtm(ConnectedProperties p_getConnectedTextureCtm_0_, IBlockAccess p_getConnectedTextureCtm_1_, IBlockState p_getConnectedTextureCtm_2_, BlockPos p_getConnectedTextureCtm_3_, int p_getConnectedTextureCtm_4_, int p_getConnectedTextureCtm_5_, TextureAtlasSprite p_getConnectedTextureCtm_6_, int p_getConnectedTextureCtm_7_, RenderEnv p_getConnectedTextureCtm_8_) {
        int i2 = ConnectedTextures.getConnectedTextureCtmIndex(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_, p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_, p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_, p_getConnectedTextureCtm_7_, p_getConnectedTextureCtm_8_);
        return p_getConnectedTextureCtm_0_.tileIcons[i2];
    }

    private static BakedQuad[] getConnectedTextureCtmCompact(ConnectedProperties p_getConnectedTextureCtmCompact_0_, IBlockAccess p_getConnectedTextureCtmCompact_1_, IBlockState p_getConnectedTextureCtmCompact_2_, BlockPos p_getConnectedTextureCtmCompact_3_, int p_getConnectedTextureCtmCompact_4_, int p_getConnectedTextureCtmCompact_5_, BakedQuad p_getConnectedTextureCtmCompact_6_, int p_getConnectedTextureCtmCompact_7_, RenderEnv p_getConnectedTextureCtmCompact_8_) {
        TextureAtlasSprite textureatlassprite = p_getConnectedTextureCtmCompact_6_.getSprite();
        int i2 = ConnectedTextures.getConnectedTextureCtmIndex(p_getConnectedTextureCtmCompact_0_, p_getConnectedTextureCtmCompact_1_, p_getConnectedTextureCtmCompact_2_, p_getConnectedTextureCtmCompact_3_, p_getConnectedTextureCtmCompact_4_, p_getConnectedTextureCtmCompact_5_, textureatlassprite, p_getConnectedTextureCtmCompact_7_, p_getConnectedTextureCtmCompact_8_);
        return ConnectedTexturesCompact.getConnectedTextureCtmCompact(i2, p_getConnectedTextureCtmCompact_0_, p_getConnectedTextureCtmCompact_5_, p_getConnectedTextureCtmCompact_6_, p_getConnectedTextureCtmCompact_8_);
    }

    private static BakedQuad[] getConnectedTextureOverlay(ConnectedProperties p_getConnectedTextureOverlay_0_, IBlockAccess p_getConnectedTextureOverlay_1_, IBlockState p_getConnectedTextureOverlay_2_, BlockPos p_getConnectedTextureOverlay_3_, int p_getConnectedTextureOverlay_4_, int p_getConnectedTextureOverlay_5_, BakedQuad p_getConnectedTextureOverlay_6_, int p_getConnectedTextureOverlay_7_, RenderEnv p_getConnectedTextureOverlay_8_) {
        Object dirEdges;
        if (!p_getConnectedTextureOverlay_6_.isFullQuad()) {
            return null;
        }
        TextureAtlasSprite textureatlassprite = p_getConnectedTextureOverlay_6_.getSprite();
        BlockDir[] ablockdir = ConnectedTextures.getSideDirections(p_getConnectedTextureOverlay_5_, p_getConnectedTextureOverlay_4_);
        boolean[] aboolean = p_getConnectedTextureOverlay_8_.getBorderFlags();
        for (int i2 = 0; i2 < 4; ++i2) {
            aboolean[i2] = ConnectedTextures.isNeighbourOverlay(p_getConnectedTextureOverlay_0_, p_getConnectedTextureOverlay_1_, p_getConnectedTextureOverlay_2_, ablockdir[i2].offset(p_getConnectedTextureOverlay_3_), p_getConnectedTextureOverlay_5_, textureatlassprite, p_getConnectedTextureOverlay_7_);
        }
        ListQuadsOverlay listquadsoverlay = p_getConnectedTextureOverlay_8_.getListQuadsOverlay(p_getConnectedTextureOverlay_0_.layer);
        try {
            if (!(aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3])) {
                if (aboolean[0] && aboolean[1] && aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[5], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    Object dirEdges2 = null;
                    BakedQuad[] arrbakedQuad = dirEdges2;
                    return arrbakedQuad;
                }
                if (aboolean[0] && aboolean[2] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[6], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    Object dirEdges3 = null;
                    BakedQuad[] arrbakedQuad = dirEdges3;
                    return arrbakedQuad;
                }
                if (aboolean[1] && aboolean[2] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[12], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    Object dirEdges4 = null;
                    BakedQuad[] arrbakedQuad = dirEdges4;
                    return arrbakedQuad;
                }
                if (aboolean[0] && aboolean[1] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[13], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    Object dirEdges5 = null;
                    BakedQuad[] arrbakedQuad = dirEdges5;
                    return arrbakedQuad;
                }
                BlockDir[] ablockdir1 = ConnectedTextures.getEdgeDirections(p_getConnectedTextureOverlay_5_, p_getConnectedTextureOverlay_4_);
                boolean[] aboolean1 = p_getConnectedTextureOverlay_8_.getBorderFlags2();
                for (int j2 = 0; j2 < 4; ++j2) {
                    aboolean1[j2] = ConnectedTextures.isNeighbourOverlay(p_getConnectedTextureOverlay_0_, p_getConnectedTextureOverlay_1_, p_getConnectedTextureOverlay_2_, ablockdir1[j2].offset(p_getConnectedTextureOverlay_3_), p_getConnectedTextureOverlay_5_, textureatlassprite, p_getConnectedTextureOverlay_7_);
                }
                if (aboolean[1] && aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[3], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    if (aboolean1[3]) {
                        listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[16], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    }
                    Object object4 = null;
                    BakedQuad[] arrbakedQuad = object4;
                    return arrbakedQuad;
                }
                if (aboolean[0] && aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[4], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    if (aboolean1[2]) {
                        listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[14], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    }
                    Object object3 = null;
                    BakedQuad[] arrbakedQuad = object3;
                    return arrbakedQuad;
                }
                if (aboolean[1] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[10], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    if (aboolean1[1]) {
                        listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[2], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    }
                    Object object2 = null;
                    BakedQuad[] arrbakedQuad = object2;
                    return arrbakedQuad;
                }
                if (aboolean[0] && aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[11], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    if (aboolean1[0]) {
                        listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[0], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                    }
                    Object object1 = null;
                    BakedQuad[] arrbakedQuad = object1;
                    return arrbakedQuad;
                }
                boolean[] aboolean2 = p_getConnectedTextureOverlay_8_.getBorderFlags3();
                for (int k2 = 0; k2 < 4; ++k2) {
                    aboolean2[k2] = ConnectedTextures.isNeighbourMatching(p_getConnectedTextureOverlay_0_, p_getConnectedTextureOverlay_1_, p_getConnectedTextureOverlay_2_, ablockdir[k2].offset(p_getConnectedTextureOverlay_3_), p_getConnectedTextureOverlay_5_, textureatlassprite, p_getConnectedTextureOverlay_7_);
                }
                if (aboolean[0]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[9], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                }
                if (aboolean[1]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[7], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                }
                if (aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[1], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                }
                if (aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[15], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                }
                if (aboolean1[0] && (aboolean2[1] || aboolean2[2]) && !aboolean[1] && !aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[0], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                }
                if (aboolean1[1] && (aboolean2[0] || aboolean2[2]) && !aboolean[0] && !aboolean[2]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[2], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                }
                if (aboolean1[2] && (aboolean2[1] || aboolean2[3]) && !aboolean[1] && !aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[14], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                }
                if (aboolean1[3] && (aboolean2[0] || aboolean2[3]) && !aboolean[0] && !aboolean[3]) {
                    listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[16], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
                }
                Object object5 = null;
                BakedQuad[] arrbakedQuad = object5;
                return arrbakedQuad;
            }
            listquadsoverlay.addQuad(ConnectedTextures.getQuadFull(p_getConnectedTextureOverlay_0_.tileIcons[8], p_getConnectedTextureOverlay_6_, p_getConnectedTextureOverlay_0_.tintIndex), p_getConnectedTextureOverlay_0_.tintBlockState);
            dirEdges = null;
        }
        finally {
            if (listquadsoverlay.size() > 0) {
                p_getConnectedTextureOverlay_8_.setOverlaysRendered(true);
            }
        }
        return dirEdges;
    }

    private static BlockDir[] getSideDirections(int p_getSideDirections_0_, int p_getSideDirections_1_) {
        switch (p_getSideDirections_0_) {
            case 0: {
                return SIDES_Y_NEG_DOWN;
            }
            case 1: {
                return SIDES_Y_POS_UP;
            }
            case 2: {
                if (p_getSideDirections_1_ == 1) {
                    return SIDES_Z_NEG_NORTH_Z_AXIS;
                }
                return SIDES_Z_NEG_NORTH;
            }
            case 3: {
                return SIDES_Z_POS_SOUTH;
            }
            case 4: {
                return SIDES_X_NEG_WEST;
            }
            case 5: {
                if (p_getSideDirections_1_ == 2) {
                    return SIDES_X_POS_EAST_X_AXIS;
                }
                return SIDES_X_POS_EAST;
            }
        }
        throw new IllegalArgumentException("Unknown side: " + p_getSideDirections_0_);
    }

    private static BlockDir[] getEdgeDirections(int p_getEdgeDirections_0_, int p_getEdgeDirections_1_) {
        switch (p_getEdgeDirections_0_) {
            case 0: {
                return EDGES_Y_NEG_DOWN;
            }
            case 1: {
                return EDGES_Y_POS_UP;
            }
            case 2: {
                if (p_getEdgeDirections_1_ == 1) {
                    return EDGES_Z_NEG_NORTH_Z_AXIS;
                }
                return EDGES_Z_NEG_NORTH;
            }
            case 3: {
                return EDGES_Z_POS_SOUTH;
            }
            case 4: {
                return EDGES_X_NEG_WEST;
            }
            case 5: {
                if (p_getEdgeDirections_1_ == 2) {
                    return EDGES_X_POS_EAST_X_AXIS;
                }
                return EDGES_X_POS_EAST;
            }
        }
        throw new IllegalArgumentException("Unknown side: " + p_getEdgeDirections_0_);
    }

    protected static Map[][] getSpriteQuadCompactMaps() {
        return spriteQuadCompactMaps;
    }

    private static int getConnectedTextureCtmIndex(ConnectedProperties p_getConnectedTextureCtmIndex_0_, IBlockAccess p_getConnectedTextureCtmIndex_1_, IBlockState p_getConnectedTextureCtmIndex_2_, BlockPos p_getConnectedTextureCtmIndex_3_, int p_getConnectedTextureCtmIndex_4_, int p_getConnectedTextureCtmIndex_5_, TextureAtlasSprite p_getConnectedTextureCtmIndex_6_, int p_getConnectedTextureCtmIndex_7_, RenderEnv p_getConnectedTextureCtmIndex_8_) {
        boolean[] aboolean = p_getConnectedTextureCtmIndex_8_.getBorderFlags();
        switch (p_getConnectedTextureCtmIndex_5_) {
            case 0: {
                aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                break;
            }
            case 1: {
                aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                break;
            }
            case 2: {
                aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.down(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.up(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                if (p_getConnectedTextureCtmIndex_4_ != 1) break;
                ConnectedTextures.switchValues(0, 1, aboolean);
                ConnectedTextures.switchValues(2, 3, aboolean);
                break;
            }
            case 3: {
                aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.down(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.up(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                break;
            }
            case 4: {
                aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.down(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.up(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                break;
            }
            case 5: {
                aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.down(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.up(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                if (p_getConnectedTextureCtmIndex_4_ != 2) break;
                ConnectedTextures.switchValues(0, 1, aboolean);
                ConnectedTextures.switchValues(2, 3, aboolean);
            }
        }
        int i2 = 0;
        if (aboolean[0] & !aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i2 = 3;
        } else if (!aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i2 = 1;
        } else if (!aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3]) {
            i2 = 12;
        } else if (!aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3]) {
            i2 = 36;
        } else if (aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i2 = 2;
        } else if (!aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3]) {
            i2 = 24;
        } else if (aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3]) {
            i2 = 15;
        } else if (aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3]) {
            i2 = 39;
        } else if (!aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3]) {
            i2 = 13;
        } else if (!aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3]) {
            i2 = 37;
        } else if (!aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3]) {
            i2 = 25;
        } else if (aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3]) {
            i2 = 27;
        } else if (aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3]) {
            i2 = 38;
        } else if (aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3]) {
            i2 = 14;
        } else if (aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3]) {
            i2 = 26;
        }
        if (i2 == 0) {
            return i2;
        }
        if (!Config.isConnectedTexturesFancy()) {
            return i2;
        }
        switch (p_getConnectedTextureCtmIndex_5_) {
            case 0: {
                aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east().north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west().north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east().south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west().south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                break;
            }
            case 1: {
                aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east().south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west().south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east().north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west().north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                break;
            }
            case 2: {
                aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west().down(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east().down(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west().up(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                boolean bl2 = aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east().up(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                if (p_getConnectedTextureCtmIndex_4_ != 1) break;
                ConnectedTextures.switchValues(0, 3, aboolean);
                ConnectedTextures.switchValues(1, 2, aboolean);
                break;
            }
            case 3: {
                aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east().down(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west().down(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.east().up(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.west().up(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                break;
            }
            case 4: {
                aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.down().south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.down().north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.up().south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.up().north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                break;
            }
            case 5: {
                aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.down().north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.down().south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.up().north(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                boolean bl3 = aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtmIndex_0_, p_getConnectedTextureCtmIndex_1_, p_getConnectedTextureCtmIndex_2_, p_getConnectedTextureCtmIndex_3_.up().south(), p_getConnectedTextureCtmIndex_5_, p_getConnectedTextureCtmIndex_6_, p_getConnectedTextureCtmIndex_7_);
                if (p_getConnectedTextureCtmIndex_4_ != 2) break;
                ConnectedTextures.switchValues(0, 3, aboolean);
                ConnectedTextures.switchValues(1, 2, aboolean);
            }
        }
        if (i2 == 13 && aboolean[0]) {
            i2 = 4;
        } else if (i2 == 15 && aboolean[1]) {
            i2 = 5;
        } else if (i2 == 37 && aboolean[2]) {
            i2 = 16;
        } else if (i2 == 39 && aboolean[3]) {
            i2 = 17;
        } else if (i2 == 14 && aboolean[0] && aboolean[1]) {
            i2 = 7;
        } else if (i2 == 25 && aboolean[0] && aboolean[2]) {
            i2 = 6;
        } else if (i2 == 27 && aboolean[3] && aboolean[1]) {
            i2 = 19;
        } else if (i2 == 38 && aboolean[3] && aboolean[2]) {
            i2 = 18;
        } else if (i2 == 14 && !aboolean[0] && aboolean[1]) {
            i2 = 31;
        } else if (i2 == 25 && aboolean[0] && !aboolean[2]) {
            i2 = 30;
        } else if (i2 == 27 && !aboolean[3] && aboolean[1]) {
            i2 = 41;
        } else if (i2 == 38 && aboolean[3] && !aboolean[2]) {
            i2 = 40;
        } else if (i2 == 14 && aboolean[0] && !aboolean[1]) {
            i2 = 29;
        } else if (i2 == 25 && !aboolean[0] && aboolean[2]) {
            i2 = 28;
        } else if (i2 == 27 && aboolean[3] && !aboolean[1]) {
            i2 = 43;
        } else if (i2 == 38 && !aboolean[3] && aboolean[2]) {
            i2 = 42;
        } else if (i2 == 26 && aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3]) {
            i2 = 46;
        } else if (i2 == 26 && !aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3]) {
            i2 = 9;
        } else if (i2 == 26 && aboolean[0] && !aboolean[1] && aboolean[2] && aboolean[3]) {
            i2 = 21;
        } else if (i2 == 26 && aboolean[0] && aboolean[1] && !aboolean[2] && aboolean[3]) {
            i2 = 8;
        } else if (i2 == 26 && aboolean[0] && aboolean[1] && aboolean[2] && !aboolean[3]) {
            i2 = 20;
        } else if (i2 == 26 && aboolean[0] && aboolean[1] && !aboolean[2] && !aboolean[3]) {
            i2 = 11;
        } else if (i2 == 26 && !aboolean[0] && !aboolean[1] && aboolean[2] && aboolean[3]) {
            i2 = 22;
        } else if (i2 == 26 && !aboolean[0] && aboolean[1] && !aboolean[2] && aboolean[3]) {
            i2 = 23;
        } else if (i2 == 26 && aboolean[0] && !aboolean[1] && aboolean[2] && !aboolean[3]) {
            i2 = 10;
        } else if (i2 == 26 && aboolean[0] && !aboolean[1] && !aboolean[2] && aboolean[3]) {
            i2 = 34;
        } else if (i2 == 26 && !aboolean[0] && aboolean[1] && aboolean[2] && !aboolean[3]) {
            i2 = 35;
        } else if (i2 == 26 && aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3]) {
            i2 = 32;
        } else if (i2 == 26 && !aboolean[0] && aboolean[1] && !aboolean[2] && !aboolean[3]) {
            i2 = 33;
        } else if (i2 == 26 && !aboolean[0] && !aboolean[1] && aboolean[2] && !aboolean[3]) {
            i2 = 44;
        } else if (i2 == 26 && !aboolean[0] && !aboolean[1] && !aboolean[2] && aboolean[3]) {
            i2 = 45;
        }
        return i2;
    }

    private static void switchValues(int p_switchValues_0_, int p_switchValues_1_, boolean[] p_switchValues_2_) {
        boolean flag = p_switchValues_2_[p_switchValues_0_];
        p_switchValues_2_[p_switchValues_0_] = p_switchValues_2_[p_switchValues_1_];
        p_switchValues_2_[p_switchValues_1_] = flag;
    }

    private static boolean isNeighbourOverlay(ConnectedProperties p_isNeighbourOverlay_0_, IBlockAccess p_isNeighbourOverlay_1_, IBlockState p_isNeighbourOverlay_2_, BlockPos p_isNeighbourOverlay_3_, int p_isNeighbourOverlay_4_, TextureAtlasSprite p_isNeighbourOverlay_5_, int p_isNeighbourOverlay_6_) {
        TextureAtlasSprite textureatlassprite;
        BlockStateBase blockstatebase;
        IBlockState iblockstate = p_isNeighbourOverlay_1_.getBlockState(p_isNeighbourOverlay_3_);
        if (!ConnectedTextures.isFullCubeModel(iblockstate)) {
            return false;
        }
        if (p_isNeighbourOverlay_0_.connectBlocks != null && !Matches.block((blockstatebase = (BlockStateBase)iblockstate).getBlockId(), blockstatebase.getMetadata(), p_isNeighbourOverlay_0_.connectBlocks)) {
            return false;
        }
        if (p_isNeighbourOverlay_0_.connectTileIcons != null && !Config.isSameOne(textureatlassprite = ConnectedTextures.getNeighbourIcon(p_isNeighbourOverlay_1_, p_isNeighbourOverlay_2_, p_isNeighbourOverlay_3_, iblockstate, p_isNeighbourOverlay_4_), p_isNeighbourOverlay_0_.connectTileIcons)) {
            return false;
        }
        IBlockState iblockstate1 = p_isNeighbourOverlay_1_.getBlockState(p_isNeighbourOverlay_3_.offset(ConnectedTextures.getFacing(p_isNeighbourOverlay_4_)));
        if (iblockstate1.isOpaqueCube()) {
            return false;
        }
        if (p_isNeighbourOverlay_4_ == 1 && iblockstate1.getBlock() == Blocks.SNOW_LAYER) {
            return false;
        }
        return !ConnectedTextures.isNeighbour(p_isNeighbourOverlay_0_, p_isNeighbourOverlay_1_, p_isNeighbourOverlay_2_, p_isNeighbourOverlay_3_, iblockstate, p_isNeighbourOverlay_4_, p_isNeighbourOverlay_5_, p_isNeighbourOverlay_6_);
    }

    private static boolean isFullCubeModel(IBlockState p_isFullCubeModel_0_) {
        if (p_isFullCubeModel_0_.isFullCube()) {
            return true;
        }
        Block block = p_isFullCubeModel_0_.getBlock();
        if (block instanceof BlockGlass) {
            return true;
        }
        return block instanceof BlockStainedGlass;
    }

    private static boolean isNeighbourMatching(ConnectedProperties p_isNeighbourMatching_0_, IBlockAccess p_isNeighbourMatching_1_, IBlockState p_isNeighbourMatching_2_, BlockPos p_isNeighbourMatching_3_, int p_isNeighbourMatching_4_, TextureAtlasSprite p_isNeighbourMatching_5_, int p_isNeighbourMatching_6_) {
        TextureAtlasSprite textureatlassprite;
        BlockStateBase blockstatebase;
        IBlockState iblockstate = p_isNeighbourMatching_1_.getBlockState(p_isNeighbourMatching_3_);
        if (iblockstate == AIR_DEFAULT_STATE) {
            return false;
        }
        if (p_isNeighbourMatching_0_.matchBlocks != null && iblockstate instanceof BlockStateBase && !p_isNeighbourMatching_0_.matchesBlock((blockstatebase = (BlockStateBase)iblockstate).getBlockId(), blockstatebase.getMetadata())) {
            return false;
        }
        if (p_isNeighbourMatching_0_.matchTileIcons != null && (textureatlassprite = ConnectedTextures.getNeighbourIcon(p_isNeighbourMatching_1_, p_isNeighbourMatching_2_, p_isNeighbourMatching_3_, iblockstate, p_isNeighbourMatching_4_)) != p_isNeighbourMatching_5_) {
            return false;
        }
        IBlockState iblockstate1 = p_isNeighbourMatching_1_.getBlockState(p_isNeighbourMatching_3_.offset(ConnectedTextures.getFacing(p_isNeighbourMatching_4_)));
        if (iblockstate1.isOpaqueCube()) {
            return false;
        }
        return p_isNeighbourMatching_4_ != 1 || iblockstate1.getBlock() != Blocks.SNOW_LAYER;
    }

    private static boolean isNeighbour(ConnectedProperties p_isNeighbour_0_, IBlockAccess p_isNeighbour_1_, IBlockState p_isNeighbour_2_, BlockPos p_isNeighbour_3_, int p_isNeighbour_4_, TextureAtlasSprite p_isNeighbour_5_, int p_isNeighbour_6_) {
        IBlockState iblockstate = p_isNeighbour_1_.getBlockState(p_isNeighbour_3_);
        return ConnectedTextures.isNeighbour(p_isNeighbour_0_, p_isNeighbour_1_, p_isNeighbour_2_, p_isNeighbour_3_, iblockstate, p_isNeighbour_4_, p_isNeighbour_5_, p_isNeighbour_6_);
    }

    private static boolean isNeighbour(ConnectedProperties p_isNeighbour_0_, IBlockAccess p_isNeighbour_1_, IBlockState p_isNeighbour_2_, BlockPos p_isNeighbour_3_, IBlockState p_isNeighbour_4_, int p_isNeighbour_5_, TextureAtlasSprite p_isNeighbour_6_, int p_isNeighbour_7_) {
        if (p_isNeighbour_2_ == p_isNeighbour_4_) {
            return true;
        }
        if (p_isNeighbour_0_.connect == 2) {
            if (p_isNeighbour_4_ == null) {
                return false;
            }
            if (p_isNeighbour_4_ == AIR_DEFAULT_STATE) {
                return false;
            }
            TextureAtlasSprite textureatlassprite = ConnectedTextures.getNeighbourIcon(p_isNeighbour_1_, p_isNeighbour_2_, p_isNeighbour_3_, p_isNeighbour_4_, p_isNeighbour_5_);
            return textureatlassprite == p_isNeighbour_6_;
        }
        if (p_isNeighbour_0_.connect == 3) {
            if (p_isNeighbour_4_ == null) {
                return false;
            }
            if (p_isNeighbour_4_ == AIR_DEFAULT_STATE) {
                return false;
            }
            return p_isNeighbour_4_.getMaterial() == p_isNeighbour_2_.getMaterial();
        }
        if (!(p_isNeighbour_4_ instanceof BlockStateBase)) {
            return false;
        }
        BlockStateBase blockstatebase = (BlockStateBase)p_isNeighbour_4_;
        Block block = blockstatebase.getBlock();
        int i2 = blockstatebase.getMetadata();
        return block == p_isNeighbour_2_.getBlock() && i2 == p_isNeighbour_7_;
    }

    private static TextureAtlasSprite getNeighbourIcon(IBlockAccess p_getNeighbourIcon_0_, IBlockState p_getNeighbourIcon_1_, BlockPos p_getNeighbourIcon_2_, IBlockState p_getNeighbourIcon_3_, int p_getNeighbourIcon_4_) {
        p_getNeighbourIcon_3_ = p_getNeighbourIcon_3_.getBlock().getActualState(p_getNeighbourIcon_3_, p_getNeighbourIcon_0_, p_getNeighbourIcon_2_);
        IBakedModel ibakedmodel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(p_getNeighbourIcon_3_);
        if (ibakedmodel == null) {
            return null;
        }
        EnumFacing enumfacing = ConnectedTextures.getFacing(p_getNeighbourIcon_4_);
        List list = ibakedmodel.getQuads(p_getNeighbourIcon_3_, enumfacing, 0L);
        if (Config.isBetterGrass()) {
            list = BetterGrass.getFaceQuads(p_getNeighbourIcon_0_, p_getNeighbourIcon_3_, p_getNeighbourIcon_2_, enumfacing, list);
        }
        if (list.size() > 0) {
            BakedQuad bakedquad1 = list.get(0);
            return bakedquad1.getSprite();
        }
        List<BakedQuad> list1 = ibakedmodel.getQuads(p_getNeighbourIcon_3_, null, 0L);
        for (int i2 = 0; i2 < list1.size(); ++i2) {
            BakedQuad bakedquad = list1.get(i2);
            if (bakedquad.getFace() != enumfacing) continue;
            return bakedquad.getSprite();
        }
        return null;
    }

    private static TextureAtlasSprite getConnectedTextureHorizontal(ConnectedProperties p_getConnectedTextureHorizontal_0_, IBlockAccess p_getConnectedTextureHorizontal_1_, IBlockState p_getConnectedTextureHorizontal_2_, BlockPos p_getConnectedTextureHorizontal_3_, int p_getConnectedTextureHorizontal_4_, int p_getConnectedTextureHorizontal_5_, TextureAtlasSprite p_getConnectedTextureHorizontal_6_, int p_getConnectedTextureHorizontal_7_) {
        boolean flag = false;
        boolean flag1 = false;
        block0 : switch (p_getConnectedTextureHorizontal_4_) {
            case 0: {
                switch (p_getConnectedTextureHorizontal_5_) {
                    case 0: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 1: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 2: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 3: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 4: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 5: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                    }
                }
                break;
            }
            case 1: {
                switch (p_getConnectedTextureHorizontal_5_) {
                    case 0: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 1: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 2: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 3: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 4: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.down(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.up(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 5: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.up(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.down(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                    }
                }
                break;
            }
            case 2: {
                switch (p_getConnectedTextureHorizontal_5_) {
                    case 0: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 1: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 2: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.down(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.up(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 3: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.up(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.down(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 4: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        break block0;
                    }
                    case 5: {
                        flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                        flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_, p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_, p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_, p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
                    }
                }
            }
        }
        int i2 = 3;
        i2 = flag ? (flag1 ? 1 : 2) : (flag1 ? 0 : 3);
        return p_getConnectedTextureHorizontal_0_.tileIcons[i2];
    }

    private static TextureAtlasSprite getConnectedTextureVertical(ConnectedProperties p_getConnectedTextureVertical_0_, IBlockAccess p_getConnectedTextureVertical_1_, IBlockState p_getConnectedTextureVertical_2_, BlockPos p_getConnectedTextureVertical_3_, int p_getConnectedTextureVertical_4_, int p_getConnectedTextureVertical_5_, TextureAtlasSprite p_getConnectedTextureVertical_6_, int p_getConnectedTextureVertical_7_) {
        boolean flag = false;
        boolean flag1 = false;
        switch (p_getConnectedTextureVertical_4_) {
            case 0: {
                if (p_getConnectedTextureVertical_5_ == 1) {
                    flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.south(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.north(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    break;
                }
                if (p_getConnectedTextureVertical_5_ == 0) {
                    flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.north(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.south(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    break;
                }
                flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.down(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.up(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                break;
            }
            case 1: {
                if (p_getConnectedTextureVertical_5_ == 3) {
                    flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.down(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.up(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    break;
                }
                if (p_getConnectedTextureVertical_5_ == 2) {
                    flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.up(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.down(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    break;
                }
                flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.south(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.north(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                break;
            }
            case 2: {
                if (p_getConnectedTextureVertical_5_ == 5) {
                    flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.up(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.down(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    break;
                }
                if (p_getConnectedTextureVertical_5_ == 4) {
                    flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.down(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.up(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                    break;
                }
                flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.west(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
                flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_, p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.east(), p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_, p_getConnectedTextureVertical_7_);
            }
        }
        int i2 = 3;
        i2 = flag ? (flag1 ? 1 : 2) : (flag1 ? 0 : 3);
        return p_getConnectedTextureVertical_0_.tileIcons[i2];
    }

    private static TextureAtlasSprite getConnectedTextureHorizontalVertical(ConnectedProperties p_getConnectedTextureHorizontalVertical_0_, IBlockAccess p_getConnectedTextureHorizontalVertical_1_, IBlockState p_getConnectedTextureHorizontalVertical_2_, BlockPos p_getConnectedTextureHorizontalVertical_3_, int p_getConnectedTextureHorizontalVertical_4_, int p_getConnectedTextureHorizontalVertical_5_, TextureAtlasSprite p_getConnectedTextureHorizontalVertical_6_, int p_getConnectedTextureHorizontalVertical_7_) {
        TextureAtlasSprite[] atextureatlassprite = p_getConnectedTextureHorizontalVertical_0_.tileIcons;
        TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureHorizontal(p_getConnectedTextureHorizontalVertical_0_, p_getConnectedTextureHorizontalVertical_1_, p_getConnectedTextureHorizontalVertical_2_, p_getConnectedTextureHorizontalVertical_3_, p_getConnectedTextureHorizontalVertical_4_, p_getConnectedTextureHorizontalVertical_5_, p_getConnectedTextureHorizontalVertical_6_, p_getConnectedTextureHorizontalVertical_7_);
        if (textureatlassprite != null && textureatlassprite != p_getConnectedTextureHorizontalVertical_6_ && textureatlassprite != atextureatlassprite[3]) {
            return textureatlassprite;
        }
        TextureAtlasSprite textureatlassprite1 = ConnectedTextures.getConnectedTextureVertical(p_getConnectedTextureHorizontalVertical_0_, p_getConnectedTextureHorizontalVertical_1_, p_getConnectedTextureHorizontalVertical_2_, p_getConnectedTextureHorizontalVertical_3_, p_getConnectedTextureHorizontalVertical_4_, p_getConnectedTextureHorizontalVertical_5_, p_getConnectedTextureHorizontalVertical_6_, p_getConnectedTextureHorizontalVertical_7_);
        if (textureatlassprite1 == atextureatlassprite[0]) {
            return atextureatlassprite[4];
        }
        if (textureatlassprite1 == atextureatlassprite[1]) {
            return atextureatlassprite[5];
        }
        return textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6] : textureatlassprite1;
    }

    private static TextureAtlasSprite getConnectedTextureVerticalHorizontal(ConnectedProperties p_getConnectedTextureVerticalHorizontal_0_, IBlockAccess p_getConnectedTextureVerticalHorizontal_1_, IBlockState p_getConnectedTextureVerticalHorizontal_2_, BlockPos p_getConnectedTextureVerticalHorizontal_3_, int p_getConnectedTextureVerticalHorizontal_4_, int p_getConnectedTextureVerticalHorizontal_5_, TextureAtlasSprite p_getConnectedTextureVerticalHorizontal_6_, int p_getConnectedTextureVerticalHorizontal_7_) {
        TextureAtlasSprite[] atextureatlassprite = p_getConnectedTextureVerticalHorizontal_0_.tileIcons;
        TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureVertical(p_getConnectedTextureVerticalHorizontal_0_, p_getConnectedTextureVerticalHorizontal_1_, p_getConnectedTextureVerticalHorizontal_2_, p_getConnectedTextureVerticalHorizontal_3_, p_getConnectedTextureVerticalHorizontal_4_, p_getConnectedTextureVerticalHorizontal_5_, p_getConnectedTextureVerticalHorizontal_6_, p_getConnectedTextureVerticalHorizontal_7_);
        if (textureatlassprite != null && textureatlassprite != p_getConnectedTextureVerticalHorizontal_6_ && textureatlassprite != atextureatlassprite[3]) {
            return textureatlassprite;
        }
        TextureAtlasSprite textureatlassprite1 = ConnectedTextures.getConnectedTextureHorizontal(p_getConnectedTextureVerticalHorizontal_0_, p_getConnectedTextureVerticalHorizontal_1_, p_getConnectedTextureVerticalHorizontal_2_, p_getConnectedTextureVerticalHorizontal_3_, p_getConnectedTextureVerticalHorizontal_4_, p_getConnectedTextureVerticalHorizontal_5_, p_getConnectedTextureVerticalHorizontal_6_, p_getConnectedTextureVerticalHorizontal_7_);
        if (textureatlassprite1 == atextureatlassprite[0]) {
            return atextureatlassprite[4];
        }
        if (textureatlassprite1 == atextureatlassprite[1]) {
            return atextureatlassprite[5];
        }
        return textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6] : textureatlassprite1;
    }

    private static TextureAtlasSprite getConnectedTextureTop(ConnectedProperties p_getConnectedTextureTop_0_, IBlockAccess p_getConnectedTextureTop_1_, IBlockState p_getConnectedTextureTop_2_, BlockPos p_getConnectedTextureTop_3_, int p_getConnectedTextureTop_4_, int p_getConnectedTextureTop_5_, TextureAtlasSprite p_getConnectedTextureTop_6_, int p_getConnectedTextureTop_7_) {
        boolean flag = false;
        switch (p_getConnectedTextureTop_4_) {
            case 0: {
                if (p_getConnectedTextureTop_5_ == 1 || p_getConnectedTextureTop_5_ == 0) {
                    return null;
                }
                flag = ConnectedTextures.isNeighbour(p_getConnectedTextureTop_0_, p_getConnectedTextureTop_1_, p_getConnectedTextureTop_2_, p_getConnectedTextureTop_3_.up(), p_getConnectedTextureTop_5_, p_getConnectedTextureTop_6_, p_getConnectedTextureTop_7_);
                break;
            }
            case 1: {
                if (p_getConnectedTextureTop_5_ == 3 || p_getConnectedTextureTop_5_ == 2) {
                    return null;
                }
                flag = ConnectedTextures.isNeighbour(p_getConnectedTextureTop_0_, p_getConnectedTextureTop_1_, p_getConnectedTextureTop_2_, p_getConnectedTextureTop_3_.south(), p_getConnectedTextureTop_5_, p_getConnectedTextureTop_6_, p_getConnectedTextureTop_7_);
                break;
            }
            case 2: {
                if (p_getConnectedTextureTop_5_ == 5 || p_getConnectedTextureTop_5_ == 4) {
                    return null;
                }
                flag = ConnectedTextures.isNeighbour(p_getConnectedTextureTop_0_, p_getConnectedTextureTop_1_, p_getConnectedTextureTop_2_, p_getConnectedTextureTop_3_.east(), p_getConnectedTextureTop_5_, p_getConnectedTextureTop_6_, p_getConnectedTextureTop_7_);
            }
        }
        if (flag) {
            return p_getConnectedTextureTop_0_.tileIcons[0];
        }
        return null;
    }

    public static void updateIcons(TextureMap p_updateIcons_0_) {
        blockProperties = null;
        tileProperties = null;
        spriteQuadMaps = null;
        spriteQuadCompactMaps = null;
        if (Config.isConnectedTextures()) {
            IResourcePack[] airesourcepack = Config.getResourcePacks();
            for (int i2 = airesourcepack.length - 1; i2 >= 0; --i2) {
                IResourcePack iresourcepack = airesourcepack[i2];
                ConnectedTextures.updateIcons(p_updateIcons_0_, iresourcepack);
            }
            ConnectedTextures.updateIcons(p_updateIcons_0_, Config.getDefaultResourcePack());
            ResourceLocation resourcelocation = new ResourceLocation("mcpatcher/ctm/default/empty");
            emptySprite = p_updateIcons_0_.registerSprite(resourcelocation);
            spriteQuadMaps = new Map[p_updateIcons_0_.getCountRegisteredSprites() + 1];
            spriteQuadFullMaps = new Map[p_updateIcons_0_.getCountRegisteredSprites() + 1];
            spriteQuadCompactMaps = new Map[p_updateIcons_0_.getCountRegisteredSprites() + 1][];
            if (blockProperties.length <= 0) {
                blockProperties = null;
            }
            if (tileProperties.length <= 0) {
                tileProperties = null;
            }
        }
    }

    private static void updateIconEmpty(TextureMap p_updateIconEmpty_0_) {
    }

    public static void updateIcons(TextureMap p_updateIcons_0_, IResourcePack p_updateIcons_1_) {
        Object[] astring = ResUtils.collectFiles(p_updateIcons_1_, "mcpatcher/ctm/", ".properties", ConnectedTextures.getDefaultCtmPaths());
        Arrays.sort(astring);
        List list = ConnectedTextures.makePropertyList(tileProperties);
        List list1 = ConnectedTextures.makePropertyList(blockProperties);
        for (int i2 = 0; i2 < astring.length; ++i2) {
            Object s2 = astring[i2];
            Config.dbg("ConnectedTextures: " + (String)s2);
            try {
                ResourceLocation resourcelocation = new ResourceLocation((String)s2);
                InputStream inputstream = p_updateIcons_1_.getInputStream(resourcelocation);
                if (inputstream == null) {
                    Config.warn("ConnectedTextures file not found: " + (String)s2);
                    continue;
                }
                Properties properties = new Properties();
                properties.load(inputstream);
                ConnectedProperties connectedproperties = new ConnectedProperties(properties, (String)s2);
                if (!connectedproperties.isValid((String)s2)) continue;
                connectedproperties.updateIcons(p_updateIcons_0_);
                ConnectedTextures.addToTileList(connectedproperties, list);
                ConnectedTextures.addToBlockList(connectedproperties, list1);
                continue;
            }
            catch (FileNotFoundException var11) {
                Config.warn("ConnectedTextures file not found: " + (String)s2);
                continue;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        blockProperties = ConnectedTextures.propertyListToArray(list1);
        tileProperties = ConnectedTextures.propertyListToArray(list);
        multipass = ConnectedTextures.detectMultipass();
        Config.dbg("Multipass connected textures: " + multipass);
    }

    private static List makePropertyList(ConnectedProperties[][] p_makePropertyList_0_) {
        ArrayList<ArrayList<ConnectedProperties>> list = new ArrayList<ArrayList<ConnectedProperties>>();
        if (p_makePropertyList_0_ != null) {
            for (int i2 = 0; i2 < p_makePropertyList_0_.length; ++i2) {
                ConnectedProperties[] aconnectedproperties = p_makePropertyList_0_[i2];
                ArrayList<ConnectedProperties> list1 = null;
                if (aconnectedproperties != null) {
                    list1 = new ArrayList<ConnectedProperties>(Arrays.asList(aconnectedproperties));
                }
                list.add(list1);
            }
        }
        return list;
    }

    private static boolean detectMultipass() {
        ArrayList<ConnectedProperties> list = new ArrayList<ConnectedProperties>();
        for (int i2 = 0; i2 < tileProperties.length; ++i2) {
            ConnectedProperties[] aconnectedproperties = tileProperties[i2];
            if (aconnectedproperties == null) continue;
            list.addAll(Arrays.asList(aconnectedproperties));
        }
        for (int k2 = 0; k2 < blockProperties.length; ++k2) {
            ConnectedProperties[] aconnectedproperties2 = blockProperties[k2];
            if (aconnectedproperties2 == null) continue;
            list.addAll(Arrays.asList(aconnectedproperties2));
        }
        ConnectedProperties[] aconnectedproperties1 = list.toArray(new ConnectedProperties[list.size()]);
        HashSet<TextureAtlasSprite> set1 = new HashSet<TextureAtlasSprite>();
        HashSet<TextureAtlasSprite> set = new HashSet<TextureAtlasSprite>();
        for (int j2 = 0; j2 < aconnectedproperties1.length; ++j2) {
            ConnectedProperties connectedproperties = aconnectedproperties1[j2];
            if (connectedproperties.matchTileIcons != null) {
                set1.addAll(Arrays.asList(connectedproperties.matchTileIcons));
            }
            if (connectedproperties.tileIcons == null) continue;
            set.addAll(Arrays.asList(connectedproperties.tileIcons));
        }
        set1.retainAll(set);
        return !set1.isEmpty();
    }

    private static ConnectedProperties[][] propertyListToArray(List p_propertyListToArray_0_) {
        ConnectedProperties[][] aconnectedproperties = new ConnectedProperties[p_propertyListToArray_0_.size()][];
        for (int i2 = 0; i2 < p_propertyListToArray_0_.size(); ++i2) {
            List list = (List)p_propertyListToArray_0_.get(i2);
            if (list == null) continue;
            ConnectedProperties[] aconnectedproperties1 = list.toArray(new ConnectedProperties[list.size()]);
            aconnectedproperties[i2] = aconnectedproperties1;
        }
        return aconnectedproperties;
    }

    private static void addToTileList(ConnectedProperties p_addToTileList_0_, List p_addToTileList_1_) {
        if (p_addToTileList_0_.matchTileIcons != null) {
            for (int i2 = 0; i2 < p_addToTileList_0_.matchTileIcons.length; ++i2) {
                TextureAtlasSprite textureatlassprite = p_addToTileList_0_.matchTileIcons[i2];
                if (!(textureatlassprite instanceof TextureAtlasSprite)) {
                    Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + textureatlassprite + ", name: " + textureatlassprite.getIconName());
                    continue;
                }
                int j2 = textureatlassprite.getIndexInMap();
                if (j2 < 0) {
                    Config.warn("Invalid tile ID: " + j2 + ", icon: " + textureatlassprite.getIconName());
                    continue;
                }
                ConnectedTextures.addToList(p_addToTileList_0_, p_addToTileList_1_, j2);
            }
        }
    }

    private static void addToBlockList(ConnectedProperties p_addToBlockList_0_, List p_addToBlockList_1_) {
        if (p_addToBlockList_0_.matchBlocks != null) {
            for (int i2 = 0; i2 < p_addToBlockList_0_.matchBlocks.length; ++i2) {
                int j2 = p_addToBlockList_0_.matchBlocks[i2].getBlockId();
                if (j2 < 0) {
                    Config.warn("Invalid block ID: " + j2);
                    continue;
                }
                ConnectedTextures.addToList(p_addToBlockList_0_, p_addToBlockList_1_, j2);
            }
        }
    }

    private static void addToList(ConnectedProperties p_addToList_0_, List p_addToList_1_, int p_addToList_2_) {
        while (p_addToList_2_ >= p_addToList_1_.size()) {
            p_addToList_1_.add(null);
        }
        ArrayList<ConnectedProperties> list = (ArrayList<ConnectedProperties>)p_addToList_1_.get(p_addToList_2_);
        if (list == null) {
            list = new ArrayList<ConnectedProperties>();
            p_addToList_1_.set(p_addToList_2_, list);
        }
        list.add(p_addToList_0_);
    }

    private static String[] getDefaultCtmPaths() {
        ArrayList<String> list = new ArrayList<String>();
        String s2 = "mcpatcher/ctm/default/";
        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
            list.add(String.valueOf(s2) + "glass.properties");
            list.add(String.valueOf(s2) + "glasspane.properties");
        }
        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
            list.add(String.valueOf(s2) + "bookshelf.properties");
        }
        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
            list.add(String.valueOf(s2) + "sandstone.properties");
        }
        String[] astring = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"};
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s1 = astring[i2];
            if (!Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + s1 + ".png"))) continue;
            list.add(String.valueOf(s2) + i2 + "_glass_" + s1 + "/glass_" + s1 + ".properties");
            list.add(String.valueOf(s2) + i2 + "_glass_" + s1 + "/glass_pane_" + s1 + ".properties");
        }
        String[] astring1 = list.toArray(new String[list.size()]);
        return astring1;
    }
}

