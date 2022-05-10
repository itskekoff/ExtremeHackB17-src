package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

public final class NetUtil {
    public static final Inet4Address LOCALHOST4;
    public static final Inet6Address LOCALHOST6;
    public static final InetAddress LOCALHOST;
    public static final NetworkInterface LOOPBACK_IF;
    public static final int SOMAXCONN;
    private static final int IPV6_WORD_COUNT = 8;
    private static final int IPV6_MAX_CHAR_COUNT = 39;
    private static final int IPV6_BYTE_COUNT = 16;
    private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
    private static final int IPV6_MIN_SEPARATORS = 2;
    private static final int IPV6_MAX_SEPARATORS = 8;
    private static final int IPV4_BYTE_COUNT = 4;
    private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
    private static final int IPV4_SEPARATORS = 3;
    private static final boolean IPV4_PREFERRED;
    private static final boolean IPV6_ADDRESSES_PREFERRED;
    private static final InternalLogger logger;

    public static boolean isIpV4StackPreferred() {
        return IPV4_PREFERRED;
    }

    public static boolean isIpV6AddressesPreferred() {
        return IPV6_ADDRESSES_PREFERRED;
    }

    public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
        if (NetUtil.isValidIpV4Address(ipAddressString)) {
            StringTokenizer tokenizer = new StringTokenizer(ipAddressString, ".");
            byte[] byteAddress = new byte[4];
            for (int i2 = 0; i2 < 4; ++i2) {
                String token = tokenizer.nextToken();
                int tempInt = Integer.parseInt(token);
                byteAddress[i2] = (byte)tempInt;
            }
            return byteAddress;
        }
        if (NetUtil.isValidIpV6Address(ipAddressString)) {
            int i3;
            int percentPos;
            if (ipAddressString.charAt(0) == '[') {
                ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
            }
            if ((percentPos = ipAddressString.indexOf(37)) >= 0) {
                ipAddressString = ipAddressString.substring(0, percentPos);
            }
            StringTokenizer tokenizer = new StringTokenizer(ipAddressString, ":.", true);
            ArrayList<String> hexStrings = new ArrayList<String>();
            ArrayList<String> decStrings = new ArrayList<String>();
            String token = "";
            String prevToken = "";
            int doubleColonIndex = -1;
            while (tokenizer.hasMoreTokens()) {
                prevToken = token;
                token = tokenizer.nextToken();
                if (":".equals(token)) {
                    if (":".equals(prevToken)) {
                        doubleColonIndex = hexStrings.size();
                        continue;
                    }
                    if (prevToken.isEmpty()) continue;
                    hexStrings.add(prevToken);
                    continue;
                }
                if (!".".equals(token)) continue;
                decStrings.add(prevToken);
            }
            if (":".equals(prevToken)) {
                if (":".equals(token)) {
                    doubleColonIndex = hexStrings.size();
                } else {
                    hexStrings.add(token);
                }
            } else if (".".equals(prevToken)) {
                decStrings.add(token);
            }
            int hexStringsLength = 8;
            if (!decStrings.isEmpty()) {
                hexStringsLength -= 2;
            }
            if (doubleColonIndex != -1) {
                int numberToInsert = hexStringsLength - hexStrings.size();
                for (i3 = 0; i3 < numberToInsert; ++i3) {
                    hexStrings.add(doubleColonIndex, "0");
                }
            }
            byte[] ipByteArray = new byte[16];
            for (i3 = 0; i3 < hexStrings.size(); ++i3) {
                NetUtil.convertToBytes((String)hexStrings.get(i3), ipByteArray, i3 << 1);
            }
            for (i3 = 0; i3 < decStrings.size(); ++i3) {
                ipByteArray[i3 + 12] = (byte)(Integer.parseInt((String)decStrings.get(i3)) & 0xFF);
            }
            return ipByteArray;
        }
        return null;
    }

    private static void convertToBytes(String hexWord, byte[] ipByteArray, int byteIndex) {
        int charValue;
        int hexWordLength = hexWord.length();
        int hexWordIndex = 0;
        ipByteArray[byteIndex] = 0;
        ipByteArray[byteIndex + 1] = 0;
        if (hexWordLength > 3) {
            charValue = NetUtil.getIntValue(hexWord.charAt(hexWordIndex++));
            int n2 = byteIndex;
            ipByteArray[n2] = (byte)(ipByteArray[n2] | charValue << 4);
        }
        if (hexWordLength > 2) {
            charValue = NetUtil.getIntValue(hexWord.charAt(hexWordIndex++));
            int n3 = byteIndex;
            ipByteArray[n3] = (byte)(ipByteArray[n3] | charValue);
        }
        if (hexWordLength > 1) {
            charValue = NetUtil.getIntValue(hexWord.charAt(hexWordIndex++));
            int n4 = byteIndex + 1;
            ipByteArray[n4] = (byte)(ipByteArray[n4] | charValue << 4);
        }
        charValue = NetUtil.getIntValue(hexWord.charAt(hexWordIndex));
        int n5 = byteIndex + 1;
        ipByteArray[n5] = (byte)(ipByteArray[n5] | charValue & 0xF);
    }

    private static int getIntValue(char c2) {
        switch (c2) {
            case '0': {
                return 0;
            }
            case '1': {
                return 1;
            }
            case '2': {
                return 2;
            }
            case '3': {
                return 3;
            }
            case '4': {
                return 4;
            }
            case '5': {
                return 5;
            }
            case '6': {
                return 6;
            }
            case '7': {
                return 7;
            }
            case '8': {
                return 8;
            }
            case '9': {
                return 9;
            }
        }
        c2 = Character.toLowerCase(c2);
        switch (c2) {
            case 'a': {
                return 10;
            }
            case 'b': {
                return 11;
            }
            case 'c': {
                return 12;
            }
            case 'd': {
                return 13;
            }
            case 'e': {
                return 14;
            }
            case 'f': {
                return 15;
            }
        }
        return 0;
    }

    public static String intToIpAddress(int i2) {
        StringBuilder buf2 = new StringBuilder(15);
        buf2.append(i2 >> 24 & 0xFF);
        buf2.append('.');
        buf2.append(i2 >> 16 & 0xFF);
        buf2.append('.');
        buf2.append(i2 >> 8 & 0xFF);
        buf2.append('.');
        buf2.append(i2 & 0xFF);
        return buf2.toString();
    }

    public static String bytesToIpAddress(byte[] bytes) {
        return NetUtil.bytesToIpAddress(bytes, 0, bytes.length);
    }

    public static String bytesToIpAddress(byte[] bytes, int offset, int length) {
        switch (length) {
            case 4: {
                return new StringBuilder(15).append(bytes[offset] & 0xFF).append('.').append(bytes[offset + 1] & 0xFF).append('.').append(bytes[offset + 2] & 0xFF).append('.').append(bytes[offset + 3] & 0xFF).toString();
            }
            case 16: {
                return NetUtil.toAddressString(bytes, offset, false);
            }
        }
        throw new IllegalArgumentException("length: " + length + " (expected: 4 or 16)");
    }

    public static boolean isValidIpV6Address(String ipAddress) {
        int percentIdx;
        int length = ipAddress.length();
        boolean doubleColon = false;
        int numberOfColons = 0;
        int numberOfPeriods = 0;
        StringBuilder word = new StringBuilder();
        char c2 = '\u0000';
        int startOffset = 0;
        int endOffset = ipAddress.length();
        if (endOffset < 2) {
            return false;
        }
        if (ipAddress.charAt(0) == '[') {
            if (ipAddress.charAt(endOffset - 1) != ']') {
                return false;
            }
            startOffset = 1;
            --endOffset;
        }
        if ((percentIdx = ipAddress.indexOf(37, startOffset)) >= 0) {
            endOffset = percentIdx;
        }
        block4: for (int i2 = startOffset; i2 < endOffset; ++i2) {
            char prevChar = c2;
            c2 = ipAddress.charAt(i2);
            switch (c2) {
                case '.': {
                    if (++numberOfPeriods > 3) {
                        return false;
                    }
                    if (!NetUtil.isValidIp4Word(word.toString())) {
                        return false;
                    }
                    if (numberOfColons != 6 && !doubleColon) {
                        return false;
                    }
                    if (numberOfColons == 7 && ipAddress.charAt(startOffset) != ':' && ipAddress.charAt(1 + startOffset) != ':') {
                        return false;
                    }
                    word.delete(0, word.length());
                    continue block4;
                }
                case ':': {
                    if (i2 == startOffset && (ipAddress.length() <= i2 || ipAddress.charAt(i2 + 1) != ':')) {
                        return false;
                    }
                    if (++numberOfColons > 7) {
                        return false;
                    }
                    if (numberOfPeriods > 0) {
                        return false;
                    }
                    if (prevChar == ':') {
                        if (doubleColon) {
                            return false;
                        }
                        doubleColon = true;
                    }
                    word.delete(0, word.length());
                    continue block4;
                }
                default: {
                    if (word != null && word.length() > 3) {
                        return false;
                    }
                    if (!NetUtil.isValidHexChar(c2)) {
                        return false;
                    }
                    word.append(c2);
                }
            }
        }
        if (numberOfPeriods > 0) {
            if (numberOfPeriods != 3 || !NetUtil.isValidIp4Word(word.toString()) || numberOfColons >= 7) {
                return false;
            }
        } else {
            if (numberOfColons != 7 && !doubleColon) {
                return false;
            }
            if (word.length() == 0 && ipAddress.charAt(length - 1 - startOffset) == ':' && ipAddress.charAt(length - 2 - startOffset) != ':') {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidIp4Word(String word) {
        if (word.length() < 1 || word.length() > 3) {
            return false;
        }
        for (int i2 = 0; i2 < word.length(); ++i2) {
            char c2 = word.charAt(i2);
            if (c2 >= '0' && c2 <= '9') continue;
            return false;
        }
        return Integer.parseInt(word) <= 255;
    }

    private static boolean isValidHexChar(char c2) {
        return c2 >= '0' && c2 <= '9' || c2 >= 'A' && c2 <= 'F' || c2 >= 'a' && c2 <= 'f';
    }

    private static boolean isValidNumericChar(char c2) {
        return c2 >= '0' && c2 <= '9';
    }

    public static boolean isValidIpV4Address(String value) {
        int periods = 0;
        int length = value.length();
        if (length > 15) {
            return false;
        }
        StringBuilder word = new StringBuilder();
        for (int i2 = 0; i2 < length; ++i2) {
            char c2 = value.charAt(i2);
            if (c2 == '.') {
                if (++periods > 3) {
                    return false;
                }
                if (word.length() == 0) {
                    return false;
                }
                if (Integer.parseInt(word.toString()) > 255) {
                    return false;
                }
                word.delete(0, word.length());
                continue;
            }
            if (!Character.isDigit(c2)) {
                return false;
            }
            if (word.length() > 2) {
                return false;
            }
            word.append(c2);
        }
        if (word.length() == 0 || Integer.parseInt(word.toString()) > 255) {
            return false;
        }
        return periods == 3;
    }

    public static Inet6Address getByName(CharSequence ip2) {
        return NetUtil.getByName(ip2, true);
    }

    public static Inet6Address getByName(CharSequence ip2, boolean ipv4Mapped) {
        boolean isCompressed;
        int tmp;
        int i2;
        byte[] bytes = new byte[16];
        int ipLength = ip2.length();
        int compressBegin = 0;
        int compressLength = 0;
        int currentIndex = 0;
        int value = 0;
        int begin = -1;
        int ipv6Seperators = 0;
        int ipv4Seperators = 0;
        boolean needsShift = false;
        block6: for (i2 = 0; i2 < ipLength; ++i2) {
            char c2 = ip2.charAt(i2);
            switch (c2) {
                case ':': {
                    if (i2 - begin > 4 || ipv4Seperators > 0 || ++ipv6Seperators > 8 || currentIndex + 1 >= bytes.length) {
                        return null;
                    }
                    value <<= 4 - (i2 - begin) << 2;
                    if (compressLength > 0) {
                        compressLength -= 2;
                    }
                    bytes[currentIndex++] = (byte)((value & 0xF) << 4 | value >> 4 & 0xF);
                    bytes[currentIndex++] = (byte)((value >> 8 & 0xF) << 4 | value >> 12 & 0xF);
                    tmp = i2 + 1;
                    if (tmp < ipLength && ip2.charAt(tmp) == ':') {
                        if (compressBegin != 0 || ++tmp < ipLength && ip2.charAt(tmp) == ':') {
                            return null;
                        }
                        needsShift = ++ipv6Seperators == 2 && value == 0;
                        compressBegin = currentIndex;
                        compressLength = bytes.length - compressBegin - 2;
                        ++i2;
                    }
                    value = 0;
                    begin = -1;
                    continue block6;
                }
                case '.': {
                    if (i2 - begin > 3 || ++ipv4Seperators > 3 || ipv6Seperators > 0 && currentIndex + compressLength < 12 || i2 + 1 >= ipLength || currentIndex >= bytes.length || begin < 0 || begin == 0 && (i2 == 3 && (!NetUtil.isValidNumericChar(ip2.charAt(2)) || !NetUtil.isValidNumericChar(ip2.charAt(1)) || !NetUtil.isValidNumericChar(ip2.charAt(0))) || i2 == 2 && (!NetUtil.isValidNumericChar(ip2.charAt(1)) || !NetUtil.isValidNumericChar(ip2.charAt(0))) || i2 == 1 && !NetUtil.isValidNumericChar(ip2.charAt(0)))) {
                        return null;
                    }
                    if ((begin = ((value <<= 3 - (i2 - begin) << 2) & 0xF) * 100 + (value >> 4 & 0xF) * 10 + (value >> 8 & 0xF)) < 0 || begin > 255) {
                        return null;
                    }
                    bytes[currentIndex++] = (byte)begin;
                    value = 0;
                    begin = -1;
                    continue block6;
                }
                default: {
                    if (!NetUtil.isValidHexChar(c2) || ipv4Seperators > 0 && !NetUtil.isValidNumericChar(c2)) {
                        return null;
                    }
                    if (begin < 0) {
                        begin = i2;
                    } else if (i2 - begin > 4) {
                        return null;
                    }
                    value += NetUtil.getIntValue(c2) << (i2 - begin << 2);
                }
            }
        }
        boolean bl2 = isCompressed = compressBegin > 0;
        if (ipv4Seperators > 0) {
            if (begin > 0 && i2 - begin > 3 || ipv4Seperators != 3 || currentIndex >= bytes.length) {
                return null;
            }
            if (ipv6Seperators == 0) {
                compressLength = 12;
            } else if (ipv6Seperators >= 2 && ip2.charAt(ipLength - 1) != ':' && (!isCompressed && ipv6Seperators == 6 && ip2.charAt(0) != ':' || isCompressed && ipv6Seperators + 1 < 8 && (ip2.charAt(0) != ':' || compressBegin <= 2))) {
                compressLength -= 2;
            } else {
                return null;
            }
            value <<= 3 - (i2 - begin) << 2;
            begin = (value & 0xF) * 100 + (value >> 4 & 0xF) * 10 + (value >> 8 & 0xF);
            if (begin < 0 || begin > 255) {
                return null;
            }
            bytes[currentIndex++] = (byte)begin;
        } else {
            tmp = ipLength - 1;
            if (begin > 0 && i2 - begin > 4 || ipv6Seperators < 2 || !isCompressed && (ipv6Seperators + 1 != 8 || ip2.charAt(0) == ':' || ip2.charAt(tmp) == ':') || isCompressed && (ipv6Seperators > 8 || ipv6Seperators == 8 && (compressBegin <= 2 && ip2.charAt(0) != ':' || compressBegin >= 14 && ip2.charAt(tmp) != ':')) || currentIndex + 1 >= bytes.length) {
                return null;
            }
            if (begin >= 0 && i2 - begin <= 4) {
                value <<= 4 - (i2 - begin) << 2;
            }
            bytes[currentIndex++] = (byte)((value & 0xF) << 4 | value >> 4 & 0xF);
            bytes[currentIndex++] = (byte)((value >> 8 & 0xF) << 4 | value >> 12 & 0xF);
        }
        i2 = currentIndex + compressLength;
        if (needsShift || i2 >= bytes.length) {
            if (i2 >= bytes.length) {
                ++compressBegin;
            }
            for (i2 = currentIndex; i2 < bytes.length; ++i2) {
                for (begin = bytes.length - 1; begin >= compressBegin; --begin) {
                    bytes[begin] = bytes[begin - 1];
                }
                bytes[begin] = 0;
                ++compressBegin;
            }
        } else {
            for (i2 = 0; i2 < compressLength && (currentIndex = (begin = i2 + compressBegin) + compressLength) < bytes.length; ++i2) {
                bytes[currentIndex] = bytes[begin];
                bytes[begin] = 0;
            }
        }
        if (ipv4Mapped && ipv4Seperators > 0 && bytes[0] == 0 && bytes[1] == 0 && bytes[2] == 0 && bytes[3] == 0 && bytes[4] == 0 && bytes[5] == 0 && bytes[6] == 0 && bytes[7] == 0 && bytes[8] == 0 && bytes[9] == 0) {
            bytes[11] = -1;
            bytes[10] = -1;
        }
        try {
            return Inet6Address.getByAddress(null, bytes, -1);
        }
        catch (UnknownHostException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static String toSocketAddressString(InetSocketAddress addr) {
        StringBuilder sb2;
        String port = String.valueOf(addr.getPort());
        if (addr.isUnresolved()) {
            String hostString = PlatformDependent.javaVersion() >= 7 ? addr.getHostString() : addr.getHostName();
            sb2 = NetUtil.newSocketAddressStringBuilder(hostString, port, !NetUtil.isValidIpV6Address(hostString));
        } else {
            InetAddress address = addr.getAddress();
            String hostString = NetUtil.toAddressString(address);
            sb2 = NetUtil.newSocketAddressStringBuilder(hostString, port, address instanceof Inet4Address);
        }
        return sb2.append(':').append(port).toString();
    }

    public static String toSocketAddressString(String host, int port) {
        String portStr = String.valueOf(port);
        return NetUtil.newSocketAddressStringBuilder(host, portStr, !NetUtil.isValidIpV6Address(host)).append(':').append(portStr).toString();
    }

    private static StringBuilder newSocketAddressStringBuilder(String host, String port, boolean ipv4) {
        int hostLen = host.length();
        if (ipv4) {
            return new StringBuilder(hostLen + 1 + port.length()).append(host);
        }
        StringBuilder stringBuilder = new StringBuilder(hostLen + 3 + port.length());
        if (hostLen > 1 && host.charAt(0) == '[' && host.charAt(hostLen - 1) == ']') {
            return stringBuilder.append(host);
        }
        return stringBuilder.append('[').append(host).append(']');
    }

    public static String toAddressString(InetAddress ip2) {
        return NetUtil.toAddressString(ip2, false);
    }

    public static String toAddressString(InetAddress ip2, boolean ipv4Mapped) {
        if (ip2 instanceof Inet4Address) {
            return ip2.getHostAddress();
        }
        if (!(ip2 instanceof Inet6Address)) {
            throw new IllegalArgumentException("Unhandled type: " + ip2);
        }
        return NetUtil.toAddressString(ip2.getAddress(), 0, ipv4Mapped);
    }

    private static String toAddressString(byte[] bytes, int offset, boolean ipv4Mapped) {
        int i2;
        int[] words = new int[8];
        int end = offset + words.length;
        for (i2 = offset; i2 < end; ++i2) {
            words[i2] = (bytes[i2 << 1] & 0xFF) << 8 | bytes[(i2 << 1) + 1] & 0xFF;
        }
        int currentStart = -1;
        int currentLength = 0;
        int shortestStart = -1;
        int shortestLength = 0;
        for (i2 = 0; i2 < words.length; ++i2) {
            if (words[i2] == 0) {
                if (currentStart >= 0) continue;
                currentStart = i2;
                continue;
            }
            if (currentStart < 0) continue;
            currentLength = i2 - currentStart;
            if (currentLength > shortestLength) {
                shortestStart = currentStart;
                shortestLength = currentLength;
            }
            currentStart = -1;
        }
        if (currentStart >= 0 && (currentLength = i2 - currentStart) > shortestLength) {
            shortestStart = currentStart;
            shortestLength = currentLength;
        }
        if (shortestLength == 1) {
            shortestLength = 0;
            shortestStart = -1;
        }
        int shortestEnd = shortestStart + shortestLength;
        StringBuilder b2 = new StringBuilder(39);
        if (shortestEnd < 0) {
            b2.append(Integer.toHexString(words[0]));
            for (i2 = 1; i2 < words.length; ++i2) {
                b2.append(':');
                b2.append(Integer.toHexString(words[i2]));
            }
        } else {
            boolean isIpv4Mapped;
            if (NetUtil.inRangeEndExclusive(0, shortestStart, shortestEnd)) {
                b2.append("::");
                isIpv4Mapped = ipv4Mapped && shortestEnd == 5 && words[5] == 65535;
            } else {
                b2.append(Integer.toHexString(words[0]));
                isIpv4Mapped = false;
            }
            for (i2 = 1; i2 < words.length; ++i2) {
                if (!NetUtil.inRangeEndExclusive(i2, shortestStart, shortestEnd)) {
                    if (!NetUtil.inRangeEndExclusive(i2 - 1, shortestStart, shortestEnd)) {
                        if (!isIpv4Mapped || i2 == 6) {
                            b2.append(':');
                        } else {
                            b2.append('.');
                        }
                    }
                    if (isIpv4Mapped && i2 > 5) {
                        b2.append(words[i2] >> 8);
                        b2.append('.');
                        b2.append(words[i2] & 0xFF);
                        continue;
                    }
                    b2.append(Integer.toHexString(words[i2]));
                    continue;
                }
                if (NetUtil.inRangeEndExclusive(i2 - 1, shortestStart, shortestEnd)) continue;
                b2.append("::");
            }
        }
        return b2.toString();
    }

    private static boolean inRangeEndExclusive(int value, int start, int end) {
        return value >= start && value < end;
    }

    private NetUtil() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        Enumeration<InetAddress> i2;
        IPV4_PREFERRED = Boolean.getBoolean("java.net.preferIPv4Stack");
        IPV6_ADDRESSES_PREFERRED = Boolean.getBoolean("java.net.preferIPv6Addresses");
        logger = InternalLoggerFactory.getInstance(NetUtil.class);
        logger.debug("-Djava.net.preferIPv4Stack: {}", (Object)IPV4_PREFERRED);
        logger.debug("-Djava.net.preferIPv6Addresses: {}", (Object)IPV6_ADDRESSES_PREFERRED);
        byte[] LOCALHOST4_BYTES = new byte[]{127, 0, 0, 1};
        byte[] LOCALHOST6_BYTES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        Inet4Address localhost4 = null;
        try {
            localhost4 = (Inet4Address)InetAddress.getByAddress("localhost", LOCALHOST4_BYTES);
        }
        catch (Exception e2) {
            PlatformDependent.throwException(e2);
        }
        LOCALHOST4 = localhost4;
        Inet6Address localhost6 = null;
        try {
            localhost6 = (Inet6Address)InetAddress.getByAddress("localhost", LOCALHOST6_BYTES);
        }
        catch (Exception e3) {
            PlatformDependent.throwException(e3);
        }
        LOCALHOST6 = localhost6;
        ArrayList<NetworkInterface> ifaces = new ArrayList<NetworkInterface>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    if (!SocketUtils.addressesFromNetworkInterface(iface).hasMoreElements()) continue;
                    ifaces.add(iface);
                }
            }
        }
        catch (SocketException e4) {
            logger.warn("Failed to retrieve the list of available network interfaces", e4);
        }
        NetworkInterface loopbackIface = null;
        InetAddress loopbackAddr = null;
        block14: for (NetworkInterface iface : ifaces) {
            i2 = SocketUtils.addressesFromNetworkInterface(iface);
            while (i2.hasMoreElements()) {
                InetAddress addr = i2.nextElement();
                if (!addr.isLoopbackAddress()) continue;
                loopbackIface = iface;
                loopbackAddr = addr;
                break block14;
            }
        }
        if (loopbackIface == null) {
            try {
                for (NetworkInterface iface : ifaces) {
                    if (!iface.isLoopback() || !(i2 = SocketUtils.addressesFromNetworkInterface(iface)).hasMoreElements()) continue;
                    loopbackIface = iface;
                    loopbackAddr = i2.nextElement();
                    break;
                }
                if (loopbackIface == null) {
                    logger.warn("Failed to find the loopback interface");
                }
            }
            catch (SocketException e5) {
                logger.warn("Failed to find the loopback interface", e5);
            }
        }
        if (loopbackIface != null) {
            logger.debug("Loopback interface: {} ({}, {})", loopbackIface.getName(), loopbackIface.getDisplayName(), loopbackAddr.getHostAddress());
        } else if (loopbackAddr == null) {
            try {
                if (NetworkInterface.getByInetAddress(LOCALHOST6) != null) {
                    logger.debug("Using hard-coded IPv6 localhost address: {}", (Object)localhost6);
                    loopbackAddr = localhost6;
                }
            }
            catch (Exception exception) {
            }
            finally {
                if (loopbackAddr == null) {
                    logger.debug("Using hard-coded IPv4 localhost address: {}", (Object)localhost4);
                    loopbackAddr = localhost4;
                }
            }
        }
        LOOPBACK_IF = loopbackIface;
        LOCALHOST = loopbackAddr;
        SOMAXCONN = AccessController.doPrivileged(new PrivilegedAction<Integer>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Integer run() {
                int somaxconn = PlatformDependent.isWindows() ? 200 : 128;
                File file = new File("/proc/sys/net/core/somaxconn");
                BufferedReader in2 = null;
                try {
                    if (file.exists()) {
                        in2 = new BufferedReader(new FileReader(file));
                        somaxconn = Integer.parseInt(in2.readLine());
                        if (logger.isDebugEnabled()) {
                            logger.debug("{}: {}", (Object)file, (Object)somaxconn);
                        }
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("{}: {} (non-existent)", (Object)file, (Object)somaxconn);
                    }
                }
                catch (Exception e2) {
                    logger.debug("Failed to get SOMAXCONN from: {}", (Object)file, (Object)e2);
                }
                finally {
                    if (in2 != null) {
                        try {
                            in2.close();
                        }
                        catch (Exception exception) {}
                    }
                }
                return somaxconn;
            }
        });
    }
}

