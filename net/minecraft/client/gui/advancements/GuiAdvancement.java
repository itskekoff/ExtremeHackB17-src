package net.minecraft.client.gui.advancements;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.advancements.AdvancementState;
import net.minecraft.client.gui.advancements.GuiAdvancementTab;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiAdvancement
extends Gui {
    private static final ResourceLocation field_191827_a = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final Pattern field_192996_f = Pattern.compile("(.+) \\S+");
    private final GuiAdvancementTab field_191828_f;
    private final Advancement field_191829_g;
    private final DisplayInfo field_191830_h;
    private final String field_191831_i;
    private final int field_191832_j;
    private final List<String> field_192997_l;
    private final Minecraft field_191833_k;
    private GuiAdvancement field_191834_l;
    private final List<GuiAdvancement> field_191835_m = Lists.newArrayList();
    private AdvancementProgress field_191836_n;
    private final int field_191837_o;
    private final int field_191826_p;

    public GuiAdvancement(GuiAdvancementTab p_i47385_1_, Minecraft p_i47385_2_, Advancement p_i47385_3_, DisplayInfo p_i47385_4_) {
        this.field_191828_f = p_i47385_1_;
        this.field_191829_g = p_i47385_3_;
        this.field_191830_h = p_i47385_4_;
        this.field_191833_k = p_i47385_2_;
        this.field_191831_i = p_i47385_2_.fontRendererObj.trimStringToWidth(p_i47385_4_.func_192297_a().getFormattedText(), 163);
        this.field_191837_o = MathHelper.floor(p_i47385_4_.func_192299_e() * 28.0f);
        this.field_191826_p = MathHelper.floor(p_i47385_4_.func_192296_f() * 27.0f);
        int i2 = p_i47385_3_.func_193124_g();
        int j2 = String.valueOf(i2).length();
        int k2 = i2 > 1 ? p_i47385_2_.fontRendererObj.getStringWidth("  ") + p_i47385_2_.fontRendererObj.getStringWidth("0") * j2 * 2 + p_i47385_2_.fontRendererObj.getStringWidth("/") : 0;
        int l2 = 29 + p_i47385_2_.fontRendererObj.getStringWidth(this.field_191831_i) + k2;
        String s2 = p_i47385_4_.func_193222_b().getFormattedText();
        this.field_192997_l = this.func_192995_a(s2, l2);
        for (String s1 : this.field_192997_l) {
            l2 = Math.max(l2, p_i47385_2_.fontRendererObj.getStringWidth(s1));
        }
        this.field_191832_j = l2 + 3 + 5;
    }

    private List<String> func_192995_a(String p_192995_1_, int p_192995_2_) {
        int j2;
        if (p_192995_1_.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> list = this.field_191833_k.fontRendererObj.listFormattedStringToWidth(p_192995_1_, p_192995_2_);
        if (list.size() < 2) {
            return list;
        }
        String s2 = list.get(0);
        String s1 = list.get(1);
        int i2 = this.field_191833_k.fontRendererObj.getStringWidth(String.valueOf(s2) + ' ' + s1.split(" ")[0]);
        if (i2 - p_192995_2_ <= 10) {
            return this.field_191833_k.fontRendererObj.listFormattedStringToWidth(p_192995_1_, i2);
        }
        Matcher matcher = field_192996_f.matcher(s2);
        if (matcher.matches() && p_192995_2_ - (j2 = this.field_191833_k.fontRendererObj.getStringWidth(matcher.group(1))) <= 10) {
            return this.field_191833_k.fontRendererObj.listFormattedStringToWidth(p_192995_1_, j2);
        }
        return list;
    }

    @Nullable
    private GuiAdvancement func_191818_a(Advancement p_191818_1_) {
        while ((p_191818_1_ = p_191818_1_.func_192070_b()) != null && p_191818_1_.func_192068_c() == null) {
        }
        if (p_191818_1_ != null && p_191818_1_.func_192068_c() != null) {
            return this.field_191828_f.func_191794_b(p_191818_1_);
        }
        return null;
    }

    public void func_191819_a(int p_191819_1_, int p_191819_2_, boolean p_191819_3_) {
        if (this.field_191834_l != null) {
            int j1;
            int i2 = p_191819_1_ + this.field_191834_l.field_191837_o + 13;
            int j2 = p_191819_1_ + this.field_191834_l.field_191837_o + 26 + 4;
            int k2 = p_191819_2_ + this.field_191834_l.field_191826_p + 13;
            int l2 = p_191819_1_ + this.field_191837_o + 13;
            int i1 = p_191819_2_ + this.field_191826_p + 13;
            int n2 = j1 = p_191819_3_ ? -16777216 : -1;
            if (p_191819_3_) {
                this.drawHorizontalLine(j2, i2, k2 - 1, j1);
                this.drawHorizontalLine(j2 + 1, i2, k2, j1);
                this.drawHorizontalLine(j2, i2, k2 + 1, j1);
                this.drawHorizontalLine(l2, j2 - 1, i1 - 1, j1);
                this.drawHorizontalLine(l2, j2 - 1, i1, j1);
                this.drawHorizontalLine(l2, j2 - 1, i1 + 1, j1);
                this.drawVerticalLine(j2 - 1, i1, k2, j1);
                this.drawVerticalLine(j2 + 1, i1, k2, j1);
            } else {
                this.drawHorizontalLine(j2, i2, k2, j1);
                this.drawHorizontalLine(l2, j2, i1, j1);
                this.drawVerticalLine(j2, i1, k2, j1);
            }
        }
        for (GuiAdvancement guiadvancement : this.field_191835_m) {
            guiadvancement.func_191819_a(p_191819_1_, p_191819_2_, p_191819_3_);
        }
    }

    public void func_191817_b(int p_191817_1_, int p_191817_2_) {
        if (!this.field_191830_h.func_193224_j() || this.field_191836_n != null && this.field_191836_n.func_192105_a()) {
            float f2 = this.field_191836_n == null ? 0.0f : this.field_191836_n.func_192103_c();
            AdvancementState advancementstate = f2 >= 1.0f ? AdvancementState.OBTAINED : AdvancementState.UNOBTAINED;
            this.field_191833_k.getTextureManager().bindTexture(field_191827_a);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableBlend();
            this.drawTexturedModalRect(p_191817_1_ + this.field_191837_o + 3, p_191817_2_ + this.field_191826_p, this.field_191830_h.func_192291_d().func_192309_b(), 128 + advancementstate.func_192667_a() * 26, 26, 26);
            RenderHelper.enableGUIStandardItemLighting();
            this.field_191833_k.getRenderItem().renderItemAndEffectIntoGUI(null, this.field_191830_h.func_192298_b(), p_191817_1_ + this.field_191837_o + 8, p_191817_2_ + this.field_191826_p + 5);
        }
        for (GuiAdvancement guiadvancement : this.field_191835_m) {
            guiadvancement.func_191817_b(p_191817_1_, p_191817_2_);
        }
    }

    public void func_191824_a(AdvancementProgress p_191824_1_) {
        this.field_191836_n = p_191824_1_;
    }

    public void func_191822_a(GuiAdvancement p_191822_1_) {
        this.field_191835_m.add(p_191822_1_);
    }

    public void func_191821_a(int p_191821_1_, int p_191821_2_, float p_191821_3_, int p_191821_4_, int p_191821_5_) {
        AdvancementState advancementstate2;
        AdvancementState advancementstate1;
        AdvancementState advancementstate;
        this.field_191828_f.func_193934_g();
        boolean flag = p_191821_4_ + p_191821_1_ + this.field_191837_o + this.field_191832_j + 26 >= GuiScreenAdvancements.width;
        String s2 = this.field_191836_n == null ? null : this.field_191836_n.func_193126_d();
        int i2 = s2 == null ? 0 : this.field_191833_k.fontRendererObj.getStringWidth(s2);
        boolean flag1 = 113 - p_191821_2_ - this.field_191826_p - 26 <= 6 + this.field_192997_l.size() * this.field_191833_k.fontRendererObj.FONT_HEIGHT;
        float f2 = this.field_191836_n == null ? 0.0f : this.field_191836_n.func_192103_c();
        int j2 = MathHelper.floor(f2 * (float)this.field_191832_j);
        if (f2 >= 1.0f) {
            j2 = this.field_191832_j / 2;
            advancementstate = AdvancementState.OBTAINED;
            advancementstate1 = AdvancementState.OBTAINED;
            advancementstate2 = AdvancementState.OBTAINED;
        } else if (j2 < 2) {
            j2 = this.field_191832_j / 2;
            advancementstate = AdvancementState.UNOBTAINED;
            advancementstate1 = AdvancementState.UNOBTAINED;
            advancementstate2 = AdvancementState.UNOBTAINED;
        } else if (j2 > this.field_191832_j - 2) {
            j2 = this.field_191832_j / 2;
            advancementstate = AdvancementState.OBTAINED;
            advancementstate1 = AdvancementState.OBTAINED;
            advancementstate2 = AdvancementState.UNOBTAINED;
        } else {
            advancementstate = AdvancementState.OBTAINED;
            advancementstate1 = AdvancementState.UNOBTAINED;
            advancementstate2 = AdvancementState.UNOBTAINED;
        }
        int k2 = this.field_191832_j - j2;
        this.field_191833_k.getTextureManager().bindTexture(field_191827_a);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        int l2 = p_191821_2_ + this.field_191826_p;
        int i1 = flag ? p_191821_1_ + this.field_191837_o - this.field_191832_j + 26 + 6 : p_191821_1_ + this.field_191837_o;
        int j1 = 32 + this.field_192997_l.size() * this.field_191833_k.fontRendererObj.FONT_HEIGHT;
        if (!this.field_192997_l.isEmpty()) {
            if (flag1) {
                this.func_192994_a(i1, l2 + 26 - j1, this.field_191832_j, j1, 10, 200, 26, 0, 52);
            } else {
                this.func_192994_a(i1, l2, this.field_191832_j, j1, 10, 200, 26, 0, 52);
            }
        }
        this.drawTexturedModalRect(i1, l2, 0, advancementstate.func_192667_a() * 26, j2, 26);
        this.drawTexturedModalRect(i1 + j2, l2, 200 - k2, advancementstate1.func_192667_a() * 26, k2, 26);
        this.drawTexturedModalRect(p_191821_1_ + this.field_191837_o + 3, p_191821_2_ + this.field_191826_p, this.field_191830_h.func_192291_d().func_192309_b(), 128 + advancementstate2.func_192667_a() * 26, 26, 26);
        if (flag) {
            this.field_191833_k.fontRendererObj.drawString(this.field_191831_i, i1 + 5, p_191821_2_ + this.field_191826_p + 9, -1, true);
            if (s2 != null) {
                this.field_191833_k.fontRendererObj.drawString(s2, p_191821_1_ + this.field_191837_o - i2, p_191821_2_ + this.field_191826_p + 9, -1, true);
            }
        } else {
            this.field_191833_k.fontRendererObj.drawString(this.field_191831_i, p_191821_1_ + this.field_191837_o + 32, p_191821_2_ + this.field_191826_p + 9, -1, true);
            if (s2 != null) {
                this.field_191833_k.fontRendererObj.drawString(s2, p_191821_1_ + this.field_191837_o + this.field_191832_j - i2 - 5, p_191821_2_ + this.field_191826_p + 9, -1, true);
            }
        }
        if (flag1) {
            for (int k1 = 0; k1 < this.field_192997_l.size(); ++k1) {
                this.field_191833_k.fontRendererObj.drawString(this.field_192997_l.get(k1), i1 + 5, l2 + 26 - j1 + 7 + k1 * this.field_191833_k.fontRendererObj.FONT_HEIGHT, -5592406, false);
            }
        } else {
            for (int l1 = 0; l1 < this.field_192997_l.size(); ++l1) {
                this.field_191833_k.fontRendererObj.drawString(this.field_192997_l.get(l1), i1 + 5, p_191821_2_ + this.field_191826_p + 9 + 17 + l1 * this.field_191833_k.fontRendererObj.FONT_HEIGHT, -5592406, false);
            }
        }
        RenderHelper.enableGUIStandardItemLighting();
        this.field_191833_k.getRenderItem().renderItemAndEffectIntoGUI(null, this.field_191830_h.func_192298_b(), p_191821_1_ + this.field_191837_o + 8, p_191821_2_ + this.field_191826_p + 5);
    }

    protected void func_192994_a(int p_192994_1_, int p_192994_2_, int p_192994_3_, int p_192994_4_, int p_192994_5_, int p_192994_6_, int p_192994_7_, int p_192994_8_, int p_192994_9_) {
        this.drawTexturedModalRect(p_192994_1_, p_192994_2_, p_192994_8_, p_192994_9_, p_192994_5_, p_192994_5_);
        this.func_192993_a(p_192994_1_ + p_192994_5_, p_192994_2_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_);
        this.drawTexturedModalRect(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_, p_192994_5_, p_192994_5_);
        this.drawTexturedModalRect(p_192994_1_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_8_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_5_, p_192994_5_);
        this.func_192993_a(p_192994_1_ + p_192994_5_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_);
        this.drawTexturedModalRect(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_5_, p_192994_5_);
        this.func_192993_a(p_192994_1_, p_192994_2_ + p_192994_5_, p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_, p_192994_9_ + p_192994_5_, p_192994_6_, p_192994_7_ - p_192994_5_ - p_192994_5_);
        this.func_192993_a(p_192994_1_ + p_192994_5_, p_192994_2_ + p_192994_5_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_ + p_192994_5_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_ - p_192994_5_ - p_192994_5_);
        this.func_192993_a(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_ + p_192994_5_, p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_ + p_192994_5_, p_192994_6_, p_192994_7_ - p_192994_5_ - p_192994_5_);
    }

    protected void func_192993_a(int p_192993_1_, int p_192993_2_, int p_192993_3_, int p_192993_4_, int p_192993_5_, int p_192993_6_, int p_192993_7_, int p_192993_8_) {
        for (int i2 = 0; i2 < p_192993_3_; i2 += p_192993_7_) {
            int j2 = p_192993_1_ + i2;
            int k2 = Math.min(p_192993_7_, p_192993_3_ - i2);
            for (int l2 = 0; l2 < p_192993_4_; l2 += p_192993_8_) {
                int i1 = p_192993_2_ + l2;
                int j1 = Math.min(p_192993_8_, p_192993_4_ - l2);
                this.drawTexturedModalRect(j2, i1, p_192993_5_, p_192993_6_, k2, j1);
            }
        }
    }

    public boolean func_191816_c(int p_191816_1_, int p_191816_2_, int p_191816_3_, int p_191816_4_) {
        if (!this.field_191830_h.func_193224_j() || this.field_191836_n != null && this.field_191836_n.func_192105_a()) {
            int i2 = p_191816_1_ + this.field_191837_o;
            int j2 = i2 + 26;
            int k2 = p_191816_2_ + this.field_191826_p;
            int l2 = k2 + 26;
            return p_191816_3_ >= i2 && p_191816_3_ <= j2 && p_191816_4_ >= k2 && p_191816_4_ <= l2;
        }
        return false;
    }

    public void func_191825_b() {
        if (this.field_191834_l == null && this.field_191829_g.func_192070_b() != null) {
            this.field_191834_l = this.func_191818_a(this.field_191829_g);
            if (this.field_191834_l != null) {
                this.field_191834_l.func_191822_a(this);
            }
        }
    }

    public int func_191820_c() {
        return this.field_191826_p;
    }

    public int func_191823_d() {
        return this.field_191837_o;
    }
}

