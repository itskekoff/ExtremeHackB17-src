package org.apache.commons.io.input;

import java.io.IOException;
import java.io.Reader;

public class BoundedReader
extends Reader {
    private static final int INVALID = -1;
    private final Reader target;
    private int charsRead = 0;
    private int markedAt = -1;
    private int readAheadLimit;
    private final int maxCharsFromTargetReader;

    public BoundedReader(Reader target, int maxCharsFromTargetReader) throws IOException {
        this.target = target;
        this.maxCharsFromTargetReader = maxCharsFromTargetReader;
    }

    @Override
    public void close() throws IOException {
        this.target.close();
    }

    @Override
    public void reset() throws IOException {
        this.charsRead = this.markedAt;
        this.target.reset();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.readAheadLimit = readAheadLimit - this.charsRead;
        this.markedAt = this.charsRead;
        this.target.mark(readAheadLimit);
    }

    @Override
    public int read() throws IOException {
        if (this.charsRead >= this.maxCharsFromTargetReader) {
            return -1;
        }
        if (this.markedAt >= 0 && this.charsRead - this.markedAt >= this.readAheadLimit) {
            return -1;
        }
        ++this.charsRead;
        return this.target.read();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        for (int i2 = 0; i2 < len; ++i2) {
            int c2 = this.read();
            if (c2 == -1) {
                return i2;
            }
            cbuf[off + i2] = (char)c2;
        }
        return len;
    }
}

