package net.minecraft.util;

import io.netty.util.ResourceLeakDetector;

public class ChatAllowedCharacters {
    public static final ResourceLeakDetector.Level NETTY_LEAK_DETECTION = ResourceLeakDetector.Level.DISABLED;
    public static final char[] ILLEGAL_STRUCTURE_CHARACTERS;
    public static final char[] ILLEGAL_FILE_CHARACTERS;

    static {
        char[] arrc = new char[14];
        arrc[0] = 46;
        arrc[1] = 10;
        arrc[2] = 13;
        arrc[3] = 9;
        arrc[5] = 12;
        arrc[6] = 96;
        arrc[7] = 63;
        arrc[8] = 42;
        arrc[9] = 92;
        arrc[10] = 60;
        arrc[11] = 62;
        arrc[12] = 124;
        arrc[13] = 34;
        ILLEGAL_STRUCTURE_CHARACTERS = arrc;
        char[] arrc2 = new char[15];
        arrc2[0] = 47;
        arrc2[1] = 10;
        arrc2[2] = 13;
        arrc2[3] = 9;
        arrc2[5] = 12;
        arrc2[6] = 96;
        arrc2[7] = 63;
        arrc2[8] = 42;
        arrc2[9] = 92;
        arrc2[10] = 60;
        arrc2[11] = 62;
        arrc2[12] = 124;
        arrc2[13] = 34;
        arrc2[14] = 58;
        ILLEGAL_FILE_CHARACTERS = arrc2;
        ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
    }

    public static boolean isAllowedCharacter(char character) {
        return character != '\u00a7' && character >= ' ' && character != '\u007f';
    }

    public static String filterAllowedCharacters(String input) {
        StringBuilder stringbuilder = new StringBuilder();
        for (char c0 : input.toCharArray()) {
            if (!ChatAllowedCharacters.isAllowedCharacter(c0)) continue;
            stringbuilder.append(c0);
        }
        return stringbuilder.toString();
    }
}

