package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.Nullable;

@GwtIncompatible
class AppendableWriter
extends Writer {
    private final Appendable target;
    private boolean closed;

    AppendableWriter(Appendable target) {
        this.target = Preconditions.checkNotNull(target);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.checkNotClosed();
        this.target.append(new String(cbuf, off, len));
    }

    @Override
    public void flush() throws IOException {
        this.checkNotClosed();
        if (this.target instanceof Flushable) {
            ((Flushable)((Object)this.target)).flush();
        }
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        if (this.target instanceof Closeable) {
            ((Closeable)((Object)this.target)).close();
        }
    }

    @Override
    public void write(int c2) throws IOException {
        this.checkNotClosed();
        this.target.append((char)c2);
    }

    @Override
    public void write(@Nullable String str) throws IOException {
        this.checkNotClosed();
        this.target.append(str);
    }

    @Override
    public void write(@Nullable String str, int off, int len) throws IOException {
        this.checkNotClosed();
        this.target.append(str, off, off + len);
    }

    @Override
    public Writer append(char c2) throws IOException {
        this.checkNotClosed();
        this.target.append(c2);
        return this;
    }

    @Override
    public Writer append(@Nullable CharSequence charSeq) throws IOException {
        this.checkNotClosed();
        this.target.append(charSeq);
        return this;
    }

    @Override
    public Writer append(@Nullable CharSequence charSeq, int start, int end) throws IOException {
        this.checkNotClosed();
        this.target.append(charSeq, start, end);
        return this;
    }

    private void checkNotClosed() throws IOException {
        if (this.closed) {
            throw new IOException("Cannot write to a closed writer.");
        }
    }
}

