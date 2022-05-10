package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.HpackDynamicTable;
import io.netty.handler.codec.http2.HpackHeaderField;
import io.netty.handler.codec.http2.HpackHuffmanDecoder;
import io.netty.handler.codec.http2.HpackStaticTable;
import io.netty.handler.codec.http2.HpackUtil;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;

final class HpackDecoder {
    private static final Http2Exception DECODE_ULE_128_DECOMPRESSION_EXCEPTION = ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - decompression failure", new Object[0]), HpackDecoder.class, "decodeULE128(..)");
    private static final Http2Exception DECODE_ULE_128_TO_LONG_DECOMPRESSION_EXCEPTION = ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - long overflow", new Object[0]), HpackDecoder.class, "decodeULE128(..)");
    private static final Http2Exception DECODE_ULE_128_TO_INT_DECOMPRESSION_EXCEPTION = ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - int overflow", new Object[0]), HpackDecoder.class, "decodeULE128ToInt(..)");
    private static final Http2Exception DECODE_ILLEGAL_INDEX_VALUE = ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value", new Object[0]), HpackDecoder.class, "decode(..)");
    private static final Http2Exception INDEX_HEADER_ILLEGAL_INDEX_VALUE = ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value", new Object[0]), HpackDecoder.class, "indexHeader(..)");
    private static final Http2Exception READ_NAME_ILLEGAL_INDEX_VALUE = ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - illegal index value", new Object[0]), HpackDecoder.class, "readName(..)");
    private static final Http2Exception INVALID_MAX_DYNAMIC_TABLE_SIZE = ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - invalid max dynamic table size", new Object[0]), HpackDecoder.class, "setDynamicTableSize(..)");
    private static final Http2Exception MAX_DYNAMIC_TABLE_SIZE_CHANGE_REQUIRED = ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - max dynamic table size change required", new Object[0]), HpackDecoder.class, "decode(..)");
    private static final byte READ_HEADER_REPRESENTATION = 0;
    private static final byte READ_MAX_DYNAMIC_TABLE_SIZE = 1;
    private static final byte READ_INDEXED_HEADER = 2;
    private static final byte READ_INDEXED_HEADER_NAME = 3;
    private static final byte READ_LITERAL_HEADER_NAME_LENGTH_PREFIX = 4;
    private static final byte READ_LITERAL_HEADER_NAME_LENGTH = 5;
    private static final byte READ_LITERAL_HEADER_NAME = 6;
    private static final byte READ_LITERAL_HEADER_VALUE_LENGTH_PREFIX = 7;
    private static final byte READ_LITERAL_HEADER_VALUE_LENGTH = 8;
    private static final byte READ_LITERAL_HEADER_VALUE = 9;
    private final HpackDynamicTable hpackDynamicTable;
    private final HpackHuffmanDecoder hpackHuffmanDecoder;
    private long maxHeaderListSizeGoAway;
    private long maxHeaderListSize;
    private long maxDynamicTableSize;
    private long encoderMaxDynamicTableSize;
    private boolean maxDynamicTableSizeChangeRequired;

    HpackDecoder(long maxHeaderListSize, int initialHuffmanDecodeCapacity) {
        this(maxHeaderListSize, initialHuffmanDecodeCapacity, 4096);
    }

    HpackDecoder(long maxHeaderListSize, int initialHuffmanDecodeCapacity, int maxHeaderTableSize) {
        this.maxHeaderListSize = ObjectUtil.checkPositive(maxHeaderListSize, "maxHeaderListSize");
        this.maxHeaderListSizeGoAway = Http2CodecUtil.calculateMaxHeaderListSizeGoAway(maxHeaderListSize);
        this.maxDynamicTableSize = this.encoderMaxDynamicTableSize = (long)maxHeaderTableSize;
        this.maxDynamicTableSizeChangeRequired = false;
        this.hpackDynamicTable = new HpackDynamicTable(maxHeaderTableSize);
        this.hpackHuffmanDecoder = new HpackHuffmanDecoder(initialHuffmanDecodeCapacity);
    }

    public void decode(int streamId, ByteBuf in2, Http2Headers headers) throws Http2Exception {
        int index = 0;
        long headersLength = 0L;
        int nameLength = 0;
        int valueLength = 0;
        int state = 0;
        boolean huffmanEncoded = false;
        CharSequence name = null;
        HpackUtil.IndexType indexType = HpackUtil.IndexType.NONE;
        block28: while (in2.isReadable()) {
            switch (state) {
                case 0: {
                    byte b2 = in2.readByte();
                    if (this.maxDynamicTableSizeChangeRequired && (b2 & 0xE0) != 32) {
                        throw MAX_DYNAMIC_TABLE_SIZE_CHANGE_REQUIRED;
                    }
                    if (b2 < 0) {
                        index = b2 & 0x7F;
                        switch (index) {
                            case 0: {
                                throw DECODE_ILLEGAL_INDEX_VALUE;
                            }
                            case 127: {
                                state = 2;
                                continue block28;
                            }
                        }
                        headersLength = this.indexHeader(streamId, index, headers, headersLength);
                        continue block28;
                    }
                    if ((b2 & 0x40) == 64) {
                        indexType = HpackUtil.IndexType.INCREMENTAL;
                        index = b2 & 0x3F;
                        switch (index) {
                            case 0: {
                                state = 4;
                                continue block28;
                            }
                            case 63: {
                                state = 3;
                                continue block28;
                            }
                        }
                        name = this.readName(index);
                        state = 7;
                        continue block28;
                    }
                    if ((b2 & 0x20) == 32) {
                        index = b2 & 0x1F;
                        if (index == 31) {
                            state = 1;
                            continue block28;
                        }
                        this.setDynamicTableSize(index);
                        state = 0;
                        continue block28;
                    }
                    indexType = (b2 & 0x10) == 16 ? HpackUtil.IndexType.NEVER : HpackUtil.IndexType.NONE;
                    index = b2 & 0xF;
                    switch (index) {
                        case 0: {
                            state = 4;
                            continue block28;
                        }
                        case 15: {
                            state = 3;
                            continue block28;
                        }
                    }
                    name = this.readName(index);
                    state = 7;
                    continue block28;
                }
                case 1: {
                    this.setDynamicTableSize(HpackDecoder.decodeULE128(in2, (long)index));
                    state = 0;
                    continue block28;
                }
                case 2: {
                    headersLength = this.indexHeader(streamId, HpackDecoder.decodeULE128(in2, index), headers, headersLength);
                    state = 0;
                    continue block28;
                }
                case 3: {
                    name = this.readName(HpackDecoder.decodeULE128(in2, index));
                    state = 7;
                    continue block28;
                }
                case 4: {
                    byte b2 = in2.readByte();
                    huffmanEncoded = (b2 & 0x80) == 128;
                    index = b2 & 0x7F;
                    if (index == 127) {
                        state = 5;
                        continue block28;
                    }
                    if ((long)index > this.maxHeaderListSizeGoAway - headersLength) {
                        Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
                    }
                    nameLength = index;
                    state = 6;
                    continue block28;
                }
                case 5: {
                    nameLength = HpackDecoder.decodeULE128(in2, index);
                    if ((long)nameLength > this.maxHeaderListSizeGoAway - headersLength) {
                        Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
                    }
                    state = 6;
                    continue block28;
                }
                case 6: {
                    if (in2.readableBytes() < nameLength) {
                        throw HpackDecoder.notEnoughDataException(in2);
                    }
                    name = this.readStringLiteral(in2, nameLength, huffmanEncoded);
                    state = 7;
                    continue block28;
                }
                case 7: {
                    byte b2 = in2.readByte();
                    huffmanEncoded = (b2 & 0x80) == 128;
                    index = b2 & 0x7F;
                    switch (index) {
                        case 127: {
                            state = 8;
                            continue block28;
                        }
                        case 0: {
                            headersLength = this.insertHeader(streamId, headers, name, AsciiString.EMPTY_STRING, indexType, headersLength);
                            state = 0;
                            continue block28;
                        }
                    }
                    if ((long)index + (long)nameLength > this.maxHeaderListSizeGoAway - headersLength) {
                        Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
                    }
                    valueLength = index;
                    state = 9;
                    continue block28;
                }
                case 8: {
                    valueLength = HpackDecoder.decodeULE128(in2, index);
                    if ((long)valueLength + (long)nameLength > this.maxHeaderListSizeGoAway - headersLength) {
                        Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
                    }
                    state = 9;
                    continue block28;
                }
                case 9: {
                    if (in2.readableBytes() < valueLength) {
                        throw HpackDecoder.notEnoughDataException(in2);
                    }
                    CharSequence value = this.readStringLiteral(in2, valueLength, huffmanEncoded);
                    headersLength = this.insertHeader(streamId, headers, name, value, indexType, headersLength);
                    state = 0;
                    continue block28;
                }
            }
            throw new Error("should not reach here state: " + state);
        }
        if (headersLength > this.maxHeaderListSize) {
            Http2CodecUtil.headerListSizeExceeded(streamId, this.maxHeaderListSize, true);
        }
    }

    public void setMaxHeaderTableSize(long maxHeaderTableSize) throws Http2Exception {
        if (maxHeaderTableSize < 0L || maxHeaderTableSize > 0xFFFFFFFFL) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header Table Size must be >= %d and <= %d but was %d", 0L, 0xFFFFFFFFL, maxHeaderTableSize);
        }
        this.maxDynamicTableSize = maxHeaderTableSize;
        if (this.maxDynamicTableSize < this.encoderMaxDynamicTableSize) {
            this.maxDynamicTableSizeChangeRequired = true;
            this.hpackDynamicTable.setCapacity(this.maxDynamicTableSize);
        }
    }

    public void setMaxHeaderListSize(long maxHeaderListSize, long maxHeaderListSizeGoAway) throws Http2Exception {
        if (maxHeaderListSizeGoAway < maxHeaderListSize || maxHeaderListSizeGoAway < 0L) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "Header List Size GO_AWAY %d must be positive and >= %d", maxHeaderListSizeGoAway, maxHeaderListSize);
        }
        if (maxHeaderListSize < 0L || maxHeaderListSize > 0xFFFFFFFFL) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header List Size must be >= %d and <= %d but was %d", 0L, 0xFFFFFFFFL, maxHeaderListSize);
        }
        this.maxHeaderListSize = maxHeaderListSize;
        this.maxHeaderListSizeGoAway = maxHeaderListSizeGoAway;
    }

    public long getMaxHeaderListSize() {
        return this.maxHeaderListSize;
    }

    public long getMaxHeaderListSizeGoAway() {
        return this.maxHeaderListSizeGoAway;
    }

    public long getMaxHeaderTableSize() {
        return this.hpackDynamicTable.capacity();
    }

    int length() {
        return this.hpackDynamicTable.length();
    }

    long size() {
        return this.hpackDynamicTable.size();
    }

    HpackHeaderField getHeaderField(int index) {
        return this.hpackDynamicTable.getEntry(index + 1);
    }

    private void setDynamicTableSize(long dynamicTableSize) throws Http2Exception {
        if (dynamicTableSize > this.maxDynamicTableSize) {
            throw INVALID_MAX_DYNAMIC_TABLE_SIZE;
        }
        this.encoderMaxDynamicTableSize = dynamicTableSize;
        this.maxDynamicTableSizeChangeRequired = false;
        this.hpackDynamicTable.setCapacity(dynamicTableSize);
    }

    private CharSequence readName(int index) throws Http2Exception {
        if (index <= HpackStaticTable.length) {
            HpackHeaderField hpackHeaderField = HpackStaticTable.getEntry(index);
            return hpackHeaderField.name;
        }
        if (index - HpackStaticTable.length <= this.hpackDynamicTable.length()) {
            HpackHeaderField hpackHeaderField = this.hpackDynamicTable.getEntry(index - HpackStaticTable.length);
            return hpackHeaderField.name;
        }
        throw READ_NAME_ILLEGAL_INDEX_VALUE;
    }

    private long indexHeader(int streamId, int index, Http2Headers headers, long headersLength) throws Http2Exception {
        if (index <= HpackStaticTable.length) {
            HpackHeaderField hpackHeaderField = HpackStaticTable.getEntry(index);
            return this.addHeader(streamId, headers, hpackHeaderField.name, hpackHeaderField.value, headersLength);
        }
        if (index - HpackStaticTable.length <= this.hpackDynamicTable.length()) {
            HpackHeaderField hpackHeaderField = this.hpackDynamicTable.getEntry(index - HpackStaticTable.length);
            return this.addHeader(streamId, headers, hpackHeaderField.name, hpackHeaderField.value, headersLength);
        }
        throw INDEX_HEADER_ILLEGAL_INDEX_VALUE;
    }

    private long insertHeader(int streamId, Http2Headers headers, CharSequence name, CharSequence value, HpackUtil.IndexType indexType, long headerSize) throws Http2Exception {
        headerSize = this.addHeader(streamId, headers, name, value, headerSize);
        switch (indexType) {
            case NONE: 
            case NEVER: {
                break;
            }
            case INCREMENTAL: {
                this.hpackDynamicTable.add(new HpackHeaderField(name, value));
                break;
            }
            default: {
                throw new Error("should not reach here");
            }
        }
        return headerSize;
    }

    private long addHeader(int streamId, Http2Headers headers, CharSequence name, CharSequence value, long headersLength) throws Http2Exception {
        if ((headersLength += (long)(name.length() + value.length())) > this.maxHeaderListSizeGoAway) {
            Http2CodecUtil.headerListSizeExceeded(this.maxHeaderListSizeGoAway);
        }
        headers.add(name, value);
        return headersLength;
    }

    private CharSequence readStringLiteral(ByteBuf in2, int length, boolean huffmanEncoded) throws Http2Exception {
        if (huffmanEncoded) {
            return this.hpackHuffmanDecoder.decode(in2, length);
        }
        byte[] buf2 = new byte[length];
        in2.readBytes(buf2);
        return new AsciiString(buf2, false);
    }

    private static IllegalArgumentException notEnoughDataException(ByteBuf in2) {
        return new IllegalArgumentException("decode only works with an entire header block! " + in2);
    }

    static int decodeULE128(ByteBuf in2, int result) throws Http2Exception {
        int readerIndex = in2.readerIndex();
        long v2 = HpackDecoder.decodeULE128(in2, (long)result);
        if (v2 > Integer.MAX_VALUE) {
            in2.readerIndex(readerIndex);
            throw DECODE_ULE_128_TO_INT_DECOMPRESSION_EXCEPTION;
        }
        return (int)v2;
    }

    static long decodeULE128(ByteBuf in2, long result) throws Http2Exception {
        assert (result <= 127L && result >= 0L);
        boolean resultStartedAtZero = result == 0L;
        int writerIndex = in2.writerIndex();
        int readerIndex = in2.readerIndex();
        int shift = 0;
        while (readerIndex < writerIndex) {
            byte b2 = in2.getByte(readerIndex);
            if (shift == 56 && ((b2 & 0x80) != 0 || b2 == 127 && !resultStartedAtZero)) {
                throw DECODE_ULE_128_TO_LONG_DECOMPRESSION_EXCEPTION;
            }
            if ((b2 & 0x80) == 0) {
                in2.readerIndex(readerIndex + 1);
                return result + (((long)b2 & 0x7FL) << shift);
            }
            result += ((long)b2 & 0x7FL) << shift;
            ++readerIndex;
            shift += 7;
        }
        throw DECODE_ULE_128_DECOMPRESSION_EXCEPTION;
    }
}

