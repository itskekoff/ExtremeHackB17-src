package org.apache.commons.compress.compressors.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.snappy.PureJavaCrc32C;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class FramedSnappyCompressorInputStream
extends CompressorInputStream {
    static final long MASK_OFFSET = 2726488792L;
    private static final int STREAM_IDENTIFIER_TYPE = 255;
    private static final int COMPRESSED_CHUNK_TYPE = 0;
    private static final int UNCOMPRESSED_CHUNK_TYPE = 1;
    private static final int PADDING_CHUNK_TYPE = 254;
    private static final int MIN_UNSKIPPABLE_TYPE = 2;
    private static final int MAX_UNSKIPPABLE_TYPE = 127;
    private static final int MAX_SKIPPABLE_TYPE = 253;
    private static final byte[] SZ_SIGNATURE = new byte[]{-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
    private final PushbackInputStream in;
    private SnappyCompressorInputStream currentCompressedChunk;
    private final byte[] oneByte = new byte[1];
    private boolean endReached;
    private boolean inUncompressedChunk;
    private int uncompressedBytesRemaining;
    private long expectedChecksum = -1L;
    private final PureJavaCrc32C checksum = new PureJavaCrc32C();

    public FramedSnappyCompressorInputStream(InputStream in2) throws IOException {
        this.in = new PushbackInputStream(in2, 1);
        this.readStreamIdentifier();
    }

    public int read() throws IOException {
        return this.read(this.oneByte, 0, 1) == -1 ? -1 : this.oneByte[0] & 0xFF;
    }

    public void close() throws IOException {
        if (this.currentCompressedChunk != null) {
            this.currentCompressedChunk.close();
            this.currentCompressedChunk = null;
        }
        this.in.close();
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        int read = this.readOnce(b2, off, len);
        if (read == -1) {
            this.readNextBlock();
            if (this.endReached) {
                return -1;
            }
            read = this.readOnce(b2, off, len);
        }
        return read;
    }

    public int available() throws IOException {
        if (this.inUncompressedChunk) {
            return Math.min(this.uncompressedBytesRemaining, this.in.available());
        }
        if (this.currentCompressedChunk != null) {
            return this.currentCompressedChunk.available();
        }
        return 0;
    }

    private int readOnce(byte[] b2, int off, int len) throws IOException {
        int read = -1;
        if (this.inUncompressedChunk) {
            int amount = Math.min(this.uncompressedBytesRemaining, len);
            if (amount == 0) {
                return -1;
            }
            read = this.in.read(b2, off, amount);
            if (read != -1) {
                this.uncompressedBytesRemaining -= read;
                this.count(read);
            }
        } else if (this.currentCompressedChunk != null) {
            long before = this.currentCompressedChunk.getBytesRead();
            read = this.currentCompressedChunk.read(b2, off, len);
            if (read == -1) {
                this.currentCompressedChunk.close();
                this.currentCompressedChunk = null;
            } else {
                this.count(this.currentCompressedChunk.getBytesRead() - before);
            }
        }
        if (read > 0) {
            this.checksum.update(b2, off, read);
        }
        return read;
    }

    private void readNextBlock() throws IOException {
        this.verifyLastChecksumAndReset();
        this.inUncompressedChunk = false;
        int type = this.readOneByte();
        if (type == -1) {
            this.endReached = true;
        } else if (type == 255) {
            this.in.unread(type);
            this.pushedBackBytes(1L);
            this.readStreamIdentifier();
            this.readNextBlock();
        } else if (type == 254 || type > 127 && type <= 253) {
            this.skipBlock();
            this.readNextBlock();
        } else {
            if (type >= 2 && type <= 127) {
                throw new IOException("unskippable chunk with type " + type + " (hex " + Integer.toHexString(type) + ")" + " detected.");
            }
            if (type == 1) {
                this.inUncompressedChunk = true;
                this.uncompressedBytesRemaining = this.readSize() - 4;
                this.expectedChecksum = FramedSnappyCompressorInputStream.unmask(this.readCrc());
            } else if (type == 0) {
                long size = this.readSize() - 4;
                this.expectedChecksum = FramedSnappyCompressorInputStream.unmask(this.readCrc());
                this.currentCompressedChunk = new SnappyCompressorInputStream(new BoundedInputStream(this.in, size));
                this.count(this.currentCompressedChunk.getBytesRead());
            } else {
                throw new IOException("unknown chunk type " + type + " detected.");
            }
        }
    }

    private long readCrc() throws IOException {
        byte[] b2 = new byte[4];
        int read = IOUtils.readFully(this.in, b2);
        this.count(read);
        if (read != 4) {
            throw new IOException("premature end of stream");
        }
        long crc = 0L;
        for (int i2 = 0; i2 < 4; ++i2) {
            crc |= ((long)b2[i2] & 0xFFL) << 8 * i2;
        }
        return crc;
    }

    static long unmask(long x2) {
        x2 -= 2726488792L;
        return ((x2 &= 0xFFFFFFFFL) >> 17 | x2 << 15) & 0xFFFFFFFFL;
    }

    private int readSize() throws IOException {
        int b2 = 0;
        int sz2 = 0;
        for (int i2 = 0; i2 < 3; ++i2) {
            b2 = this.readOneByte();
            if (b2 == -1) {
                throw new IOException("premature end of stream");
            }
            sz2 |= b2 << i2 * 8;
        }
        return sz2;
    }

    private void skipBlock() throws IOException {
        int size = this.readSize();
        long read = IOUtils.skip(this.in, size);
        this.count(read);
        if (read != (long)size) {
            throw new IOException("premature end of stream");
        }
    }

    private void readStreamIdentifier() throws IOException {
        byte[] b2 = new byte[10];
        int read = IOUtils.readFully(this.in, b2);
        this.count(read);
        if (10 != read || !FramedSnappyCompressorInputStream.matches(b2, 10)) {
            throw new IOException("Not a framed Snappy stream");
        }
    }

    private int readOneByte() throws IOException {
        int b2 = this.in.read();
        if (b2 != -1) {
            this.count(1);
            return b2 & 0xFF;
        }
        return -1;
    }

    private void verifyLastChecksumAndReset() throws IOException {
        if (this.expectedChecksum >= 0L && this.expectedChecksum != this.checksum.getValue()) {
            throw new IOException("Checksum verification failed");
        }
        this.expectedChecksum = -1L;
        this.checksum.reset();
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < SZ_SIGNATURE.length) {
            return false;
        }
        byte[] shortenedSig = signature;
        if (signature.length > SZ_SIGNATURE.length) {
            shortenedSig = new byte[SZ_SIGNATURE.length];
            System.arraycopy(signature, 0, shortenedSig, 0, SZ_SIGNATURE.length);
        }
        return Arrays.equals(shortenedSig, SZ_SIGNATURE);
    }
}

