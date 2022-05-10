package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.UCharacterNameReader;
import com.ibm.icu.impl.UCharacterUtility;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.UnicodeSet;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;

public final class UCharacterName {
    public static final UCharacterName INSTANCE;
    public static final int LINES_PER_GROUP_ = 32;
    public int m_groupcount_ = 0;
    int m_groupsize_ = 0;
    private char[] m_tokentable_;
    private byte[] m_tokenstring_;
    private char[] m_groupinfo_;
    private byte[] m_groupstring_;
    private AlgorithmName[] m_algorithm_;
    private char[] m_groupoffsets_ = new char[33];
    private char[] m_grouplengths_ = new char[33];
    private static final String NAME_FILE_NAME_ = "data/icudt51b/unames.icu";
    private static final int GROUP_SHIFT_ = 5;
    private static final int GROUP_MASK_ = 31;
    private static final int NAME_BUFFER_SIZE_ = 100000;
    private static final int OFFSET_HIGH_OFFSET_ = 1;
    private static final int OFFSET_LOW_OFFSET_ = 2;
    private static final int SINGLE_NIBBLE_MAX_ = 11;
    private int[] m_nameSet_ = new int[8];
    private int[] m_ISOCommentSet_ = new int[8];
    private StringBuffer m_utilStringBuffer_ = new StringBuffer();
    private int[] m_utilIntBuffer_ = new int[2];
    private int m_maxISOCommentLength_;
    private int m_maxNameLength_;
    private static final String[] TYPE_NAMES_;
    private static final String UNKNOWN_TYPE_NAME_ = "unknown";
    private static final int NON_CHARACTER_ = 30;
    private static final int LEAD_SURROGATE_ = 31;
    private static final int TRAIL_SURROGATE_ = 32;
    static final int EXTENDED_CATEGORY_ = 33;

    public String getName(int ch2, int choice) {
        if (ch2 < 0 || ch2 > 0x10FFFF || choice > 4) {
            return null;
        }
        String result = null;
        result = this.getAlgName(ch2, choice);
        if (result == null || result.length() == 0) {
            result = choice == 2 ? this.getExtendedName(ch2) : this.getGroupName(ch2, choice);
        }
        return result;
    }

    public int getCharFromName(int choice, String name) {
        if (choice >= 4 || name == null || name.length() == 0) {
            return -1;
        }
        int result = UCharacterName.getExtendedChar(name.toLowerCase(Locale.ENGLISH), choice);
        if (result >= -1) {
            return result;
        }
        String upperCaseName = name.toUpperCase(Locale.ENGLISH);
        if (choice == 0 || choice == 2) {
            int count = 0;
            if (this.m_algorithm_ != null) {
                count = this.m_algorithm_.length;
            }
            --count;
            while (count >= 0) {
                result = this.m_algorithm_[count].getChar(upperCaseName);
                if (result >= 0) {
                    return result;
                }
                --count;
            }
        }
        if (choice == 2) {
            result = this.getGroupChar(upperCaseName, 0);
            if (result == -1) {
                result = this.getGroupChar(upperCaseName, 3);
            }
        } else {
            result = this.getGroupChar(upperCaseName, choice);
        }
        return result;
    }

    public int getGroupLengths(int index, char[] offsets, char[] lengths) {
        int length = 65535;
        byte b2 = 0;
        byte n2 = 0;
        int stringoffset = UCharacterUtility.toInt(this.m_groupinfo_[(index *= this.m_groupsize_) + 1], this.m_groupinfo_[index + 2]);
        offsets[0] = '\u0000';
        int i2 = 0;
        while (i2 < 32) {
            b2 = this.m_groupstring_[stringoffset];
            for (int shift = 4; shift >= 0; shift -= 4) {
                n2 = (byte)(b2 >> shift & 0xF);
                if (length == 65535 && n2 > 11) {
                    length = (char)(n2 - 12 << 4);
                    continue;
                }
                lengths[i2] = length != 65535 ? (char)((length | n2) + 12) : (char)n2;
                if (i2 < 32) {
                    offsets[i2 + 1] = (char)(offsets[i2] + lengths[i2]);
                }
                length = 65535;
                ++i2;
            }
            ++stringoffset;
        }
        return stringoffset;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getGroupName(int index, int length, int choice) {
        if (choice != 0 && choice != 2) {
            if (59 >= this.m_tokentable_.length || this.m_tokentable_[59] == '\uffff') {
                int fieldIndex = choice == 4 ? 2 : choice;
                do {
                    int oldindex = index;
                    index += UCharacterUtility.skipByteSubString(this.m_groupstring_, index, length, (byte)59);
                    length -= index - oldindex;
                } while (--fieldIndex > 0);
            } else {
                length = 0;
            }
        }
        StringBuffer stringBuffer = this.m_utilStringBuffer_;
        synchronized (stringBuffer) {
            this.m_utilStringBuffer_.delete(0, this.m_utilStringBuffer_.length());
            int i2 = 0;
            while (i2 < length) {
                byte b2 = this.m_groupstring_[index + i2];
                ++i2;
                if (b2 >= this.m_tokentable_.length) {
                    if (b2 == 59) break;
                    this.m_utilStringBuffer_.append(b2);
                    continue;
                }
                char token = this.m_tokentable_[b2 & 0xFF];
                if (token == '\ufffe') {
                    token = this.m_tokentable_[b2 << 8 | this.m_groupstring_[index + i2] & 0xFF];
                    ++i2;
                }
                if (token == '\uffff') {
                    if (b2 == 59) {
                        if (this.m_utilStringBuffer_.length() != 0 || choice != 2) break;
                        continue;
                    }
                    this.m_utilStringBuffer_.append((char)(b2 & 0xFF));
                    continue;
                }
                UCharacterUtility.getNullTermByteSubString(this.m_utilStringBuffer_, this.m_tokenstring_, token);
            }
            if (this.m_utilStringBuffer_.length() > 0) {
                return this.m_utilStringBuffer_.toString();
            }
        }
        return null;
    }

    public String getExtendedName(int ch2) {
        String result = this.getName(ch2, 0);
        if (result == null && result == null) {
            result = this.getExtendedOr10Name(ch2);
        }
        return result;
    }

    public int getGroup(int codepoint) {
        int endGroup = this.m_groupcount_;
        int msb = UCharacterName.getCodepointMSB(codepoint);
        int result = 0;
        while (result < endGroup - 1) {
            int gindex = result + endGroup >> 1;
            if (msb < this.getGroupMSB(gindex)) {
                endGroup = gindex;
                continue;
            }
            result = gindex;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getExtendedOr10Name(int ch2) {
        String result = null;
        if (result == null) {
            int type = UCharacterName.getType(ch2);
            result = type >= TYPE_NAMES_.length ? UNKNOWN_TYPE_NAME_ : TYPE_NAMES_[type];
            StringBuffer stringBuffer = this.m_utilStringBuffer_;
            synchronized (stringBuffer) {
                this.m_utilStringBuffer_.delete(0, this.m_utilStringBuffer_.length());
                this.m_utilStringBuffer_.append('<');
                this.m_utilStringBuffer_.append(result);
                this.m_utilStringBuffer_.append('-');
                String chStr = Integer.toHexString(ch2).toUpperCase(Locale.ENGLISH);
                for (int zeros = 4 - chStr.length(); zeros > 0; --zeros) {
                    this.m_utilStringBuffer_.append('0');
                }
                this.m_utilStringBuffer_.append(chStr);
                this.m_utilStringBuffer_.append('>');
                result = this.m_utilStringBuffer_.toString();
            }
        }
        return result;
    }

    public int getGroupMSB(int gindex) {
        if (gindex >= this.m_groupcount_) {
            return -1;
        }
        return this.m_groupinfo_[gindex * this.m_groupsize_];
    }

    public static int getCodepointMSB(int codepoint) {
        return codepoint >> 5;
    }

    public static int getGroupLimit(int msb) {
        return (msb << 5) + 32;
    }

    public static int getGroupMin(int msb) {
        return msb << 5;
    }

    public static int getGroupOffset(int codepoint) {
        return codepoint & 0x1F;
    }

    public static int getGroupMinFromCodepoint(int codepoint) {
        return codepoint & 0xFFFFFFE0;
    }

    public int getAlgorithmLength() {
        return this.m_algorithm_.length;
    }

    public int getAlgorithmStart(int index) {
        return this.m_algorithm_[index].m_rangestart_;
    }

    public int getAlgorithmEnd(int index) {
        return this.m_algorithm_[index].m_rangeend_;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getAlgorithmName(int index, int codepoint) {
        String result = null;
        StringBuffer stringBuffer = this.m_utilStringBuffer_;
        synchronized (stringBuffer) {
            this.m_utilStringBuffer_.delete(0, this.m_utilStringBuffer_.length());
            this.m_algorithm_[index].appendName(codepoint, this.m_utilStringBuffer_);
            result = this.m_utilStringBuffer_.toString();
        }
        return result;
    }

    public synchronized String getGroupName(int ch2, int choice) {
        int group;
        int msb = UCharacterName.getCodepointMSB(ch2);
        if (msb == this.m_groupinfo_[(group = this.getGroup(ch2)) * this.m_groupsize_]) {
            int index = this.getGroupLengths(group, this.m_groupoffsets_, this.m_grouplengths_);
            int offset = ch2 & 0x1F;
            return this.getGroupName(index + this.m_groupoffsets_[offset], this.m_grouplengths_[offset], choice);
        }
        return null;
    }

    public int getMaxCharNameLength() {
        if (this.initNameSetsLengths()) {
            return this.m_maxNameLength_;
        }
        return 0;
    }

    public int getMaxISOCommentLength() {
        if (this.initNameSetsLengths()) {
            return this.m_maxISOCommentLength_;
        }
        return 0;
    }

    public void getCharNameCharacters(UnicodeSet set) {
        this.convert(this.m_nameSet_, set);
    }

    public void getISOCommentCharacters(UnicodeSet set) {
        this.convert(this.m_ISOCommentSet_, set);
    }

    boolean setToken(char[] token, byte[] tokenstring) {
        if (token != null && tokenstring != null && token.length > 0 && tokenstring.length > 0) {
            this.m_tokentable_ = token;
            this.m_tokenstring_ = tokenstring;
            return true;
        }
        return false;
    }

    boolean setAlgorithm(AlgorithmName[] alg2) {
        if (alg2 != null && alg2.length != 0) {
            this.m_algorithm_ = alg2;
            return true;
        }
        return false;
    }

    boolean setGroupCountSize(int count, int size) {
        if (count <= 0 || size <= 0) {
            return false;
        }
        this.m_groupcount_ = count;
        this.m_groupsize_ = size;
        return true;
    }

    boolean setGroup(char[] group, byte[] groupstring) {
        if (group != null && groupstring != null && group.length > 0 && groupstring.length > 0) {
            this.m_groupinfo_ = group;
            this.m_groupstring_ = groupstring;
            return true;
        }
        return false;
    }

    private UCharacterName() throws IOException {
        InputStream is2 = ICUData.getRequiredStream(NAME_FILE_NAME_);
        BufferedInputStream b2 = new BufferedInputStream(is2, 100000);
        UCharacterNameReader reader = new UCharacterNameReader(b2);
        reader.read(this);
        b2.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getAlgName(int ch2, int choice) {
        if (choice == 0 || choice == 2) {
            StringBuffer stringBuffer = this.m_utilStringBuffer_;
            synchronized (stringBuffer) {
                this.m_utilStringBuffer_.delete(0, this.m_utilStringBuffer_.length());
                for (int index = this.m_algorithm_.length - 1; index >= 0; --index) {
                    if (!this.m_algorithm_[index].contains(ch2)) continue;
                    this.m_algorithm_[index].appendName(ch2, this.m_utilStringBuffer_);
                    return this.m_utilStringBuffer_.toString();
                }
            }
        }
        return null;
    }

    private synchronized int getGroupChar(String name, int choice) {
        for (int i2 = 0; i2 < this.m_groupcount_; ++i2) {
            int startgpstrindex = this.getGroupLengths(i2, this.m_groupoffsets_, this.m_grouplengths_);
            int result = this.getGroupChar(startgpstrindex, this.m_grouplengths_, name, choice);
            if (result == -1) continue;
            return this.m_groupinfo_[i2 * this.m_groupsize_] << 5 | result;
        }
        return -1;
    }

    private int getGroupChar(int index, char[] length, String name, int choice) {
        byte b2 = 0;
        int namelen = name.length();
        for (int result = 0; result <= 32; ++result) {
            int nindex = 0;
            int len = length[result];
            if (choice != 0 && choice != 2) {
                int fieldIndex = choice == 4 ? 2 : choice;
                do {
                    int oldindex = index;
                    index += UCharacterUtility.skipByteSubString(this.m_groupstring_, index, len, (byte)59);
                    len -= index - oldindex;
                } while (--fieldIndex > 0);
            }
            int count = 0;
            while (count < len && nindex != -1 && nindex < namelen) {
                b2 = this.m_groupstring_[index + count];
                ++count;
                if (b2 >= this.m_tokentable_.length) {
                    if (name.charAt(nindex++) == (b2 & 0xFF)) continue;
                    nindex = -1;
                    continue;
                }
                char token = this.m_tokentable_[b2 & 0xFF];
                if (token == '\ufffe') {
                    token = this.m_tokentable_[b2 << 8 | this.m_groupstring_[index + count] & 0xFF];
                    ++count;
                }
                if (token == '\uffff') {
                    if (name.charAt(nindex++) == (b2 & 0xFF)) continue;
                    nindex = -1;
                    continue;
                }
                nindex = UCharacterUtility.compareNullTermByteSubString(name, this.m_tokenstring_, nindex, token);
            }
            if (namelen == nindex && (count == len || this.m_groupstring_[index + count] == 59)) {
                return result;
            }
            index += len;
        }
        return -1;
    }

    private static int getType(int ch2) {
        if (UCharacterUtility.isNonCharacter(ch2)) {
            return 30;
        }
        int result = UCharacter.getType(ch2);
        if (result == 18) {
            result = ch2 <= 56319 ? 31 : 32;
        }
        return result;
    }

    private static int getExtendedChar(String name, int choice) {
        if (name.charAt(0) == '<') {
            int startIndex;
            int endIndex;
            if (choice == 2 && name.charAt(endIndex = name.length() - 1) == '>' && (startIndex = name.lastIndexOf(45)) >= 0) {
                ++startIndex;
                int result = -1;
                try {
                    result = Integer.parseInt(name.substring(startIndex, endIndex), 16);
                }
                catch (NumberFormatException e2) {
                    return -1;
                }
                String type = name.substring(1, startIndex - 1);
                int length = TYPE_NAMES_.length;
                for (int i2 = 0; i2 < length; ++i2) {
                    if (type.compareTo(TYPE_NAMES_[i2]) != 0) continue;
                    if (UCharacterName.getType(result) != i2) break;
                    return result;
                }
            }
            return -1;
        }
        return -2;
    }

    private static void add(int[] set, char ch2) {
        int n2 = ch2 >>> 5;
        set[n2] = set[n2] | 1 << (ch2 & 0x1F);
    }

    private static boolean contains(int[] set, char ch2) {
        return (set[ch2 >>> 5] & 1 << (ch2 & 0x1F)) != 0;
    }

    private static int add(int[] set, String str) {
        int result = str.length();
        for (int i2 = result - 1; i2 >= 0; --i2) {
            UCharacterName.add(set, str.charAt(i2));
        }
        return result;
    }

    private static int add(int[] set, StringBuffer str) {
        int result = str.length();
        for (int i2 = result - 1; i2 >= 0; --i2) {
            UCharacterName.add(set, str.charAt(i2));
        }
        return result;
    }

    private int addAlgorithmName(int maxlength) {
        int result = 0;
        for (int i2 = this.m_algorithm_.length - 1; i2 >= 0; --i2) {
            result = this.m_algorithm_[i2].add(this.m_nameSet_, maxlength);
            if (result <= maxlength) continue;
            maxlength = result;
        }
        return maxlength;
    }

    private int addExtendedName(int maxlength) {
        for (int i2 = TYPE_NAMES_.length - 1; i2 >= 0; --i2) {
            int length = 9 + UCharacterName.add(this.m_nameSet_, TYPE_NAMES_[i2]);
            if (length <= maxlength) continue;
            maxlength = length;
        }
        return maxlength;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int[] addGroupName(int offset, int length, byte[] tokenlength, int[] set) {
        int resultnlength = 0;
        int resultplength = 0;
        while (resultplength < length) {
            char b2 = (char)(this.m_groupstring_[offset + resultplength] & 0xFF);
            ++resultplength;
            if (b2 == ';') break;
            if (b2 >= this.m_tokentable_.length) {
                UCharacterName.add(set, b2);
                ++resultnlength;
                continue;
            }
            char token = this.m_tokentable_[b2 & 0xFF];
            if (token == '\ufffe') {
                b2 = (char)(b2 << 8 | this.m_groupstring_[offset + resultplength] & 0xFF);
                token = this.m_tokentable_[b2];
                ++resultplength;
            }
            if (token == '\uffff') {
                UCharacterName.add(set, b2);
                ++resultnlength;
                continue;
            }
            byte tlength = tokenlength[b2];
            if (tlength == 0) {
                StringBuffer stringBuffer = this.m_utilStringBuffer_;
                synchronized (stringBuffer) {
                    this.m_utilStringBuffer_.delete(0, this.m_utilStringBuffer_.length());
                    UCharacterUtility.getNullTermByteSubString(this.m_utilStringBuffer_, this.m_tokenstring_, token);
                    tlength = (byte)UCharacterName.add(set, this.m_utilStringBuffer_);
                }
                tokenlength[b2] = tlength;
            }
            resultnlength += tlength;
        }
        this.m_utilIntBuffer_[0] = resultnlength;
        this.m_utilIntBuffer_[1] = resultplength;
        return this.m_utilIntBuffer_;
    }

    private void addGroupName(int maxlength) {
        int maxisolength = 0;
        char[] offsets = new char[34];
        char[] lengths = new char[34];
        byte[] tokenlengths = new byte[this.m_tokentable_.length];
        for (int i2 = 0; i2 < this.m_groupcount_; ++i2) {
            int offset = this.getGroupLengths(i2, offsets, lengths);
            for (int linenumber = 0; linenumber < 32; ++linenumber) {
                int lineoffset = offset + offsets[linenumber];
                int length = lengths[linenumber];
                if (length == 0) continue;
                int[] parsed = this.addGroupName(lineoffset, length, tokenlengths, this.m_nameSet_);
                if (parsed[0] > maxlength) {
                    maxlength = parsed[0];
                }
                lineoffset += parsed[1];
                if (parsed[1] >= length) continue;
                if ((parsed = this.addGroupName(lineoffset, length -= parsed[1], tokenlengths, this.m_nameSet_))[0] > maxlength) {
                    maxlength = parsed[0];
                }
                lineoffset += parsed[1];
                if (parsed[1] >= length || (parsed = this.addGroupName(lineoffset, length -= parsed[1], tokenlengths, this.m_ISOCommentSet_))[1] <= maxisolength) continue;
                maxisolength = length;
            }
        }
        this.m_maxISOCommentLength_ = maxisolength;
        this.m_maxNameLength_ = maxlength;
    }

    private boolean initNameSetsLengths() {
        if (this.m_maxNameLength_ > 0) {
            return true;
        }
        String extra = "0123456789ABCDEF<>-";
        for (int i2 = extra.length() - 1; i2 >= 0; --i2) {
            UCharacterName.add(this.m_nameSet_, extra.charAt(i2));
        }
        this.m_maxNameLength_ = this.addAlgorithmName(0);
        this.m_maxNameLength_ = this.addExtendedName(this.m_maxNameLength_);
        this.addGroupName(this.m_maxNameLength_);
        return true;
    }

    private void convert(int[] set, UnicodeSet uset) {
        uset.clear();
        if (!this.initNameSetsLengths()) {
            return;
        }
        for (char c2 = '\u00ff'; c2 > '\u0000'; c2 = (char)(c2 - '\u0001')) {
            if (!UCharacterName.contains(set, c2)) continue;
            uset.add(c2);
        }
    }

    static {
        try {
            INSTANCE = new UCharacterName();
        }
        catch (IOException e2) {
            throw new MissingResourceException("Could not construct UCharacterName. Missing unames.icu", "", "");
        }
        TYPE_NAMES_ = new String[]{"unassigned", "uppercase letter", "lowercase letter", "titlecase letter", "modifier letter", "other letter", "non spacing mark", "enclosing mark", "combining spacing mark", "decimal digit number", "letter number", "other number", "space separator", "line separator", "paragraph separator", "control", "format", "private use area", "surrogate", "dash punctuation", "start punctuation", "end punctuation", "connector punctuation", "other punctuation", "math symbol", "currency symbol", "modifier symbol", "other symbol", "initial punctuation", "final punctuation", "noncharacter", "lead surrogate", "trail surrogate"};
    }

    static final class AlgorithmName {
        static final int TYPE_0_ = 0;
        static final int TYPE_1_ = 1;
        private int m_rangestart_;
        private int m_rangeend_;
        private byte m_type_;
        private byte m_variant_;
        private char[] m_factor_;
        private String m_prefix_;
        private byte[] m_factorstring_;
        private StringBuffer m_utilStringBuffer_ = new StringBuffer();
        private int[] m_utilIntBuffer_ = new int[256];

        AlgorithmName() {
        }

        boolean setInfo(int rangestart, int rangeend, byte type, byte variant) {
            if (rangestart >= 0 && rangestart <= rangeend && rangeend <= 0x10FFFF && (type == 0 || type == 1)) {
                this.m_rangestart_ = rangestart;
                this.m_rangeend_ = rangeend;
                this.m_type_ = type;
                this.m_variant_ = variant;
                return true;
            }
            return false;
        }

        boolean setFactor(char[] factor) {
            if (factor.length == this.m_variant_) {
                this.m_factor_ = factor;
                return true;
            }
            return false;
        }

        boolean setPrefix(String prefix) {
            if (prefix != null && prefix.length() > 0) {
                this.m_prefix_ = prefix;
                return true;
            }
            return false;
        }

        boolean setFactorString(byte[] string) {
            this.m_factorstring_ = string;
            return true;
        }

        boolean contains(int ch2) {
            return this.m_rangestart_ <= ch2 && ch2 <= this.m_rangeend_;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        void appendName(int ch2, StringBuffer str) {
            str.append(this.m_prefix_);
            switch (this.m_type_) {
                case 0: {
                    str.append(Utility.hex(ch2, this.m_variant_));
                    return;
                }
                case 1: {
                    int offset = ch2 - this.m_rangestart_;
                    int[] indexes = this.m_utilIntBuffer_;
                    int[] arrn = this.m_utilIntBuffer_;
                    synchronized (this.m_utilIntBuffer_) {
                        for (int i2 = this.m_variant_ - 1; i2 > 0; --i2) {
                            int factor = this.m_factor_[i2] & 0xFF;
                            indexes[i2] = offset % factor;
                            offset /= factor;
                        }
                        indexes[0] = offset;
                        str.append(this.getFactorString(indexes, this.m_variant_));
                        // ** MonitorExit[var6_5] (shouldn't be in output)
                        return;
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Converted monitor instructions to comments
         * Lifted jumps to return sites
         */
        int getChar(String name) {
            int prefixlen = this.m_prefix_.length();
            if (name.length() < prefixlen) return -1;
            if (!this.m_prefix_.equals(name.substring(0, prefixlen))) {
                return -1;
            }
            switch (this.m_type_) {
                case 0: {
                    try {
                        int result = Integer.parseInt(name.substring(prefixlen), 16);
                        if (this.m_rangestart_ > result) return -1;
                        if (result > this.m_rangeend_) return -1;
                        return result;
                    }
                    catch (NumberFormatException e2) {
                        return -1;
                    }
                }
                case 1: {
                    int ch2 = this.m_rangestart_;
                    while (ch2 <= this.m_rangeend_) {
                        int factor;
                        int offset = ch2 - this.m_rangestart_;
                        int[] indexes = this.m_utilIntBuffer_;
                        int[] arrn = this.m_utilIntBuffer_;
                        // MONITORENTER : this.m_utilIntBuffer_
                        for (int i2 = this.m_variant_ - 1; i2 > 0; offset /= factor, --i2) {
                            factor = this.m_factor_[i2] & 0xFF;
                            indexes[i2] = offset % factor;
                        }
                        indexes[0] = offset;
                        if (this.compareFactorString(indexes, this.m_variant_, name, prefixlen)) {
                            // MONITOREXIT : arrn
                            return ch2;
                        }
                        // MONITOREXIT : arrn
                        ++ch2;
                    }
                    return -1;
                }
            }
            return -1;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        int add(int[] set, int maxlength) {
            int length = UCharacterName.add(set, this.m_prefix_);
            switch (this.m_type_) {
                case 0: {
                    length += this.m_variant_;
                    break;
                }
                case 1: {
                    for (int i2 = this.m_variant_ - 1; i2 > 0; --i2) {
                        int maxfactorlength = 0;
                        int count = 0;
                        for (int factor = this.m_factor_[i2]; factor > 0; --factor) {
                            StringBuffer stringBuffer = this.m_utilStringBuffer_;
                            synchronized (stringBuffer) {
                                this.m_utilStringBuffer_.delete(0, this.m_utilStringBuffer_.length());
                                count = UCharacterUtility.getNullTermByteSubString(this.m_utilStringBuffer_, this.m_factorstring_, count);
                                UCharacterName.add(set, this.m_utilStringBuffer_);
                                if (this.m_utilStringBuffer_.length() > maxfactorlength) {
                                    maxfactorlength = this.m_utilStringBuffer_.length();
                                }
                                continue;
                            }
                        }
                        length += maxfactorlength;
                    }
                    break;
                }
            }
            if (length > maxlength) {
                return length;
            }
            return maxlength;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private String getFactorString(int[] index, int length) {
            int size = this.m_factor_.length;
            if (index == null || length != size) {
                return null;
            }
            StringBuffer stringBuffer = this.m_utilStringBuffer_;
            synchronized (stringBuffer) {
                this.m_utilStringBuffer_.delete(0, this.m_utilStringBuffer_.length());
                int count = 0;
                for (int i2 = 0; i2 <= --size; ++i2) {
                    char factor = this.m_factor_[i2];
                    count = UCharacterUtility.skipNullTermByteSubString(this.m_factorstring_, count, index[i2]);
                    count = UCharacterUtility.getNullTermByteSubString(this.m_utilStringBuffer_, this.m_factorstring_, count);
                    if (i2 == size) continue;
                    count = UCharacterUtility.skipNullTermByteSubString(this.m_factorstring_, count, factor - index[i2] - 1);
                }
                return this.m_utilStringBuffer_.toString();
            }
        }

        private boolean compareFactorString(int[] index, int length, String str, int offset) {
            int size = this.m_factor_.length;
            if (index == null || length != size) {
                return false;
            }
            int count = 0;
            int strcount = offset;
            for (int i2 = 0; i2 <= --size; ++i2) {
                char factor = this.m_factor_[i2];
                if ((strcount = UCharacterUtility.compareNullTermByteSubString(str, this.m_factorstring_, strcount, count = UCharacterUtility.skipNullTermByteSubString(this.m_factorstring_, count, index[i2]))) < 0) {
                    return false;
                }
                if (i2 == size) continue;
                count = UCharacterUtility.skipNullTermByteSubString(this.m_factorstring_, count, factor - index[i2]);
            }
            return strcount == str.length();
        }
    }
}

