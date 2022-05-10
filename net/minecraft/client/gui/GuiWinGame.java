package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWinGame
extends GuiScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation field_194401_g = new ResourceLocation("textures/gui/title/edition.png");
    private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
    private final boolean field_193980_h;
    private final Runnable field_193981_i;
    private float time;
    private List<String> lines;
    private int totalScrollLength;
    private float scrollSpeed = 0.5f;

    public GuiWinGame(boolean p_i47590_1_, Runnable p_i47590_2_) {
        this.field_193980_h = p_i47590_1_;
        this.field_193981_i = p_i47590_2_;
        if (!p_i47590_1_) {
            this.scrollSpeed = 0.75f;
        }
    }

    @Override
    public void updateScreen() {
        this.mc.getMusicTicker().update();
        this.mc.getSoundHandler().update();
        float f2 = (float)(this.totalScrollLength + height + height + 24) / this.scrollSpeed;
        if (this.time > f2) {
            this.sendRespawnPacket();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.sendRespawnPacket();
        }
    }

    private void sendRespawnPacket() {
        this.field_193981_i.run();
        this.mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void initGui() {
        block11: {
            if (this.lines == null) {
                this.lines = Lists.newArrayList();
                IResource iresource = null;
                try {
                    try {
                        String s4;
                        String s2 = "" + (Object)((Object)TextFormatting.WHITE) + (Object)((Object)TextFormatting.OBFUSCATED) + (Object)((Object)TextFormatting.GREEN) + (Object)((Object)TextFormatting.AQUA);
                        int i2 = 274;
                        if (this.field_193980_h) {
                            String s1;
                            iresource = this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
                            InputStream inputstream = iresource.getInputStream();
                            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                            Random random = new Random(8124371L);
                            while ((s1 = bufferedreader.readLine()) != null) {
                                s1 = s1.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                                while (s1.contains(s2)) {
                                    int j2 = s1.indexOf(s2);
                                    String s22 = s1.substring(0, j2);
                                    String s3 = s1.substring(j2 + s2.length());
                                    s1 = String.valueOf(s22) + (Object)((Object)TextFormatting.WHITE) + (Object)((Object)TextFormatting.OBFUSCATED) + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + s3;
                                }
                                this.lines.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(s1, 274));
                                this.lines.add("");
                            }
                            inputstream.close();
                            for (int k2 = 0; k2 < 8; ++k2) {
                                this.lines.add("");
                            }
                        }
                        InputStream inputstream1 = this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
                        BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(inputstream1, StandardCharsets.UTF_8));
                        while ((s4 = bufferedreader1.readLine()) != null) {
                            s4 = s4.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                            s4 = s4.replaceAll("\t", "    ");
                            this.lines.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(s4, 274));
                            this.lines.add("");
                        }
                        inputstream1.close();
                        this.totalScrollLength = this.lines.size() * 12;
                    }
                    catch (Exception exception) {
                        LOGGER.error("Couldn't load credits", (Throwable)exception);
                        IOUtils.closeQuietly(iresource);
                        break block11;
                    }
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(iresource);
                    throw throwable;
                }
                IOUtils.closeQuietly((Closeable)iresource);
            }
        }
    }

    private void drawWinGameScreen(int p_146575_1_, int p_146575_2_, float p_146575_3_) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int i2 = width;
        float f2 = -this.time * 0.5f * this.scrollSpeed;
        float f1 = (float)height - this.time * 0.5f * this.scrollSpeed;
        float f22 = 0.015625f;
        float f3 = this.time * 0.02f;
        float f4 = (float)(this.totalScrollLength + height + height + 24) / this.scrollSpeed;
        float f5 = (f4 - 20.0f - this.time) * 0.005f;
        if (f5 < f3) {
            f3 = f5;
        }
        if (f3 > 1.0f) {
            f3 = 1.0f;
        }
        f3 *= f3;
        f3 = f3 * 96.0f / 255.0f;
        bufferbuilder.pos(0.0, height, zLevel).tex(0.0, f2 * 0.015625f).color(f3, f3, f3, 1.0f).endVertex();
        bufferbuilder.pos(i2, height, zLevel).tex((float)i2 * 0.015625f, f2 * 0.015625f).color(f3, f3, f3, 1.0f).endVertex();
        bufferbuilder.pos(i2, 0.0, zLevel).tex((float)i2 * 0.015625f, f1 * 0.015625f).color(f3, f3, f3, 1.0f).endVertex();
        bufferbuilder.pos(0.0, 0.0, zLevel).tex(0.0, f1 * 0.015625f).color(f3, f3, f3, 1.0f).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWinGameScreen(mouseX, mouseY, partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int i2 = 274;
        int j2 = width / 2 - 137;
        int k2 = height + 50;
        this.time += partialTicks;
        float f2 = -this.time * this.scrollSpeed;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, f2, 0.0f);
        this.mc.getTextureManager().bindTexture(MINECRAFT_LOGO);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlpha();
        this.drawTexturedModalRect(j2, k2, 0, 0, 155, 44);
        this.drawTexturedModalRect(j2 + 155, k2, 0, 45, 155, 44);
        this.mc.getTextureManager().bindTexture(field_194401_g);
        GuiWinGame.drawModalRectWithCustomSizedTexture(j2 + 88, k2 + 37, 0.0f, 0.0f, 98, 14, 128.0f, 16.0f);
        GlStateManager.disableAlpha();
        int l2 = k2 + 100;
        for (int i1 = 0; i1 < this.lines.size(); ++i1) {
            float f1;
            if (i1 == this.lines.size() - 1 && (f1 = (float)l2 + f2 - (float)(height / 2 - 6)) < 0.0f) {
                GlStateManager.translate(0.0f, -f1, 0.0f);
            }
            if ((float)l2 + f2 + 12.0f + 8.0f > 0.0f && (float)l2 + f2 < (float)height) {
                String s2 = this.lines.get(i1);
                if (s2.startsWith("[C]")) {
                    this.fontRendererObj.drawStringWithShadow(s2.substring(3), j2 + (274 - this.fontRendererObj.getStringWidth(s2.substring(3))) / 2, l2, 0xFFFFFF);
                } else {
                    this.fontRendererObj.fontRandom.setSeed((long)((float)((long)i1 * 4238972211L) + this.time / 4.0f));
                    this.fontRendererObj.drawStringWithShadow(s2, j2, l2, 0xFFFFFF);
                }
            }
            l2 += 12;
        }
        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        int j1 = width;
        int k1 = height;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0, k1, zLevel).tex(0.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(j1, k1, zLevel).tex(1.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(j1, 0.0, zLevel).tex(1.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(0.0, 0.0, zLevel).tex(0.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

