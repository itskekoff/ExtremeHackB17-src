package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.PromiseTask;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

final class ScheduledFutureTask<V>
extends PromiseTask<V>
implements ScheduledFuture<V> {
    private static final AtomicLong nextTaskId = new AtomicLong();
    private static final long START_TIME = System.nanoTime();
    private final long id = nextTaskId.getAndIncrement();
    private long deadlineNanos;
    private final long periodNanos;

    static long nanoTime() {
        return System.nanoTime() - START_TIME;
    }

    static long deadlineNanos(long delay) {
        return ScheduledFutureTask.nanoTime() + delay;
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Runnable runnable, V result, long nanoTime) {
        this(executor, ScheduledFutureTask.toCallable(runnable, result), nanoTime);
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Callable<V> callable, long nanoTime, long period) {
        super(executor, callable);
        if (period == 0L) {
            throw new IllegalArgumentException("period: 0 (expected: != 0)");
        }
        this.deadlineNanos = nanoTime;
        this.periodNanos = period;
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Callable<V> callable, long nanoTime) {
        super(executor, callable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = 0L;
    }

    @Override
    protected EventExecutor executor() {
        return super.executor();
    }

    public long deadlineNanos() {
        return this.deadlineNanos;
    }

    public long delayNanos() {
        return Math.max(0L, this.deadlineNanos() - ScheduledFutureTask.nanoTime());
    }

    public long delayNanos(long currentTimeNanos) {
        return Math.max(0L, this.deadlineNanos() - (currentTimeNanos - START_TIME));
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.delayNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o2) {
        if (this == o2) {
            return 0;
        }
        ScheduledFutureTask that = (ScheduledFutureTask)o2;
        long d2 = this.deadlineNanos() - that.deadlineNanos();
        if (d2 < 0L) {
            return -1;
        }
        if (d2 > 0L) {
            return 1;
        }
        if (this.id < that.id) {
            return -1;
        }
        if (this.id == that.id) {
            throw new Error();
        }
        return 1;
    }

    @Override
    public void run() {
        assert (this.executor().inEventLoop());
        try {
            if (this.periodNanos == 0L) {
                if (this.setUncancellableInternal()) {
                    Object result = this.task.call();
                    this.setSuccessInternal(result);
                }
            } else if (!this.isCancelled()) {
                this.task.call();
                if (!this.executor().isShutdown()) {
                    long p2 = this.periodNanos;
                    this.deadlineNanos = p2 > 0L ? (this.deadlineNanos += p2) : ScheduledFutureTask.nanoTime() - p2;
                    if (!this.isCancelled()) {
                        Queue<ScheduledFutureTask<?>> scheduledTaskQueue = ((AbstractScheduledEventExecutor)this.executor()).scheduledTaskQueue;
                        assert (scheduledTaskQueue != null);
                        scheduledTaskQueue.add(this);
                    }
                }
            }
        }
        catch (Throwable cause) {
            this.setFailureInternal(cause);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean canceled = super.cancel(mayInterruptIfRunning);
        if (canceled) {
            ((AbstractScheduledEventExecutor)this.executor()).removeScheduled(this);
        }
        return canceled;
    }

    boolean cancelWithoutRemove(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf2 = super.toStringBuilder();
        buf2.setCharAt(buf2.length() - 1, ',');
        return buf2.append(" id: ").append(this.id).append(", deadline: ").append(this.deadlineNanos).append(", period: ").append(this.periodNanos).append(')');
    }
}

