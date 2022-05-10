package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import me.themchaxor.font.SlickRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.CustomColors;
import optifine.FontUtils;
import optifine.GlBlendState;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontRenderer
implements IResourceManagerReloadListener {
    private static final ResourceLocation[] UNICODE_PAGE_LOCATIONS = new ResourceLocation[256];
    private final int[] charWidth = new int[256];
    public int FONT_HEIGHT = 9;
    public Random fontRandom = new Random();
    private final byte[] glyphWidth = new byte[65536];
    public final int[] colorCode = new int[32];
    private ResourceLocation locationFontTexture;
    private final TextureManager renderEngine;
    protected float posX;
    protected float posY;
    private boolean unicodeFlag;
    protected boolean bidiFlag;
    protected float red;
    protected float blue;
    protected float green;
    protected float alpha;
    private int textColor;
    private boolean randomStyle;
    private boolean boldStyle;
    private boolean italicStyle;
    private boolean underlineStyle;
    private boolean strikethroughStyle;
    public GameSettings gameSettings;
    public ResourceLocation locationFontTextureBase;
    public boolean enabled = true;
    public float offsetBold = 1.0f;
    private float[] charWidthFloat = new float[256];
    private boolean blend = false;
    private GlBlendState oldBlendState = new GlBlendState();
    private static UnicodeFont primaryFont;
    private static UnicodeFont secondaryFont;
    private static UnicodeFont guiFont;
    private static UnicodeFont sickTitle;

    public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
        this.gameSettings = gameSettingsIn;
        this.locationFontTextureBase = location;
        this.locationFontTexture = location;
        this.renderEngine = textureManagerIn;
        this.unicodeFlag = unicode;
        this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
        this.bindTexture(this.locationFontTexture);
        for (int i2 = 0; i2 < 32; ++i2) {
            int j2 = (i2 >> 3 & 1) * 85;
            int k2 = (i2 >> 2 & 1) * 170 + j2;
            int l2 = (i2 >> 1 & 1) * 170 + j2;
            int i1 = (i2 >> 0 & 1) * 170 + j2;
            if (i2 == 6) {
                k2 += 85;
            }
            if (gameSettingsIn.anaglyph) {
                int j1 = (k2 * 30 + l2 * 59 + i1 * 11) / 100;
                int k1 = (k2 * 30 + l2 * 70) / 100;
                int l1 = (k2 * 30 + i1 * 70) / 100;
                k2 = j1;
                l2 = k1;
                i1 = l1;
            }
            if (i2 >= 16) {
                k2 /= 4;
                l2 /= 4;
                i1 /= 4;
            }
            this.colorCode[i2] = (k2 & 0xFF) << 16 | (l2 & 0xFF) << 8 | i1 & 0xFF;
        }
        this.readGlyphSizes();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
        for (int i2 = 0; i2 < UNICODE_PAGE_LOCATIONS.length; ++i2) {
            FontRenderer.UNICODE_PAGE_LOCATIONS[i2] = null;
        }
        this.readFontTexture();
        this.readGlyphSizes();
    }

    private void readFontTexture() {
        BufferedImage bufferedimage;
        IResource iresource = null;
        try {
            try {
                iresource = this.getResource(this.locationFontTexture);
                bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            }
            catch (IOException ioexception) {
                throw new RuntimeException(ioexception);
            }
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(iresource);
            throw throwable;
        }
        IOUtils.closeQuietly((Closeable)iresource);
        Properties props = FontUtils.readFontProperties(this.locationFontTexture);
        this.blend = FontUtils.readBoolean(props, "blend", false);
        int imgWidth = bufferedimage.getWidth();
        int imgHeight = bufferedimage.getHeight();
        int charW = imgWidth / 16;
        int charH = imgHeight / 16;
        float kx2 = (float)imgWidth / 128.0f;
        float boldScaleFactor = Config.limit(kx2, 1.0f, 2.0f);
        this.offsetBold = 1.0f / boldScaleFactor;
        float offsetBoldConfig = FontUtils.readFloat(props, "offsetBold", -1.0f);
        if (offsetBoldConfig >= 0.0f) {
            this.offsetBold = offsetBoldConfig;
        }
        int[] aint = new int[imgWidth * imgHeight];
        bufferedimage.getRGB(0, 0, imgWidth, imgHeight, aint, 0, imgWidth);
        for (int i1 = 0; i1 < 256; ++i1) {
            int j1 = i1 % 16;
            int k1 = i1 / 16;
            int l1 = 0;
            for (l1 = charW - 1; l1 >= 0; --l1) {
                int i2 = j1 * charW + l1;
                boolean flag = true;
                for (int j2 = 0; j2 < charH && flag; ++j2) {
                    int k2 = (k1 * charH + j2) * imgWidth;
                    int l2 = aint[i2 + k2];
                    int i3 = l2 >> 24 & 0xFF;
                    if (i3 <= 16) continue;
                    flag = false;
                }
                if (!flag) break;
            }
            if (i1 == 65) {
                // empty if block
            }
            if (i1 == 32) {
                l1 = charW <= 8 ? (int)(2.0f * kx2) : (int)(1.5f * kx2);
            }
            this.charWidthFloat[i1] = (float)(l1 + 1) / kx2 + 1.0f;
        }
        FontUtils.readCustomCharWidths(props, this.charWidthFloat);
        for (int j3 = 0; j3 < this.charWidth.length; ++j3) {
            this.charWidth[j3] = Math.round(this.charWidthFloat[j3]);
        }
    }

    private void readGlyphSizes() {
        IResource iresource = null;
        try {
            try {
                iresource = this.getResource(new ResourceLocation("font/glyph_sizes.bin"));
                iresource.getInputStream().read(this.glyphWidth);
            }
            catch (IOException ioexception) {
                throw new RuntimeException(ioexception);
            }
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(iresource);
            throw throwable;
        }
        IOUtils.closeQuietly((Closeable)iresource);
    }

    private float renderChar(char ch2, boolean italic) {
        if (ch2 != ' ' && ch2 != '\u00a0') {
            int i2 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch2);
            return i2 != -1 && !this.unicodeFlag ? this.renderDefaultChar(i2, italic) : this.renderUnicodeChar(ch2, italic);
        }
        return !this.unicodeFlag ? this.charWidthFloat[ch2] : 4.0f;
    }

    private float renderDefaultChar(int ch2, boolean italic) {
        int i2 = ch2 % 16 * 8;
        int j2 = ch2 / 16 * 8;
        boolean k2 = italic;
        this.bindTexture(this.locationFontTexture);
        float f2 = this.charWidthFloat[ch2];
        float f1 = 7.99f;
        GlStateManager.glBegin(5);
        GlStateManager.glTexCoord2f((float)i2 / 128.0f, (float)j2 / 128.0f);
        GlStateManager.glVertex3f(this.posX + (float)k2, this.posY, 0.0f);
        GlStateManager.glTexCoord2f((float)i2 / 128.0f, ((float)j2 + 7.99f) / 128.0f);
        GlStateManager.glVertex3f(this.posX - (float)k2, this.posY + 7.99f, 0.0f);
        GlStateManager.glTexCoord2f(((float)i2 + f1 - 1.0f) / 128.0f, (float)j2 / 128.0f);
        GlStateManager.glVertex3f(this.posX + f1 - 1.0f + (float)k2, this.posY, 0.0f);
        GlStateManager.glTexCoord2f(((float)i2 + f1 - 1.0f) / 128.0f, ((float)j2 + 7.99f) / 128.0f);
        GlStateManager.glVertex3f(this.posX + f1 - 1.0f - (float)k2, this.posY + 7.99f, 0.0f);
        GlStateManager.glEnd();
        return f2;
    }

    private ResourceLocation getUnicodePageLocation(int page) {
        if (UNICODE_PAGE_LOCATIONS[page] == null) {
            FontRenderer.UNICODE_PAGE_LOCATIONS[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page));
            FontRenderer.UNICODE_PAGE_LOCATIONS[page] = FontUtils.getHdFontLocation(UNICODE_PAGE_LOCATIONS[page]);
        }
        return UNICODE_PAGE_LOCATIONS[page];
    }

    private void loadGlyphTexture(int page) {
        this.bindTexture(this.getUnicodePageLocation(page));
    }

    private float renderUnicodeChar(char ch2, boolean italic) {
        int i2 = this.glyphWidth[ch2] & 0xFF;
        if (i2 == 0) {
            return 0.0f;
        }
        int j2 = ch2 / 256;
        this.loadGlyphTexture(j2);
        int k2 = i2 >>> 4;
        int l2 = i2 & 0xF;
        float f2 = k2;
        float f1 = l2 + 1;
        float f22 = (float)(ch2 % 16 * 16) + f2;
        float f3 = (ch2 & 0xFF) / 16 * 16;
        float f4 = f1 - f2 - 0.02f;
        float f5 = italic ? 1.0f : 0.0f;
        GlStateManager.glBegin(5);
        GlStateManager.glTexCoord2f(f22 / 256.0f, f3 / 256.0f);
        GlStateManager.glVertex3f(this.posX + f5, this.posY, 0.0f);
        GlStateManager.glTexCoord2f(f22 / 256.0f, (f3 + 15.98f) / 256.0f);
        GlStateManager.glVertex3f(this.posX - f5, this.posY + 7.99f, 0.0f);
        GlStateManager.glTexCoord2f((f22 + f4) / 256.0f, f3 / 256.0f);
        GlStateManager.glVertex3f(this.posX + f4 / 2.0f + f5, this.posY, 0.0f);
        GlStateManager.glTexCoord2f((f22 + f4) / 256.0f, (f3 + 15.98f) / 256.0f);
        GlStateManager.glVertex3f(this.posX + f4 / 2.0f - f5, this.posY + 7.99f, 0.0f);
        GlStateManager.glEnd();
        return (f1 - f2) / 2.0f + 1.0f;
    }

    public int drawStringWithShadow(String text, float x2, float y2, int color) {
        return this.drawString(text, x2, y2, color, true);
    }

    public int drawString(String text, int x2, int y2, int color) {
        return !this.enabled ? 0 : this.drawString(text, x2, y2, color, false);
    }

    public int drawString(String text, float x2, float y2, int color, boolean dropShadow) {
        int i2;
        this.enableAlpha();
        if (this.blend) {
            GlStateManager.getBlendState(this.oldBlendState);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        this.resetStyles();
        if (dropShadow) {
            i2 = this.renderString(text, x2 + 1.0f, y2 + 1.0f, color, true);
            i2 = Math.max(i2, this.renderString(text, x2, y2, color, false));
        } else {
            i2 = this.renderString(text, x2, y2, color, false);
        }
        if (this.blend) {
            GlStateManager.setBlendState(this.oldBlendState);
        }
        return i2;
    }

    protected String bidiReorder(String text) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException var31) {
            return text;
        }
    }

    private void resetStyles() {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    private void renderStringAtPos(String text, boolean shadow) {
        for (int i2 = 0; i2 < text.length(); ++i2) {
            boolean flag;
            char c0 = text.charAt(i2);
            if (c0 == '\u00a7' && i2 + 1 < text.length()) {
                int l2 = "0123456789abcdefklmnor".indexOf(String.valueOf(text.charAt(i2 + 1)).toLowerCase(Locale.ROOT).charAt(0));
                if (l2 < 16) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    if (l2 < 0 || l2 > 15) {
                        l2 = 15;
                    }
                    if (shadow) {
                        l2 += 16;
                    }
                    int i1 = this.colorCode[l2];
                    if (Config.isCustomColors()) {
                        i1 = CustomColors.getTextColor(l2, i1);
                    }
                    this.textColor = i1;
                    this.setColor((float)(i1 >> 16) / 255.0f, (float)(i1 >> 8 & 0xFF) / 255.0f, (float)(i1 & 0xFF) / 255.0f, this.alpha);
                } else if (l2 == 16) {
                    this.randomStyle = true;
                } else if (l2 == 17) {
                    this.boldStyle = true;
                } else if (l2 == 18) {
                    this.strikethroughStyle = true;
                } else if (l2 == 19) {
                    this.underlineStyle = true;
                } else if (l2 == 20) {
                    this.italicStyle = true;
                } else if (l2 == 21) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    this.setColor(this.red, this.blue, this.green, this.alpha);
                }
                ++i2;
                continue;
            }
            int j2 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c0);
            if (this.randomStyle && j2 != -1) {
                char c1;
                int k2 = this.getCharWidth(c0);
                while (k2 != this.getCharWidth(c1 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".charAt(j2 = this.fontRandom.nextInt("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".length())))) {
                }
                c0 = c1;
            }
            float f1 = j2 != -1 && !this.unicodeFlag ? this.offsetBold : 0.5f;
            boolean bl2 = flag = (c0 == '\u0000' || j2 == -1 || this.unicodeFlag) && shadow;
            if (flag) {
                this.posX -= f1;
                this.posY -= f1;
            }
            float f2 = this.renderChar(c0, this.italicStyle);
            if (flag) {
                this.posX += f1;
                this.posY += f1;
            }
            if (this.boldStyle) {
                this.posX += f1;
                if (flag) {
                    this.posX -= f1;
                    this.posY -= f1;
                }
                this.renderChar(c0, this.italicStyle);
                this.posX -= f1;
                if (flag) {
                    this.posX += f1;
                    this.posY += f1;
                }
                f2 += f1;
            }
            this.doDraw(f2);
        }
    }

    protected void doDraw(float p_doDraw_1_) {
        if (this.strikethroughStyle) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.disableTexture2D();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
            bufferbuilder.pos(this.posX, this.posY + (float)(this.FONT_HEIGHT / 2), 0.0).endVertex();
            bufferbuilder.pos(this.posX + p_doDraw_1_, this.posY + (float)(this.FONT_HEIGHT / 2), 0.0).endVertex();
            bufferbuilder.pos(this.posX + p_doDraw_1_, this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0f, 0.0).endVertex();
            bufferbuilder.pos(this.posX, this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0f, 0.0).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }
        if (this.underlineStyle) {
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
            GlStateManager.disableTexture2D();
            bufferbuilder1.begin(7, DefaultVertexFormats.POSITION);
            int i2 = this.underlineStyle ? -1 : 0;
            bufferbuilder1.pos(this.posX + (float)i2, this.posY + (float)this.FONT_HEIGHT, 0.0).endVertex();
            bufferbuilder1.pos(this.posX + p_doDraw_1_, this.posY + (float)this.FONT_HEIGHT, 0.0).endVertex();
            bufferbuilder1.pos(this.posX + p_doDraw_1_, this.posY + (float)this.FONT_HEIGHT - 1.0f, 0.0).endVertex();
            bufferbuilder1.pos(this.posX + (float)i2, this.posY + (float)this.FONT_HEIGHT - 1.0f, 0.0).endVertex();
            tessellator1.draw();
            GlStateManager.enableTexture2D();
        }
        this.posX += p_doDraw_1_;
    }

    private int renderStringAligned(String text, int x2, int y2, int width, int color, boolean dropShadow) {
        if (this.bidiFlag) {
            int i2 = this.getStringWidth(this.bidiReorder(text));
            x2 = x2 + width - i2;
        }
        return this.renderString(text, x2, y2, color, dropShadow);
    }

    private int renderString(String text, float x2, float y2, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        }
        if (this.bidiFlag) {
            text = this.bidiReorder(text);
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }
        if (dropShadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        this.red = (float)(color >> 16 & 0xFF) / 255.0f;
        this.blue = (float)(color >> 8 & 0xFF) / 255.0f;
        this.green = (float)(color & 0xFF) / 255.0f;
        this.alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        this.setColor(this.red, this.blue, this.green, this.alpha);
        this.posX = x2;
        this.posY = y2;
        this.renderStringAtPos(text, dropShadow);
        return (int)this.posX;
    }

    public void drawSlick(String par1Str, int par2, int par3, int par4) {
        if (guiFont == null) {
            guiFont = new UnicodeFont(new Font("Verdana", 1, 24));
            guiFont.addAsciiGlyphs();
            guiFont.getEffects().add(new ColorEffect(Color.WHITE));
            try {
                guiFont.loadGlyphs();
            }
            catch (SlickException e2) {
                e2.printStackTrace();
            }
        }
        try {
            SlickRenderer.drawString(guiFont, par2, par3 - 2, par1Str, par4);
        }
        catch (Exception e2) {
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            e2.printStackTrace();
        }
    }

    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        float f2 = 0.0f;
        boolean flag = false;
        for (int i2 = 0; i2 < text.length(); ++i2) {
            char c0 = text.charAt(i2);
            float f1 = this.getCharWidthFloat(c0);
            if (f1 < 0.0f && i2 < text.length() - 1) {
                if ((c0 = text.charAt(++i2)) != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag = false;
                    }
                } else {
                    flag = true;
                }
                f1 = 0.0f;
            }
            f2 += f1;
            if (!flag || !(f1 > 0.0f)) continue;
            f2 += this.unicodeFlag ? 1.0f : this.offsetBold;
        }
        return Math.round(f2);
    }

    public int getCharWidth(char character) {
        return Math.round(this.getCharWidthFloat(character));
    }

    private float getCharWidthFloat(char p_getCharWidthFloat_1_) {
        if (p_getCharWidthFloat_1_ == '\u00a7') {
            return -1.0f;
        }
        if (p_getCharWidthFloat_1_ != ' ' && p_getCharWidthFloat_1_ != '\u00a0') {
            int i2 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(p_getCharWidthFloat_1_);
            if (p_getCharWidthFloat_1_ > '\u0000' && i2 != -1 && !this.unicodeFlag) {
                return this.charWidthFloat[i2];
            }
            if (this.glyphWidth[p_getCharWidthFloat_1_] != 0) {
                int j2 = this.glyphWidth[p_getCharWidthFloat_1_] & 0xFF;
                int k2 = j2 >>> 4;
                int l2 = j2 & 0xF;
                return (++l2 - k2) / 2 + 1;
            }
            return 0.0f;
        }
        return this.charWidthFloat[32];
    }

    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    public String trimStringToWidth(String text, int width, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();
        float f2 = 0.0f;
        int i2 = reverse ? text.length() - 1 : 0;
        int j2 = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;
        for (int k2 = i2; k2 >= 0 && k2 < text.length() && f2 < (float)width; k2 += j2) {
            char c0 = text.charAt(k2);
            float f1 = this.getCharWidthFloat(c0);
            if (flag) {
                flag = false;
                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag1 = false;
                    }
                } else {
                    flag1 = true;
                }
            } else if (f1 < 0.0f) {
                flag = true;
            } else {
                f2 += f1;
                if (flag1) {
                    f2 += 1.0f;
                }
            }
            if (f2 > (float)width) break;
            if (reverse) {
                stringbuilder.insert(0, c0);
                continue;
            }
            stringbuilder.append(c0);
        }
        return stringbuilder.toString();
    }

    private String trimStringNewline(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    public void drawSplitString(String str, int x2, int y2, int wrapWidth, int textColor) {
        if (this.blend) {
            GlStateManager.getBlendState(this.oldBlendState);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        this.resetStyles();
        this.textColor = textColor;
        str = this.trimStringNewline(str);
        this.renderSplitString(str, x2, y2, wrapWidth, false);
        if (this.blend) {
            GlStateManager.setBlendState(this.oldBlendState);
        }
    }

    private void renderSplitString(String str, int x2, int y2, int wrapWidth, boolean addShadow) {
        for (String s2 : this.listFormattedStringToWidth(str, wrapWidth)) {
            this.renderStringAligned(s2, x2, y2, wrapWidth, this.textColor, addShadow);
            y2 += this.FONT_HEIGHT;
        }
    }

    public int splitStringWidth(String str, int maxLength) {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
    }

    public void setUnicodeFlag(boolean unicodeFlagIn) {
        this.unicodeFlag = unicodeFlagIn;
    }

    public boolean getUnicodeFlag() {
        return this.unicodeFlag;
    }

    public void setBidiFlag(boolean bidiFlagIn) {
        this.bidiFlag = bidiFlagIn;
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    String wrapFormattedStringToWidth(String str, int wrapWidth) {
        if (str.length() <= 1) {
            return str;
        }
        int i2 = this.sizeStringToWidth(str, wrapWidth);
        if (str.length() <= i2) {
            return str;
        }
        String s2 = str.substring(0, i2);
        char c0 = str.charAt(i2);
        boolean flag = c0 == ' ' || c0 == '\n';
        String s1 = String.valueOf(FontRenderer.getFormatFromString(s2)) + str.substring(i2 + (flag ? 1 : 0));
        return String.valueOf(s2) + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
    }

    private int sizeStringToWidth(String str, int wrapWidth) {
        int j2;
        int i2 = str.length();
        float f2 = 0.0f;
        int k2 = -1;
        boolean flag = false;
        for (j2 = 0; j2 < i2; ++j2) {
            char c0 = str.charAt(j2);
            switch (c0) {
                case '\n': {
                    --j2;
                    break;
                }
                case ' ': {
                    k2 = j2;
                }
                default: {
                    f2 += this.getCharWidthFloat(c0);
                    if (!flag) break;
                    f2 += 1.0f;
                    break;
                }
                case '\u00a7': {
                    char c1;
                    if (j2 >= i2 - 1) break;
                    if ((c1 = str.charAt(++j2)) != 'l' && c1 != 'L') {
                        if (c1 != 'r' && c1 != 'R' && !FontRenderer.isFormatColor(c1)) break;
                        flag = false;
                        break;
                    }
                    flag = true;
                }
            }
            if (c0 == '\n') {
                k2 = ++j2;
                break;
            }
            if (Math.round(f2) > wrapWidth) break;
        }
        return j2 != i2 && k2 != -1 && k2 < j2 ? k2 : j2;
    }

    private static boolean isFormatColor(char colorChar) {
        return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
    }

    private static boolean isFormatSpecial(char formatChar) {
        return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
    }

    public static String getFormatFromString(String text) {
        String s2 = "";
        int i2 = -1;
        int j2 = text.length();
        while ((i2 = text.indexOf(167, i2 + 1)) != -1) {
            if (i2 >= j2 - 1) continue;
            char c0 = text.charAt(i2 + 1);
            if (FontRenderer.isFormatColor(c0)) {
                s2 = "\u00a7" + c0;
                continue;
            }
            if (!FontRenderer.isFormatSpecial(c0)) continue;
            s2 = String.valueOf(s2) + "\u00a7" + c0;
        }
        return s2;
    }

    public boolean getBidiFlag() {
        return this.bidiFlag;
    }

    public int getColorCode(char character) {
        int i2 = "0123456789abcdef".indexOf(character);
        if (i2 >= 0 && i2 < this.colorCode.length) {
            int j2 = this.colorCode[i2];
            if (Config.isCustomColors()) {
                j2 = CustomColors.getTextColor(i2, j2);
            }
            return j2;
        }
        return 0xFFFFFF;
    }

    protected void setColor(float p_setColor_1_, float p_setColor_2_, float p_setColor_3_, float p_setColor_4_) {
        GlStateManager.color(p_setColor_1_, p_setColor_2_, p_setColor_3_, p_setColor_4_);
    }

    protected void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    protected void bindTexture(ResourceLocation p_bindTexture_1_) {
        this.renderEngine.bindTexture(p_bindTexture_1_);
    }

    public static int getSlickWidth(String s2) {
        if (s2 == null || s2.length() <= 0) {
            return 0;
        }
        if (primaryFont == null) {
            primaryFont = new UnicodeFont(new Font("Verdana", 1, 24));
            primaryFont.addAsciiGlyphs();
            primaryFont.getEffects().add(new ColorEffect(Color.WHITE));
            try {
                primaryFont.loadGlyphs();
            }
            catch (SlickException e2) {
                GL11.glScalef(2.0f, 2.0f, 2.0f);
                e2.printStackTrace();
            }
        }
        return primaryFont.getWidth(s2) / 2 + 2;
    }

    public static int getSlickLargeWidth(String s2) {
        if (s2 == null || s2.length() <= 0) {
            return 0;
        }
        if (sickTitle == null) {
            sickTitle = new UnicodeFont(new Font("Verdana", 1, 40));
            sickTitle.addAsciiGlyphs();
            sickTitle.getEffects().add(new ColorEffect(Color.WHITE));
            try {
                sickTitle.loadGlyphs();
            }
            catch (SlickException e2) {
                GL11.glScalef(2.0f, 2.0f, 2.0f);
                e2.printStackTrace();
            }
        }
        return sickTitle.getWidth(s2) / 2 + 2;
    }

    public void drawSlickLarge(String par1Str, int par2, int par3, int par4) {
        if (sickTitle == null) {
            sickTitle = new UnicodeFont(new Font("Verdana", 1, 40));
            sickTitle.addAsciiGlyphs();
            sickTitle.getEffects().add(new ColorEffect(Color.WHITE));
            try {
                sickTitle.loadGlyphs();
            }
            catch (SlickException e2) {
                e2.printStackTrace();
            }
        }
        try {
            SlickRenderer.drawString(sickTitle, par2, par3 - 2, par1Str, par4);
        }
        catch (Exception e2) {
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            e2.printStackTrace();
        }
    }

    protected IResource getResource(ResourceLocation p_getResource_1_) throws IOException {
        return Minecraft.getMinecraft().getResourceManager().getResource(p_getResource_1_);
    }

    public void drawCenteredSlick(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5) {
        this.renderEngine.bindTexture(this.locationFontTexture);
        par1FontRenderer.drawSlick(par2Str, par3 - FontRenderer.getSlickWidth(par2Str) / 2, par4, par5);
    }

    public void drawCenteredSlickTitle(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5) {
        this.renderEngine.bindTexture(this.locationFontTexture);
        par1FontRenderer.drawSlickLarge(par2Str, par3 - FontRenderer.getSlickLargeWidth(par2Str) / 2, par4, par5);
    }
}

