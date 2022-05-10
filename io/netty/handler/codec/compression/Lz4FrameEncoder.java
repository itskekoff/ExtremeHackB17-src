 * Could not load the following classes:
 *  net.jpountz.lz4.LZ4Compressor
 *  net.jpountz.lz4.LZ4Exception
 *  net.jpountz.lz4.LZ4Factory
 *  net.jpountz.xxhash.XXHashFactory
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.ByteBufChecksum;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.zip.Checksum;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.xxhash.XXHashFactory;

public class Lz4FrameEncoder
extends MessageToByteEncoder<ByteBuf> {
    static final int DEFAULT_MAX_ENCODE_SIZE = Integer.MAX_VALUE;
    private final int blockSize;
    private LZ4Compressor compressor;
    private ByteBufChecksum checksum;
    private final int compressionLevel;
    private ByteBuf buffer;
    private final int maxEncodeSize;
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;

    public Lz4FrameEncoder() {
        this(false);
    }

    public Lz4FrameEncoder(boolean highCompressor) {
        this(LZ4Factory.fastestInstance(), highCompressor, 65536, XXHashFactory.fastestInstance().newStreamingHash32(-1756908916).asChecksum());
    }

    public Lz4FrameEncoder(LZ4Factory factory, boolean highCompressor, int blockSize, Checksum checksum) {
        this(factory, highCompressor, blockSize, checksum, Integer.MAX_VALUE);
    }

    public Lz4FrameEncoder(LZ4Factory factory, boolean highCompressor, int blockSize, Checksum checksum, int maxEncodeSize) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        if (checksum == null) {
            throw new NullPointerException("checksum");
        }
        this.compressor = highCompressor ? factory.highCompressor() : factory.fastCompressor();
        this.checksum = ByteBufChecksum.wrapChecksum(checksum);
        this.compressionLevel = Lz4FrameEncoder.compressionLevel(blockSize);
        this.blockSize = blockSize;
        this.maxEncodeSize = ObjectUtil.checkPositive(maxEncodeSize, "maxEncodeSize");
        this.finished = false;
    }

    private static int compressionLevel(int blockSize) {
        if (blockSize < 64 || blockSize > 0x2000000) {
            throw new IllegalArgumentException(String.format("blockSize: %d (expected: %d-%d)", blockSize, 64, 0x2000000));
        }
        int compressionLevel = 32 - Integer.numberOfLeadingZeros(blockSize - 1);
        compressionLevel = Math.max(0, compressionLevel - 10);
        return compressionLevel;
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) {
        return this.allocateBuffer(ctx, msg, preferDirect, true);
    }

    private ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect, boolean allowEmptyReturn) {
        int targetBufSize = 0;
        int remaining = msg.readableBytes() + this.buffer.readableBytes();
        if (remaining < 0) {
            throw new EncoderException("too much data to allocate a buffer for compression");
        }
        while (remaining > 0) {
            int curSize = Math.min(this.blockSize, remaining);
            remaining -= curSize;
            targetBufSize += this.compressor.maxCompressedLength(curSize) + 21;
        }
        if (targetBufSize > this.maxEncodeSize || 0 > targetBufSize) {
            throw new EncoderException(String.format("requested encode buffer size (%d bytes) exceeds the maximum allowable size (%d bytes)", targetBufSize, this.maxEncodeSize));
        }
        if (allowEmptyReturn && targetBufSize < this.blockSize) {
            return Unpooled.EMPTY_BUFFER;
        }
        if (preferDirect) {
            return ctx.alloc().ioBuffer(targetBufSize, targetBufSize);
        }
        return ctx.alloc().heapBuffer(targetBufSize, targetBufSize);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in2, ByteBuf out) throws Exception {
        int length;
        if (this.finished) {
            out.writeBytes(in2);
            return;
        }
        ByteBuf buffer = this.buffer;
        while ((length = in2.readableBytes()) > 0) {
            int nextChunkSize = Math.min(length, buffer.writableBytes());
            in2.readBytes(buffer, nextChunkSize);
            if (buffer.isWritable()) continue;
            this.flushBufferedData(out);
        }
    }

    private void flushBufferedData(ByteBuf out) {
        int blockType;
        int compressedLength;
        int flushableBytes = this.buffer.readableBytes();
        if (flushableBytes == 0) {
            return;
        }
        this.checksum.reset();
        this.checksum.update(this.buffer, this.buffer.readerIndex(), flushableBytes);
        int check = (int)this.checksum.getValue();
        int bufSize = this.compressor.maxCompressedLength(flushableBytes) + 21;
        out.ensureWritable(bufSize);
        int idx = out.writerIndex();
        try {
            ByteBuffer outNioBuffer = out.internalNioBuffer(idx + 21, out.writableBytes() - 21);
            int pos = outNioBuffer.position();
            this.compressor.compress(this.buffer.internalNioBuffer(this.buffer.readerIndex(), flushableBytes), outNioBuffer);
            compressedLength = outNioBuffer.position() - pos;
        }
        catch (LZ4Exception e2) {
            throw new CompressionException(e2);
        }
        if (compressedLength >= flushableBytes) {
            blockType = 16;
            compressedLength = flushableBytes;
            out.setBytes(idx + 21, this.buffer, 0, flushableBytes);
        } else {
            blockType = 32;
        }
        out.setLong(idx, 5501767354678207339L);
        out.setByte(idx + 8, (byte)(blockType | this.compressionLevel));
        out.setIntLE(idx + 9, compressedLength);
        out.setIntLE(idx + 13, flushableBytes);
        out.setIntLE(idx + 17, check);
        out.writerIndex(idx + 21 + compressedLength);
        this.buffer.clear();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.buffer != null && this.buffer.isReadable()) {
            ByteBuf buf2 = this.allocateBuffer(ctx, Unpooled.EMPTY_BUFFER, this.isPreferDirect(), false);
            this.flushBufferedData(buf2);
            ctx.write(buf2);
        }
        ctx.flush();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
        if (this.finished) {
            promise.setSuccess();
            return promise;
        }
        this.finished = true;
        try {
            ByteBuf footer = ctx.alloc().heapBuffer(this.compressor.maxCompressedLength(this.buffer.readableBytes()) + 21);
            this.flushBufferedData(footer);
            int idx = footer.writerIndex();
            footer.setLong(idx, 5501767354678207339L);
            footer.setByte(idx + 8, (byte)(0x10 | this.compressionLevel));
            footer.setInt(idx + 9, 0);
            footer.setInt(idx + 13, 0);
            footer.setInt(idx + 17, 0);
            footer.writerIndex(idx + 21);
            ChannelFuture channelFuture = ctx.writeAndFlush(footer, promise);
            return channelFuture;
        }
        finally {
            this.cleanup();
        }
    }

    private void cleanup() {
        this.compressor = null;
        this.checksum = null;
        if (this.buffer != null) {
            this.buffer.release();
            this.buffer = null;
        }
    }

    public boolean isClosed() {
        return this.finished;
    }

    public ChannelFuture close() {
        return this.close(this.ctx().newPromise());
    }

    public ChannelFuture close(final ChannelPromise promise) {
        ChannelHandlerContext ctx = this.ctx();
        EventExecutor executor = ctx.executor();
        if (executor.inEventLoop()) {
            return this.finishEncode(ctx, promise);
        }
        executor.execute(new Runnable(){

            @Override
            public void run() {
                ChannelFuture f2 = Lz4FrameEncoder.this.finishEncode(Lz4FrameEncoder.this.ctx(), promise);
                f2.addListener(new ChannelPromiseNotifier(promise));
            }
        });
        return promise;
    }

    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ChannelFuture f2 = this.finishEncode(ctx, ctx.newPromise());
        f2.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture f2) throws Exception {
                ctx.close(promise);
            }
        });
        if (!f2.isDone()) {
            ctx.executor().schedule(new Runnable(){

                @Override
                public void run() {
                    ctx.close(promise);
                }
            }, 10L, TimeUnit.SECONDS);
        }
    }

    private ChannelHandlerContext ctx() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException("not added to a pipeline");
        }
        return ctx;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.buffer = Unpooled.wrappedBuffer(new byte[this.blockSize]);
        this.buffer.clear();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        this.cleanup();
    }

    final ByteBuf getBackingBuffer() {
        return this.buffer;
    }
}

