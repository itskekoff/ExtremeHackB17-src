package ShwepSS.B17;

import ShwepSS.B17.Utils.RandomUtils;
import ShwepSS.B17.cg.ColorUtils;
import java.awt.Color;
import net.minecraft.entity.EntityLivingBase;

public class HackConfigs {
    public static int ThemeColor = ColorUtils.astolfo(3, 20.0f) * 10;
    public static int ThemeColorGui = new Color(0, 45, 55).getRGB();
    public static EntityLivingBase Mudila;
    public static int packets;
    public static String spom;

    static {
        packets = 1;
        spom = "ExtremeHack \u041b\u0443\u0447\u0448\u0438\u0439 \u0447\u0438\u0442 " + RandomUtils.randomRuString(6);
    }
}

