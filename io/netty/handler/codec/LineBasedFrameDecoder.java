package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import java.util.List;

public class LineBasedFrameDecoder
extends ByteToMessageDecoder {
    private final int maxLength;
    private final boolean failFast;
    private final boolean stripDelimiter;
    private boolean discarding;
    private int discardedBytes;

    public LineBasedFrameDecoder(int maxLength) {
        this(maxLength, true, false);
    }

    public LineBasedFrameDecoder(int maxLength, boolean stripDelimiter, boolean failFast) {
        this.maxLength = maxLength;
        this.failFast = failFast;
        this.stripDelimiter = stripDelimiter;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        Object decoded = this.decode(ctx, in2);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        int eol = LineBasedFrameDecoder.findEndOfLine(buffer);
        if (!this.discarding) {
            if (eol >= 0) {
                ByteBuf frame;
                int delimLength;
                int length = eol - buffer.readerIndex();
                int n2 = delimLength = buffer.getByte(eol) == 13 ? 2 : 1;
                if (length > this.maxLength) {
                    buffer.readerIndex(eol + delimLength);
                    this.fail(ctx, length);
                    return null;
                }
                if (this.stripDelimiter) {
                    frame = buffer.readRetainedSlice(length);
                    buffer.skipBytes(delimLength);
                } else {
                    frame = buffer.readRetainedSlice(length + delimLength);
                }
                return frame;
            }
            int length = buffer.readableBytes();
            if (length > this.maxLength) {
                this.discardedBytes = length;
                buffer.readerIndex(buffer.writerIndex());
                this.discarding = true;
                if (this.failFast) {
                    this.fail(ctx, "over " + this.discardedBytes);
                }
            }
            return null;
        }
        if (eol >= 0) {
            int length = this.discardedBytes + eol - buffer.readerIndex();
            int delimLength = buffer.getByte(eol) == 13 ? 2 : 1;
            buffer.readerIndex(eol + delimLength);
            this.discardedBytes = 0;
            this.discarding = false;
            if (!this.failFast) {
                this.fail(ctx, length);
            }
        } else {
            this.discardedBytes += buffer.readableBytes();
            buffer.readerIndex(buffer.writerIndex());
        }
        return null;
    }

    private void fail(ChannelHandlerContext ctx, int length) {
        this.fail(ctx, String.valueOf(length));
    }

    private void fail(ChannelHandlerContext ctx, String length) {
        ctx.fireExceptionCaught(new TooLongFrameException("frame length (" + length + ") exceeds the allowed maximum (" + this.maxLength + ')'));
    }

    private static int findEndOfLine(ByteBuf buffer) {
        int i2 = buffer.forEachByte(ByteProcessor.FIND_LF);
        if (i2 > 0 && buffer.getByte(i2 - 1) == 13) {
            --i2;
        }
        return i2;
    }
}

