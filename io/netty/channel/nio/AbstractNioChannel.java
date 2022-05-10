package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioChannel
extends AbstractChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);
    private static final ClosedChannelException DO_CLOSE_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractNioChannel.class, "doClose()");
    private final SelectableChannel ch;
    protected final int readInterestOp;
    volatile SelectionKey selectionKey;
    boolean readPending;
    private final Runnable clearReadPendingRunnable = new Runnable(){

        @Override
        public void run() {
            AbstractNioChannel.this.clearReadPending0();
        }
    };
    private ChannelPromise connectPromise;
    private ScheduledFuture<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;

    protected AbstractNioChannel(Channel parent, SelectableChannel ch2, int readInterestOp) {
        super(parent);
        this.ch = ch2;
        this.readInterestOp = readInterestOp;
        try {
            ch2.configureBlocking(false);
        }
        catch (IOException e2) {
            block4: {
                try {
                    ch2.close();
                }
                catch (IOException e22) {
                    if (!logger.isWarnEnabled()) break block4;
                    logger.warn("Failed to close a partially initialized socket.", e22);
                }
            }
            throw new ChannelException("Failed to enter non-blocking mode.", e2);
        }
    }

    @Override
    public boolean isOpen() {
        return this.ch.isOpen();
    }

    @Override
    public NioUnsafe unsafe() {
        return (NioUnsafe)super.unsafe();
    }

    protected SelectableChannel javaChannel() {
        return this.ch;
    }

    @Override
    public NioEventLoop eventLoop() {
        return (NioEventLoop)super.eventLoop();
    }

    protected SelectionKey selectionKey() {
        assert (this.selectionKey != null);
        return this.selectionKey;
    }

    @Deprecated
    protected boolean isReadPending() {
        return this.readPending;
    }

    @Deprecated
    protected void setReadPending(final boolean readPending) {
        if (this.isRegistered()) {
            NioEventLoop eventLoop = this.eventLoop();
            if (eventLoop.inEventLoop()) {
                this.setReadPending0(readPending);
            } else {
                eventLoop.execute(new Runnable(){

                    @Override
                    public void run() {
                        AbstractNioChannel.this.setReadPending0(readPending);
                    }
                });
            }
        } else {
            this.readPending = readPending;
        }
    }

    protected final void clearReadPending() {
        if (this.isRegistered()) {
            NioEventLoop eventLoop = this.eventLoop();
            if (eventLoop.inEventLoop()) {
                this.clearReadPending0();
            } else {
                eventLoop.execute(this.clearReadPendingRunnable);
            }
        } else {
            this.readPending = false;
        }
    }

    private void setReadPending0(boolean readPending) {
        this.readPending = readPending;
        if (!readPending) {
            ((AbstractNioUnsafe)this.unsafe()).removeReadOp();
        }
    }

    private void clearReadPending0() {
        this.readPending = false;
        ((AbstractNioUnsafe)this.unsafe()).removeReadOp();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof NioEventLoop;
    }

    @Override
    protected void doRegister() throws Exception {
        boolean selected = false;
        while (true) {
            try {
                this.selectionKey = this.javaChannel().register(this.eventLoop().unwrappedSelector(), 0, this);
                return;
            }
            catch (CancelledKeyException e2) {
                if (!selected) {
                    this.eventLoop().selectNow();
                    selected = true;
                    continue;
                }
                throw e2;
            }
            break;
        }
    }

    @Override
    protected void doDeregister() throws Exception {
        this.eventLoop().cancel(this.selectionKey());
    }

    @Override
    protected void doBeginRead() throws Exception {
        SelectionKey selectionKey = this.selectionKey;
        if (!selectionKey.isValid()) {
            return;
        }
        this.readPending = true;
        int interestOps = selectionKey.interestOps();
        if ((interestOps & this.readInterestOp) == 0) {
            selectionKey.interestOps(interestOps | this.readInterestOp);
        }
    }

    protected abstract boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

    protected abstract void doFinishConnect() throws Exception;

    protected final ByteBuf newDirectBuffer(ByteBuf buf2) {
        int readableBytes = buf2.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.safeRelease(buf2);
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            ByteBuf directBuf = alloc.directBuffer(readableBytes);
            directBuf.writeBytes(buf2, buf2.readerIndex(), readableBytes);
            ReferenceCountUtil.safeRelease(buf2);
            return directBuf;
        }
        ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf != null) {
            directBuf.writeBytes(buf2, buf2.readerIndex(), readableBytes);
            ReferenceCountUtil.safeRelease(buf2);
            return directBuf;
        }
        return buf2;
    }

    protected final ByteBuf newDirectBuffer(ReferenceCounted holder, ByteBuf buf2) {
        int readableBytes = buf2.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.safeRelease(holder);
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            ByteBuf directBuf = alloc.directBuffer(readableBytes);
            directBuf.writeBytes(buf2, buf2.readerIndex(), readableBytes);
            ReferenceCountUtil.safeRelease(holder);
            return directBuf;
        }
        ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf != null) {
            directBuf.writeBytes(buf2, buf2.readerIndex(), readableBytes);
            ReferenceCountUtil.safeRelease(holder);
            return directBuf;
        }
        if (holder != buf2) {
            buf2.retain();
            ReferenceCountUtil.safeRelease(holder);
        }
        return buf2;
    }

    @Override
    protected void doClose() throws Exception {
        ScheduledFuture<?> future;
        ChannelPromise promise = this.connectPromise;
        if (promise != null) {
            promise.tryFailure(DO_CLOSE_CLOSED_CHANNEL_EXCEPTION);
            this.connectPromise = null;
        }
        if ((future = this.connectTimeoutFuture) != null) {
            future.cancel(false);
            this.connectTimeoutFuture = null;
        }
    }

    protected abstract class AbstractNioUnsafe
    extends AbstractChannel.AbstractUnsafe
    implements NioUnsafe {
        protected AbstractNioUnsafe() {
        }

        protected final void removeReadOp() {
            SelectionKey key = AbstractNioChannel.this.selectionKey();
            if (!key.isValid()) {
                return;
            }
            int interestOps = key.interestOps();
            if ((interestOps & AbstractNioChannel.this.readInterestOp) != 0) {
                key.interestOps(interestOps & ~AbstractNioChannel.this.readInterestOp);
            }
        }

        @Override
        public final SelectableChannel ch() {
            return AbstractNioChannel.this.javaChannel();
        }

        @Override
        public final void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            try {
                if (AbstractNioChannel.this.connectPromise != null) {
                    throw new ConnectionPendingException();
                }
                boolean wasActive = AbstractNioChannel.this.isActive();
                if (AbstractNioChannel.this.doConnect(remoteAddress, localAddress)) {
                    this.fulfillConnectPromise(promise, wasActive);
                } else {
                    AbstractNioChannel.this.connectPromise = promise;
                    AbstractNioChannel.this.requestedRemoteAddress = remoteAddress;
                    int connectTimeoutMillis = AbstractNioChannel.this.config().getConnectTimeoutMillis();
                    if (connectTimeoutMillis > 0) {
                        AbstractNioChannel.this.connectTimeoutFuture = AbstractNioChannel.this.eventLoop().schedule(new Runnable(){

                            @Override
                            public void run() {
                                ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
                                ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
                                if (connectPromise != null && connectPromise.tryFailure(cause)) {
                                    AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                                }
                            }
                        }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                    }
                    promise.addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isCancelled()) {
                                if (AbstractNioChannel.this.connectTimeoutFuture != null) {
                                    AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
                                }
                                AbstractNioChannel.this.connectPromise = null;
                                AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                            }
                        }
                    });
                }
            }
            catch (Throwable t2) {
                promise.tryFailure(this.annotateConnectException(t2, remoteAddress));
                this.closeIfClosed();
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
            if (promise == null) {
                return;
            }
            boolean active = AbstractNioChannel.this.isActive();
            boolean promiseSet = promise.trySuccess();
            if (!wasActive && active) {
                AbstractNioChannel.this.pipeline().fireChannelActive();
            }
            if (!promiseSet) {
                this.close(this.voidPromise());
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
            if (promise == null) {
                return;
            }
            promise.tryFailure(cause);
            this.closeIfClosed();
        }

        @Override
        public final void finishConnect() {
            assert (AbstractNioChannel.this.eventLoop().inEventLoop());
            try {
                boolean wasActive = AbstractNioChannel.this.isActive();
                AbstractNioChannel.this.doFinishConnect();
                this.fulfillConnectPromise(AbstractNioChannel.this.connectPromise, wasActive);
            }
            catch (Throwable t2) {
                this.fulfillConnectPromise(AbstractNioChannel.this.connectPromise, this.annotateConnectException(t2, AbstractNioChannel.this.requestedRemoteAddress));
            }
            finally {
                if (AbstractNioChannel.this.connectTimeoutFuture != null) {
                    AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
                }
                AbstractNioChannel.this.connectPromise = null;
            }
        }

        @Override
        protected final void flush0() {
            if (this.isFlushPending()) {
                return;
            }
            super.flush0();
        }

        @Override
        public final void forceFlush() {
            super.flush0();
        }

        private boolean isFlushPending() {
            SelectionKey selectionKey = AbstractNioChannel.this.selectionKey();
            return selectionKey.isValid() && (selectionKey.interestOps() & 4) != 0;
        }
    }

    public static interface NioUnsafe
    extends Channel.Unsafe {
        public SelectableChannel ch();

        public void finishConnect();

        public void read();

        public void forceFlush();
    }
}

