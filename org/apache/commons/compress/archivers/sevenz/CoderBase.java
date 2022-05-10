package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.sevenz.Coder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class CoderBase {
    private final Class<?>[] acceptableOptions;
    private static final byte[] NONE = new byte[0];

    protected CoderBase(Class<?> ... acceptableOptions) {
        this.acceptableOptions = acceptableOptions;
    }

    boolean canAcceptOptions(Object opts) {
        for (Class<?> c2 : this.acceptableOptions) {
            if (!c2.isInstance(opts)) continue;
            return true;
        }
        return false;
    }

    byte[] getOptionsAsProperties(Object options) {
        return NONE;
    }

    Object getOptionsFromCoder(Coder coder, InputStream in2) {
        return null;
    }

    abstract InputStream decode(InputStream var1, Coder var2, byte[] var3) throws IOException;

    OutputStream encode(OutputStream out, Object options) throws IOException {
        throw new UnsupportedOperationException("method doesn't support writing");
    }

    protected static int numberOptionOrDefault(Object options, int defaultValue) {
        return options instanceof Number ? ((Number)options).intValue() : defaultValue;
    }
}
