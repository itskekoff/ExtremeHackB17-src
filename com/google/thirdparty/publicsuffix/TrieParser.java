package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.thirdparty.publicsuffix.PublicSuffixType;
import java.util.List;

@GwtCompatible
final class TrieParser {
    private static final Joiner PREFIX_JOINER = Joiner.on("");

    TrieParser() {
    }

    static ImmutableMap<String, PublicSuffixType> parseTrie(CharSequence encoded) {
        ImmutableMap.Builder<String, PublicSuffixType> builder = ImmutableMap.builder();
        int encodedLen = encoded.length();
        for (int idx = 0; idx < encodedLen; idx += TrieParser.doParseTrieToBuilder(Lists.newLinkedList(), encoded.subSequence(idx, encodedLen), builder)) {
        }
        return builder.build();
    }

    private static int doParseTrieToBuilder(List<CharSequence> stack, CharSequence encoded, ImmutableMap.Builder<String, PublicSuffixType> builder) {
        String domain;
        int idx;
        int encodedLen = encoded.length();
        char c2 = '\u0000';
        for (idx = 0; idx < encodedLen && (c2 = encoded.charAt(idx)) != '&' && c2 != '?' && c2 != '!' && c2 != ':' && c2 != ','; ++idx) {
        }
        stack.add(0, TrieParser.reverse(encoded.subSequence(0, idx)));
        if ((c2 == '!' || c2 == '?' || c2 == ':' || c2 == ',') && (domain = PREFIX_JOINER.join(stack)).length() > 0) {
            builder.put(domain, PublicSuffixType.fromCode(c2));
        }
        ++idx;
        if (c2 != '?' && c2 != ',') {
            while (idx < encodedLen) {
                if (encoded.charAt(idx += TrieParser.doParseTrieToBuilder(stack, encoded.subSequence(idx, encodedLen), builder)) != '?' && encoded.charAt(idx) != ',') continue;
                ++idx;
                break;
            }
        }
        stack.remove(0);
        return idx;
    }

    private static CharSequence reverse(CharSequence s2) {
        return new StringBuilder(s2).reverse();
    }
}

