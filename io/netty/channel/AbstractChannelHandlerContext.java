package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.DefaultChannelProgressivePromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.FailedChannelFuture;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.SucceededChannelFuture;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakHint;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractChannelHandlerContext
extends DefaultAttributeMap
implements ChannelHandlerContext,
ResourceLeakHint {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);
    volatile AbstractChannelHandlerContext next;
    volatile AbstractChannelHandlerContext prev;
    private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractChannelHandlerContext.class, "handlerState");
    private static final int ADD_PENDING = 1;
    private static final int ADD_COMPLETE = 2;
    private static final int REMOVE_COMPLETE = 3;
    private static final int INIT = 0;
    private final boolean inbound;
    private final boolean outbound;
    private final DefaultChannelPipeline pipeline;
    private final String name;
    private final boolean ordered;
    final EventExecutor executor;
    private ChannelFuture succeededFuture;
    private Runnable invokeChannelReadCompleteTask;
    private Runnable invokeReadTask;
    private Runnable invokeChannelWritableStateChangedTask;
    private Runnable invokeFlushTask;
    private volatile int handlerState = 0;

    AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name, boolean inbound, boolean outbound) {
        this.name = ObjectUtil.checkNotNull(name, "name");
        this.pipeline = pipeline;
        this.executor = executor;
        this.inbound = inbound;
        this.outbound = outbound;
        this.ordered = executor == null || executor instanceof OrderedEventExecutor;
    }

    @Override
    public Channel channel() {
        return this.pipeline.channel();
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.channel().config().getAllocator();
    }

    @Override
    public EventExecutor executor() {
        if (this.executor == null) {
            return this.channel().eventLoop();
        }
        return this.executor;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        AbstractChannelHandlerContext.invokeChannelRegistered(this.findContextInbound());
        return this;
    }

    static void invokeChannelRegistered(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRegistered();
        } else {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    next.invokeChannelRegistered();
                }
            });
        }
    }

    private void invokeChannelRegistered() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelRegistered(this);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.fireChannelRegistered();
        }
    }

    @Override
    public ChannelHandlerContext fireChannelUnregistered() {
        AbstractChannelHandlerContext.invokeChannelUnregistered(this.findContextInbound());
        return this;
    }

    static void invokeChannelUnregistered(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelUnregistered();
        } else {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    next.invokeChannelUnregistered();
                }
            });
        }
    }

    private void invokeChannelUnregistered() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelUnregistered(this);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.fireChannelUnregistered();
        }
    }

    @Override
    public ChannelHandlerContext fireChannelActive() {
        AbstractChannelHandlerContext.invokeChannelActive(this.findContextInbound());
        return this;
    }

    static void invokeChannelActive(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelActive();
        } else {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    next.invokeChannelActive();
                }
            });
        }
    }

    private void invokeChannelActive() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelActive(this);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.fireChannelActive();
        }
    }

    @Override
    public ChannelHandlerContext fireChannelInactive() {
        AbstractChannelHandlerContext.invokeChannelInactive(this.findContextInbound());
        return this;
    }

    static void invokeChannelInactive(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelInactive();
        } else {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    next.invokeChannelInactive();
                }
            });
        }
    }

    private void invokeChannelInactive() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelInactive(this);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.fireChannelInactive();
        }
    }

    @Override
    public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
        AbstractChannelHandlerContext.invokeExceptionCaught(this.next, cause);
        return this;
    }

    static void invokeExceptionCaught(final AbstractChannelHandlerContext next, final Throwable cause) {
        block4: {
            ObjectUtil.checkNotNull(cause, "cause");
            EventExecutor executor = next.executor();
            if (executor.inEventLoop()) {
                next.invokeExceptionCaught(cause);
            } else {
                try {
                    executor.execute(new Runnable(){

                        @Override
                        public void run() {
                            next.invokeExceptionCaught(cause);
                        }
                    });
                }
                catch (Throwable t2) {
                    if (!logger.isWarnEnabled()) break block4;
                    logger.warn("Failed to submit an exceptionCaught() event.", t2);
                    logger.warn("The exceptionCaught() event that was failed to submit was:", cause);
                }
            }
        }
    }

    private void invokeExceptionCaught(Throwable cause) {
        if (this.invokeHandler()) {
            try {
                this.handler().exceptionCaught(this, cause);
            }
            catch (Throwable error) {
                if (logger.isDebugEnabled()) {
                    logger.debug("An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", (Object)ThrowableUtil.stackTraceToString(error), (Object)cause);
                } else if (logger.isWarnEnabled()) {
                    logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", (Object)error, (Object)cause);
                }
            }
        } else {
            this.fireExceptionCaught(cause);
        }
    }

    @Override
    public ChannelHandlerContext fireUserEventTriggered(Object event) {
        AbstractChannelHandlerContext.invokeUserEventTriggered(this.findContextInbound(), event);
        return this;
    }

    static void invokeUserEventTriggered(final AbstractChannelHandlerContext next, final Object event) {
        ObjectUtil.checkNotNull(event, "event");
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeUserEventTriggered(event);
        } else {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    next.invokeUserEventTriggered(event);
                }
            });
        }
    }

    private void invokeUserEventTriggered(Object event) {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).userEventTriggered(this, event);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.fireUserEventTriggered(event);
        }
    }

    @Override
    public ChannelHandlerContext fireChannelRead(Object msg) {
        AbstractChannelHandlerContext.invokeChannelRead(this.findContextInbound(), msg);
        return this;
    }

    static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
        final Object m2 = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRead(m2);
        } else {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    next.invokeChannelRead(m2);
                }
            });
        }
    }

    private void invokeChannelRead(Object msg) {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelRead(this, msg);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.fireChannelRead(msg);
        }
    }

    @Override
    public ChannelHandlerContext fireChannelReadComplete() {
        AbstractChannelHandlerContext.invokeChannelReadComplete(this.findContextInbound());
        return this;
    }

    static void invokeChannelReadComplete(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelReadComplete();
        } else {
            Runnable task = next.invokeChannelReadCompleteTask;
            if (task == null) {
                next.invokeChannelReadCompleteTask = task = new Runnable(){

                    @Override
                    public void run() {
                        next.invokeChannelReadComplete();
                    }
                };
            }
            executor.execute(task);
        }
    }

    private void invokeChannelReadComplete() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelReadComplete(this);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.fireChannelReadComplete();
        }
    }

    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged() {
        AbstractChannelHandlerContext.invokeChannelWritabilityChanged(this.findContextInbound());
        return this;
    }

    static void invokeChannelWritabilityChanged(final AbstractChannelHandlerContext next) {
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelWritabilityChanged();
        } else {
            Runnable task = next.invokeChannelWritableStateChangedTask;
            if (task == null) {
                next.invokeChannelWritableStateChangedTask = task = new Runnable(){

                    @Override
                    public void run() {
                        next.invokeChannelWritabilityChanged();
                    }
                };
            }
            executor.execute(task);
        }
    }

    private void invokeChannelWritabilityChanged() {
        if (this.invokeHandler()) {
            try {
                ((ChannelInboundHandler)this.handler()).channelWritabilityChanged(this);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.fireChannelWritabilityChanged();
        }
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return this.bind(localAddress, this.newPromise());
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.connect(remoteAddress, this.newPromise());
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.connect(remoteAddress, localAddress, this.newPromise());
    }

    @Override
    public ChannelFuture disconnect() {
        return this.disconnect(this.newPromise());
    }

    @Override
    public ChannelFuture close() {
        return this.close(this.newPromise());
    }

    @Override
    public ChannelFuture deregister() {
        return this.deregister(this.newPromise());
    }

    @Override
    public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
        if (localAddress == null) {
            throw new NullPointerException("localAddress");
        }
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeBind(localAddress, promise);
        } else {
            AbstractChannelHandlerContext.safeExecute(executor, new Runnable(){

                @Override
                public void run() {
                    next.invokeBind(localAddress, promise);
                }
            }, promise, null);
        }
        return promise;
    }

    private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).bind(this, localAddress, promise);
            }
            catch (Throwable t2) {
                AbstractChannelHandlerContext.notifyOutboundHandlerException(t2, promise);
            }
        } else {
            this.bind(localAddress, promise);
        }
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.connect(remoteAddress, null, promise);
    }

    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        if (remoteAddress == null) {
            throw new NullPointerException("remoteAddress");
        }
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeConnect(remoteAddress, localAddress, promise);
        } else {
            AbstractChannelHandlerContext.safeExecute(executor, new Runnable(){

                @Override
                public void run() {
                    next.invokeConnect(remoteAddress, localAddress, promise);
                }
            }, promise, null);
        }
        return promise;
    }

    private void invokeConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).connect(this, remoteAddress, localAddress, promise);
            }
            catch (Throwable t2) {
                AbstractChannelHandlerContext.notifyOutboundHandlerException(t2, promise);
            }
        } else {
            this.connect(remoteAddress, localAddress, promise);
        }
    }

    @Override
    public ChannelFuture disconnect(final ChannelPromise promise) {
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            if (!this.channel().metadata().hasDisconnect()) {
                next.invokeClose(promise);
            } else {
                next.invokeDisconnect(promise);
            }
        } else {
            AbstractChannelHandlerContext.safeExecute(executor, new Runnable(){

                @Override
                public void run() {
                    if (!AbstractChannelHandlerContext.this.channel().metadata().hasDisconnect()) {
                        next.invokeClose(promise);
                    } else {
                        next.invokeDisconnect(promise);
                    }
                }
            }, promise, null);
        }
        return promise;
    }

    private void invokeDisconnect(ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).disconnect(this, promise);
            }
            catch (Throwable t2) {
                AbstractChannelHandlerContext.notifyOutboundHandlerException(t2, promise);
            }
        } else {
            this.disconnect(promise);
        }
    }

    @Override
    public ChannelFuture close(final ChannelPromise promise) {
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeClose(promise);
        } else {
            AbstractChannelHandlerContext.safeExecute(executor, new Runnable(){

                @Override
                public void run() {
                    next.invokeClose(promise);
                }
            }, promise, null);
        }
        return promise;
    }

    private void invokeClose(ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).close(this, promise);
            }
            catch (Throwable t2) {
                AbstractChannelHandlerContext.notifyOutboundHandlerException(t2, promise);
            }
        } else {
            this.close(promise);
        }
    }

    @Override
    public ChannelFuture deregister(final ChannelPromise promise) {
        if (this.isNotValidPromise(promise, false)) {
            return promise;
        }
        final AbstractChannelHandlerContext next = this.findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeDeregister(promise);
        } else {
            AbstractChannelHandlerContext.safeExecute(executor, new Runnable(){

                @Override
                public void run() {
                    next.invokeDeregister(promise);
                }
            }, promise, null);
        }
        return promise;
    }

    private void invokeDeregister(ChannelPromise promise) {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).deregister(this, promise);
            }
            catch (Throwable t2) {
                AbstractChannelHandlerContext.notifyOutboundHandlerException(t2, promise);
            }
        } else {
            this.deregister(promise);
        }
    }

    @Override
    public ChannelHandlerContext read() {
        final AbstractChannelHandlerContext next = this.findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeRead();
        } else {
            Runnable task = next.invokeReadTask;
            if (task == null) {
                next.invokeReadTask = task = new Runnable(){

                    @Override
                    public void run() {
                        next.invokeRead();
                    }
                };
            }
            executor.execute(task);
        }
        return this;
    }

    private void invokeRead() {
        if (this.invokeHandler()) {
            try {
                ((ChannelOutboundHandler)this.handler()).read(this);
            }
            catch (Throwable t2) {
                this.notifyHandlerException(t2);
            }
        } else {
            this.read();
        }
    }

    @Override
    public ChannelFuture write(Object msg) {
        return this.write(msg, this.newPromise());
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        if (msg == null) {
            throw new NullPointerException("msg");
        }
        try {
            if (this.isNotValidPromise(promise, true)) {
                ReferenceCountUtil.release(msg);
                return promise;
            }
        }
        catch (RuntimeException e2) {
            ReferenceCountUtil.release(msg);
            throw e2;
        }
        this.write(msg, false, promise);
        return promise;
    }

    private void invokeWrite(Object msg, ChannelPromise promise) {
        if (this.invokeHandler()) {
            this.invokeWrite0(msg, promise);
        } else {
            this.write(msg, promise);
        }
    }

    private void invokeWrite0(Object msg, ChannelPromise promise) {
        try {
            ((ChannelOutboundHandler)this.handler()).write(this, msg, promise);
        }
        catch (Throwable t2) {
            AbstractChannelHandlerContext.notifyOutboundHandlerException(t2, promise);
        }
    }

    @Override
    public ChannelHandlerContext flush() {
        final AbstractChannelHandlerContext next = this.findContextOutbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeFlush();
        } else {
            Runnable task = next.invokeFlushTask;
            if (task == null) {
                next.invokeFlushTask = task = new Runnable(){

                    @Override
                    public void run() {
                        next.invokeFlush();
                    }
                };
            }
            AbstractChannelHandlerContext.safeExecute(executor, task, this.channel().voidPromise(), null);
        }
        return this;
    }

    private void invokeFlush() {
        if (this.invokeHandler()) {
            this.invokeFlush0();
        } else {
            this.flush();
        }
    }

    private void invokeFlush0() {
        try {
            ((ChannelOutboundHandler)this.handler()).flush(this);
        }
        catch (Throwable t2) {
            this.notifyHandlerException(t2);
        }
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        if (msg == null) {
            throw new NullPointerException("msg");
        }
        if (this.isNotValidPromise(promise, true)) {
            ReferenceCountUtil.release(msg);
            return promise;
        }
        this.write(msg, true, promise);
        return promise;
    }

    private void invokeWriteAndFlush(Object msg, ChannelPromise promise) {
        if (this.invokeHandler()) {
            this.invokeWrite0(msg, promise);
            this.invokeFlush0();
        } else {
            this.writeAndFlush(msg, promise);
        }
    }

    private void write(Object msg, boolean flush, ChannelPromise promise) {
        AbstractChannelHandlerContext next = this.findContextOutbound();
        Object m2 = this.pipeline.touch(msg, next);
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            if (flush) {
                next.invokeWriteAndFlush(m2, promise);
            } else {
                next.invokeWrite(m2, promise);
            }
        } else {
            AbstractWriteTask task = flush ? WriteAndFlushTask.newInstance(next, m2, promise) : WriteTask.newInstance(next, m2, promise);
            AbstractChannelHandlerContext.safeExecute(executor, task, promise, m2);
        }
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.writeAndFlush(msg, this.newPromise());
    }

    private static void notifyOutboundHandlerException(Throwable cause, ChannelPromise promise) {
        if (!(promise instanceof VoidChannelPromise)) {
            PromiseNotificationUtil.tryFailure(promise, cause, logger);
        }
    }

    private void notifyHandlerException(Throwable cause) {
        if (AbstractChannelHandlerContext.inExceptionCaught(cause)) {
            if (logger.isWarnEnabled()) {
                logger.warn("An exception was thrown by a user handler while handling an exceptionCaught event", cause);
            }
            return;
        }
        this.invokeExceptionCaught(cause);
    }

    private static boolean inExceptionCaught(Throwable cause) {
        do {
            StackTraceElement[] trace;
            if ((trace = cause.getStackTrace()) == null) continue;
            for (StackTraceElement t2 : trace) {
                if (t2 == null) break;
                if (!"exceptionCaught".equals(t2.getMethodName())) continue;
                return true;
            }
        } while ((cause = cause.getCause()) != null);
        return false;
    }

    @Override
    public ChannelPromise newPromise() {
        return new DefaultChannelPromise(this.channel(), this.executor());
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise(this.channel(), this.executor());
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        ChannelFuture succeededFuture = this.succeededFuture;
        if (succeededFuture == null) {
            this.succeededFuture = succeededFuture = new SucceededChannelFuture(this.channel(), this.executor());
        }
        return succeededFuture;
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return new FailedChannelFuture(this.channel(), this.executor(), cause);
    }

    private boolean isNotValidPromise(ChannelPromise promise, boolean allowVoidPromise) {
        if (promise == null) {
            throw new NullPointerException("promise");
        }
        if (promise.isDone()) {
            if (promise.isCancelled()) {
                return true;
            }
            throw new IllegalArgumentException("promise already done: " + promise);
        }
        if (promise.channel() != this.channel()) {
            throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", promise.channel(), this.channel()));
        }
        if (promise.getClass() == DefaultChannelPromise.class) {
            return false;
        }
        if (!allowVoidPromise && promise instanceof VoidChannelPromise) {
            throw new IllegalArgumentException(StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
        }
        if (promise instanceof AbstractChannel.CloseFuture) {
            throw new IllegalArgumentException(StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
        }
        return false;
    }

    private AbstractChannelHandlerContext findContextInbound() {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.next;
        } while (!ctx.inbound);
        return ctx;
    }

    private AbstractChannelHandlerContext findContextOutbound() {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.prev;
        } while (!ctx.outbound);
        return ctx;
    }

    @Override
    public ChannelPromise voidPromise() {
        return this.channel().voidPromise();
    }

    final void setRemoved() {
        this.handlerState = 3;
    }

    final void setAddComplete() {
        int oldState;
        while ((oldState = this.handlerState) != 3 && !HANDLER_STATE_UPDATER.compareAndSet(this, oldState, 2)) {
        }
    }

    final void setAddPending() {
        boolean updated = HANDLER_STATE_UPDATER.compareAndSet(this, 0, 1);
        assert (updated);
    }

    private boolean invokeHandler() {
        int handlerState = this.handlerState;
        return handlerState == 2 || !this.ordered && handlerState == 1;
    }

    @Override
    public boolean isRemoved() {
        return this.handlerState == 3;
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.channel().attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.channel().hasAttr(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void safeExecute(EventExecutor executor, Runnable runnable, ChannelPromise promise, Object msg) {
        try {
            executor.execute(runnable);
        }
        catch (Throwable cause) {
            try {
                promise.setFailure(cause);
            }
            finally {
                if (msg != null) {
                    ReferenceCountUtil.release(msg);
                }
            }
        }
    }

    @Override
    public String toHintString() {
        return '\'' + this.name + "' will handle the message from this point.";
    }

    public String toString() {
        return StringUtil.simpleClassName(ChannelHandlerContext.class) + '(' + this.name + ", " + this.channel() + ')';
    }

    static final class WriteAndFlushTask
    extends AbstractWriteTask {
        private static final Recycler<WriteAndFlushTask> RECYCLER = new Recycler<WriteAndFlushTask>(){

            @Override
            protected WriteAndFlushTask newObject(Recycler.Handle<WriteAndFlushTask> handle) {
                return new WriteAndFlushTask(handle);
            }
        };

        private static WriteAndFlushTask newInstance(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            WriteAndFlushTask task = RECYCLER.get();
            WriteAndFlushTask.init(task, ctx, msg, promise);
            return task;
        }

        private WriteAndFlushTask(Recycler.Handle<WriteAndFlushTask> handle) {
            super(handle);
        }

        @Override
        public void write(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            super.write(ctx, msg, promise);
            ctx.invokeFlush();
        }
    }

    static final class WriteTask
    extends AbstractWriteTask
    implements SingleThreadEventLoop.NonWakeupRunnable {
        private static final Recycler<WriteTask> RECYCLER = new Recycler<WriteTask>(){

            @Override
            protected WriteTask newObject(Recycler.Handle<WriteTask> handle) {
                return new WriteTask(handle);
            }
        };

        private static WriteTask newInstance(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            WriteTask task = RECYCLER.get();
            WriteTask.init(task, ctx, msg, promise);
            return task;
        }

        private WriteTask(Recycler.Handle<WriteTask> handle) {
            super(handle);
        }
    }

    static abstract class AbstractWriteTask
    implements Runnable {
        private static final boolean ESTIMATE_TASK_SIZE_ON_SUBMIT = SystemPropertyUtil.getBoolean("io.netty.transport.estimateSizeOnSubmit", true);
        private static final int WRITE_TASK_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.writeTaskSizeOverhead", 48);
        private final Recycler.Handle<AbstractWriteTask> handle;
        private AbstractChannelHandlerContext ctx;
        private Object msg;
        private ChannelPromise promise;
        private int size;

        private AbstractWriteTask(Recycler.Handle<? extends AbstractWriteTask> handle) {
            this.handle = handle;
        }

        protected static void init(AbstractWriteTask task, AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            task.ctx = ctx;
            task.msg = msg;
            task.promise = promise;
            if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
                ChannelOutboundBuffer buffer = ctx.channel().unsafe().outboundBuffer();
                if (buffer != null) {
                    task.size = ctx.pipeline.estimatorHandle().size(msg) + WRITE_TASK_OVERHEAD;
                    buffer.incrementPendingOutboundBytes(task.size);
                } else {
                    task.size = 0;
                }
            } else {
                task.size = 0;
            }
        }

        @Override
        public final void run() {
            try {
                ChannelOutboundBuffer buffer = this.ctx.channel().unsafe().outboundBuffer();
                if (ESTIMATE_TASK_SIZE_ON_SUBMIT && buffer != null) {
                    buffer.decrementPendingOutboundBytes(this.size);
                }
                this.write(this.ctx, this.msg, this.promise);
            }
            finally {
                this.ctx = null;
                this.msg = null;
                this.promise = null;
                this.handle.recycle(this);
            }
        }

        protected void write(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            ctx.invokeWrite(msg, promise);
        }
    }
}

