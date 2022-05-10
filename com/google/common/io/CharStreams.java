package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.AppendableWriter;
import com.google.common.io.LineProcessor;
import com.google.common.io.LineReader;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

@Beta
@GwtIncompatible
public final class CharStreams {
    static CharBuffer createBuffer() {
        return CharBuffer.allocate(2048);
    }

    private CharStreams() {
    }

    @CanIgnoreReturnValue
    public static long copy(Readable from, Appendable to2) throws IOException {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to2);
        CharBuffer buf2 = CharStreams.createBuffer();
        long total = 0L;
        while (from.read(buf2) != -1) {
            buf2.flip();
            to2.append(buf2);
            total += (long)buf2.remaining();
            buf2.clear();
        }
        return total;
    }

    public static String toString(Readable r2) throws IOException {
        return CharStreams.toStringBuilder(r2).toString();
    }

    private static StringBuilder toStringBuilder(Readable r2) throws IOException {
        StringBuilder sb2 = new StringBuilder();
        CharStreams.copy(r2, sb2);
        return sb2;
    }

    public static List<String> readLines(Readable r2) throws IOException {
        String line;
        ArrayList<String> result = new ArrayList<String>();
        LineReader lineReader = new LineReader(r2);
        while ((line = lineReader.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    @CanIgnoreReturnValue
    public static <T> T readLines(Readable readable, LineProcessor<T> processor) throws IOException {
        String line;
        Preconditions.checkNotNull(readable);
        Preconditions.checkNotNull(processor);
        LineReader lineReader = new LineReader(readable);
        while ((line = lineReader.readLine()) != null && processor.processLine(line)) {
        }
        return processor.getResult();
    }

    @CanIgnoreReturnValue
    public static long exhaust(Readable readable) throws IOException {
        long read;
        long total = 0L;
        CharBuffer buf2 = CharStreams.createBuffer();
        while ((read = (long)readable.read(buf2)) != -1L) {
            total += read;
            buf2.clear();
        }
        return total;
    }

    public static void skipFully(Reader reader, long n2) throws IOException {
        Preconditions.checkNotNull(reader);
        while (n2 > 0L) {
            long amt2 = reader.skip(n2);
            if (amt2 == 0L) {
                throw new EOFException();
            }
            n2 -= amt2;
        }
    }

    public static Writer nullWriter() {
        return NullWriter.INSTANCE;
    }

    public static Writer asWriter(Appendable target) {
        if (target instanceof Writer) {
            return (Writer)target;
        }
        return new AppendableWriter(target);
    }

    private static final class NullWriter
    extends Writer {
        private static final NullWriter INSTANCE = new NullWriter();

        private NullWriter() {
        }

        @Override
        public void write(int c2) {
        }

        @Override
        public void write(char[] cbuf) {
            Preconditions.checkNotNull(cbuf);
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            Preconditions.checkPositionIndexes(off, off + len, cbuf.length);
        }

        @Override
        public void write(String str) {
            Preconditions.checkNotNull(str);
        }

        @Override
        public void write(String str, int off, int len) {
            Preconditions.checkPositionIndexes(off, off + len, str.length());
        }

        @Override
        public Writer append(CharSequence csq) {
            Preconditions.checkNotNull(csq);
            return this;
        }

        @Override
        public Writer append(CharSequence csq, int start, int end) {
            Preconditions.checkPositionIndexes(start, end, csq.length());
            return this;
        }

        @Override
        public Writer append(char c2) {
            return this;
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        public String toString() {
            return "CharStreams.nullWriter()";
        }
    }
}

