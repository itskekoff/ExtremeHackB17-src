package io.netty.handler.ssl;

import io.netty.handler.ssl.CipherSuiteFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class IdentityCipherSuiteFilter
implements CipherSuiteFilter {
    public static final IdentityCipherSuiteFilter INSTANCE = new IdentityCipherSuiteFilter();

    private IdentityCipherSuiteFilter() {
    }

    @Override
    public String[] filterCipherSuites(Iterable<String> ciphers, List<String> defaultCiphers, Set<String> supportedCiphers) {
        if (ciphers == null) {
            return defaultCiphers.toArray(new String[defaultCiphers.size()]);
        }
        ArrayList<String> newCiphers = new ArrayList<String>(supportedCiphers.size());
        for (String c2 : ciphers) {
            if (c2 == null) break;
            newCiphers.add(c2);
        }
        return newCiphers.toArray(new String[newCiphers.size()]);
    }
}

