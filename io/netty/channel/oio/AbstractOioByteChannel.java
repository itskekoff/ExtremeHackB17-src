package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioChannel;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;

public abstract class AbstractOioByteChannel
extends AbstractOioChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';

    protected AbstractOioByteChannel(Channel parent) {
        super(parent);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    protected abstract boolean isInputShutdown();

    protected abstract ChannelFuture shutdownInput();

    private void closeOnRead(ChannelPipeline pipeline) {
        if (this.isOpen()) {
            if (Boolean.TRUE.equals(this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
                this.shutdownInput();
                pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
            } else {
                this.unsafe().close(this.unsafe().voidPromise());
            }
        }
    }

    private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, RecvByteBufAllocator.Handle allocHandle) {
        if (byteBuf != null) {
            if (byteBuf.isReadable()) {
                this.readPending = false;
                pipeline.fireChannelRead(byteBuf);
            } else {
                byteBuf.release();
            }
        }
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
        pipeline.fireExceptionCaught(cause);
        if (close || cause instanceof IOException) {
            this.closeOnRead(pipeline);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doRead() {
        ChannelConfig config = this.config();
        if (this.isInputShutdown() || !this.readPending) {
            return;
        }
        this.readPending = false;
        ChannelPipeline pipeline = this.pipeline();
        ByteBufAllocator allocator = config.getAllocator();
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        allocHandle.reset(config);
        ByteBuf byteBuf = null;
        boolean close = false;
        boolean readData = false;
        try {
            byteBuf = allocHandle.allocate(allocator);
            do {
                int maxCapacity;
                allocHandle.lastBytesRead(this.doReadBytes(byteBuf));
                if (allocHandle.lastBytesRead() <= 0) {
                    if (byteBuf.isReadable()) break;
                    byteBuf.release();
                    byteBuf = null;
                    close = allocHandle.lastBytesRead() < 0;
                    break;
                }
                readData = true;
                int available = this.available();
                if (available <= 0) break;
                if (byteBuf.isWritable()) continue;
                int capacity = byteBuf.capacity();
                if (capacity == (maxCapacity = byteBuf.maxCapacity())) {
                    allocHandle.incMessagesRead(1);
                    this.readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                    byteBuf = allocHandle.allocate(allocator);
                    continue;
                }
                int writerIndex = byteBuf.writerIndex();
                if (writerIndex + available > maxCapacity) {
                    byteBuf.capacity(maxCapacity);
                    continue;
                }
                byteBuf.ensureWritable(available);
            } while (allocHandle.continueReading());
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    this.readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                } else {
                    byteBuf.release();
                }
                byteBuf = null;
            }
            if (readData) {
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
            }
            if (close) {
                this.closeOnRead(pipeline);
            }
        }
        catch (Throwable t2) {
            this.handleReadException(pipeline, byteBuf, t2, close, allocHandle);
        }
        finally {
            if (this.readPending || config.isAutoRead() || !readData && this.isActive()) {
                this.read();
            }
        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in2) throws Exception {
        Object msg;
        while ((msg = in2.current()) != null) {
            if (msg instanceof ByteBuf) {
                ByteBuf buf2 = (ByteBuf)msg;
                int readableBytes = buf2.readableBytes();
                while (readableBytes > 0) {
                    this.doWriteBytes(buf2);
                    int newReadableBytes = buf2.readableBytes();
                    in2.progress(readableBytes - newReadableBytes);
                    readableBytes = newReadableBytes;
                }
                in2.remove();
                continue;
            }
            if (msg instanceof FileRegion) {
                FileRegion region = (FileRegion)msg;
                long transferred = region.transferred();
                this.doWriteFileRegion(region);
                in2.progress(region.transferred() - transferred);
                in2.remove();
                continue;
            }
            in2.remove(new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg)));
        }
    }

    @Override
    protected final Object filterOutboundMessage(Object msg) throws Exception {
        if (msg instanceof ByteBuf || msg instanceof FileRegion) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    protected abstract int available();

    protected abstract int doReadBytes(ByteBuf var1) throws Exception;

    protected abstract void doWriteBytes(ByteBuf var1) throws Exception;

    protected abstract void doWriteFileRegion(FileRegion var1) throws Exception;
}

