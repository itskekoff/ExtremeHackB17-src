package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import optifine.Config;
import optifine.CustomColors;

public class TileEntitySignRenderer
extends TileEntitySpecialRenderer<TileEntitySign> {
    private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");
    private final ModelSign model = new ModelSign();

    @Override
    public void func_192841_a(TileEntitySign p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_) {
        Block block = p_192841_1_.getBlockType();
        GlStateManager.pushMatrix();
        float f2 = 0.6666667f;
        if (block == Blocks.STANDING_SIGN) {
            GlStateManager.translate((float)p_192841_2_ + 0.5f, (float)p_192841_4_ + 0.5f, (float)p_192841_6_ + 0.5f);
            float f1 = (float)(p_192841_1_.getBlockMetadata() * 360) / 16.0f;
            GlStateManager.rotate(-f1, 0.0f, 1.0f, 0.0f);
            this.model.signStick.showModel = true;
        } else {
            int k2 = p_192841_1_.getBlockMetadata();
            float f22 = 0.0f;
            if (k2 == 2) {
                f22 = 180.0f;
            }
            if (k2 == 4) {
                f22 = 90.0f;
            }
            if (k2 == 5) {
                f22 = -90.0f;
            }
            GlStateManager.translate((float)p_192841_2_ + 0.5f, (float)p_192841_4_ + 0.5f, (float)p_192841_6_ + 0.5f);
            GlStateManager.rotate(-f22, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -0.3125f, -0.4375f);
            this.model.signStick.showModel = false;
        }
        if (p_192841_9_ >= 0) {
            this.bindTexture(DESTROY_STAGES[p_192841_9_]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 2.0f, 1.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(SIGN_TEXTURE);
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.6666667f, -0.6666667f, -0.6666667f);
        this.model.renderSign();
        GlStateManager.popMatrix();
        FontRenderer fontrenderer = this.getFontRenderer();
        float f3 = 0.010416667f;
        GlStateManager.translate(0.0f, 0.33333334f, 0.046666667f);
        GlStateManager.scale(0.010416667f, -0.010416667f, 0.010416667f);
        GlStateManager.glNormal3f(0.0f, 0.0f, -0.010416667f);
        GlStateManager.depthMask(false);
        int i2 = 0;
        if (Config.isCustomColors()) {
            i2 = CustomColors.getSignTextColor(i2);
        }
        if (p_192841_9_ < 0) {
            for (int j2 = 0; j2 < p_192841_1_.signText.length; ++j2) {
                String s2;
                if (p_192841_1_.signText[j2] == null) continue;
                ITextComponent itextcomponent = p_192841_1_.signText[j2];
                List<ITextComponent> list = GuiUtilRenderComponents.splitText(itextcomponent, 90, fontrenderer, false, true);
                String string = s2 = list != null && !list.isEmpty() ? list.get(0).getFormattedText() : "";
                if (j2 == p_192841_1_.lineBeingEdited) {
                    s2 = "> " + s2 + " <";
                    fontrenderer.drawString(s2, -fontrenderer.getStringWidth(s2) / 2, j2 * 10 - p_192841_1_.signText.length * 5, i2);
                    continue;
                }
                fontrenderer.drawString(s2, -fontrenderer.getStringWidth(s2) / 2, j2 * 10 - p_192841_1_.signText.length * 5, i2);
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        if (p_192841_9_ >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}

