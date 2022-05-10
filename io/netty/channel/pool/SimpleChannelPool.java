package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import java.util.Deque;

public class SimpleChannelPool
implements ChannelPool {
    private static final AttributeKey<SimpleChannelPool> POOL_KEY = AttributeKey.newInstance("channelPool");
    private static final IllegalStateException FULL_EXCEPTION = ThrowableUtil.unknownStackTrace(new IllegalStateException("ChannelPool full"), SimpleChannelPool.class, "releaseAndOffer(...)");
    private static final IllegalStateException UNHEALTHY_NON_OFFERED_TO_POOL = ThrowableUtil.unknownStackTrace(new IllegalStateException("Channel is unhealthy not offering it back to pool"), SimpleChannelPool.class, "releaseAndOffer(...)");
    private final Deque<Channel> deque = PlatformDependent.newConcurrentDeque();
    private final ChannelPoolHandler handler;
    private final ChannelHealthChecker healthCheck;
    private final Bootstrap bootstrap;
    private final boolean releaseHealthCheck;

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
        this(bootstrap, handler, ChannelHealthChecker.ACTIVE);
    }

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck) {
        this(bootstrap, handler, healthCheck, true);
    }

    public SimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck) {
        this.handler = ObjectUtil.checkNotNull(handler, "handler");
        this.healthCheck = ObjectUtil.checkNotNull(healthCheck, "healthCheck");
        this.releaseHealthCheck = releaseHealthCheck;
        this.bootstrap = ObjectUtil.checkNotNull(bootstrap, "bootstrap").clone();
        this.bootstrap.handler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel ch2) throws Exception {
                assert (ch2.eventLoop().inEventLoop());
                handler.channelCreated(ch2);
            }
        });
    }

    protected Bootstrap bootstrap() {
        return this.bootstrap;
    }

    protected ChannelPoolHandler handler() {
        return this.handler;
    }

    protected ChannelHealthChecker healthChecker() {
        return this.healthCheck;
    }

    protected boolean releaseHealthCheck() {
        return this.releaseHealthCheck;
    }

    @Override
    public final Future<Channel> acquire() {
        return this.acquire(this.bootstrap.config().group().next().newPromise());
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        return this.acquireHealthyFromPoolOrNew(promise);
    }

    private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> promise) {
        try {
            final Channel ch2 = this.pollChannel();
            if (ch2 == null) {
                Bootstrap bs2 = this.bootstrap.clone();
                bs2.attr(POOL_KEY, this);
                ChannelFuture f2 = this.connectChannel(bs2);
                if (f2.isDone()) {
                    this.notifyConnect(f2, promise);
                } else {
                    f2.addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            SimpleChannelPool.this.notifyConnect(future, promise);
                        }
                    });
                }
                return promise;
            }
            EventLoop loop = ch2.eventLoop();
            if (loop.inEventLoop()) {
                this.doHealthCheck(ch2, promise);
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        SimpleChannelPool.this.doHealthCheck(ch2, promise);
                    }
                });
            }
        }
        catch (Throwable cause) {
            promise.tryFailure(cause);
        }
        return promise;
    }

    private void notifyConnect(ChannelFuture future, Promise<Channel> promise) {
        if (future.isSuccess()) {
            Channel channel = future.channel();
            if (!promise.trySuccess(channel)) {
                this.release(channel);
            }
        } else {
            promise.tryFailure(future.cause());
        }
    }

    private void doHealthCheck(final Channel ch2, final Promise<Channel> promise) {
        assert (ch2.eventLoop().inEventLoop());
        Future<Boolean> f2 = this.healthCheck.isHealthy(ch2);
        if (f2.isDone()) {
            this.notifyHealthCheck(f2, ch2, promise);
        } else {
            f2.addListener((GenericFutureListener<Future<Boolean>>)new FutureListener<Boolean>(){

                @Override
                public void operationComplete(Future<Boolean> future) throws Exception {
                    SimpleChannelPool.this.notifyHealthCheck(future, ch2, promise);
                }
            });
        }
    }

    private void notifyHealthCheck(Future<Boolean> future, Channel ch2, Promise<Channel> promise) {
        assert (ch2.eventLoop().inEventLoop());
        if (future.isSuccess()) {
            if (future.getNow().booleanValue()) {
                try {
                    ch2.attr(POOL_KEY).set(this);
                    this.handler.channelAcquired(ch2);
                    promise.setSuccess(ch2);
                }
                catch (Throwable cause) {
                    SimpleChannelPool.closeAndFail(ch2, cause, promise);
                }
            } else {
                SimpleChannelPool.closeChannel(ch2);
                this.acquireHealthyFromPoolOrNew(promise);
            }
        } else {
            SimpleChannelPool.closeChannel(ch2);
            this.acquireHealthyFromPoolOrNew(promise);
        }
    }

    protected ChannelFuture connectChannel(Bootstrap bs2) {
        return bs2.connect();
    }

    @Override
    public final Future<Void> release(Channel channel) {
        return this.release(channel, channel.eventLoop().newPromise());
    }

    @Override
    public Future<Void> release(final Channel channel, final Promise<Void> promise) {
        ObjectUtil.checkNotNull(channel, "channel");
        ObjectUtil.checkNotNull(promise, "promise");
        try {
            EventLoop loop = channel.eventLoop();
            if (loop.inEventLoop()) {
                this.doReleaseChannel(channel, promise);
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        SimpleChannelPool.this.doReleaseChannel(channel, promise);
                    }
                });
            }
        }
        catch (Throwable cause) {
            SimpleChannelPool.closeAndFail(channel, cause, promise);
        }
        return promise;
    }

    private void doReleaseChannel(Channel channel, Promise<Void> promise) {
        assert (channel.eventLoop().inEventLoop());
        if (channel.attr(POOL_KEY).getAndSet(null) != this) {
            SimpleChannelPool.closeAndFail(channel, new IllegalArgumentException("Channel " + channel + " was not acquired from this ChannelPool"), promise);
        } else {
            try {
                if (this.releaseHealthCheck) {
                    this.doHealthCheckOnRelease(channel, promise);
                } else {
                    this.releaseAndOffer(channel, promise);
                }
            }
            catch (Throwable cause) {
                SimpleChannelPool.closeAndFail(channel, cause, promise);
            }
        }
    }

    private void doHealthCheckOnRelease(final Channel channel, final Promise<Void> promise) throws Exception {
        final Future<Boolean> f2 = this.healthCheck.isHealthy(channel);
        if (f2.isDone()) {
            this.releaseAndOfferIfHealthy(channel, promise, f2);
        } else {
            f2.addListener((GenericFutureListener<Future<Boolean>>)new FutureListener<Boolean>(){

                @Override
                public void operationComplete(Future<Boolean> future) throws Exception {
                    SimpleChannelPool.this.releaseAndOfferIfHealthy(channel, promise, f2);
                }
            });
        }
    }

    private void releaseAndOfferIfHealthy(Channel channel, Promise<Void> promise, Future<Boolean> future) throws Exception {
        if (future.getNow().booleanValue()) {
            this.releaseAndOffer(channel, promise);
        } else {
            this.handler.channelReleased(channel);
            SimpleChannelPool.closeAndFail(channel, UNHEALTHY_NON_OFFERED_TO_POOL, promise);
        }
    }

    private void releaseAndOffer(Channel channel, Promise<Void> promise) throws Exception {
        if (this.offerChannel(channel)) {
            this.handler.channelReleased(channel);
            promise.setSuccess(null);
        } else {
            SimpleChannelPool.closeAndFail(channel, FULL_EXCEPTION, promise);
        }
    }

    private static void closeChannel(Channel channel) {
        channel.attr(POOL_KEY).getAndSet(null);
        channel.close();
    }

    private static void closeAndFail(Channel channel, Throwable cause, Promise<?> promise) {
        SimpleChannelPool.closeChannel(channel);
        promise.tryFailure(cause);
    }

    protected Channel pollChannel() {
        return this.deque.pollLast();
    }

    protected boolean offerChannel(Channel channel) {
        return this.deque.offer(channel);
    }

    @Override
    public void close() {
        Channel channel;
        while ((channel = this.pollChannel()) != null) {
            channel.close();
        }
    }
}

