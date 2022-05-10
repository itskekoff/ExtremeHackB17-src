package io.netty.handler.ssl;

import java.util.ArrayList;
import java.util.List;

final class ApplicationProtocolUtil {
    private static final int DEFAULT_LIST_SIZE = 2;

    private ApplicationProtocolUtil() {
    }

    static List<String> toList(Iterable<String> protocols) {
        return ApplicationProtocolUtil.toList(2, protocols);
    }

    static List<String> toList(int initialListSize, Iterable<String> protocols) {
        if (protocols == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>(initialListSize);
        for (String p2 : protocols) {
            if (p2 == null || p2.isEmpty()) {
                throw new IllegalArgumentException("protocol cannot be null or empty");
            }
            result.add(p2);
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("protocols cannot empty");
        }
        return result;
    }

    static List<String> toList(String ... protocols) {
        return ApplicationProtocolUtil.toList(2, protocols);
    }

    static List<String> toList(int initialListSize, String ... protocols) {
        if (protocols == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>(initialListSize);
        for (String p2 : protocols) {
            if (p2 == null || p2.isEmpty()) {
                throw new IllegalArgumentException("protocol cannot be null or empty");
            }
            result.add(p2);
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("protocols cannot empty");
        }
        return result;
    }
}

