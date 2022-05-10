package net.minecraft.util;

import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class StringUtils {
    private static final Pattern PATTERN_CONTROL_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String ticksToElapsedTime(int ticks) {
        int i2 = ticks / 20;
        int j2 = i2 / 60;
        return (i2 %= 60) < 10 ? String.valueOf(j2) + ":0" + i2 : String.valueOf(j2) + ":" + i2;
    }

    public static String stripControlCodes(String text) {
        return PATTERN_CONTROL_CODE.matcher(text).replaceAll("");
    }

    public static boolean isNullOrEmpty(@Nullable String string) {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }
}

