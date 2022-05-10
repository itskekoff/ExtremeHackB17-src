package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Map;

@Beta
@GwtCompatible
public final class ArrayBasedEscaperMap {
    private final char[][] replacementArray;
    private static final char[][] EMPTY_REPLACEMENT_ARRAY = new char[0][0];

    public static ArrayBasedEscaperMap create(Map<Character, String> replacements) {
        return new ArrayBasedEscaperMap(ArrayBasedEscaperMap.createReplacementArray(replacements));
    }

    private ArrayBasedEscaperMap(char[][] replacementArray) {
        this.replacementArray = replacementArray;
    }

    char[][] getReplacementArray() {
        return this.replacementArray;
    }

    @VisibleForTesting
    static char[][] createReplacementArray(Map<Character, String> map) {
        Preconditions.checkNotNull(map);
        if (map.isEmpty()) {
            return EMPTY_REPLACEMENT_ARRAY;
        }
        char max = Collections.max(map.keySet()).charValue();
        char[][] replacements = new char[max + '\u0001'][];
        for (char c2 : map.keySet()) {
            replacements[c2] = map.get(Character.valueOf(c2)).toCharArray();
        }
        return replacements;
    }
}

