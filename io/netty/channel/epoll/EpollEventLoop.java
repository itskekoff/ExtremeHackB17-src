package io.netty.channel.epoll;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollEventArray;
import io.netty.channel.epoll.IovArray;
import io.netty.channel.epoll.Native;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class EpollEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
    private static final AtomicIntegerFieldUpdater<EpollEventLoop> WAKEN_UP_UPDATER = AtomicIntegerFieldUpdater.newUpdater(EpollEventLoop.class, "wakenUp");
    private final FileDescriptor epollFd;
    private final FileDescriptor eventFd;
    private final IntObjectMap<AbstractEpollChannel> channels = new IntObjectHashMap<AbstractEpollChannel>(4096);
    private final boolean allowGrowing;
    private final EpollEventArray events;
    private final IovArray iovArray = new IovArray();
    private final SelectStrategy selectStrategy;
    private final IntSupplier selectNowSupplier = new IntSupplier(){

        @Override
        public int get() throws Exception {
            return Native.epollWait(EpollEventLoop.this.epollFd.intValue(), EpollEventLoop.this.events, 0);
        }
    };
    private final Callable<Integer> pendingTasksCallable = new Callable<Integer>(){

        @Override
        public Integer call() throws Exception {
            return EpollEventLoop.super.pendingTasks();
        }
    };
    private volatile int wakenUp;
    private volatile int ioRatio = 50;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    EpollEventLoop(EventLoopGroup parent, Executor executor, int maxEvents, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler) {
        super(parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "strategy");
        if (maxEvents == 0) {
            this.allowGrowing = true;
            this.events = new EpollEventArray(4096);
        } else {
            this.allowGrowing = false;
            this.events = new EpollEventArray(maxEvents);
        }
        boolean success = false;
        FileDescriptor epollFd = null;
        FileDescriptor eventFd = null;
        try {
            this.epollFd = epollFd = Native.newEpollCreate();
            this.eventFd = eventFd = Native.newEventFd();
            try {
                Native.epollCtlAdd(epollFd.intValue(), eventFd.intValue(), Native.EPOLLIN);
            }
            catch (IOException e2) {
                throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", e2);
            }
            success = true;
        }
        finally {
            if (!success) {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    }
                    catch (Exception exception) {}
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
    }

    IovArray cleanArray() {
        this.iovArray.clear();
        return this.iovArray;
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
            Native.eventFdWrite(this.eventFd.intValue(), 1L);
        }
    }

    void add(AbstractEpollChannel ch2) throws IOException {
        assert (this.inEventLoop());
        int fd2 = ch2.fd().intValue();
        Native.epollCtlAdd(this.epollFd.intValue(), fd2, ch2.flags);
        this.channels.put(fd2, ch2);
    }

    void modify(AbstractEpollChannel ch2) throws IOException {
        assert (this.inEventLoop());
        Native.epollCtlMod(this.epollFd.intValue(), ch2.fd().intValue(), ch2.flags);
    }

    void remove(AbstractEpollChannel ch2) throws IOException {
        int fd2;
        assert (this.inEventLoop());
        if (ch2.isOpen() && this.channels.remove(fd2 = ch2.fd().intValue()) != null) {
            Native.epollCtlDel(this.epollFd.intValue(), ch2.fd().intValue());
        }
    }

    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return PlatformDependent.newMpscQueue(maxPendingTasks);
    }

    @Override
    public int pendingTasks() {
        if (this.inEventLoop()) {
            return super.pendingTasks();
        }
        return (Integer)this.submit(this.pendingTasksCallable).syncUninterruptibly().getNow();
    }

    public int getIoRatio() {
        return this.ioRatio;
    }

    public void setIoRatio(int ioRatio) {
        if (ioRatio <= 0 || ioRatio > 100) {
            throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
        }
        this.ioRatio = ioRatio;
    }

    private int epollWait(boolean oldWakenUp) throws IOException {
        int selectCnt = 0;
        long currentTimeNanos = System.nanoTime();
        long selectDeadLineNanos = currentTimeNanos + this.delayNanos(currentTimeNanos);
        while (true) {
            long timeoutMillis;
            if ((timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L) <= 0L) {
                int ready;
                if (selectCnt != 0 || (ready = Native.epollWait(this.epollFd.intValue(), this.events, 0)) <= 0) break;
                return ready;
            }
            if (this.hasTasks() && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
                return Native.epollWait(this.epollFd.intValue(), this.events, 0);
            }
            int selectedKeys = Native.epollWait(this.epollFd.intValue(), this.events, (int)timeoutMillis);
            ++selectCnt;
            if (selectedKeys != 0 || oldWakenUp || this.wakenUp == 1 || this.hasTasks() || this.hasScheduledTasks()) {
                return selectedKeys;
            }
            currentTimeNanos = System.nanoTime();
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    protected void run() {
        while (true) {
            try {
                block15: while (true) {
                    strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
                    switch (strategy) {
                        case -2: {
                            continue block15;
                        }
                        case -1: {
                            strategy = this.epollWait(EpollEventLoop.WAKEN_UP_UPDATER.getAndSet(this, 0) == 1);
                            if (this.wakenUp != 1) break block15;
                            Native.eventFdWrite(this.eventFd.intValue(), 1L);
                            break block15;
                        }
                    }
                    break;
                }
                ioRatio = this.ioRatio;
                if (ioRatio == 100) {
                    try {
                        if (strategy <= 0) ** GOTO lbl33
                        this.processReady(this.events, strategy);
                    }
                    finally {
                        this.runAllTasks();
                    }
                } else {
                    ioStartTime = System.nanoTime();
                    try {
                        if (strategy > 0) {
                            this.processReady(this.events, strategy);
                        }
                    }
                    finally {
                        ioTime = System.nanoTime() - ioStartTime;
                        this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                    }
                }
                if (this.allowGrowing && strategy == this.events.length()) {
                    this.events.increase();
                }
            }
            catch (Throwable t) {
                EpollEventLoop.handleLoopException(t);
            }
            try {
                if (!this.isShuttingDown()) continue;
                this.closeAll();
                if (!this.confirmShutdown()) continue;
                return;
            }
            catch (Throwable t) {
                EpollEventLoop.handleLoopException(t);
                continue;
            }
            break;
        }
    }

    private static void handleLoopException(Throwable t2) {
        logger.warn("Unexpected exception in the selector loop.", t2);
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void closeAll() {
        try {
            Native.epollWait(this.epollFd.intValue(), this.events, 0);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        ArrayList<AbstractEpollChannel> array = new ArrayList<AbstractEpollChannel>(this.channels.size());
        for (AbstractEpollChannel channel : this.channels.values()) {
            array.add(channel);
        }
        for (AbstractEpollChannel ch2 : array) {
            ch2.unsafe().close(ch2.unsafe().voidPromise());
        }
    }

    private void processReady(EpollEventArray events, int ready) {
        for (int i2 = 0; i2 < ready; ++i2) {
            int fd2 = events.fd(i2);
            if (fd2 == this.eventFd.intValue()) {
                Native.eventFdRead(this.eventFd.intValue());
                continue;
            }
            long ev2 = events.events(i2);
            AbstractEpollChannel ch2 = this.channels.get(fd2);
            if (ch2 != null) {
                AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch2.unsafe();
                if ((ev2 & (long)(Native.EPOLLERR | Native.EPOLLOUT)) != 0L) {
                    unsafe.epollOutReady();
                }
                if ((ev2 & (long)(Native.EPOLLERR | Native.EPOLLIN)) != 0L) {
                    unsafe.epollInReady();
                }
                if ((ev2 & (long)Native.EPOLLRDHUP) == 0L) continue;
                unsafe.epollRdHupReady();
                continue;
            }
            try {
                Native.epollCtlDel(this.epollFd.intValue(), fd2);
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    protected void cleanup() {
        try {
            try {
                this.epollFd.close();
            }
            catch (IOException e2) {
                logger.warn("Failed to close the epoll fd.", e2);
            }
            try {
                this.eventFd.close();
            }
            catch (IOException e3) {
                logger.warn("Failed to close the event fd.", e3);
            }
        }
        finally {
            this.iovArray.release();
            this.events.free();
        }
    }
}

