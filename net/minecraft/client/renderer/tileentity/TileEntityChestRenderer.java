package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityChestRenderer
extends TileEntitySpecialRenderer<TileEntityChest> {
    private static final ResourceLocation TEXTURE_TRAPPED_DOUBLE = new ResourceLocation("textures/entity/chest/trapped_double.png");
    private static final ResourceLocation TEXTURE_CHRISTMAS_DOUBLE = new ResourceLocation("textures/entity/chest/christmas_double.png");
    private static final ResourceLocation TEXTURE_NORMAL_DOUBLE = new ResourceLocation("textures/entity/chest/normal_double.png");
    private static final ResourceLocation TEXTURE_TRAPPED = new ResourceLocation("textures/entity/chest/trapped.png");
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    private final ModelChest simpleChest = new ModelChest();
    private final ModelChest largeChest = new ModelLargeChest();
    private boolean isChristmas;

    public TileEntityChestRenderer() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.isChristmas = true;
        }
    }

    @Override
    public void func_192841_a(TileEntityChest p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_) {
        int i2;
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        if (p_192841_1_.hasWorldObj()) {
            Block block = p_192841_1_.getBlockType();
            i2 = p_192841_1_.getBlockMetadata();
            if (block instanceof BlockChest && i2 == 0) {
                ((BlockChest)block).checkForSurroundingChests(p_192841_1_.getWorld(), p_192841_1_.getPos(), p_192841_1_.getWorld().getBlockState(p_192841_1_.getPos()));
                i2 = p_192841_1_.getBlockMetadata();
            }
            p_192841_1_.checkForAdjacentChests();
        } else {
            i2 = 0;
        }
        if (p_192841_1_.adjacentChestZNeg == null && p_192841_1_.adjacentChestXNeg == null) {
            float f2;
            float f1;
            ModelChest modelchest;
            if (p_192841_1_.adjacentChestXPos == null && p_192841_1_.adjacentChestZPos == null) {
                modelchest = this.simpleChest;
                if (p_192841_9_ >= 0) {
                    this.bindTexture(DESTROY_STAGES[p_192841_9_]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(4.0f, 4.0f, 1.0f);
                    GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
                    GlStateManager.matrixMode(5888);
                } else if (this.isChristmas) {
                    this.bindTexture(TEXTURE_CHRISTMAS);
                } else if (p_192841_1_.getChestType() == BlockChest.Type.TRAP) {
                    this.bindTexture(TEXTURE_TRAPPED);
                } else {
                    this.bindTexture(TEXTURE_NORMAL);
                }
            } else {
                modelchest = this.largeChest;
                if (p_192841_9_ >= 0) {
                    this.bindTexture(DESTROY_STAGES[p_192841_9_]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0f, 4.0f, 1.0f);
                    GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
                    GlStateManager.matrixMode(5888);
                } else if (this.isChristmas) {
                    this.bindTexture(TEXTURE_CHRISTMAS_DOUBLE);
                } else if (p_192841_1_.getChestType() == BlockChest.Type.TRAP) {
                    this.bindTexture(TEXTURE_TRAPPED_DOUBLE);
                } else {
                    this.bindTexture(TEXTURE_NORMAL_DOUBLE);
                }
            }
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            if (p_192841_9_ < 0) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, p_192841_10_);
            }
            GlStateManager.translate((float)p_192841_2_, (float)p_192841_4_ + 1.0f, (float)p_192841_6_ + 1.0f);
            GlStateManager.scale(1.0f, -1.0f, -1.0f);
            GlStateManager.translate(0.5f, 0.5f, 0.5f);
            int j2 = 0;
            if (i2 == 2) {
                j2 = 180;
            }
            if (i2 == 3) {
                j2 = 0;
            }
            if (i2 == 4) {
                j2 = 90;
            }
            if (i2 == 5) {
                j2 = -90;
            }
            if (i2 == 2 && p_192841_1_.adjacentChestXPos != null) {
                GlStateManager.translate(1.0f, 0.0f, 0.0f);
            }
            if (i2 == 5 && p_192841_1_.adjacentChestZPos != null) {
                GlStateManager.translate(0.0f, 0.0f, -1.0f);
            }
            GlStateManager.rotate(j2, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5f, -0.5f, -0.5f);
            float f3 = p_192841_1_.prevLidAngle + (p_192841_1_.lidAngle - p_192841_1_.prevLidAngle) * p_192841_8_;
            if (p_192841_1_.adjacentChestZNeg != null && (f1 = p_192841_1_.adjacentChestZNeg.prevLidAngle + (p_192841_1_.adjacentChestZNeg.lidAngle - p_192841_1_.adjacentChestZNeg.prevLidAngle) * p_192841_8_) > f3) {
                f3 = f1;
            }
            if (p_192841_1_.adjacentChestXNeg != null && (f2 = p_192841_1_.adjacentChestXNeg.prevLidAngle + (p_192841_1_.adjacentChestXNeg.lidAngle - p_192841_1_.adjacentChestXNeg.prevLidAngle) * p_192841_8_) > f3) {
                f3 = f2;
            }
            f3 = 1.0f - f3;
            f3 = 1.0f - f3 * f3 * f3;
            modelchest.chestLid.rotateAngleX = -(f3 * 1.5707964f);
            modelchest.renderAll();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (p_192841_9_ >= 0) {
                GlStateManager.matrixMode(5890);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
            }
        }
    }
}

