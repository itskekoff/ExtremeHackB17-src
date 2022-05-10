package com.ibm.icu.text;

import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.text.ArabicShapingException;

public final class ArabicShaping {
    private final int options;
    private boolean isLogical;
    private boolean spacesRelativeToTextBeginEnd;
    private char tailChar;
    public static final int SEEN_TWOCELL_NEAR = 0x200000;
    public static final int SEEN_MASK = 0x700000;
    public static final int YEHHAMZA_TWOCELL_NEAR = 0x1000000;
    public static final int YEHHAMZA_MASK = 0x3800000;
    public static final int TASHKEEL_BEGIN = 262144;
    public static final int TASHKEEL_END = 393216;
    public static final int TASHKEEL_RESIZE = 524288;
    public static final int TASHKEEL_REPLACE_BY_TATWEEL = 786432;
    public static final int TASHKEEL_MASK = 917504;
    public static final int SPACES_RELATIVE_TO_TEXT_BEGIN_END = 0x4000000;
    public static final int SPACES_RELATIVE_TO_TEXT_MASK = 0x4000000;
    public static final int SHAPE_TAIL_NEW_UNICODE = 0x8000000;
    public static final int SHAPE_TAIL_TYPE_MASK = 0x8000000;
    public static final int LENGTH_GROW_SHRINK = 0;
    public static final int LAMALEF_RESIZE = 0;
    public static final int LENGTH_FIXED_SPACES_NEAR = 1;
    public static final int LAMALEF_NEAR = 1;
    public static final int LENGTH_FIXED_SPACES_AT_END = 2;
    public static final int LAMALEF_END = 2;
    public static final int LENGTH_FIXED_SPACES_AT_BEGINNING = 3;
    public static final int LAMALEF_BEGIN = 3;
    public static final int LAMALEF_AUTO = 65536;
    public static final int LENGTH_MASK = 65539;
    public static final int LAMALEF_MASK = 65539;
    public static final int TEXT_DIRECTION_LOGICAL = 0;
    public static final int TEXT_DIRECTION_VISUAL_RTL = 0;
    public static final int TEXT_DIRECTION_VISUAL_LTR = 4;
    public static final int TEXT_DIRECTION_MASK = 4;
    public static final int LETTERS_NOOP = 0;
    public static final int LETTERS_SHAPE = 8;
    public static final int LETTERS_UNSHAPE = 16;
    public static final int LETTERS_SHAPE_TASHKEEL_ISOLATED = 24;
    public static final int LETTERS_MASK = 24;
    public static final int DIGITS_NOOP = 0;
    public static final int DIGITS_EN2AN = 32;
    public static final int DIGITS_AN2EN = 64;
    public static final int DIGITS_EN2AN_INIT_LR = 96;
    public static final int DIGITS_EN2AN_INIT_AL = 128;
    public static final int DIGITS_MASK = 224;
    public static final int DIGIT_TYPE_AN = 0;
    public static final int DIGIT_TYPE_AN_EXTENDED = 256;
    public static final int DIGIT_TYPE_MASK = 256;
    private static final char HAMZAFE_CHAR = '\ufe80';
    private static final char HAMZA06_CHAR = '\u0621';
    private static final char YEH_HAMZA_CHAR = '\u0626';
    private static final char YEH_HAMZAFE_CHAR = '\ufe89';
    private static final char LAMALEF_SPACE_SUB = '\uffff';
    private static final char TASHKEEL_SPACE_SUB = '\ufffe';
    private static final char LAM_CHAR = '\u0644';
    private static final char SPACE_CHAR = ' ';
    private static final char SHADDA_CHAR = '\ufe7c';
    private static final char SHADDA06_CHAR = '\u0651';
    private static final char TATWEEL_CHAR = '\u0640';
    private static final char SHADDA_TATWEEL_CHAR = '\ufe7d';
    private static final char NEW_TAIL_CHAR = '\ufe73';
    private static final char OLD_TAIL_CHAR = '\u200b';
    private static final int SHAPE_MODE = 0;
    private static final int DESHAPE_MODE = 1;
    private static final int IRRELEVANT = 4;
    private static final int LAMTYPE = 16;
    private static final int ALEFTYPE = 32;
    private static final int LINKR = 1;
    private static final int LINKL = 2;
    private static final int LINK_MASK = 3;
    private static final int[] irrelevantPos = new int[]{0, 2, 4, 6, 8, 10, 12, 14};
    private static final int[] tailFamilyIsolatedFinal = new int[]{1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1};
    private static final int[] tashkeelMedial = new int[]{0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1};
    private static final char[] yehHamzaToYeh = new char[]{'\ufeef', '\ufef0'};
    private static final char[] convertNormalizedLamAlef = new char[]{'\u0622', '\u0623', '\u0625', '\u0627'};
    private static final int[] araLink = new int[]{4385, 4897, 5377, 5921, 6403, 7457, 7939, 8961, 9475, 10499, 11523, 12547, 13571, 14593, 15105, 15617, 16129, 16643, 17667, 18691, 19715, 20739, 21763, 22787, 23811, 0, 0, 0, 0, 0, 3, 24835, 25859, 26883, 27923, 28931, 29955, 30979, 32001, 32513, 33027, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 34049, 34561, 35073, 35585, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 33, 33, 0, 33, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 1, 1};
    private static final int[] presLink = new int[]{3, 3, 3, 0, 3, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 32, 33, 32, 33, 0, 1, 32, 33, 0, 2, 3, 1, 32, 33, 0, 2, 3, 1, 0, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 16, 18, 19, 17, 0, 2, 3, 1, 0, 2, 3, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 2, 3, 1, 0, 1, 0, 1, 0, 1, 0, 1};
    private static int[] convertFEto06 = new int[]{1611, 1611, 1612, 1612, 1613, 1613, 1614, 1614, 1615, 1615, 1616, 1616, 1617, 1617, 1618, 1618, 1569, 1570, 1570, 1571, 1571, 1572, 1572, 1573, 1573, 1574, 1574, 1574, 1574, 1575, 1575, 1576, 1576, 1576, 1576, 1577, 1577, 1578, 1578, 1578, 1578, 1579, 1579, 1579, 1579, 1580, 1580, 1580, 1580, 1581, 1581, 1581, 1581, 1582, 1582, 1582, 1582, 1583, 1583, 1584, 1584, 1585, 1585, 1586, 1586, 1587, 1587, 1587, 1587, 1588, 1588, 1588, 1588, 1589, 1589, 1589, 1589, 1590, 1590, 1590, 1590, 1591, 1591, 1591, 1591, 1592, 1592, 1592, 1592, 1593, 1593, 1593, 1593, 1594, 1594, 1594, 1594, 1601, 1601, 1601, 1601, 1602, 1602, 1602, 1602, 1603, 1603, 1603, 1603, 1604, 1604, 1604, 1604, 1605, 1605, 1605, 1605, 1606, 1606, 1606, 1606, 1607, 1607, 1607, 1607, 1608, 1608, 1609, 1609, 1610, 1610, 1610, 1610, 1628, 1628, 1629, 1629, 1630, 1630, 1631, 1631};
    private static final int[][][] shapeTable = new int[][][]{new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 1, 0, 3}, {0, 1, 0, 1}}, new int[][]{{0, 0, 2, 2}, {0, 0, 1, 2}, {0, 1, 1, 2}, {0, 1, 1, 3}}, new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 1, 0, 3}, {0, 1, 0, 3}}, new int[][]{{0, 0, 1, 2}, {0, 0, 1, 2}, {0, 1, 1, 2}, {0, 1, 1, 3}}};

    public int shape(char[] source, int sourceStart, int sourceLength, char[] dest, int destStart, int destSize) throws ArabicShapingException {
        if (source == null) {
            throw new IllegalArgumentException("source can not be null");
        }
        if (sourceStart < 0 || sourceLength < 0 || sourceStart + sourceLength > source.length) {
            throw new IllegalArgumentException("bad source start (" + sourceStart + ") or length (" + sourceLength + ") for buffer of length " + source.length);
        }
        if (dest == null && destSize != 0) {
            throw new IllegalArgumentException("null dest requires destSize == 0");
        }
        if (destSize != 0 && (destStart < 0 || destSize < 0 || destStart + destSize > dest.length)) {
            throw new IllegalArgumentException("bad dest start (" + destStart + ") or size (" + destSize + ") for buffer of length " + dest.length);
        }
        if ((this.options & 0xE0000) > 0 && (this.options & 0xE0000) != 262144 && (this.options & 0xE0000) != 393216 && (this.options & 0xE0000) != 524288 && (this.options & 0xE0000) != 786432) {
            throw new IllegalArgumentException("Wrong Tashkeel argument");
        }
        if ((this.options & 0x10003) > 0 && (this.options & 0x10003) != 3 && (this.options & 0x10003) != 2 && (this.options & 0x10003) != 0 && (this.options & 0x10003) != 65536 && (this.options & 0x10003) != 1) {
            throw new IllegalArgumentException("Wrong Lam Alef argument");
        }
        if ((this.options & 0xE0000) > 0 && (this.options & 0x18) == 16) {
            throw new IllegalArgumentException("Tashkeel replacement should not be enabled in deshaping mode ");
        }
        return this.internalShape(source, sourceStart, sourceLength, dest, destStart, destSize);
    }

    public void shape(char[] source, int start, int length) throws ArabicShapingException {
        if ((this.options & 0x10003) == 0) {
            throw new ArabicShapingException("Cannot shape in place with length option resize.");
        }
        this.shape(source, start, length, source, start, length);
    }

    public String shape(String text) throws ArabicShapingException {
        char[] src;
        char[] dest = src = text.toCharArray();
        if ((this.options & 0x10003) == 0 && (this.options & 0x18) == 16) {
            dest = new char[src.length * 2];
        }
        int len = this.shape(src, 0, src.length, dest, 0, dest.length);
        return new String(dest, 0, len);
    }

    public ArabicShaping(int options) {
        this.options = options;
        if ((options & 0xE0) > 128) {
            throw new IllegalArgumentException("bad DIGITS options");
        }
        this.isLogical = (options & 4) == 0;
        this.spacesRelativeToTextBeginEnd = (options & 0x4000000) == 0x4000000;
        this.tailChar = (options & 0x8000000) == 0x8000000 ? (char)65139 : (char)8203;
    }

    public boolean equals(Object rhs) {
        return rhs != null && rhs.getClass() == ArabicShaping.class && this.options == ((ArabicShaping)rhs).options;
    }

    public int hashCode() {
        return this.options;
    }

    public String toString() {
        StringBuilder buf2 = new StringBuilder(super.toString());
        buf2.append('[');
        switch (this.options & 0x10003) {
            case 0: {
                buf2.append("LamAlef resize");
                break;
            }
            case 1: {
                buf2.append("LamAlef spaces at near");
                break;
            }
            case 3: {
                buf2.append("LamAlef spaces at begin");
                break;
            }
            case 2: {
                buf2.append("LamAlef spaces at end");
                break;
            }
            case 65536: {
                buf2.append("lamAlef auto");
            }
        }
        switch (this.options & 4) {
            case 0: {
                buf2.append(", logical");
                break;
            }
            case 4: {
                buf2.append(", visual");
            }
        }
        switch (this.options & 0x18) {
            case 0: {
                buf2.append(", no letter shaping");
                break;
            }
            case 8: {
                buf2.append(", shape letters");
                break;
            }
            case 24: {
                buf2.append(", shape letters tashkeel isolated");
                break;
            }
            case 16: {
                buf2.append(", unshape letters");
            }
        }
        switch (this.options & 0x700000) {
            case 0x200000: {
                buf2.append(", Seen at near");
            }
        }
        switch (this.options & 0x3800000) {
            case 0x1000000: {
                buf2.append(", Yeh Hamza at near");
            }
        }
        switch (this.options & 0xE0000) {
            case 262144: {
                buf2.append(", Tashkeel at begin");
                break;
            }
            case 393216: {
                buf2.append(", Tashkeel at end");
                break;
            }
            case 786432: {
                buf2.append(", Tashkeel replace with tatweel");
                break;
            }
            case 524288: {
                buf2.append(", Tashkeel resize");
            }
        }
        switch (this.options & 0xE0) {
            case 0: {
                buf2.append(", no digit shaping");
                break;
            }
            case 32: {
                buf2.append(", shape digits to AN");
                break;
            }
            case 64: {
                buf2.append(", shape digits to EN");
                break;
            }
            case 96: {
                buf2.append(", shape digits to AN contextually: default EN");
                break;
            }
            case 128: {
                buf2.append(", shape digits to AN contextually: default AL");
            }
        }
        switch (this.options & 0x100) {
            case 0: {
                buf2.append(", standard Arabic-Indic digits");
                break;
            }
            case 256: {
                buf2.append(", extended Arabic-Indic digits");
            }
        }
        buf2.append("]");
        return buf2.toString();
    }

    private void shapeToArabicDigitsWithContext(char[] dest, int start, int length, char digitBase, boolean lastStrongWasAL) {
        UBiDiProps bdp2 = UBiDiProps.INSTANCE;
        digitBase = (char)(digitBase - 48);
        int i2 = start + length;
        while (--i2 >= start) {
            char ch2 = dest[i2];
            switch (bdp2.getClass(ch2)) {
                case 0: 
                case 1: {
                    lastStrongWasAL = false;
                    break;
                }
                case 13: {
                    lastStrongWasAL = true;
                    break;
                }
                case 2: {
                    if (!lastStrongWasAL || ch2 > '9') break;
                    dest[i2] = (char)(ch2 + digitBase);
                    break;
                }
            }
        }
    }

    private static void invertBuffer(char[] buffer, int start, int length) {
        int i2 = start;
        for (int j2 = start + length - 1; i2 < j2; ++i2, --j2) {
            char temp = buffer[i2];
            buffer[i2] = buffer[j2];
            buffer[j2] = temp;
        }
    }

    private static char changeLamAlef(char ch2) {
        switch (ch2) {
            case '\u0622': {
                return '\u065c';
            }
            case '\u0623': {
                return '\u065d';
            }
            case '\u0625': {
                return '\u065e';
            }
            case '\u0627': {
                return '\u065f';
            }
        }
        return '\u0000';
    }

    private static int specialChar(char ch2) {
        if (ch2 > '\u0621' && ch2 < '\u0626' || ch2 == '\u0627' || ch2 > '\u062e' && ch2 < '\u0633' || ch2 > '\u0647' && ch2 < '\u064a' || ch2 == '\u0629') {
            return 1;
        }
        if (ch2 >= '\u064b' && ch2 <= '\u0652') {
            return 2;
        }
        if (ch2 >= '\u0653' && ch2 <= '\u0655' || ch2 == '\u0670' || ch2 >= '\ufe70' && ch2 <= '\ufe7f') {
            return 3;
        }
        return 0;
    }

    private static int getLink(char ch2) {
        if (ch2 >= '\u0622' && ch2 <= '\u06d3') {
            return araLink[ch2 - 1570];
        }
        if (ch2 == '\u200d') {
            return 3;
        }
        if (ch2 >= '\u206d' && ch2 <= '\u206f') {
            return 4;
        }
        if (ch2 >= '\ufe70' && ch2 <= '\ufefc') {
            return presLink[ch2 - 65136];
        }
        return 0;
    }

    private static int countSpacesLeft(char[] dest, int start, int count) {
        int e2 = start + count;
        for (int i2 = start; i2 < e2; ++i2) {
            if (dest[i2] == ' ') continue;
            return i2 - start;
        }
        return count;
    }

    private static int countSpacesRight(char[] dest, int start, int count) {
        int i2 = start + count;
        while (--i2 >= start) {
            if (dest[i2] == ' ') continue;
            return start + count - 1 - i2;
        }
        return count;
    }

    private static boolean isTashkeelChar(char ch2) {
        return ch2 >= '\u064b' && ch2 <= '\u0652';
    }

    private static int isSeenTailFamilyChar(char ch2) {
        if (ch2 >= '\ufeb1' && ch2 < '\ufebf') {
            return tailFamilyIsolatedFinal[ch2 - 65201];
        }
        return 0;
    }

    private static int isSeenFamilyChar(char ch2) {
        if (ch2 >= '\u0633' && ch2 <= '\u0636') {
            return 1;
        }
        return 0;
    }

    private static boolean isTailChar(char ch2) {
        return ch2 == '\u200b' || ch2 == '\ufe73';
    }

    private static boolean isAlefMaksouraChar(char ch2) {
        return ch2 == '\ufeef' || ch2 == '\ufef0' || ch2 == '\u0649';
    }

    private static boolean isYehHamzaChar(char ch2) {
        return ch2 == '\ufe89' || ch2 == '\ufe8a';
    }

    private static boolean isTashkeelCharFE(char ch2) {
        return ch2 != '\ufe75' && ch2 >= '\ufe70' && ch2 <= '\ufe7f';
    }

    private static int isTashkeelOnTatweelChar(char ch2) {
        if (ch2 >= '\ufe70' && ch2 <= '\ufe7f' && ch2 != '\ufe73' && ch2 != '\ufe75' && ch2 != '\ufe7d') {
            return tashkeelMedial[ch2 - 65136];
        }
        if (ch2 >= '\ufcf2' && ch2 <= '\ufcf4' || ch2 == '\ufe7d') {
            return 2;
        }
        return 0;
    }

    private static int isIsolatedTashkeelChar(char ch2) {
        if (ch2 >= '\ufe70' && ch2 <= '\ufe7f' && ch2 != '\ufe73' && ch2 != '\ufe75') {
            return 1 - tashkeelMedial[ch2 - 65136];
        }
        if (ch2 >= '\ufc5e' && ch2 <= '\ufc63') {
            return 1;
        }
        return 0;
    }

    private static boolean isAlefChar(char ch2) {
        return ch2 == '\u0622' || ch2 == '\u0623' || ch2 == '\u0625' || ch2 == '\u0627';
    }

    private static boolean isLamAlefChar(char ch2) {
        return ch2 >= '\ufef5' && ch2 <= '\ufefc';
    }

    private static boolean isNormalizedLamAlefChar(char ch2) {
        return ch2 >= '\u065c' && ch2 <= '\u065f';
    }

    private int calculateSize(char[] source, int sourceStart, int sourceLength) {
        int destSize = sourceLength;
        switch (this.options & 0x18) {
            case 8: 
            case 24: {
                if (this.isLogical) {
                    int e2 = sourceStart + sourceLength - 1;
                    for (int i2 = sourceStart; i2 < e2; ++i2) {
                        if ((source[i2] != '\u0644' || !ArabicShaping.isAlefChar(source[i2 + 1])) && !ArabicShaping.isTashkeelCharFE(source[i2])) continue;
                        --destSize;
                    }
                } else {
                    int e3 = sourceStart + sourceLength;
                    for (int i3 = sourceStart + 1; i3 < e3; ++i3) {
                        if ((source[i3] != '\u0644' || !ArabicShaping.isAlefChar(source[i3 - 1])) && !ArabicShaping.isTashkeelCharFE(source[i3])) continue;
                        --destSize;
                    }
                }
                break;
            }
            case 16: {
                int e4 = sourceStart + sourceLength;
                for (int i4 = sourceStart; i4 < e4; ++i4) {
                    if (!ArabicShaping.isLamAlefChar(source[i4])) continue;
                    ++destSize;
                }
                break;
            }
        }
        return destSize;
    }

    private static int countSpaceSub(char[] dest, int length, char subChar) {
        int count = 0;
        for (int i2 = 0; i2 < length; ++i2) {
            if (dest[i2] != subChar) continue;
            ++count;
        }
        return count;
    }

    private static void shiftArray(char[] dest, int start, int e2, char subChar) {
        int w2 = e2;
        int r2 = e2;
        while (--r2 >= start) {
            char ch2 = dest[r2];
            if (ch2 == subChar || --w2 == r2) continue;
            dest[w2] = ch2;
        }
    }

    private static int flipArray(char[] dest, int start, int e2, int w2) {
        if (w2 > start) {
            int r2 = w2;
            w2 = start;
            while (r2 < e2) {
                dest[w2++] = dest[r2++];
            }
        } else {
            w2 = e2;
        }
        return w2;
    }

    private static int handleTashkeelWithTatweel(char[] dest, int sourceLength) {
        for (int i2 = 0; i2 < sourceLength; ++i2) {
            if (ArabicShaping.isTashkeelOnTatweelChar(dest[i2]) == 1) {
                dest[i2] = 1600;
                continue;
            }
            if (ArabicShaping.isTashkeelOnTatweelChar(dest[i2]) == 2) {
                dest[i2] = 65149;
                continue;
            }
            if (ArabicShaping.isIsolatedTashkeelChar(dest[i2]) != 1 || dest[i2] == '\ufe7c') continue;
            dest[i2] = 32;
        }
        return sourceLength;
    }

    private int handleGeneratedSpaces(char[] dest, int start, int length) {
        block26: {
            boolean tashkeelOn;
            boolean lamAlefOn;
            int lenOptionsTashkeel;
            int lenOptionsLamAlef;
            block25: {
                int i2;
                lenOptionsLamAlef = this.options & 0x10003;
                lenOptionsTashkeel = this.options & 0xE0000;
                lamAlefOn = false;
                tashkeelOn = false;
                if (!this.isLogical & !this.spacesRelativeToTextBeginEnd) {
                    switch (lenOptionsLamAlef) {
                        case 3: {
                            lenOptionsLamAlef = 2;
                            break;
                        }
                        case 2: {
                            lenOptionsLamAlef = 3;
                            break;
                        }
                    }
                    switch (lenOptionsTashkeel) {
                        case 262144: {
                            lenOptionsTashkeel = 393216;
                            break;
                        }
                        case 393216: {
                            lenOptionsTashkeel = 262144;
                            break;
                        }
                    }
                }
                if (lenOptionsLamAlef != 1) break block25;
                int e2 = i2 + length;
                for (i2 = start; i2 < e2; ++i2) {
                    if (dest[i2] != '\uffff') continue;
                    dest[i2] = 32;
                }
                break block26;
            }
            int e3 = start + length;
            int wL = ArabicShaping.countSpaceSub(dest, length, '\uffff');
            int wT = ArabicShaping.countSpaceSub(dest, length, '\ufffe');
            if (lenOptionsLamAlef == 2) {
                lamAlefOn = true;
            }
            if (lenOptionsTashkeel == 393216) {
                tashkeelOn = true;
            }
            if (lamAlefOn && lenOptionsLamAlef == 2) {
                ArabicShaping.shiftArray(dest, start, e3, '\uffff');
                while (wL > start) {
                    dest[--wL] = 32;
                }
            }
            if (tashkeelOn && lenOptionsTashkeel == 393216) {
                ArabicShaping.shiftArray(dest, start, e3, '\ufffe');
                while (wT > start) {
                    dest[--wT] = 32;
                }
            }
            lamAlefOn = false;
            tashkeelOn = false;
            if (lenOptionsLamAlef == 0) {
                lamAlefOn = true;
            }
            if (lenOptionsTashkeel == 524288) {
                tashkeelOn = true;
            }
            if (lamAlefOn && lenOptionsLamAlef == 0) {
                ArabicShaping.shiftArray(dest, start, e3, '\uffff');
                wL = ArabicShaping.flipArray(dest, start, e3, wL);
                length = wL - start;
            }
            if (tashkeelOn && lenOptionsTashkeel == 524288) {
                ArabicShaping.shiftArray(dest, start, e3, '\ufffe');
                wT = ArabicShaping.flipArray(dest, start, e3, wT);
                length = wT - start;
            }
            lamAlefOn = false;
            tashkeelOn = false;
            if (lenOptionsLamAlef == 3 || lenOptionsLamAlef == 65536) {
                lamAlefOn = true;
            }
            if (lenOptionsTashkeel == 262144) {
                tashkeelOn = true;
            }
            if (lamAlefOn && (lenOptionsLamAlef == 3 || lenOptionsLamAlef == 65536)) {
                ArabicShaping.shiftArray(dest, start, e3, '\uffff');
                wL = ArabicShaping.flipArray(dest, start, e3, wL);
                while (wL < e3) {
                    dest[wL++] = 32;
                }
            }
            if (!tashkeelOn || lenOptionsTashkeel != 262144) break block26;
            ArabicShaping.shiftArray(dest, start, e3, '\ufffe');
            wT = ArabicShaping.flipArray(dest, start, e3, wT);
            while (wT < e3) {
                dest[wT++] = 32;
            }
        }
        return length;
    }

    private boolean expandCompositCharAtBegin(char[] dest, int start, int length, int lacount) {
        boolean spaceNotFound = false;
        if (lacount > ArabicShaping.countSpacesRight(dest, start, length)) {
            spaceNotFound = true;
            return spaceNotFound;
        }
        int r2 = start + length - lacount;
        int w2 = start + length;
        while (--r2 >= start) {
            char ch2 = dest[r2];
            if (ArabicShaping.isNormalizedLamAlefChar(ch2)) {
                dest[--w2] = 1604;
                dest[--w2] = convertNormalizedLamAlef[ch2 - 1628];
                continue;
            }
            dest[--w2] = ch2;
        }
        return spaceNotFound;
    }

    private boolean expandCompositCharAtEnd(char[] dest, int start, int length, int lacount) {
        boolean spaceNotFound = false;
        if (lacount > ArabicShaping.countSpacesLeft(dest, start, length)) {
            spaceNotFound = true;
            return spaceNotFound;
        }
        int w2 = start;
        int e2 = start + length;
        for (int r2 = start + lacount; r2 < e2; ++r2) {
            char ch2 = dest[r2];
            if (ArabicShaping.isNormalizedLamAlefChar(ch2)) {
                dest[w2++] = convertNormalizedLamAlef[ch2 - 1628];
                dest[w2++] = 1604;
                continue;
            }
            dest[w2++] = ch2;
        }
        return spaceNotFound;
    }

    private boolean expandCompositCharAtNear(char[] dest, int start, int length, int yehHamzaOption, int seenTailOption, int lamAlefOption) {
        boolean spaceNotFound = false;
        if (ArabicShaping.isNormalizedLamAlefChar(dest[start])) {
            spaceNotFound = true;
            return spaceNotFound;
        }
        int i2 = start + length;
        while (--i2 >= start) {
            char ch2 = dest[i2];
            if (lamAlefOption == 1 && ArabicShaping.isNormalizedLamAlefChar(ch2)) {
                if (i2 > start && dest[i2 - 1] == ' ') {
                    dest[i2] = 1604;
                    dest[--i2] = convertNormalizedLamAlef[ch2 - 1628];
                    continue;
                }
                spaceNotFound = true;
                return spaceNotFound;
            }
            if (seenTailOption == 1 && ArabicShaping.isSeenTailFamilyChar(ch2) == 1) {
                if (i2 > start && dest[i2 - 1] == ' ') {
                    dest[i2 - 1] = this.tailChar;
                    continue;
                }
                spaceNotFound = true;
                return spaceNotFound;
            }
            if (yehHamzaOption != 1 || !ArabicShaping.isYehHamzaChar(ch2)) continue;
            if (i2 > start && dest[i2 - 1] == ' ') {
                dest[i2] = yehHamzaToYeh[ch2 - 65161];
                dest[i2 - 1] = 65152;
                continue;
            }
            spaceNotFound = true;
            return spaceNotFound;
        }
        return false;
    }

    private int expandCompositChar(char[] dest, int start, int length, int lacount, int shapingMode) throws ArabicShapingException {
        int lenOptionsLamAlef = this.options & 0x10003;
        int lenOptionsSeen = this.options & 0x700000;
        int lenOptionsYehHamza = this.options & 0x3800000;
        boolean spaceNotFound = false;
        if (!this.isLogical && !this.spacesRelativeToTextBeginEnd) {
            switch (lenOptionsLamAlef) {
                case 3: {
                    lenOptionsLamAlef = 2;
                    break;
                }
                case 2: {
                    lenOptionsLamAlef = 3;
                    break;
                }
            }
        }
        if (shapingMode == 1) {
            if (lenOptionsLamAlef == 65536) {
                if (this.isLogical) {
                    spaceNotFound = this.expandCompositCharAtEnd(dest, start, length, lacount);
                    if (spaceNotFound) {
                        spaceNotFound = this.expandCompositCharAtBegin(dest, start, length, lacount);
                    }
                    if (spaceNotFound) {
                        spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 0, 0, 1);
                    }
                    if (spaceNotFound) {
                        throw new ArabicShapingException("No spacefor lamalef");
                    }
                } else {
                    spaceNotFound = this.expandCompositCharAtBegin(dest, start, length, lacount);
                    if (spaceNotFound) {
                        spaceNotFound = this.expandCompositCharAtEnd(dest, start, length, lacount);
                    }
                    if (spaceNotFound) {
                        spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 0, 0, 1);
                    }
                    if (spaceNotFound) {
                        throw new ArabicShapingException("No spacefor lamalef");
                    }
                }
            } else if (lenOptionsLamAlef == 2) {
                spaceNotFound = this.expandCompositCharAtEnd(dest, start, length, lacount);
                if (spaceNotFound) {
                    throw new ArabicShapingException("No spacefor lamalef");
                }
            } else if (lenOptionsLamAlef == 3) {
                spaceNotFound = this.expandCompositCharAtBegin(dest, start, length, lacount);
                if (spaceNotFound) {
                    throw new ArabicShapingException("No spacefor lamalef");
                }
            } else if (lenOptionsLamAlef == 1) {
                spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 0, 0, 1);
                if (spaceNotFound) {
                    throw new ArabicShapingException("No spacefor lamalef");
                }
            } else if (lenOptionsLamAlef == 0) {
                int r2 = start + length;
                int w2 = r2 + lacount;
                while (--r2 >= start) {
                    char ch2 = dest[r2];
                    if (ArabicShaping.isNormalizedLamAlefChar(ch2)) {
                        dest[--w2] = 1604;
                        dest[--w2] = convertNormalizedLamAlef[ch2 - 1628];
                        continue;
                    }
                    dest[--w2] = ch2;
                }
                length += lacount;
            }
        } else {
            if (lenOptionsSeen == 0x200000 && (spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 0, 1, 0))) {
                throw new ArabicShapingException("No space for Seen tail expansion");
            }
            if (lenOptionsYehHamza == 0x1000000 && (spaceNotFound = this.expandCompositCharAtNear(dest, start, length, 1, 0, 0))) {
                throw new ArabicShapingException("No space for YehHamza expansion");
            }
        }
        return length;
    }

    private int normalize(char[] dest, int start, int length) {
        int i2;
        int lacount = 0;
        int e2 = i2 + length;
        for (i2 = start; i2 < e2; ++i2) {
            char ch2 = dest[i2];
            if (ch2 < '\ufe70' || ch2 > '\ufefc') continue;
            if (ArabicShaping.isLamAlefChar(ch2)) {
                ++lacount;
            }
            dest[i2] = (char)convertFEto06[ch2 - 65136];
        }
        return lacount;
    }

    private int deshapeNormalize(char[] dest, int start, int length) {
        int i2;
        int lacount = 0;
        boolean yehHamzaComposeEnabled = false;
        boolean seenComposeEnabled = false;
        yehHamzaComposeEnabled = (this.options & 0x3800000) == 0x1000000;
        seenComposeEnabled = (this.options & 0x700000) == 0x200000;
        int e2 = i2 + length;
        for (i2 = start; i2 < e2; ++i2) {
            char ch2 = dest[i2];
            if (yehHamzaComposeEnabled && (ch2 == '\u0621' || ch2 == '\ufe80') && i2 < length - 1 && ArabicShaping.isAlefMaksouraChar(dest[i2 + 1])) {
                dest[i2] = 32;
                dest[i2 + 1] = 1574;
                continue;
            }
            if (seenComposeEnabled && ArabicShaping.isTailChar(ch2) && i2 < length - 1 && ArabicShaping.isSeenTailFamilyChar(dest[i2 + 1]) == 1) {
                dest[i2] = 32;
                continue;
            }
            if (ch2 < '\ufe70' || ch2 > '\ufefc') continue;
            if (ArabicShaping.isLamAlefChar(ch2)) {
                ++lacount;
            }
            dest[i2] = (char)convertFEto06[ch2 - 65136];
        }
        return lacount;
    }

    private int shapeUnicode(char[] dest, int start, int length, int destSize, int tashkeelFlag) throws ArabicShapingException {
        int lamalef_count = this.normalize(dest, start, length);
        boolean lamalef_found = false;
        boolean seenfam_found = false;
        boolean yehhamza_found = false;
        boolean tashkeel_found = false;
        int i2 = start + length - 1;
        int currLink = ArabicShaping.getLink(dest[i2]);
        int nextLink = 0;
        int prevLink = 0;
        int lastLink = 0;
        int lastPos = i2;
        int nx2 = -2;
        int nw = 0;
        while (i2 >= 0) {
            if ((currLink & 0xFF00) > 0 || ArabicShaping.isTashkeelChar(dest[i2])) {
                nw = i2 - 1;
                nx2 = -2;
                while (nx2 < 0) {
                    if (nw == -1) {
                        nextLink = 0;
                        nx2 = Integer.MAX_VALUE;
                        continue;
                    }
                    nextLink = ArabicShaping.getLink(dest[nw]);
                    if ((nextLink & 4) == 0) {
                        nx2 = nw;
                        continue;
                    }
                    --nw;
                }
                if ((currLink & 0x20) > 0 && (lastLink & 0x10) > 0) {
                    lamalef_found = true;
                    char wLamalef = ArabicShaping.changeLamAlef(dest[i2]);
                    if (wLamalef != '\u0000') {
                        dest[i2] = 65535;
                        dest[lastPos] = wLamalef;
                        i2 = lastPos;
                    }
                    lastLink = prevLink;
                    currLink = ArabicShaping.getLink(wLamalef);
                }
                if (i2 > 0 && dest[i2 - 1] == ' ') {
                    if (ArabicShaping.isSeenFamilyChar(dest[i2]) == 1) {
                        seenfam_found = true;
                    } else if (dest[i2] == '\u0626') {
                        yehhamza_found = true;
                    }
                } else if (i2 == 0) {
                    if (ArabicShaping.isSeenFamilyChar(dest[i2]) == 1) {
                        seenfam_found = true;
                    } else if (dest[i2] == '\u0626') {
                        yehhamza_found = true;
                    }
                }
                int flag = ArabicShaping.specialChar(dest[i2]);
                int shape = shapeTable[nextLink & 3][lastLink & 3][currLink & 3];
                if (flag == 1) {
                    shape &= 1;
                } else if (flag == 2) {
                    shape = tashkeelFlag == 0 && (lastLink & 2) != 0 && (nextLink & 1) != 0 && dest[i2] != '\u064c' && dest[i2] != '\u064d' && ((nextLink & 0x20) != 32 || (lastLink & 0x10) != 16) ? 1 : (tashkeelFlag == 2 && dest[i2] == '\u0651' ? 1 : 0);
                }
                if (flag == 2) {
                    if (tashkeelFlag == 2 && dest[i2] != '\u0651') {
                        dest[i2] = 65534;
                        tashkeel_found = true;
                    } else {
                        dest[i2] = (char)(65136 + irrelevantPos[dest[i2] - 1611] + shape);
                    }
                } else {
                    dest[i2] = (char)(65136 + (currLink >> 8) + shape);
                }
            }
            if ((currLink & 4) == 0) {
                prevLink = lastLink;
                lastLink = currLink;
                lastPos = i2;
            }
            if (--i2 == nx2) {
                currLink = nextLink;
                nx2 = -2;
                continue;
            }
            if (i2 == -1) continue;
            currLink = ArabicShaping.getLink(dest[i2]);
        }
        destSize = length;
        if (lamalef_found || tashkeel_found) {
            destSize = this.handleGeneratedSpaces(dest, start, length);
        }
        if (seenfam_found || yehhamza_found) {
            destSize = this.expandCompositChar(dest, start, destSize, lamalef_count, 0);
        }
        return destSize;
    }

    private int deShapeUnicode(char[] dest, int start, int length, int destSize) throws ArabicShapingException {
        int lamalef_count = this.deshapeNormalize(dest, start, length);
        destSize = lamalef_count != 0 ? this.expandCompositChar(dest, start, length, lamalef_count, 1) : length;
        return destSize;
    }

    private int internalShape(char[] source, int sourceStart, int sourceLength, char[] dest, int destStart, int destSize) throws ArabicShapingException {
        if (sourceLength == 0) {
            return 0;
        }
        if (destSize == 0) {
            if ((this.options & 0x18) != 0 && (this.options & 0x10003) == 0) {
                return this.calculateSize(source, sourceStart, sourceLength);
            }
            return sourceLength;
        }
        char[] temp = new char[sourceLength * 2];
        System.arraycopy(source, sourceStart, temp, 0, sourceLength);
        if (this.isLogical) {
            ArabicShaping.invertBuffer(temp, 0, sourceLength);
        }
        int outputSize = sourceLength;
        switch (this.options & 0x18) {
            case 24: {
                outputSize = this.shapeUnicode(temp, 0, sourceLength, destSize, 1);
                break;
            }
            case 8: {
                if ((this.options & 0xE0000) > 0 && (this.options & 0xE0000) != 786432) {
                    outputSize = this.shapeUnicode(temp, 0, sourceLength, destSize, 2);
                    break;
                }
                outputSize = this.shapeUnicode(temp, 0, sourceLength, destSize, 0);
                if ((this.options & 0xE0000) != 786432) break;
                outputSize = ArabicShaping.handleTashkeelWithTatweel(temp, sourceLength);
                break;
            }
            case 16: {
                outputSize = this.deShapeUnicode(temp, 0, sourceLength, destSize);
                break;
            }
        }
        if (outputSize > destSize) {
            throw new ArabicShapingException("not enough room for result data");
        }
        if ((this.options & 0xE0) != 0) {
            char digitBase = '0';
            switch (this.options & 0x100) {
                case 0: {
                    digitBase = '\u0660';
                    break;
                }
                case 256: {
                    digitBase = '\u06f0';
                    break;
                }
            }
            switch (this.options & 0xE0) {
                case 32: {
                    int digitDelta = digitBase - 48;
                    for (int i2 = 0; i2 < outputSize; ++i2) {
                        char ch2 = temp[i2];
                        if (ch2 > '9' || ch2 < '0') continue;
                        int n2 = i2;
                        temp[n2] = (char)(temp[n2] + digitDelta);
                    }
                    break;
                }
                case 64: {
                    char digitTop = (char)(digitBase + 9);
                    int digitDelta = 48 - digitBase;
                    for (int i3 = 0; i3 < outputSize; ++i3) {
                        char ch3 = temp[i3];
                        if (ch3 > digitTop || ch3 < digitBase) continue;
                        int n3 = i3;
                        temp[n3] = (char)(temp[n3] + digitDelta);
                    }
                    break;
                }
                case 96: {
                    this.shapeToArabicDigitsWithContext(temp, 0, outputSize, digitBase, false);
                    break;
                }
                case 128: {
                    this.shapeToArabicDigitsWithContext(temp, 0, outputSize, digitBase, true);
                    break;
                }
            }
        }
        if (this.isLogical) {
            ArabicShaping.invertBuffer(temp, 0, outputSize);
        }
        System.arraycopy(temp, 0, dest, destStart, outputSize);
        return outputSize;
    }
}

