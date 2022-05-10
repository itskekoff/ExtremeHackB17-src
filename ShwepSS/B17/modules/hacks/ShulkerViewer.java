package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.RenderTTEvent;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class ShulkerViewer
extends Module {
    Minecraft mc = Minecraft.getMinecraft();

    public ShulkerViewer() {
        super("ShulkerViewer", "\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0432 \u0448\u0430\u043b\u043a\u0435\u0440 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0435", 0, Category.Visuals, true);
    }

    public void onEnabled() {
    }

    @Override
    public void onRender() {
        if (this.isEnabled()) {
            ScaledResolution sr2 = new ScaledResolution(this.mc);
            if (this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if (iblockstate.getMaterial() == Material.AIR) {
                    return;
                }
                if (block instanceof BlockShulkerBox) {
                    ItemStack itemstack = block.getItem(this.mc.world, blockpos, iblockstate);
                    NBTTagCompound nBTTagCompound = itemstack.getTagCompound();
                }
            }
        }
    }

    @EventTarget
    public void onRender(RenderTTEvent event) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound;
        if (this.mc.player == null) {
            return;
        }
        if (event.getStack().getItem() instanceof ItemShulkerBox && (tagCompound = event.getStack().getTagCompound()) != null && tagCompound.hasKey("BlockEntityTag", 10) && (blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) {
            event.setCancelled(true);
            NonNullList<ItemStack> nonnulllist = NonNullList.func_191197_a(27, ItemStack.field_190927_a);
            ItemStackHelper.func_191283_b(blockEntityTag, nonnulllist);
            GlStateManager.enableBlend();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int width = Math.max(144, Minecraft.getMinecraft().fontRendererObj.getStringWidth(event.getStack().getDisplayName()) + 3);
            int x1 = event.getX() + 12;
            int y1 = event.getY() - 12;
            int height = 57;
            Minecraft.getMinecraft().getRenderItem().zLevel = 300.0f;
            this.drawGradientRectP(x1 - 3, y1 - 4, x1 + width + 3, y1 - 3, -267386864, -267386864);
            this.drawGradientRectP(x1 - 3, y1 + 57 + 3, x1 + width + 3, y1 + 57 + 4, -267386864, -267386864);
            this.drawGradientRectP(x1 - 3, y1 - 3, x1 + width + 3, y1 + 57 + 3, -267386864, -267386864);
            this.drawGradientRectP(x1 - 4, y1 - 3, x1 - 3, y1 + 57 + 3, -267386864, -267386864);
            this.drawGradientRectP(x1 + width + 3, y1 - 3, x1 + width + 4, y1 + 57 + 3, -267386864, -267386864);
            this.drawGradientRectP(x1 - 3, y1 - 3 + 1, x1 - 3 + 1, y1 + 57 + 3 - 1, 0x505000FF, 1344798847);
            this.drawGradientRectP(x1 + width + 2, y1 - 3 + 1, x1 + width + 3, y1 + 57 + 3 - 1, 0x505000FF, 1344798847);
            this.drawGradientRectP(x1 - 3, y1 - 3, x1 + width + 3, y1 - 3 + 1, 0x505000FF, 0x505000FF);
            this.drawGradientRectP(x1 - 3, y1 + 57 + 2, x1 + width + 3, y1 + 57 + 3, 1344798847, 1344798847);
            Minecraft.getMinecraft().fontRendererObj.drawString(event.getStack().getDisplayName(), event.getX() + 12, event.getY() - 12, 0xFFFFFF);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            for (int i2 = 0; i2 < nonnulllist.size(); ++i2) {
                int iX = event.getX() + i2 % 9 * 16 + 11;
                int iY = event.getY() + i2 / 9 * 16 - 11 + 8;
                ItemStack itemStack = nonnulllist.get(i2);
                Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemStack, iX, iY);
                Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, itemStack, iX, iY, null);
            }
            RenderHelper.disableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    private void drawGradientRectP(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f2 = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f22 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 300.0).color(f1, f22, f3, f2).endVertex();
        bufferbuilder.pos(left, top, 300.0).color(f1, f22, f3, f2).endVertex();
        bufferbuilder.pos(left, bottom, 300.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, 300.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}

