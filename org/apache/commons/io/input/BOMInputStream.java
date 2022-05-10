package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.ProxyInputStream;

public class BOMInputStream
extends ProxyInputStream {
    private final boolean include;
    private final List<ByteOrderMark> boms;
    private ByteOrderMark byteOrderMark;
    private int[] firstBytes;
    private int fbLength;
    private int fbIndex;
    private int markFbIndex;
    private boolean markedAtStart;
    private static final Comparator<ByteOrderMark> ByteOrderMarkLengthComparator = new Comparator<ByteOrderMark>(){

        @Override
        public int compare(ByteOrderMark bom1, ByteOrderMark bom2) {
            int len2;
            int len1 = bom1.length();
            if (len1 > (len2 = bom2.length())) {
                return -1;
            }
            if (len2 > len1) {
                return 1;
            }
            return 0;
        }
    };

    public BOMInputStream(InputStream delegate) {
        this(delegate, false, ByteOrderMark.UTF_8);
    }

    public BOMInputStream(InputStream delegate, boolean include) {
        this(delegate, include, ByteOrderMark.UTF_8);
    }

    public BOMInputStream(InputStream delegate, ByteOrderMark ... boms) {
        this(delegate, false, boms);
    }

    public BOMInputStream(InputStream delegate, boolean include, ByteOrderMark ... boms) {
        super(delegate);
        if (boms == null || boms.length == 0) {
            throw new IllegalArgumentException("No BOMs specified");
        }
        this.include = include;
        Arrays.sort(boms, ByteOrderMarkLengthComparator);
        this.boms = Arrays.asList(boms);
    }

    public boolean hasBOM() throws IOException {
        return this.getBOM() != null;
    }

    public boolean hasBOM(ByteOrderMark bom2) throws IOException {
        if (!this.boms.contains(bom2)) {
            throw new IllegalArgumentException("Stream not configure to detect " + bom2);
        }
        return this.byteOrderMark != null && this.getBOM().equals(bom2);
    }

    public ByteOrderMark getBOM() throws IOException {
        if (this.firstBytes == null) {
            this.fbLength = 0;
            int maxBomSize = this.boms.get(0).length();
            this.firstBytes = new int[maxBomSize];
            for (int i2 = 0; i2 < this.firstBytes.length; ++i2) {
                this.firstBytes[i2] = this.in.read();
                ++this.fbLength;
                if (this.firstBytes[i2] < 0) break;
            }
            this.byteOrderMark = this.find();
            if (this.byteOrderMark != null && !this.include) {
                if (this.byteOrderMark.length() < this.firstBytes.length) {
                    this.fbIndex = this.byteOrderMark.length();
                } else {
                    this.fbLength = 0;
                }
            }
        }
        return this.byteOrderMark;
    }

    public String getBOMCharsetName() throws IOException {
        this.getBOM();
        return this.byteOrderMark == null ? null : this.byteOrderMark.getCharsetName();
    }

    private int readFirstBytes() throws IOException {
        this.getBOM();
        return this.fbIndex < this.fbLength ? this.firstBytes[this.fbIndex++] : -1;
    }

    private ByteOrderMark find() {
        for (ByteOrderMark bom2 : this.boms) {
            if (!this.matches(bom2)) continue;
            return bom2;
        }
        return null;
    }

    private boolean matches(ByteOrderMark bom2) {
        for (int i2 = 0; i2 < bom2.length(); ++i2) {
            if (bom2.get(i2) == this.firstBytes[i2]) continue;
            return false;
        }
        return true;
    }

    @Override
    public int read() throws IOException {
        int b2 = this.readFirstBytes();
        return b2 >= 0 ? b2 : this.in.read();
    }

    @Override
    public int read(byte[] buf2, int off, int len) throws IOException {
        int firstCount = 0;
        int b2 = 0;
        while (len > 0 && b2 >= 0) {
            b2 = this.readFirstBytes();
            if (b2 < 0) continue;
            buf2[off++] = (byte)(b2 & 0xFF);
            --len;
            ++firstCount;
        }
        int secondCount = this.in.read(buf2, off, len);
        return secondCount < 0 ? (firstCount > 0 ? firstCount : -1) : firstCount + secondCount;
    }

    @Override
    public int read(byte[] buf2) throws IOException {
        return this.read(buf2, 0, buf2.length);
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.markFbIndex = this.fbIndex;
        this.markedAtStart = this.firstBytes == null;
        this.in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.fbIndex = this.markFbIndex;
        if (this.markedAtStart) {
            this.firstBytes = null;
        }
        this.in.reset();
    }

    @Override
    public long skip(long n2) throws IOException {
        int skipped = 0;
        while (n2 > (long)skipped && this.readFirstBytes() >= 0) {
            ++skipped;
        }
        return this.in.skip(n2 - (long)skipped) + (long)skipped;
    }
}

