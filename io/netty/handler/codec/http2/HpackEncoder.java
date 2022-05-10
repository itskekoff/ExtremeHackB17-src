package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.HpackHeaderField;
import io.netty.handler.codec.http2.HpackHuffmanEncoder;
import io.netty.handler.codec.http2.HpackStaticTable;
import io.netty.handler.codec.http2.HpackUtil;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersEncoder;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.MathUtil;
import java.util.Arrays;
import java.util.Map;

final class HpackEncoder {
    private final HeaderEntry[] headerFields;
    private final HeaderEntry head = new HeaderEntry(-1, AsciiString.EMPTY_STRING, AsciiString.EMPTY_STRING, Integer.MAX_VALUE, null);
    private final HpackHuffmanEncoder hpackHuffmanEncoder = new HpackHuffmanEncoder();
    private final byte hashMask;
    private final boolean ignoreMaxHeaderListSize;
    private long size;
    private long maxHeaderTableSize;
    private long maxHeaderListSize;

    HpackEncoder() {
        this(false);
    }

    public HpackEncoder(boolean ignoreMaxHeaderListSize) {
        this(ignoreMaxHeaderListSize, 16);
    }

    public HpackEncoder(boolean ignoreMaxHeaderListSize, int arraySizeHint) {
        this.ignoreMaxHeaderListSize = ignoreMaxHeaderListSize;
        this.maxHeaderTableSize = 4096L;
        this.maxHeaderListSize = 8192L;
        this.headerFields = new HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(arraySizeHint, 128)))];
        this.hashMask = (byte)(this.headerFields.length - 1);
        this.head.before = this.head.after = this.head;
    }

    public void encodeHeaders(int streamId, ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector) throws Http2Exception {
        if (this.ignoreMaxHeaderListSize) {
            this.encodeHeadersIgnoreMaxHeaderListSize(out, headers, sensitivityDetector);
        } else {
            this.encodeHeadersEnforceMaxHeaderListSize(streamId, out, headers, sensitivityDetector);
        }
    }

    private void encodeHeadersEnforceMaxHeaderListSize(int streamId, ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector) throws Http2Exception {
        long headerSize = 0L;
        for (Map.Entry<CharSequence, CharSequence> header : headers) {
            CharSequence value;
            CharSequence name = header.getKey();
            if ((headerSize += HpackHeaderField.sizeOf(name, value = header.getValue())) <= this.maxHeaderListSize) continue;
            Http2CodecUtil.headerListSizeExceeded(streamId, this.maxHeaderListSize, false);
        }
        this.encodeHeadersIgnoreMaxHeaderListSize(out, headers, sensitivityDetector);
    }

    private void encodeHeadersIgnoreMaxHeaderListSize(ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector) throws Http2Exception {
        for (Map.Entry<CharSequence, CharSequence> header : headers) {
            CharSequence name = header.getKey();
            CharSequence value = header.getValue();
            this.encodeHeader(out, name, value, sensitivityDetector.isSensitive(name, value), HpackHeaderField.sizeOf(name, value));
        }
    }

    private void encodeHeader(ByteBuf out, CharSequence name, CharSequence value, boolean sensitive, long headerSize) {
        if (sensitive) {
            int nameIndex = this.getNameIndex(name);
            this.encodeLiteral(out, name, value, HpackUtil.IndexType.NEVER, nameIndex);
            return;
        }
        if (this.maxHeaderTableSize == 0L) {
            int staticTableIndex = HpackStaticTable.getIndex(name, value);
            if (staticTableIndex == -1) {
                int nameIndex = HpackStaticTable.getIndex(name);
                this.encodeLiteral(out, name, value, HpackUtil.IndexType.NONE, nameIndex);
            } else {
                HpackEncoder.encodeInteger(out, 128, 7, staticTableIndex);
            }
            return;
        }
        if (headerSize > this.maxHeaderTableSize) {
            int nameIndex = this.getNameIndex(name);
            this.encodeLiteral(out, name, value, HpackUtil.IndexType.NONE, nameIndex);
            return;
        }
        HeaderEntry headerField = this.getEntry(name, value);
        if (headerField != null) {
            int index = this.getIndex(headerField.index) + HpackStaticTable.length;
            HpackEncoder.encodeInteger(out, 128, 7, index);
        } else {
            int staticTableIndex = HpackStaticTable.getIndex(name, value);
            if (staticTableIndex != -1) {
                HpackEncoder.encodeInteger(out, 128, 7, staticTableIndex);
            } else {
                this.ensureCapacity(headerSize);
                this.encodeLiteral(out, name, value, HpackUtil.IndexType.INCREMENTAL, this.getNameIndex(name));
                this.add(name, value, headerSize);
            }
        }
    }

    public void setMaxHeaderTableSize(ByteBuf out, long maxHeaderTableSize) throws Http2Exception {
        if (maxHeaderTableSize < 0L || maxHeaderTableSize > 0xFFFFFFFFL) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header Table Size must be >= %d and <= %d but was %d", 0L, 0xFFFFFFFFL, maxHeaderTableSize);
        }
        if (this.maxHeaderTableSize == maxHeaderTableSize) {
            return;
        }
        this.maxHeaderTableSize = maxHeaderTableSize;
        this.ensureCapacity(0L);
        HpackEncoder.encodeInteger(out, 32, 5, maxHeaderTableSize);
    }

    public long getMaxHeaderTableSize() {
        return this.maxHeaderTableSize;
    }

    public void setMaxHeaderListSize(long maxHeaderListSize) throws Http2Exception {
        if (maxHeaderListSize < 0L || maxHeaderListSize > 0xFFFFFFFFL) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header List Size must be >= %d and <= %d but was %d", 0L, 0xFFFFFFFFL, maxHeaderListSize);
        }
        this.maxHeaderListSize = maxHeaderListSize;
    }

    public long getMaxHeaderListSize() {
        return this.maxHeaderListSize;
    }

    private static void encodeInteger(ByteBuf out, int mask, int n2, int i2) {
        HpackEncoder.encodeInteger(out, mask, n2, (long)i2);
    }

    private static void encodeInteger(ByteBuf out, int mask, int n2, long i2) {
        assert (n2 >= 0 && n2 <= 8) : "N: " + n2;
        int nbits = 255 >>> 8 - n2;
        if (i2 < (long)nbits) {
            out.writeByte((int)((long)mask | i2));
        } else {
            out.writeByte(mask | nbits);
            long length = i2 - (long)nbits;
            while ((length & 0xFFFFFFFFFFFFFF80L) != 0L) {
                out.writeByte((int)(length & 0x7FL | 0x80L));
                length >>>= 7;
            }
            out.writeByte((int)length);
        }
    }

    private void encodeStringLiteral(ByteBuf out, CharSequence string) {
        int huffmanLength = this.hpackHuffmanEncoder.getEncodedLength(string);
        if (huffmanLength < string.length()) {
            HpackEncoder.encodeInteger(out, 128, 7, huffmanLength);
            this.hpackHuffmanEncoder.encode(out, string);
        } else {
            HpackEncoder.encodeInteger(out, 0, 7, string.length());
            if (string instanceof AsciiString) {
                AsciiString asciiString = (AsciiString)string;
                out.writeBytes(asciiString.array(), asciiString.arrayOffset(), asciiString.length());
            } else {
                out.writeCharSequence(string, CharsetUtil.ISO_8859_1);
            }
        }
    }

    private void encodeLiteral(ByteBuf out, CharSequence name, CharSequence value, HpackUtil.IndexType indexType, int nameIndex) {
        boolean nameIndexValid = nameIndex != -1;
        switch (indexType) {
            case INCREMENTAL: {
                HpackEncoder.encodeInteger(out, 64, 6, nameIndexValid ? nameIndex : 0);
                break;
            }
            case NONE: {
                HpackEncoder.encodeInteger(out, 0, 4, nameIndexValid ? nameIndex : 0);
                break;
            }
            case NEVER: {
                HpackEncoder.encodeInteger(out, 16, 4, nameIndexValid ? nameIndex : 0);
                break;
            }
            default: {
                throw new Error("should not reach here");
            }
        }
        if (!nameIndexValid) {
            this.encodeStringLiteral(out, name);
        }
        this.encodeStringLiteral(out, value);
    }

    private int getNameIndex(CharSequence name) {
        int index = HpackStaticTable.getIndex(name);
        if (index == -1 && (index = this.getIndex(name)) >= 0) {
            index += HpackStaticTable.length;
        }
        return index;
    }

    private void ensureCapacity(long headerSize) {
        int index;
        while (this.maxHeaderTableSize - this.size < headerSize && (index = this.length()) != 0) {
            this.remove();
        }
    }

    int length() {
        return this.size == 0L ? 0 : this.head.after.index - this.head.before.index + 1;
    }

    long size() {
        return this.size;
    }

    HpackHeaderField getHeaderField(int index) {
        HeaderEntry entry = this.head;
        while (index-- >= 0) {
            entry = entry.before;
        }
        return entry;
    }

    private HeaderEntry getEntry(CharSequence name, CharSequence value) {
        if (this.length() == 0 || name == null || value == null) {
            return null;
        }
        int h2 = AsciiString.hashCode(name);
        int i2 = this.index(h2);
        HeaderEntry e2 = this.headerFields[i2];
        while (e2 != null) {
            if (e2.hash == h2 && (HpackUtil.equalsConstantTime(name, e2.name) & HpackUtil.equalsConstantTime(value, e2.value)) != 0) {
                return e2;
            }
            e2 = e2.next;
        }
        return null;
    }

    private int getIndex(CharSequence name) {
        if (this.length() == 0 || name == null) {
            return -1;
        }
        int h2 = AsciiString.hashCode(name);
        int i2 = this.index(h2);
        HeaderEntry e2 = this.headerFields[i2];
        while (e2 != null) {
            if (e2.hash == h2 && HpackUtil.equalsConstantTime(name, e2.name) != 0) {
                return this.getIndex(e2.index);
            }
            e2 = e2.next;
        }
        return -1;
    }

    private int getIndex(int index) {
        return index == -1 ? -1 : index - this.head.before.index + 1;
    }

    private void add(CharSequence name, CharSequence value, long headerSize) {
        HeaderEntry e2;
        if (headerSize > this.maxHeaderTableSize) {
            this.clear();
            return;
        }
        while (this.maxHeaderTableSize - this.size < headerSize) {
            this.remove();
        }
        int h2 = AsciiString.hashCode(name);
        int i2 = this.index(h2);
        HeaderEntry old = this.headerFields[i2];
        this.headerFields[i2] = e2 = new HeaderEntry(h2, name, value, this.head.before.index - 1, old);
        e2.addBefore(this.head);
        this.size += headerSize;
    }

    private HpackHeaderField remove() {
        HeaderEntry prev;
        if (this.size == 0L) {
            return null;
        }
        HeaderEntry eldest = this.head.after;
        int h2 = eldest.hash;
        int i2 = this.index(h2);
        HeaderEntry e2 = prev = this.headerFields[i2];
        while (e2 != null) {
            HeaderEntry next = e2.next;
            if (e2 == eldest) {
                if (prev == eldest) {
                    this.headerFields[i2] = next;
                } else {
                    prev.next = next;
                }
                eldest.remove();
                this.size -= (long)eldest.size();
                return eldest;
            }
            prev = e2;
            e2 = next;
        }
        return null;
    }

    private void clear() {
        Arrays.fill(this.headerFields, null);
        this.head.before = this.head.after = this.head;
        this.size = 0L;
    }

    private int index(int h2) {
        return h2 & this.hashMask;
    }

    private static final class HeaderEntry
    extends HpackHeaderField {
        HeaderEntry before;
        HeaderEntry after;
        HeaderEntry next;
        int hash;
        int index;

        HeaderEntry(int hash, CharSequence name, CharSequence value, int index, HeaderEntry next) {
            super(name, value);
            this.index = index;
            this.hash = hash;
            this.next = next;
        }

        private void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
            this.before = null;
            this.after = null;
            this.next = null;
        }

        private void addBefore(HeaderEntry existingEntry) {
            this.after = existingEntry;
            this.before = existingEntry.before;
            this.before.after = this;
            this.after.before = this;
        }
    }
}

