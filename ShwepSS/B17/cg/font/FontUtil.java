package ShwepSS.B17.cg.font;

import ShwepSS.B17.cg.font.FRenderer;
import java.awt.Font;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontUtil {
    public static FRenderer sfui14 = new FRenderer(FontUtil.getFontTTF("sf-ui", 14), true, true);
    public static FRenderer sfui15 = new FRenderer(FontUtil.getFontTTF("sf-ui", 15), true, true);
    public static FRenderer sfui16 = new FRenderer(FontUtil.getFontTTF("sf-ui", 16), true, true);
    public static FRenderer sfui18 = new FRenderer(FontUtil.getFontTTF("sf-ui", 18), true, true);
    public static FRenderer roboto_20 = new FRenderer(FontUtil.getFontTTF("roboto", 20), true, true);
    public static FRenderer roboto_19 = new FRenderer(FontUtil.getFontTTF("roboto", 19), true, true);
    public static FRenderer roboto_18 = new FRenderer(FontUtil.getFontTTF("roboto", 18), true, true);
    public static FRenderer roboto_16 = new FRenderer(FontUtil.getFontTTF("roboto", 16), true, true);
    public static FRenderer roboto_15 = new FRenderer(FontUtil.getFontTTF("roboto", 15), true, true);
    public static FRenderer roboto_14 = new FRenderer(FontUtil.getFontTTF("roboto", 14), true, true);
    public static FRenderer roboto_13 = new FRenderer(FontUtil.getFontTTF("roboto", 13), true, true);
    public static FRenderer neverlose500_13 = new FRenderer(FontUtil.getFontTTF("neverlose500", 13), true, true);
    public static FRenderer neverlose500_14 = new FRenderer(FontUtil.getFontTTF("neverlose500", 14), true, true);
    public static FRenderer neverlose500_15 = new FRenderer(FontUtil.getFontTTF("neverlose500", 15), true, true);
    public static FRenderer neverlose500_16 = new FRenderer(FontUtil.getFontTTF("neverlose500", 16), true, true);
    public static FRenderer neverlose500_17 = new FRenderer(FontUtil.getFontTTF("neverlose500", 17), true, true);
    public static FRenderer neverlose500_18 = new FRenderer(FontUtil.getFontTTF("neverlose500", 18), true, true);
    public static FRenderer smallestpixel_14 = new FRenderer(FontUtil.getFontTTF("smallpixel", 14), true, true);
    public static FRenderer smallestpixel_15 = new FRenderer(FontUtil.getFontTTF("smallpixel", 15), true, true);
    public static FRenderer smallestpixel_16 = new FRenderer(FontUtil.getFontTTF("smallpixel", 16), true, true);
    public static FRenderer smallestpixel_17 = new FRenderer(FontUtil.getFontTTF("smallpixel", 17), true, true);
    public static FRenderer icons_14 = new FRenderer(FontUtil.getFontTTF("icons", 14), true, true);
    public static FRenderer icons_15 = new FRenderer(FontUtil.getFontTTF("icons", 15), true, true);
    public static FRenderer icons_16 = new FRenderer(FontUtil.getFontTTF("icons", 16), true, true);
    public static FRenderer icons_17 = new FRenderer(FontUtil.getFontTTF("icons", 17), true, true);
    public static FRenderer icons_18 = new FRenderer(FontUtil.getFontTTF("icons", 18), true, true);
    public static FRenderer icons_19 = new FRenderer(FontUtil.getFontTTF("icons", 19), true, true);
    public static FRenderer icons_20 = new FRenderer(FontUtil.getFontTTF("icons", 20), true, true);
    public static FRenderer elegant_14 = new FRenderer(FontUtil.getFontTTF("ElegantIcons", 14), true, true);
    public static FRenderer elegant_15 = new FRenderer(FontUtil.getFontTTF("ElegantIcons", 15), true, true);
    public static FRenderer elegant_16 = new FRenderer(FontUtil.getFontTTF("ElegantIcons", 16), true, true);
    public static FRenderer elegant_17 = new FRenderer(FontUtil.getFontTTF("ElegantIcons", 17), true, true);
    public static FRenderer elegant_18 = new FRenderer(FontUtil.getFontTTF("ElegantIcons", 18), true, true);
    public static FRenderer elegant_19 = new FRenderer(FontUtil.getFontTTF("ElegantIcons", 19), true, true);
    public static FRenderer elegant_20 = new FRenderer(FontUtil.getFontTTF("ElegantIcons", 20), true, true);
    public static FRenderer stylesicons_14 = new FRenderer(FontUtil.getFontTTF("stylesicons", 14), true, true);
    public static FRenderer stylesicons_15 = new FRenderer(FontUtil.getFontTTF("stylesicons", 15), true, true);
    public static FRenderer stylesicons_16 = new FRenderer(FontUtil.getFontTTF("stylesicons", 16), true, true);
    public static FRenderer stylesicons_17 = new FRenderer(FontUtil.getFontTTF("stylesicons", 17), true, true);
    public static FRenderer stylesicons_18 = new FRenderer(FontUtil.getFontTTF("stylesicons", 18), true, true);
    public static FRenderer stylesicons_19 = new FRenderer(FontUtil.getFontTTF("stylesicons", 19), true, true);
    public static FRenderer stylesicons_20 = new FRenderer(FontUtil.getFontTTF("stylesicons", 20), true, true);

    public static float blob(String fontName, int fontSize, String text, float x2, float y2, int color, boolean shadow) {
        FRenderer cf2 = new FRenderer(FontUtil.getFontTTF(fontName, fontSize), true, true);
        return cf2.drawString(text, x2, y2, color, shadow);
    }

    private static Font getFontTTF(String name, int size) {
        Font font;
        try {
            InputStream is2 = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/" + name + ".ttf")).getInputStream();
            font = Font.createFont(0, is2);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex2) {
            font = new Font("default", 0, size);
        }
        return font;
    }
}

