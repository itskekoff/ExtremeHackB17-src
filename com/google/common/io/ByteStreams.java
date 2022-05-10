package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteProcessor;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

@Beta
@GwtIncompatible
public final class ByteStreams {
    private static final int ZERO_COPY_CHUNK_SIZE = 524288;
    private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream(){

        @Override
        public void write(int b2) {
        }

        @Override
        public void write(byte[] b2) {
            Preconditions.checkNotNull(b2);
        }

        @Override
        public void write(byte[] b2, int off, int len) {
            Preconditions.checkNotNull(b2);
        }

        public String toString() {
            return "ByteStreams.nullOutputStream()";
        }
    };

    static byte[] createBuffer() {
        return new byte[8192];
    }

    private ByteStreams() {
    }

    @CanIgnoreReturnValue
    public static long copy(InputStream from, OutputStream to2) throws IOException {
        int r2;
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to2);
        byte[] buf2 = ByteStreams.createBuffer();
        long total = 0L;
        while ((r2 = from.read(buf2)) != -1) {
            to2.write(buf2, 0, r2);
            total += (long)r2;
        }
        return total;
    }

    @CanIgnoreReturnValue
    public static long copy(ReadableByteChannel from, WritableByteChannel to2) throws IOException {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to2);
        if (from instanceof FileChannel) {
            long copied;
            long oldPosition;
            FileChannel sourceChannel = (FileChannel)from;
            long position = oldPosition = sourceChannel.position();
            do {
                copied = sourceChannel.transferTo(position, 524288L, to2);
                sourceChannel.position(position += copied);
            } while (copied > 0L || position < sourceChannel.size());
            return position - oldPosition;
        }
        ByteBuffer buf2 = ByteBuffer.wrap(ByteStreams.createBuffer());
        long total = 0L;
        while (from.read(buf2) != -1) {
            buf2.flip();
            while (buf2.hasRemaining()) {
                total += (long)to2.write(buf2);
            }
            buf2.clear();
        }
        return total;
    }

    public static byte[] toByteArray(InputStream in2) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, in2.available()));
        ByteStreams.copy(in2, out);
        return out.toByteArray();
    }

    static byte[] toByteArray(InputStream in2, int expectedSize) throws IOException {
        int read;
        byte[] bytes = new byte[expectedSize];
        for (int remaining = expectedSize; remaining > 0; remaining -= read) {
            int off = expectedSize - remaining;
            read = in2.read(bytes, off, remaining);
            if (read != -1) continue;
            return Arrays.copyOf(bytes, off);
        }
        int b2 = in2.read();
        if (b2 == -1) {
            return bytes;
        }
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        out.write(b2);
        ByteStreams.copy(in2, out);
        byte[] result = new byte[bytes.length + out.size()];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        out.writeTo(result, bytes.length);
        return result;
    }

    @CanIgnoreReturnValue
    public static long exhaust(InputStream in2) throws IOException {
        long read;
        long total = 0L;
        byte[] buf2 = ByteStreams.createBuffer();
        while ((read = (long)in2.read(buf2)) != -1L) {
            total += read;
        }
        return total;
    }

    public static ByteArrayDataInput newDataInput(byte[] bytes) {
        return ByteStreams.newDataInput(new ByteArrayInputStream(bytes));
    }

    public static ByteArrayDataInput newDataInput(byte[] bytes, int start) {
        Preconditions.checkPositionIndex(start, bytes.length);
        return ByteStreams.newDataInput(new ByteArrayInputStream(bytes, start, bytes.length - start));
    }

    public static ByteArrayDataInput newDataInput(ByteArrayInputStream byteArrayInputStream) {
        return new ByteArrayDataInputStream(Preconditions.checkNotNull(byteArrayInputStream));
    }

    public static ByteArrayDataOutput newDataOutput() {
        return ByteStreams.newDataOutput(new ByteArrayOutputStream());
    }

    public static ByteArrayDataOutput newDataOutput(int size) {
        if (size < 0) {
            throw new IllegalArgumentException(String.format("Invalid size: %s", size));
        }
        return ByteStreams.newDataOutput(new ByteArrayOutputStream(size));
    }

    public static ByteArrayDataOutput newDataOutput(ByteArrayOutputStream byteArrayOutputSteam) {
        return new ByteArrayDataOutputStream(Preconditions.checkNotNull(byteArrayOutputSteam));
    }

    public static OutputStream nullOutputStream() {
        return NULL_OUTPUT_STREAM;
    }

    public static InputStream limit(InputStream in2, long limit) {
        return new LimitedInputStream(in2, limit);
    }

    public static void readFully(InputStream in2, byte[] b2) throws IOException {
        ByteStreams.readFully(in2, b2, 0, b2.length);
    }

    public static void readFully(InputStream in2, byte[] b2, int off, int len) throws IOException {
        int read = ByteStreams.read(in2, b2, off, len);
        if (read != len) {
            throw new EOFException("reached end of stream after reading " + read + " bytes; " + len + " bytes expected");
        }
    }

    public static void skipFully(InputStream in2, long n2) throws IOException {
        long skipped = ByteStreams.skipUpTo(in2, n2);
        if (skipped < n2) {
            throw new EOFException("reached end of stream after skipping " + skipped + " bytes; " + n2 + " bytes expected");
        }
    }

    static long skipUpTo(InputStream in2, long n2) throws IOException {
        int skip;
        long remaining;
        long totalSkipped;
        long skipped;
        byte[] buf2 = ByteStreams.createBuffer();
        for (totalSkipped = 0L; totalSkipped < n2 && ((skipped = ByteStreams.skipSafely(in2, remaining = n2 - totalSkipped)) != 0L || (skipped = (long)in2.read(buf2, 0, skip = (int)Math.min(remaining, (long)buf2.length))) != -1L); totalSkipped += skipped) {
        }
        return totalSkipped;
    }

    private static long skipSafely(InputStream in2, long n2) throws IOException {
        int available = in2.available();
        return available == 0 ? 0L : in2.skip(Math.min((long)available, n2));
    }

    @CanIgnoreReturnValue
    public static <T> T readBytes(InputStream input, ByteProcessor<T> processor) throws IOException {
        int read;
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(processor);
        byte[] buf2 = ByteStreams.createBuffer();
        while ((read = input.read(buf2)) != -1 && processor.processBytes(buf2, 0, read)) {
        }
        return processor.getResult();
    }

    @CanIgnoreReturnValue
    public static int read(InputStream in2, byte[] b2, int off, int len) throws IOException {
        int total;
        int result;
        Preconditions.checkNotNull(in2);
        Preconditions.checkNotNull(b2);
        if (len < 0) {
            throw new IndexOutOfBoundsException("len is negative");
        }
        for (total = 0; total < len && (result = in2.read(b2, off + total, len - total)) != -1; total += result) {
        }
        return total;
    }

    private static final class LimitedInputStream
    extends FilterInputStream {
        private long left;
        private long mark = -1L;

        LimitedInputStream(InputStream in2, long limit) {
            super(in2);
            Preconditions.checkNotNull(in2);
            Preconditions.checkArgument(limit >= 0L, "limit must be non-negative");
            this.left = limit;
        }

        @Override
        public int available() throws IOException {
            return (int)Math.min((long)this.in.available(), this.left);
        }

        @Override
        public synchronized void mark(int readLimit) {
            this.in.mark(readLimit);
            this.mark = this.left;
        }

        @Override
        public int read() throws IOException {
            if (this.left == 0L) {
                return -1;
            }
            int result = this.in.read();
            if (result != -1) {
                --this.left;
            }
            return result;
        }

        @Override
        public int read(byte[] b2, int off, int len) throws IOException {
            if (this.left == 0L) {
                return -1;
            }
            int result = this.in.read(b2, off, len = (int)Math.min((long)len, this.left));
            if (result != -1) {
                this.left -= (long)result;
            }
            return result;
        }

        @Override
        public synchronized void reset() throws IOException {
            if (!this.in.markSupported()) {
                throw new IOException("Mark not supported");
            }
            if (this.mark == -1L) {
                throw new IOException("Mark not set");
            }
            this.in.reset();
            this.left = this.mark;
        }

        @Override
        public long skip(long n2) throws IOException {
            n2 = Math.min(n2, this.left);
            long skipped = this.in.skip(n2);
            this.left -= skipped;
            return skipped;
        }
    }

    private static class ByteArrayDataOutputStream
    implements ByteArrayDataOutput {
        final DataOutput output;
        final ByteArrayOutputStream byteArrayOutputSteam;

        ByteArrayDataOutputStream(ByteArrayOutputStream byteArrayOutputSteam) {
            this.byteArrayOutputSteam = byteArrayOutputSteam;
            this.output = new DataOutputStream(byteArrayOutputSteam);
        }

        @Override
        public void write(int b2) {
            try {
                this.output.write(b2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void write(byte[] b2) {
            try {
                this.output.write(b2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void write(byte[] b2, int off, int len) {
            try {
                this.output.write(b2, off, len);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeBoolean(boolean v2) {
            try {
                this.output.writeBoolean(v2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeByte(int v2) {
            try {
                this.output.writeByte(v2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeBytes(String s2) {
            try {
                this.output.writeBytes(s2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeChar(int v2) {
            try {
                this.output.writeChar(v2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeChars(String s2) {
            try {
                this.output.writeChars(s2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeDouble(double v2) {
            try {
                this.output.writeDouble(v2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeFloat(float v2) {
            try {
                this.output.writeFloat(v2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeInt(int v2) {
            try {
                this.output.writeInt(v2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeLong(long v2) {
            try {
                this.output.writeLong(v2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeShort(int v2) {
            try {
                this.output.writeShort(v2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public void writeUTF(String s2) {
            try {
                this.output.writeUTF(s2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public byte[] toByteArray() {
            return this.byteArrayOutputSteam.toByteArray();
        }
    }

    private static class ByteArrayDataInputStream
    implements ByteArrayDataInput {
        final DataInput input;

        ByteArrayDataInputStream(ByteArrayInputStream byteArrayInputStream) {
            this.input = new DataInputStream(byteArrayInputStream);
        }

        @Override
        public void readFully(byte[] b2) {
            try {
                this.input.readFully(b2);
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public void readFully(byte[] b2, int off, int len) {
            try {
                this.input.readFully(b2, off, len);
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public int skipBytes(int n2) {
            try {
                return this.input.skipBytes(n2);
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public boolean readBoolean() {
            try {
                return this.input.readBoolean();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public byte readByte() {
            try {
                return this.input.readByte();
            }
            catch (EOFException e2) {
                throw new IllegalStateException(e2);
            }
            catch (IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
        }

        @Override
        public int readUnsignedByte() {
            try {
                return this.input.readUnsignedByte();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public short readShort() {
            try {
                return this.input.readShort();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public int readUnsignedShort() {
            try {
                return this.input.readUnsignedShort();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public char readChar() {
            try {
                return this.input.readChar();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public int readInt() {
            try {
                return this.input.readInt();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public long readLong() {
            try {
                return this.input.readLong();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public float readFloat() {
            try {
                return this.input.readFloat();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public double readDouble() {
            try {
                return this.input.readDouble();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public String readLine() {
            try {
                return this.input.readLine();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }

        @Override
        public String readUTF() {
            try {
                return this.input.readUTF();
            }
            catch (IOException e2) {
                throw new IllegalStateException(e2);
            }
        }
    }

    private static final class FastByteArrayOutputStream
    extends ByteArrayOutputStream {
        private FastByteArrayOutputStream() {
        }

        void writeTo(byte[] b2, int off) {
            System.arraycopy(this.buf, 0, b2, off, this.count);
        }
    }
}

