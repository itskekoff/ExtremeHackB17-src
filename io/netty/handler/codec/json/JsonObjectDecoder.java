package io.netty.handler.codec.json;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class JsonObjectDecoder
extends ByteToMessageDecoder {
    private static final int ST_CORRUPTED = -1;
    private static final int ST_INIT = 0;
    private static final int ST_DECODING_NORMAL = 1;
    private static final int ST_DECODING_ARRAY_STREAM = 2;
    private int openBraces;
    private int idx;
    private int state;
    private boolean insideString;
    private final int maxObjectLength;
    private final boolean streamArrayElements;

    public JsonObjectDecoder() {
        this(0x100000);
    }

    public JsonObjectDecoder(int maxObjectLength) {
        this(maxObjectLength, false);
    }

    public JsonObjectDecoder(boolean streamArrayElements) {
        this(0x100000, streamArrayElements);
    }

    public JsonObjectDecoder(int maxObjectLength, boolean streamArrayElements) {
        if (maxObjectLength < 1) {
            throw new IllegalArgumentException("maxObjectLength must be a positive int");
        }
        this.maxObjectLength = maxObjectLength;
        this.streamArrayElements = streamArrayElements;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        int idx;
        if (this.state == -1) {
            in2.skipBytes(in2.readableBytes());
            return;
        }
        int wrtIdx = in2.writerIndex();
        if (wrtIdx > this.maxObjectLength) {
            in2.skipBytes(in2.readableBytes());
            this.reset();
            throw new TooLongFrameException("object length exceeds " + this.maxObjectLength + ": " + wrtIdx + " bytes discarded");
        }
        for (idx = this.idx; idx < wrtIdx; ++idx) {
            byte c2 = in2.getByte(idx);
            if (this.state == 1) {
                this.decodeByte(c2, in2, idx);
                if (this.openBraces != 0) continue;
                ByteBuf json = this.extractObject(ctx, in2, in2.readerIndex(), idx + 1 - in2.readerIndex());
                if (json != null) {
                    out.add(json);
                }
                in2.readerIndex(idx + 1);
                this.reset();
                continue;
            }
            if (this.state == 2) {
                int idxNoSpaces;
                this.decodeByte(c2, in2, idx);
                if (this.insideString || (this.openBraces != 1 || c2 != 44) && (this.openBraces != 0 || c2 != 93)) continue;
                int i2 = in2.readerIndex();
                while (Character.isWhitespace(in2.getByte(i2))) {
                    in2.skipBytes(1);
                    ++i2;
                }
                for (idxNoSpaces = idx - 1; idxNoSpaces >= in2.readerIndex() && Character.isWhitespace(in2.getByte(idxNoSpaces)); --idxNoSpaces) {
                }
                ByteBuf json = this.extractObject(ctx, in2, in2.readerIndex(), idxNoSpaces + 1 - in2.readerIndex());
                if (json != null) {
                    out.add(json);
                }
                in2.readerIndex(idx + 1);
                if (c2 != 93) continue;
                this.reset();
                continue;
            }
            if (c2 == 123 || c2 == 91) {
                this.initDecoding(c2);
                if (this.state != 2) continue;
                in2.skipBytes(1);
                continue;
            }
            if (Character.isWhitespace(c2)) {
                in2.skipBytes(1);
                continue;
            }
            this.state = -1;
            throw new CorruptedFrameException("invalid JSON received at byte position " + idx + ": " + ByteBufUtil.hexDump(in2));
        }
        this.idx = in2.readableBytes() == 0 ? 0 : idx;
    }

    protected ByteBuf extractObject(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.retainedSlice(index, length);
    }

    private void decodeByte(byte c2, ByteBuf in2, int idx) {
        if (!(c2 != 123 && c2 != 91 || this.insideString)) {
            ++this.openBraces;
        } else if (!(c2 != 125 && c2 != 93 || this.insideString)) {
            --this.openBraces;
        } else if (c2 == 34) {
            if (!this.insideString) {
                this.insideString = true;
            } else {
                int backslashCount = 0;
                --idx;
                while (idx >= 0 && in2.getByte(idx) == 92) {
                    ++backslashCount;
                    --idx;
                }
                if (backslashCount % 2 == 0) {
                    this.insideString = false;
                }
            }
        }
    }

    private void initDecoding(byte openingBrace) {
        this.openBraces = 1;
        this.state = openingBrace == 91 && this.streamArrayElements ? 2 : 1;
    }

    private void reset() {
        this.insideString = false;
        this.state = 0;
        this.openBraces = 0;
    }
}

