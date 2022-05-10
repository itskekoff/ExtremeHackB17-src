package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import optifine.Config;
import optifine.CustomColors;
import optifine.RenderEnv;
import shadersmod.client.SVertexBuilder;

public class BlockFluidRenderer {
    private final BlockColors blockColors;
    private final TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
    private final TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];
    private TextureAtlasSprite atlasSpriteWaterOverlay;

    public BlockFluidRenderer(BlockColors blockColorsIn) {
        this.blockColors = blockColorsIn;
        this.initAtlasSprites();
    }

    protected void initAtlasSprites() {
        TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        this.atlasSpritesLava[0] = texturemap.getAtlasSprite("minecraft:blocks/lava_still");
        this.atlasSpritesLava[1] = texturemap.getAtlasSprite("minecraft:blocks/lava_flow");
        this.atlasSpritesWater[0] = texturemap.getAtlasSprite("minecraft:blocks/water_still");
        this.atlasSpritesWater[1] = texturemap.getAtlasSprite("minecraft:blocks/water_flow");
        this.atlasSpriteWaterOverlay = texturemap.getAtlasSprite("minecraft:blocks/water_overlay");
    }

    public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, BufferBuilder worldRendererIn) {
        boolean flag3;
        try {
            if (Config.isShaders()) {
                SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccess, worldRendererIn);
            }
            BlockLiquid blockliquid = (BlockLiquid)blockStateIn.getBlock();
            boolean flag = blockStateIn.getMaterial() == Material.LAVA;
            TextureAtlasSprite[] atextureatlassprite = flag ? this.atlasSpritesLava : this.atlasSpritesWater;
            RenderEnv renderenv = worldRendererIn.getRenderEnv(blockAccess, blockStateIn, blockPosIn);
            int i2 = CustomColors.getFluidColor(blockAccess, blockStateIn, blockPosIn, renderenv);
            float f2 = (float)(i2 >> 16 & 0xFF) / 255.0f;
            float f1 = (float)(i2 >> 8 & 0xFF) / 255.0f;
            float f22 = (float)(i2 & 0xFF) / 255.0f;
            boolean flag1 = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.UP);
            boolean flag2 = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.DOWN);
            boolean[] aboolean = renderenv.getBorderFlags();
            aboolean[0] = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.NORTH);
            aboolean[1] = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.SOUTH);
            aboolean[2] = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.WEST);
            aboolean[3] = blockStateIn.shouldSideBeRendered(blockAccess, blockPosIn, EnumFacing.EAST);
            if (flag1 || flag2 || aboolean[0] || aboolean[1] || aboolean[2] || aboolean[3]) {
                boolean flag4;
                boolean flag32 = false;
                float f3 = 0.5f;
                float f4 = 1.0f;
                float f5 = 0.8f;
                float f6 = 0.6f;
                Material material = blockStateIn.getMaterial();
                float f7 = this.getFluidHeight(blockAccess, blockPosIn, material);
                float f8 = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
                float f9 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
                float f10 = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
                double d0 = blockPosIn.getX();
                double d1 = blockPosIn.getY();
                double d2 = blockPosIn.getZ();
                float f11 = 0.001f;
                if (flag1) {
                    float f20;
                    float f16;
                    float f19;
                    float f15;
                    float f18;
                    float f14;
                    float f17;
                    float f13;
                    flag32 = true;
                    float f12 = BlockLiquid.getSlopeAngle(blockAccess, blockPosIn, material, blockStateIn);
                    TextureAtlasSprite textureatlassprite = f12 > -999.0f ? atextureatlassprite[1] : atextureatlassprite[0];
                    worldRendererIn.setSprite(textureatlassprite);
                    f7 -= 0.001f;
                    f8 -= 0.001f;
                    f9 -= 0.001f;
                    f10 -= 0.001f;
                    if (f12 < -999.0f) {
                        f13 = textureatlassprite.getInterpolatedU(0.0);
                        f17 = textureatlassprite.getInterpolatedV(0.0);
                        f14 = f13;
                        f18 = textureatlassprite.getInterpolatedV(16.0);
                        f15 = textureatlassprite.getInterpolatedU(16.0);
                        f19 = f18;
                        f16 = f15;
                        f20 = f17;
                    } else {
                        float f21 = MathHelper.sin(f12) * 0.25f;
                        float f222 = MathHelper.cos(f12) * 0.25f;
                        float f23 = 8.0f;
                        f13 = textureatlassprite.getInterpolatedU(8.0f + (-f222 - f21) * 16.0f);
                        f17 = textureatlassprite.getInterpolatedV(8.0f + (-f222 + f21) * 16.0f);
                        f14 = textureatlassprite.getInterpolatedU(8.0f + (-f222 + f21) * 16.0f);
                        f18 = textureatlassprite.getInterpolatedV(8.0f + (f222 + f21) * 16.0f);
                        f15 = textureatlassprite.getInterpolatedU(8.0f + (f222 + f21) * 16.0f);
                        f19 = textureatlassprite.getInterpolatedV(8.0f + (f222 - f21) * 16.0f);
                        f16 = textureatlassprite.getInterpolatedU(8.0f + (f222 - f21) * 16.0f);
                        f20 = textureatlassprite.getInterpolatedV(8.0f + (-f222 - f21) * 16.0f);
                    }
                    int k2 = blockStateIn.getPackedLightmapCoords(blockAccess, blockPosIn);
                    int l2 = k2 >> 16 & 0xFFFF;
                    int i3 = k2 & 0xFFFF;
                    float f24 = 1.0f * f2;
                    float f25 = 1.0f * f1;
                    float f26 = 1.0f * f22;
                    worldRendererIn.pos(d0 + 0.0, d1 + (double)f7, d2 + 0.0).color(f24, f25, f26, 1.0f).tex(f13, f17).lightmap(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 0.0, d1 + (double)f8, d2 + 1.0).color(f24, f25, f26, 1.0f).tex(f14, f18).lightmap(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 1.0, d1 + (double)f9, d2 + 1.0).color(f24, f25, f26, 1.0f).tex(f15, f19).lightmap(l2, i3).endVertex();
                    worldRendererIn.pos(d0 + 1.0, d1 + (double)f10, d2 + 0.0).color(f24, f25, f26, 1.0f).tex(f16, f20).lightmap(l2, i3).endVertex();
                    if (blockliquid.shouldRenderSides(blockAccess, blockPosIn.up())) {
                        worldRendererIn.pos(d0 + 0.0, d1 + (double)f7, d2 + 0.0).color(f24, f25, f26, 1.0f).tex(f13, f17).lightmap(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 1.0, d1 + (double)f10, d2 + 0.0).color(f24, f25, f26, 1.0f).tex(f16, f20).lightmap(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 1.0, d1 + (double)f9, d2 + 1.0).color(f24, f25, f26, 1.0f).tex(f15, f19).lightmap(l2, i3).endVertex();
                        worldRendererIn.pos(d0 + 0.0, d1 + (double)f8, d2 + 1.0).color(f24, f25, f26, 1.0f).tex(f14, f18).lightmap(l2, i3).endVertex();
                    }
                }
                if (flag2) {
                    float f38 = atextureatlassprite[0].getMinU();
                    float f39 = atextureatlassprite[0].getMaxU();
                    float f40 = atextureatlassprite[0].getMinV();
                    float f41 = atextureatlassprite[0].getMaxV();
                    int l1 = blockStateIn.getPackedLightmapCoords(blockAccess, blockPosIn.down());
                    int i22 = l1 >> 16 & 0xFFFF;
                    int j2 = l1 & 0xFFFF;
                    worldRendererIn.pos(d0, d1, d2 + 1.0).color(f2 * 0.5f, f1 * 0.5f, f22 * 0.5f, 1.0f).tex(f38, f41).lightmap(i22, j2).endVertex();
                    worldRendererIn.pos(d0, d1, d2).color(f2 * 0.5f, f1 * 0.5f, f22 * 0.5f, 1.0f).tex(f38, f40).lightmap(i22, j2).endVertex();
                    worldRendererIn.pos(d0 + 1.0, d1, d2).color(f2 * 0.5f, f1 * 0.5f, f22 * 0.5f, 1.0f).tex(f39, f40).lightmap(i22, j2).endVertex();
                    worldRendererIn.pos(d0 + 1.0, d1, d2 + 1.0).color(f2 * 0.5f, f1 * 0.5f, f22 * 0.5f, 1.0f).tex(f39, f41).lightmap(i22, j2).endVertex();
                    flag32 = true;
                }
                for (int i1 = 0; i1 < 4; ++i1) {
                    double d6;
                    double d4;
                    double d5;
                    double d3;
                    float f45;
                    float f44;
                    int j1 = 0;
                    int k1 = 0;
                    if (i1 == 0) {
                        --k1;
                    }
                    if (i1 == 1) {
                        ++k1;
                    }
                    if (i1 == 2) {
                        --j1;
                    }
                    if (i1 == 3) {
                        ++j1;
                    }
                    BlockPos blockpos = blockPosIn.add(j1, 0, k1);
                    TextureAtlasSprite textureatlassprite1 = atextureatlassprite[1];
                    worldRendererIn.setSprite(textureatlassprite1);
                    float f42 = 0.0f;
                    float f43 = 0.0f;
                    if (!flag) {
                        BlockSlab blockslab;
                        IBlockState iblockstate = blockAccess.getBlockState(blockpos);
                        Block block = iblockstate.getBlock();
                        if (block == Blocks.GLASS || block == Blocks.STAINED_GLASS || block == Blocks.BEACON || block == Blocks.SLIME_BLOCK) {
                            textureatlassprite1 = this.atlasSpriteWaterOverlay;
                            worldRendererIn.setSprite(textureatlassprite1);
                        }
                        if (block == Blocks.FARMLAND || block == Blocks.GRASS_PATH) {
                            f42 = 0.9375f;
                            f43 = 0.9375f;
                        }
                        if (block instanceof BlockSlab && !(blockslab = (BlockSlab)block).isDouble() && iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM) {
                            f42 = 0.5f;
                            f43 = 0.5f;
                        }
                    }
                    if (!aboolean[i1]) continue;
                    if (i1 == 0) {
                        f44 = f7;
                        f45 = f10;
                        d3 = d0;
                        d5 = d0 + 1.0;
                        d4 = d2 + (double)0.001f;
                        d6 = d2 + (double)0.001f;
                    } else if (i1 == 1) {
                        f44 = f9;
                        f45 = f8;
                        d3 = d0 + 1.0;
                        d5 = d0;
                        d4 = d2 + 1.0 - (double)0.001f;
                        d6 = d2 + 1.0 - (double)0.001f;
                    } else if (i1 == 2) {
                        f44 = f8;
                        f45 = f7;
                        d3 = d0 + (double)0.001f;
                        d5 = d0 + (double)0.001f;
                        d4 = d2 + 1.0;
                        d6 = d2;
                    } else {
                        f44 = f10;
                        f45 = f9;
                        d3 = d0 + 1.0 - (double)0.001f;
                        d5 = d0 + 1.0 - (double)0.001f;
                        d4 = d2;
                        d6 = d2 + 1.0;
                    }
                    if (!(f44 > f42) && !(f45 > f43)) continue;
                    f42 = Math.min(f42, f44);
                    f43 = Math.min(f43, f45);
                    if (f42 > f11) {
                        f42 -= f11;
                    }
                    if (f43 > f11) {
                        f43 -= f11;
                    }
                    flag32 = true;
                    float f27 = textureatlassprite1.getInterpolatedU(0.0);
                    float f28 = textureatlassprite1.getInterpolatedU(8.0);
                    float f29 = textureatlassprite1.getInterpolatedV((1.0f - f44) * 16.0f * 0.5f);
                    float f30 = textureatlassprite1.getInterpolatedV((1.0f - f45) * 16.0f * 0.5f);
                    float f31 = textureatlassprite1.getInterpolatedV(8.0);
                    float f32 = textureatlassprite1.getInterpolatedV((1.0f - f42) * 16.0f * 0.5f);
                    float f33 = textureatlassprite1.getInterpolatedV((1.0f - f43) * 16.0f * 0.5f);
                    int j2 = blockStateIn.getPackedLightmapCoords(blockAccess, blockpos);
                    int k2 = j2 >> 16 & 0xFFFF;
                    int l2 = j2 & 0xFFFF;
                    float f34 = i1 < 2 ? 0.8f : 0.6f;
                    float f35 = 1.0f * f34 * f2;
                    float f36 = 1.0f * f34 * f1;
                    float f37 = 1.0f * f34 * f22;
                    worldRendererIn.pos(d3, d1 + (double)f44, d4).color(f35, f36, f37, 1.0f).tex(f27, f29).lightmap(k2, l2).endVertex();
                    worldRendererIn.pos(d5, d1 + (double)f45, d6).color(f35, f36, f37, 1.0f).tex(f28, f30).lightmap(k2, l2).endVertex();
                    worldRendererIn.pos(d5, d1 + (double)f43, d6).color(f35, f36, f37, 1.0f).tex(f28, f33).lightmap(k2, l2).endVertex();
                    worldRendererIn.pos(d3, d1 + (double)f42, d4).color(f35, f36, f37, 1.0f).tex(f27, f32).lightmap(k2, l2).endVertex();
                    if (textureatlassprite1 == this.atlasSpriteWaterOverlay) continue;
                    worldRendererIn.pos(d3, d1 + (double)f42, d4).color(f35, f36, f37, 1.0f).tex(f27, f32).lightmap(k2, l2).endVertex();
                    worldRendererIn.pos(d5, d1 + (double)f43, d6).color(f35, f36, f37, 1.0f).tex(f28, f33).lightmap(k2, l2).endVertex();
                    worldRendererIn.pos(d5, d1 + (double)f45, d6).color(f35, f36, f37, 1.0f).tex(f28, f30).lightmap(k2, l2).endVertex();
                    worldRendererIn.pos(d3, d1 + (double)f44, d4).color(f35, f36, f37, 1.0f).tex(f27, f29).lightmap(k2, l2).endVertex();
                }
                worldRendererIn.setSprite(null);
                boolean bl2 = flag4 = flag32;
                return bl2;
            }
            flag3 = false;
        }
        finally {
            if (Config.isShaders()) {
                SVertexBuilder.popEntity(worldRendererIn);
            }
        }
        return flag3;
    }

    private float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
        int i2 = 0;
        float f2 = 0.0f;
        for (int j2 = 0; j2 < 4; ++j2) {
            BlockPos blockpos = blockPosIn.add(-(j2 & 1), 0, -(j2 >> 1 & 1));
            if (blockAccess.getBlockState(blockpos.up()).getMaterial() == blockMaterial) {
                return 1.0f;
            }
            IBlockState iblockstate = blockAccess.getBlockState(blockpos);
            Material material = iblockstate.getMaterial();
            if (material != blockMaterial) {
                if (material.isSolid()) continue;
                f2 += 1.0f;
                ++i2;
                continue;
            }
            int k2 = iblockstate.getValue(BlockLiquid.LEVEL);
            if (k2 >= 8 || k2 == 0) {
                f2 += BlockLiquid.getLiquidHeightPercent(k2) * 10.0f;
                i2 += 10;
            }
            f2 += BlockLiquid.getLiquidHeightPercent(k2);
            ++i2;
        }
        return 1.0f - f2 / (float)i2;
    }
}

