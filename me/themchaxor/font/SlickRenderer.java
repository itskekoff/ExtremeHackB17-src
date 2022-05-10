package me.themchaxor.font;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

public class SlickRenderer {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static void drawCenteredString(UnicodeFont font, float x2, float y2, String text, int color) {
        SlickRenderer.renderString(font, x2 - (float)(font.getWidth(text) / 2), y2, text, color);
    }

    public static void drawString(UnicodeFont font, float x2, float y2, String text, int color) {
        SlickRenderer.renderString(font, x2, y2, text, color);
    }

    public static int drawStringWithShadow(UnicodeFont font, float x2, float y2, String text, int color) {
        SlickRenderer.renderShade(font, x2 + 1.0f, y2 + 1.0f, text, color);
        return SlickRenderer.renderString(font, x2, y2, text, color);
    }

    public static int drawStringWithShadow2(UnicodeFont font, float x2, float y2, String text, int color) {
        return SlickRenderer.renderString(font, x2, y2, text, color);
    }

    public static int renderString(UnicodeFont font, float x2, float y2, String text, int color) {
        if (text == null) {
            return 0;
        }
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        SlickRenderer.disableDefaults();
        int width = 0;
        int i2 = 0;
        x2 *= 2.0f;
        y2 *= 2.0f;
        boolean sW = false;
        try {
            if (text.contains("\ufffd")) {
                String[] messages = text.split("\ufffd");
                if (!text.startsWith("\ufffd")) {
                    font.drawString(x2, y2, messages[0], new Color(SlickRenderer.getRedFromHex(color), SlickRenderer.getGreenFromHex(color), SlickRenderer.getBlueFromHex(color)));
                    width += font.getWidth(messages[0]);
                    sW = true;
                }
                while (i2 != messages.length) {
                    String str;
                    if (!(i2 == 0 && sW || (str = messages[i2]).length() == 0)) {
                        char identifier = 'z';
                        try {
                            identifier = str.charAt(0);
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        int index = "0123456789abcdefklmno".indexOf(("" + identifier).toLowerCase());
                        if (index != -1) {
                            int colorcode = SlickRenderer.mc.fontRendererObj.colorCode[index];
                            messages[i2] = SlickRenderer.removeCharAt(messages[i2], 0);
                            font.drawString(x2 + (float)width + 2.0f, y2 + 2.0f, messages[i2], Color.black);
                            if (index == "0123456789abcdefk".indexOf(("" + identifier).toLowerCase())) {
                                font.drawString(x2 + (float)width, y2, messages[i2], new Color(SlickRenderer.getRedFromHex(colorcode), SlickRenderer.getGreenFromHex(colorcode), SlickRenderer.getBlueFromHex(colorcode)));
                            } else {
                                font.drawString(x2 + (float)width, y2, messages[i2], new Color(SlickRenderer.getRedFromHex(color), SlickRenderer.getGreenFromHex(colorcode), SlickRenderer.getBlueFromHex(color)));
                            }
                            width += font.getWidth(messages[i2]);
                        }
                    }
                    ++i2;
                }
                SlickRenderer.enableDefaults();
                GL11.glScalef(2.0f, 2.0f, 2.0f);
                return width / 2;
            }
            font.drawString(x2, y2, text, new Color(SlickRenderer.getRedFromHex(color), SlickRenderer.getGreenFromHex(color), SlickRenderer.getBlueFromHex(color)));
            SlickRenderer.enableDefaults();
            GL11.glScalef(2.0f, 2.0f, 2.0f);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        return font.getWidth(text) / 2 + 4;
    }

    public static void renderShade(UnicodeFont font, float x2, float y2, String text, int color) {
        if (text == null) {
            return;
        }
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        SlickRenderer.disableDefaults();
        int width = 0;
        int i2 = 0;
        x2 *= 2.0f;
        y2 *= 2.0f;
        boolean sW = false;
        try {
            if (text.contains("\ufffd")) {
                String[] messages = text.split("\ufffd");
                if (!text.startsWith("\ufffd")) {
                    font.drawString(x2, y2, messages[0], Color.black);
                    width += font.getWidth(messages[0]);
                    sW = true;
                }
                while (i2 != messages.length) {
                    String str;
                    if (!(i2 == 0 && sW || (str = messages[i2]).length() == 0)) {
                        char identifier = 'z';
                        try {
                            identifier = str.charAt(0);
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        int index = "0123456789abcdefk".indexOf(("" + identifier).toLowerCase());
                        if (index == -1) break;
                        messages[i2] = SlickRenderer.removeCharAt(messages[i2], 0);
                        font.drawString(x2 + (float)width, y2, messages[i2], Color.black);
                        width += font.getWidth(messages[i2]);
                    }
                    ++i2;
                }
            } else {
                font.drawString(x2, y2, text, Color.black);
            }
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            SlickRenderer.enableDefaults();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static String removeCharAt(String s2, int pos) {
        return String.valueOf(String.valueOf(s2.substring(0, pos))) + s2.substring(pos + 1);
    }

    public static float getAlphaFromHex(int color) {
        return (float)(color >> 24 & 0xFF) / 255.0f;
    }

    public static float getRedFromHex(int color) {
        return (float)(color >> 16 & 0xFF) / 255.0f;
    }

    public static float getGreenFromHex(int color) {
        return (float)(color >> 8 & 0xFF) / 255.0f;
    }

    public static float getBlueFromHex(int color) {
        return (float)(color & 0xFF) / 255.0f;
    }

    public static void disableDefaults() {
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(false);
    }

    public static void enableDefaults() {
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
    }
}

