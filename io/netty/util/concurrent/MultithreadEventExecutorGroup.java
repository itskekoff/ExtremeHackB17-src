package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultEventExecutorChooserFactory;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultithreadEventExecutorGroup
extends AbstractEventExecutorGroup {
    private final EventExecutor[] children;
    private final Set<EventExecutor> readonlyChildren;
    private final AtomicInteger terminatedChildren = new AtomicInteger();
    private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
    private final EventExecutorChooserFactory.EventExecutorChooser chooser;

    protected MultithreadEventExecutorGroup(int nThreads, ThreadFactory threadFactory, Object ... args) {
        this(nThreads, (Executor)(threadFactory == null ? null : new ThreadPerTaskExecutor(threadFactory)), args);
    }

    protected MultithreadEventExecutorGroup(int nThreads, Executor executor, Object ... args) {
        this(nThreads, executor, DefaultEventExecutorChooserFactory.INSTANCE, args);
    }

    protected MultithreadEventExecutorGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, Object ... args) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", nThreads));
        }
        if (executor == null) {
            executor = new ThreadPerTaskExecutor(this.newDefaultThreadFactory());
        }
        this.children = new EventExecutor[nThreads];
        for (int i2 = 0; i2 < nThreads; ++i2) {
            boolean success = false;
            try {
                this.children[i2] = this.newChild(executor, args);
                success = true;
                continue;
            }
            catch (Exception e2) {
                throw new IllegalStateException("failed to create a child event loop", e2);
            }
            finally {
                if (!success) {
                    int j2;
                    for (j2 = 0; j2 < i2; ++j2) {
                        this.children[j2].shutdownGracefully();
                    }
                    for (j2 = 0; j2 < i2; ++j2) {
                        EventExecutor e3 = this.children[j2];
                        try {
                            while (!e3.isTerminated()) {
                                e3.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            }
                            continue;
                        }
                        catch (InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }
        this.chooser = chooserFactory.newChooser(this.children);
        FutureListener<Object> terminationListener = new FutureListener<Object>(){

            @Override
            public void operationComplete(Future<Object> future) throws Exception {
                if (MultithreadEventExecutorGroup.this.terminatedChildren.incrementAndGet() == MultithreadEventExecutorGroup.this.children.length) {
                    MultithreadEventExecutorGroup.this.terminationFuture.setSuccess(null);
                }
            }
        };
        for (EventExecutor e4 : this.children) {
            e4.terminationFuture().addListener(terminationListener);
        }
        LinkedHashSet childrenSet = new LinkedHashSet(this.children.length);
        Collections.addAll(childrenSet, this.children);
        this.readonlyChildren = Collections.unmodifiableSet(childrenSet);
    }

    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(this.getClass());
    }

    @Override
    public EventExecutor next() {
        return this.chooser.next();
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return this.readonlyChildren.iterator();
    }

    public final int executorCount() {
        return this.children.length;
    }

    protected abstract EventExecutor newChild(Executor var1, Object ... var2) throws Exception;

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        for (EventExecutor l2 : this.children) {
            l2.shutdownGracefully(quietPeriod, timeout, unit);
        }
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Override
    @Deprecated
    public void shutdown() {
        for (EventExecutor l2 : this.children) {
            l2.shutdown();
        }
    }

    @Override
    public boolean isShuttingDown() {
        for (EventExecutor l2 : this.children) {
            if (l2.isShuttingDown()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isShutdown() {
        for (EventExecutor l2 : this.children) {
            if (l2.isShutdown()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isTerminated() {
        for (EventExecutor l2 : this.children) {
            if (l2.isTerminated()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        block0: for (EventExecutor l2 : this.children) {
            long timeLeft;
            while ((timeLeft = deadline - System.nanoTime()) > 0L) {
                if (!l2.awaitTermination(timeLeft, TimeUnit.NANOSECONDS)) continue;
                continue block0;
            }
            break block0;
        }
        return this.isTerminated();
    }
}

