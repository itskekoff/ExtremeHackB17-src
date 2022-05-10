package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class ColognePhonetic
implements StringEncoder {
    private static final char[] AEIJOUY = new char[]{'A', 'E', 'I', 'J', 'O', 'U', 'Y'};
    private static final char[] SCZ = new char[]{'S', 'C', 'Z'};
    private static final char[] WFPV = new char[]{'W', 'F', 'P', 'V'};
    private static final char[] GKQ = new char[]{'G', 'K', 'Q'};
    private static final char[] CKQ = new char[]{'C', 'K', 'Q'};
    private static final char[] AHKLOQRUX = new char[]{'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X'};
    private static final char[] SZ = new char[]{'S', 'Z'};
    private static final char[] AHOUKQX = new char[]{'A', 'H', 'O', 'U', 'K', 'Q', 'X'};
    private static final char[] TDX = new char[]{'T', 'D', 'X'};
    private static final char[][] PREPROCESS_MAP = new char[][]{{'\u00c4', 'A'}, {'\u00dc', 'U'}, {'\u00d6', 'O'}, {'\u00df', 'S'}};

    private static boolean arrayContains(char[] arr2, char key) {
        for (char element : arr2) {
            if (element != key) continue;
            return true;
        }
        return false;
    }

    public String colognePhonetic(String text) {
        if (text == null) {
            return null;
        }
        text = this.preprocess(text);
        CologneOutputBuffer output = new CologneOutputBuffer(text.length() * 2);
        CologneInputBuffer input = new CologneInputBuffer(text.toCharArray());
        char lastChar = '-';
        int lastCode = 47;
        int rightLength = input.length();
        while (rightLength > 0) {
            int code;
            char chr2 = input.removeNext();
            rightLength = input.length();
            char nextChar = rightLength > 0 ? (char)input.getNextChar() : (char)'-';
            if (ColognePhonetic.arrayContains(AEIJOUY, chr2)) {
                code = 48;
            } else if (chr2 == 'H' || chr2 < 'A' || chr2 > 'Z') {
                if (lastCode == 47) continue;
                code = 45;
            } else if (chr2 == 'B' || chr2 == 'P' && nextChar != 'H') {
                code = 49;
            } else if (!(chr2 != 'D' && chr2 != 'T' || ColognePhonetic.arrayContains(SCZ, nextChar))) {
                code = 50;
            } else if (ColognePhonetic.arrayContains(WFPV, chr2)) {
                code = 51;
            } else if (ColognePhonetic.arrayContains(GKQ, chr2)) {
                code = 52;
            } else if (chr2 == 'X' && !ColognePhonetic.arrayContains(CKQ, lastChar)) {
                code = 52;
                input.addLeft('S');
                ++rightLength;
            } else {
                code = chr2 == 'S' || chr2 == 'Z' ? 56 : (chr2 == 'C' ? (lastCode == 47 ? (ColognePhonetic.arrayContains(AHKLOQRUX, nextChar) ? 52 : 56) : (ColognePhonetic.arrayContains(SZ, lastChar) || !ColognePhonetic.arrayContains(AHOUKQX, nextChar) ? 56 : 52)) : (ColognePhonetic.arrayContains(TDX, chr2) ? 56 : (chr2 == 'R' ? 55 : (chr2 == 'L' ? 53 : (chr2 == 'M' || chr2 == 'N' ? 54 : (int)chr2)))));
            }
            if (code != 45 && (lastCode != code && (code != 48 || lastCode == 47) || code < 48 || code > 56)) {
                output.addRight((char)code);
            }
            lastChar = chr2;
            lastCode = code;
        }
        return output.toString();
    }

    @Override
    public Object encode(Object object) throws EncoderException {
        if (!(object instanceof String)) {
            throw new EncoderException("This method's parameter was expected to be of the type " + String.class.getName() + ". But actually it was of the type " + object.getClass().getName() + ".");
        }
        return this.encode((String)object);
    }

    @Override
    public String encode(String text) {
        return this.colognePhonetic(text);
    }

    public boolean isEncodeEqual(String text1, String text2) {
        return this.colognePhonetic(text1).equals(this.colognePhonetic(text2));
    }

    private String preprocess(String text) {
        text = text.toUpperCase(Locale.GERMAN);
        char[] chrs = text.toCharArray();
        block0: for (int index = 0; index < chrs.length; ++index) {
            if (chrs[index] <= 'Z') continue;
            for (char[] element : PREPROCESS_MAP) {
                if (chrs[index] != element[0]) continue;
                chrs[index] = element[1];
                continue block0;
            }
        }
        return new String(chrs);
    }

    private class CologneInputBuffer
    extends CologneBuffer {
        public CologneInputBuffer(char[] data) {
            super(data);
        }

        public void addLeft(char ch2) {
            ++this.length;
            this.data[this.getNextPos()] = ch2;
        }

        @Override
        protected char[] copyData(int start, int length) {
            char[] newData = new char[length];
            System.arraycopy(this.data, this.data.length - this.length + start, newData, 0, length);
            return newData;
        }

        public char getNextChar() {
            return this.data[this.getNextPos()];
        }

        protected int getNextPos() {
            return this.data.length - this.length;
        }

        public char removeNext() {
            char ch2 = this.getNextChar();
            --this.length;
            return ch2;
        }
    }

    private class CologneOutputBuffer
    extends CologneBuffer {
        public CologneOutputBuffer(int buffSize) {
            super(buffSize);
        }

        public void addRight(char chr2) {
            this.data[this.length] = chr2;
            ++this.length;
        }

        @Override
        protected char[] copyData(int start, int length) {
            char[] newData = new char[length];
            System.arraycopy(this.data, start, newData, 0, length);
            return newData;
        }
    }

    private abstract class CologneBuffer {
        protected final char[] data;
        protected int length = 0;

        public CologneBuffer(char[] data) {
            this.data = data;
            this.length = data.length;
        }

        public CologneBuffer(int buffSize) {
            this.data = new char[buffSize];
            this.length = 0;
        }

        protected abstract char[] copyData(int var1, int var2);

        public int length() {
            return this.length;
        }

        public String toString() {
            return new String(this.copyData(0, this.length));
        }
    }
}

