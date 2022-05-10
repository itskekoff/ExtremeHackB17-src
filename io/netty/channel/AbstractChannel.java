package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelId;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public abstract class AbstractChannel
extends DefaultAttributeMap
implements Channel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannel.class);
    private static final ClosedChannelException FLUSH0_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractUnsafe.class, "flush0()");
    private static final ClosedChannelException ENSURE_OPEN_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractUnsafe.class, "ensureOpen(...)");
    private static final ClosedChannelException CLOSE_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractUnsafe.class, "close(...)");
    private static final ClosedChannelException WRITE_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractUnsafe.class, "write(...)");
    private static final NotYetConnectedException FLUSH0_NOT_YET_CONNECTED_EXCEPTION = ThrowableUtil.unknownStackTrace(new NotYetConnectedException(), AbstractUnsafe.class, "flush0()");
    private final Channel parent;
    private final ChannelId id;
    private final Channel.Unsafe unsafe;
    private final DefaultChannelPipeline pipeline;
    private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise(this, false);
    private final CloseFuture closeFuture = new CloseFuture(this);
    private volatile SocketAddress localAddress;
    private volatile SocketAddress remoteAddress;
    private volatile EventLoop eventLoop;
    private volatile boolean registered;
    private boolean strValActive;
    private String strVal;

    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        this.id = this.newId();
        this.unsafe = this.newUnsafe();
        this.pipeline = this.newChannelPipeline();
    }

    protected AbstractChannel(Channel parent, ChannelId id2) {
        this.parent = parent;
        this.id = id2;
        this.unsafe = this.newUnsafe();
        this.pipeline = this.newChannelPipeline();
    }

    @Override
    public final ChannelId id() {
        return this.id;
    }

    protected ChannelId newId() {
        return DefaultChannelId.newInstance();
    }

    protected DefaultChannelPipeline newChannelPipeline() {
        return new DefaultChannelPipeline(this);
    }

    @Override
    public boolean isWritable() {
        ChannelOutboundBuffer buf2 = this.unsafe.outboundBuffer();
        return buf2 != null && buf2.isWritable();
    }

    @Override
    public long bytesBeforeUnwritable() {
        ChannelOutboundBuffer buf2 = this.unsafe.outboundBuffer();
        return buf2 != null ? buf2.bytesBeforeUnwritable() : 0L;
    }

    @Override
    public long bytesBeforeWritable() {
        ChannelOutboundBuffer buf2 = this.unsafe.outboundBuffer();
        return buf2 != null ? buf2.bytesBeforeWritable() : Long.MAX_VALUE;
    }

    @Override
    public Channel parent() {
        return this.parent;
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.config().getAllocator();
    }

    @Override
    public EventLoop eventLoop() {
        EventLoop eventLoop = this.eventLoop;
        if (eventLoop == null) {
            throw new IllegalStateException("channel not registered to an event loop");
        }
        return eventLoop;
    }

    @Override
    public SocketAddress localAddress() {
        SocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            try {
                this.localAddress = localAddress = this.unsafe().localAddress();
            }
            catch (Throwable t2) {
                return null;
            }
        }
        return localAddress;
    }

    @Deprecated
    protected void invalidateLocalAddress() {
        this.localAddress = null;
    }

    @Override
    public SocketAddress remoteAddress() {
        SocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress == null) {
            try {
                this.remoteAddress = remoteAddress = this.unsafe().remoteAddress();
            }
            catch (Throwable t2) {
                return null;
            }
        }
        return remoteAddress;
    }

    @Deprecated
    protected void invalidateRemoteAddress() {
        this.remoteAddress = null;
    }

    @Override
    public boolean isRegistered() {
        return this.registered;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return this.pipeline.bind(localAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.pipeline.connect(remoteAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.pipeline.connect(remoteAddress, localAddress);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.pipeline.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.pipeline.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.pipeline.deregister();
    }

    @Override
    public Channel flush() {
        this.pipeline.flush();
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline.bind(localAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.pipeline.connect(remoteAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.pipeline.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return this.pipeline.disconnect(promise);
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return this.pipeline.close(promise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        return this.pipeline.deregister(promise);
    }

    @Override
    public Channel read() {
        this.pipeline.read();
        return this;
    }

    @Override
    public ChannelFuture write(Object msg) {
        return this.pipeline.write(msg);
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.pipeline.write(msg, promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.pipeline.writeAndFlush(msg);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.pipeline.writeAndFlush(msg, promise);
    }

    @Override
    public ChannelPromise newPromise() {
        return this.pipeline.newPromise();
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return this.pipeline.newProgressivePromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return this.pipeline.newSucceededFuture();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return this.pipeline.newFailedFuture(cause);
    }

    @Override
    public ChannelFuture closeFuture() {
        return this.closeFuture;
    }

    @Override
    public Channel.Unsafe unsafe() {
        return this.unsafe;
    }

    protected abstract AbstractUnsafe newUnsafe();

    public final int hashCode() {
        return this.id.hashCode();
    }

    public final boolean equals(Object o2) {
        return this == o2;
    }

    @Override
    public final int compareTo(Channel o2) {
        if (this == o2) {
            return 0;
        }
        return this.id().compareTo(o2.id());
    }

    public String toString() {
        boolean active = this.isActive();
        if (this.strValActive == active && this.strVal != null) {
            return this.strVal;
        }
        SocketAddress remoteAddr = this.remoteAddress();
        SocketAddress localAddr = this.localAddress();
        if (remoteAddr != null) {
            StringBuilder buf2 = new StringBuilder(96).append("[id: 0x").append(this.id.asShortText()).append(", L:").append(localAddr).append(active ? " - " : " ! ").append("R:").append(remoteAddr).append(']');
            this.strVal = buf2.toString();
        } else if (localAddr != null) {
            StringBuilder buf3 = new StringBuilder(64).append("[id: 0x").append(this.id.asShortText()).append(", L:").append(localAddr).append(']');
            this.strVal = buf3.toString();
        } else {
            StringBuilder buf4 = new StringBuilder(16).append("[id: 0x").append(this.id.asShortText()).append(']');
            this.strVal = buf4.toString();
        }
        this.strValActive = active;
        return this.strVal;
    }

    @Override
    public final ChannelPromise voidPromise() {
        return this.pipeline.voidPromise();
    }

    protected abstract boolean isCompatible(EventLoop var1);

    protected abstract SocketAddress localAddress0();

    protected abstract SocketAddress remoteAddress0();

    protected void doRegister() throws Exception {
    }

    protected abstract void doBind(SocketAddress var1) throws Exception;

    protected abstract void doDisconnect() throws Exception;

    protected abstract void doClose() throws Exception;

    protected void doDeregister() throws Exception {
    }

    protected abstract void doBeginRead() throws Exception;

    protected abstract void doWrite(ChannelOutboundBuffer var1) throws Exception;

    protected Object filterOutboundMessage(Object msg) throws Exception {
        return msg;
    }

    private static final class AnnotatedSocketException
    extends SocketException {
        private static final long serialVersionUID = 3896743275010454039L;

        AnnotatedSocketException(SocketException exception, SocketAddress remoteAddress) {
            super(exception.getMessage() + ": " + remoteAddress);
            this.initCause(exception);
            this.setStackTrace(exception.getStackTrace());
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    private static final class AnnotatedNoRouteToHostException
    extends NoRouteToHostException {
        private static final long serialVersionUID = -6801433937592080623L;

        AnnotatedNoRouteToHostException(NoRouteToHostException exception, SocketAddress remoteAddress) {
            super(exception.getMessage() + ": " + remoteAddress);
            this.initCause(exception);
            this.setStackTrace(exception.getStackTrace());
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    private static final class AnnotatedConnectException
    extends ConnectException {
        private static final long serialVersionUID = 3901958112696433556L;

        AnnotatedConnectException(ConnectException exception, SocketAddress remoteAddress) {
            super(exception.getMessage() + ": " + remoteAddress);
            this.initCause(exception);
            this.setStackTrace(exception.getStackTrace());
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    static final class CloseFuture
    extends DefaultChannelPromise {
        CloseFuture(AbstractChannel ch2) {
            super(ch2);
        }

        @Override
        public ChannelPromise setSuccess() {
            throw new IllegalStateException();
        }

        @Override
        public ChannelPromise setFailure(Throwable cause) {
            throw new IllegalStateException();
        }

        @Override
        public boolean trySuccess() {
            throw new IllegalStateException();
        }

        @Override
        public boolean tryFailure(Throwable cause) {
            throw new IllegalStateException();
        }

        boolean setClosed() {
            return super.trySuccess();
        }
    }

    protected abstract class AbstractUnsafe
    implements Channel.Unsafe {
        private volatile ChannelOutboundBuffer outboundBuffer;
        private RecvByteBufAllocator.Handle recvHandle;
        private boolean inFlush0;
        private boolean neverRegistered;

        protected AbstractUnsafe() {
            this.outboundBuffer = new ChannelOutboundBuffer(AbstractChannel.this);
            this.neverRegistered = true;
        }

        private void assertEventLoop() {
            assert (!AbstractChannel.this.registered || AbstractChannel.this.eventLoop.inEventLoop());
        }

        @Override
        public RecvByteBufAllocator.Handle recvBufAllocHandle() {
            if (this.recvHandle == null) {
                this.recvHandle = AbstractChannel.this.config().getRecvByteBufAllocator().newHandle();
            }
            return this.recvHandle;
        }

        @Override
        public final ChannelOutboundBuffer outboundBuffer() {
            return this.outboundBuffer;
        }

        @Override
        public final SocketAddress localAddress() {
            return AbstractChannel.this.localAddress0();
        }

        @Override
        public final SocketAddress remoteAddress() {
            return AbstractChannel.this.remoteAddress0();
        }

        @Override
        public final void register(EventLoop eventLoop, final ChannelPromise promise) {
            if (eventLoop == null) {
                throw new NullPointerException("eventLoop");
            }
            if (AbstractChannel.this.isRegistered()) {
                promise.setFailure(new IllegalStateException("registered to an event loop already"));
                return;
            }
            if (!AbstractChannel.this.isCompatible(eventLoop)) {
                promise.setFailure(new IllegalStateException("incompatible event loop type: " + eventLoop.getClass().getName()));
                return;
            }
            AbstractChannel.this.eventLoop = eventLoop;
            if (eventLoop.inEventLoop()) {
                this.register0(promise);
            } else {
                try {
                    eventLoop.execute(new Runnable(){

                        @Override
                        public void run() {
                            AbstractUnsafe.this.register0(promise);
                        }
                    });
                }
                catch (Throwable t2) {
                    logger.warn("Force-closing a channel whose registration task was not accepted by an event loop: {}", (Object)AbstractChannel.this, (Object)t2);
                    this.closeForcibly();
                    AbstractChannel.this.closeFuture.setClosed();
                    this.safeSetFailure(promise, t2);
                }
            }
        }

        private void register0(ChannelPromise promise) {
            try {
                if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                    return;
                }
                boolean firstRegistration = this.neverRegistered;
                AbstractChannel.this.doRegister();
                this.neverRegistered = false;
                AbstractChannel.this.registered = true;
                AbstractChannel.this.pipeline.invokeHandlerAddedIfNeeded();
                this.safeSetSuccess(promise);
                AbstractChannel.this.pipeline.fireChannelRegistered();
                if (AbstractChannel.this.isActive()) {
                    if (firstRegistration) {
                        AbstractChannel.this.pipeline.fireChannelActive();
                    } else if (AbstractChannel.this.config().isAutoRead()) {
                        this.beginRead();
                    }
                }
            }
            catch (Throwable t2) {
                this.closeForcibly();
                AbstractChannel.this.closeFuture.setClosed();
                this.safeSetFailure(promise, t2);
            }
        }

        @Override
        public final void bind(SocketAddress localAddress, ChannelPromise promise) {
            this.assertEventLoop();
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            if (Boolean.TRUE.equals(AbstractChannel.this.config().getOption(ChannelOption.SO_BROADCAST)) && localAddress instanceof InetSocketAddress && !((InetSocketAddress)localAddress).getAddress().isAnyLocalAddress() && !PlatformDependent.isWindows() && !PlatformDependent.maybeSuperUser()) {
                logger.warn("A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; binding to a non-wildcard address (" + localAddress + ") anyway as requested.");
            }
            boolean wasActive = AbstractChannel.this.isActive();
            try {
                AbstractChannel.this.doBind(localAddress);
            }
            catch (Throwable t2) {
                this.safeSetFailure(promise, t2);
                this.closeIfClosed();
                return;
            }
            if (!wasActive && AbstractChannel.this.isActive()) {
                this.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        AbstractChannel.this.pipeline.fireChannelActive();
                    }
                });
            }
            this.safeSetSuccess(promise);
        }

        @Override
        public final void disconnect(ChannelPromise promise) {
            this.assertEventLoop();
            if (!promise.setUncancellable()) {
                return;
            }
            boolean wasActive = AbstractChannel.this.isActive();
            try {
                AbstractChannel.this.doDisconnect();
            }
            catch (Throwable t2) {
                this.safeSetFailure(promise, t2);
                this.closeIfClosed();
                return;
            }
            if (wasActive && !AbstractChannel.this.isActive()) {
                this.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        AbstractChannel.this.pipeline.fireChannelInactive();
                    }
                });
            }
            this.safeSetSuccess(promise);
            this.closeIfClosed();
        }

        @Override
        public final void close(ChannelPromise promise) {
            this.assertEventLoop();
            this.close(promise, CLOSE_CLOSED_CHANNEL_EXCEPTION, CLOSE_CLOSED_CHANNEL_EXCEPTION, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void close(final ChannelPromise promise, final Throwable cause, final ClosedChannelException closeCause, final boolean notify) {
            if (!promise.setUncancellable()) {
                return;
            }
            final ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
            if (outboundBuffer == null) {
                if (!(promise instanceof VoidChannelPromise)) {
                    AbstractChannel.this.closeFuture.addListener((GenericFutureListener)new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            promise.setSuccess();
                        }
                    });
                }
                return;
            }
            if (AbstractChannel.this.closeFuture.isDone()) {
                this.safeSetSuccess(promise);
                return;
            }
            final boolean wasActive = AbstractChannel.this.isActive();
            this.outboundBuffer = null;
            Executor closeExecutor = this.prepareToClose();
            if (closeExecutor != null) {
                closeExecutor.execute(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            AbstractUnsafe.this.doClose0(promise);
                        }
                        catch (Throwable throwable) {
                            AbstractUnsafe.this.invokeLater(new Runnable(){

                                @Override
                                public void run() {
                                    outboundBuffer.failFlushed(cause, notify);
                                    outboundBuffer.close(closeCause);
                                    AbstractUnsafe.this.fireChannelInactiveAndDeregister(wasActive);
                                }
                            });
                            throw throwable;
                        }
                        AbstractUnsafe.this.invokeLater(new /* invalid duplicate definition of identical inner class */);
                    }
                });
            } else {
                try {
                    this.doClose0(promise);
                }
                finally {
                    outboundBuffer.failFlushed(cause, notify);
                    outboundBuffer.close(closeCause);
                }
                if (this.inFlush0) {
                    this.invokeLater(new Runnable(){

                        @Override
                        public void run() {
                            AbstractUnsafe.this.fireChannelInactiveAndDeregister(wasActive);
                        }
                    });
                } else {
                    this.fireChannelInactiveAndDeregister(wasActive);
                }
            }
        }

        private void doClose0(ChannelPromise promise) {
            try {
                AbstractChannel.this.doClose();
                AbstractChannel.this.closeFuture.setClosed();
                this.safeSetSuccess(promise);
            }
            catch (Throwable t2) {
                AbstractChannel.this.closeFuture.setClosed();
                this.safeSetFailure(promise, t2);
            }
        }

        private void fireChannelInactiveAndDeregister(boolean wasActive) {
            this.deregister(this.voidPromise(), wasActive && !AbstractChannel.this.isActive());
        }

        @Override
        public final void closeForcibly() {
            this.assertEventLoop();
            try {
                AbstractChannel.this.doClose();
            }
            catch (Exception e2) {
                logger.warn("Failed to close a channel.", e2);
            }
        }

        @Override
        public final void deregister(ChannelPromise promise) {
            this.assertEventLoop();
            this.deregister(promise, false);
        }

        private void deregister(final ChannelPromise promise, final boolean fireChannelInactive) {
            if (!promise.setUncancellable()) {
                return;
            }
            if (!AbstractChannel.this.registered) {
                this.safeSetSuccess(promise);
                return;
            }
            this.invokeLater(new Runnable(){

                @Override
                public void run() {
                    try {
                        AbstractChannel.this.doDeregister();
                    }
                    catch (Throwable t2) {
                        logger.warn("Unexpected exception occurred while deregistering a channel.", t2);
                    }
                    finally {
                        if (fireChannelInactive) {
                            AbstractChannel.this.pipeline.fireChannelInactive();
                        }
                        if (AbstractChannel.this.registered) {
                            AbstractChannel.this.registered = false;
                            AbstractChannel.this.pipeline.fireChannelUnregistered();
                        }
                        AbstractUnsafe.this.safeSetSuccess(promise);
                    }
                }
            });
        }

        @Override
        public final void beginRead() {
            this.assertEventLoop();
            if (!AbstractChannel.this.isActive()) {
                return;
            }
            try {
                AbstractChannel.this.doBeginRead();
            }
            catch (Exception e2) {
                this.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        AbstractChannel.this.pipeline.fireExceptionCaught(e2);
                    }
                });
                this.close(this.voidPromise());
            }
        }

        @Override
        public final void write(Object msg, ChannelPromise promise) {
            int size;
            this.assertEventLoop();
            ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
            if (outboundBuffer == null) {
                this.safeSetFailure(promise, WRITE_CLOSED_CHANNEL_EXCEPTION);
                ReferenceCountUtil.release(msg);
                return;
            }
            try {
                msg = AbstractChannel.this.filterOutboundMessage(msg);
                size = AbstractChannel.this.pipeline.estimatorHandle().size(msg);
                if (size < 0) {
                    size = 0;
                }
            }
            catch (Throwable t2) {
                this.safeSetFailure(promise, t2);
                ReferenceCountUtil.release(msg);
                return;
            }
            outboundBuffer.addMessage(msg, size, promise);
        }

        @Override
        public final void flush() {
            this.assertEventLoop();
            ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
            if (outboundBuffer == null) {
                return;
            }
            outboundBuffer.addFlush();
            this.flush0();
        }

        protected void flush0() {
            if (this.inFlush0) {
                return;
            }
            ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
            if (outboundBuffer == null || outboundBuffer.isEmpty()) {
                return;
            }
            this.inFlush0 = true;
            if (!AbstractChannel.this.isActive()) {
                try {
                    if (AbstractChannel.this.isOpen()) {
                        outboundBuffer.failFlushed(FLUSH0_NOT_YET_CONNECTED_EXCEPTION, true);
                    } else {
                        outboundBuffer.failFlushed(FLUSH0_CLOSED_CHANNEL_EXCEPTION, false);
                    }
                }
                finally {
                    this.inFlush0 = false;
                }
                return;
            }
            try {
                AbstractChannel.this.doWrite(outboundBuffer);
            }
            catch (Throwable t2) {
                if (t2 instanceof IOException && AbstractChannel.this.config().isAutoClose()) {
                    this.close(this.voidPromise(), t2, FLUSH0_CLOSED_CHANNEL_EXCEPTION, false);
                } else {
                    outboundBuffer.failFlushed(t2, true);
                }
            }
            finally {
                this.inFlush0 = false;
            }
        }

        @Override
        public final ChannelPromise voidPromise() {
            this.assertEventLoop();
            return AbstractChannel.this.unsafeVoidPromise;
        }

        @Deprecated
        protected final boolean ensureOpen(ChannelPromise promise) {
            if (AbstractChannel.this.isOpen()) {
                return true;
            }
            this.safeSetFailure(promise, ENSURE_OPEN_CLOSED_CHANNEL_EXCEPTION);
            return false;
        }

        protected final void safeSetSuccess(ChannelPromise promise) {
            if (!(promise instanceof VoidChannelPromise) && !promise.trySuccess()) {
                logger.warn("Failed to mark a promise as success because it is done already: {}", (Object)promise);
            }
        }

        protected final void safeSetFailure(ChannelPromise promise, Throwable cause) {
            if (!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
                logger.warn("Failed to mark a promise as failure because it's done already: {}", (Object)promise, (Object)cause);
            }
        }

        protected final void closeIfClosed() {
            if (AbstractChannel.this.isOpen()) {
                return;
            }
            this.close(this.voidPromise());
        }

        private void invokeLater(Runnable task) {
            try {
                AbstractChannel.this.eventLoop().execute(task);
            }
            catch (RejectedExecutionException e2) {
                logger.warn("Can't invoke task later as EventLoop rejected it", e2);
            }
        }

        protected final Throwable annotateConnectException(Throwable cause, SocketAddress remoteAddress) {
            if (cause instanceof ConnectException) {
                return new AnnotatedConnectException((ConnectException)cause, remoteAddress);
            }
            if (cause instanceof NoRouteToHostException) {
                return new AnnotatedNoRouteToHostException((NoRouteToHostException)cause, remoteAddress);
            }
            if (cause instanceof SocketException) {
                return new AnnotatedSocketException((SocketException)cause, remoteAddress);
            }
            return cause;
        }

        protected Executor prepareToClose() {
            return null;
        }
    }
}

