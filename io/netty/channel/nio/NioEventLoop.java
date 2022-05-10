package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.EventLoopException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioTask;
import io.netty.channel.nio.SelectedSelectionKeySet;
import io.netty.channel.nio.SelectedSelectionKeySetSelector;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger;
    private static final int CLEANUP_INTERVAL = 256;
    private static final boolean DISABLE_KEYSET_OPTIMIZATION;
    private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
    private final IntSupplier selectNowSupplier = new IntSupplier(){

        @Override
        public int get() throws Exception {
            return NioEventLoop.this.selectNow();
        }
    };
    private final Callable<Integer> pendingTasksCallable = new Callable<Integer>(){

        @Override
        public Integer call() throws Exception {
            return NioEventLoop.super.pendingTasks();
        }
    };
    private Selector selector;
    private Selector unwrappedSelector;
    private SelectedSelectionKeySet selectedKeys;
    private final SelectorProvider provider;
    private final AtomicBoolean wakenUp = new AtomicBoolean();
    private final SelectStrategy selectStrategy;
    private volatile int ioRatio = 50;
    private int cancelledKeys;
    private boolean needsToSelectAgain;

    NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventLoopGroup)parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
        if (selectorProvider == null) {
            throw new NullPointerException("selectorProvider");
        }
        if (strategy == null) {
            throw new NullPointerException("selectStrategy");
        }
        this.provider = selectorProvider;
        this.selector = this.openSelector();
        this.selectStrategy = strategy;
    }

    private Selector openSelector() {
        try {
            this.unwrappedSelector = this.provider.openSelector();
        }
        catch (IOException e2) {
            throw new ChannelException("failed to open a new selector", e2);
        }
        if (DISABLE_KEYSET_OPTIMIZATION) {
            return this.unwrappedSelector;
        }
        final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
        Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
                }
                catch (Throwable cause) {
                    return cause;
                }
            }
        });
        if (!(maybeSelectorImplClass instanceof Class) || !((Class)maybeSelectorImplClass).isAssignableFrom(this.unwrappedSelector.getClass())) {
            if (maybeSelectorImplClass instanceof Throwable) {
                Throwable t2 = (Throwable)maybeSelectorImplClass;
                logger.trace("failed to instrument a special java.util.Set into: {}", (Object)this.unwrappedSelector, (Object)t2);
            }
            return this.unwrappedSelector;
        }
        final Class selectorImplClass = (Class)maybeSelectorImplClass;
        Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
                    Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
                    Throwable cause = ReflectionUtil.trySetAccessible(selectedKeysField);
                    if (cause != null) {
                        return cause;
                    }
                    cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField);
                    if (cause != null) {
                        return cause;
                    }
                    selectedKeysField.set(NioEventLoop.this.unwrappedSelector, selectedKeySet);
                    publicSelectedKeysField.set(NioEventLoop.this.unwrappedSelector, selectedKeySet);
                    return null;
                }
                catch (NoSuchFieldException e2) {
                    return e2;
                }
                catch (IllegalAccessException e3) {
                    return e3;
                }
            }
        });
        if (maybeException instanceof Exception) {
            this.selectedKeys = null;
            Exception e3 = (Exception)maybeException;
            logger.trace("failed to instrument a special java.util.Set into: {}", (Object)this.unwrappedSelector, (Object)e3);
            return this.unwrappedSelector;
        }
        this.selectedKeys = selectedKeySet;
        logger.trace("instrumented a special java.util.Set into: {}", (Object)this.unwrappedSelector);
        return new SelectedSelectionKeySetSelector(this.unwrappedSelector, selectedKeySet);
    }

    public SelectorProvider selectorProvider() {
        return this.provider;
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

    public void register(SelectableChannel ch2, int interestOps, NioTask<?> task) {
        if (ch2 == null) {
            throw new NullPointerException("ch");
        }
        if (interestOps == 0) {
            throw new IllegalArgumentException("interestOps must be non-zero.");
        }
        if ((interestOps & ~ch2.validOps()) != 0) {
            throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch2.validOps() + ')');
        }
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
        }
        try {
            ch2.register(this.selector, interestOps, task);
        }
        catch (Exception e2) {
            throw new EventLoopException("failed to register a channel", e2);
        }
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

    public void rebuildSelector() {
        if (!this.inEventLoop()) {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    NioEventLoop.this.rebuildSelector0();
                }
            });
            return;
        }
        this.rebuildSelector0();
    }

    private void rebuildSelector0() {
        int nChannels;
        block10: {
            Selector newSelector;
            Selector oldSelector = this.selector;
            if (oldSelector == null) {
                return;
            }
            try {
                newSelector = this.openSelector();
            }
            catch (Exception e2) {
                logger.warn("Failed to create a new Selector.", e2);
                return;
            }
            nChannels = 0;
            for (SelectionKey key : oldSelector.keys()) {
                Object a2 = key.attachment();
                try {
                    if (!key.isValid() || key.channel().keyFor(newSelector) != null) continue;
                    int interestOps = key.interestOps();
                    key.cancel();
                    SelectionKey newKey = key.channel().register(newSelector, interestOps, a2);
                    if (a2 instanceof AbstractNioChannel) {
                        ((AbstractNioChannel)a2).selectionKey = newKey;
                    }
                    ++nChannels;
                }
                catch (Exception e3) {
                    logger.warn("Failed to re-register a Channel to the new Selector.", e3);
                    if (a2 instanceof AbstractNioChannel) {
                        AbstractNioChannel ch2 = (AbstractNioChannel)a2;
                        ch2.unsafe().close(ch2.unsafe().voidPromise());
                        continue;
                    }
                    NioTask task = (NioTask)a2;
                    NioEventLoop.invokeChannelUnregistered(task, key, e3);
                }
            }
            this.selector = newSelector;
            try {
                oldSelector.close();
            }
            catch (Throwable t2) {
                if (!logger.isWarnEnabled()) break block10;
                logger.warn("Failed to close the old Selector.", t2);
            }
        }
        logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void run() {
        while (true) {
            block17: {
                try {
                    block15: while (true) {
                        switch (this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks())) {
                            case -2: {
                                continue block15;
                            }
                            case -1: {
                                this.select(this.wakenUp.getAndSet(false));
                                if (!this.wakenUp.get()) break block15;
                                this.selector.wakeup();
                            }
                        }
                        break;
                    }
                    this.cancelledKeys = 0;
                    this.needsToSelectAgain = false;
                    int ioRatio = this.ioRatio;
                    if (ioRatio == 100) {
                        try {
                            this.processSelectedKeys();
                            break block17;
                        }
                        finally {
                            this.runAllTasks();
                        }
                    }
                    long ioStartTime = System.nanoTime();
                    try {
                        this.processSelectedKeys();
                    }
                    finally {
                        long ioTime = System.nanoTime() - ioStartTime;
                        this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                    }
                }
                catch (Throwable t2) {
                    NioEventLoop.handleLoopException(t2);
                }
            }
            try {
                if (!this.isShuttingDown()) continue;
                this.closeAll();
                if (!this.confirmShutdown()) continue;
                return;
            }
            catch (Throwable t3) {
                NioEventLoop.handleLoopException(t3);
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

    private void processSelectedKeys() {
        if (this.selectedKeys != null) {
            this.processSelectedKeysOptimized();
        } else {
            this.processSelectedKeysPlain(this.selector.selectedKeys());
        }
    }

    @Override
    protected void cleanup() {
        try {
            this.selector.close();
        }
        catch (IOException e2) {
            logger.warn("Failed to close a selector.", e2);
        }
    }

    void cancel(SelectionKey key) {
        key.cancel();
        ++this.cancelledKeys;
        if (this.cancelledKeys >= 256) {
            this.cancelledKeys = 0;
            this.needsToSelectAgain = true;
        }
    }

    @Override
    protected Runnable pollTask() {
        Runnable task = super.pollTask();
        if (this.needsToSelectAgain) {
            this.selectAgain();
        }
        return task;
    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i2 = selectedKeys.iterator();
        while (true) {
            SelectionKey k2 = i2.next();
            Object a2 = k2.attachment();
            i2.remove();
            if (a2 instanceof AbstractNioChannel) {
                this.processSelectedKey(k2, (AbstractNioChannel)a2);
            } else {
                NioTask task = (NioTask)a2;
                NioEventLoop.processSelectedKey(k2, task);
            }
            if (!i2.hasNext()) break;
            if (!this.needsToSelectAgain) continue;
            this.selectAgain();
            selectedKeys = this.selector.selectedKeys();
            if (selectedKeys.isEmpty()) break;
            i2 = selectedKeys.iterator();
        }
    }

    private void processSelectedKeysOptimized() {
        for (int i2 = 0; i2 < this.selectedKeys.size; ++i2) {
            SelectionKey k2 = this.selectedKeys.keys[i2];
            this.selectedKeys.keys[i2] = null;
            Object a2 = k2.attachment();
            if (a2 instanceof AbstractNioChannel) {
                this.processSelectedKey(k2, (AbstractNioChannel)a2);
            } else {
                NioTask task = (NioTask)a2;
                NioEventLoop.processSelectedKey(k2, task);
            }
            if (!this.needsToSelectAgain) continue;
            this.selectedKeys.reset(i2 + 1);
            this.selectAgain();
            i2 = -1;
        }
    }

    private void processSelectedKey(SelectionKey k2, AbstractNioChannel ch2) {
        AbstractNioChannel.NioUnsafe unsafe = ch2.unsafe();
        if (!k2.isValid()) {
            NioEventLoop eventLoop;
            try {
                eventLoop = ch2.eventLoop();
            }
            catch (Throwable ignored) {
                return;
            }
            if (eventLoop != this || eventLoop == null) {
                return;
            }
            unsafe.close(unsafe.voidPromise());
            return;
        }
        try {
            int readyOps = k2.readyOps();
            if ((readyOps & 8) != 0) {
                int ops = k2.interestOps();
                k2.interestOps(ops &= 0xFFFFFFF7);
                unsafe.finishConnect();
            }
            if ((readyOps & 4) != 0) {
                ch2.unsafe().forceFlush();
            }
            if ((readyOps & 0x11) != 0 || readyOps == 0) {
                unsafe.read();
            }
        }
        catch (CancelledKeyException ignored) {
            unsafe.close(unsafe.voidPromise());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void processSelectedKey(SelectionKey k2, NioTask<SelectableChannel> task) {
        int state = 0;
        try {
            task.channelReady(k2.channel(), k2);
            state = 1;
        }
        catch (Exception e2) {
            k2.cancel();
            NioEventLoop.invokeChannelUnregistered(task, k2, e2);
            state = 2;
        }
        finally {
            switch (state) {
                case 0: {
                    k2.cancel();
                    NioEventLoop.invokeChannelUnregistered(task, k2, null);
                    break;
                }
                case 1: {
                    if (k2.isValid()) break;
                    NioEventLoop.invokeChannelUnregistered(task, k2, null);
                }
            }
        }
    }

    private void closeAll() {
        this.selectAgain();
        Set<SelectionKey> keys = this.selector.keys();
        ArrayList<AbstractNioChannel> channels = new ArrayList<AbstractNioChannel>(keys.size());
        for (SelectionKey k2 : keys) {
            Object a2 = k2.attachment();
            if (a2 instanceof AbstractNioChannel) {
                channels.add((AbstractNioChannel)a2);
                continue;
            }
            k2.cancel();
            NioTask task = (NioTask)a2;
            NioEventLoop.invokeChannelUnregistered(task, k2, null);
        }
        for (AbstractNioChannel ch2 : channels) {
            ch2.unsafe().close(ch2.unsafe().voidPromise());
        }
    }

    private static void invokeChannelUnregistered(NioTask<SelectableChannel> task, SelectionKey k2, Throwable cause) {
        try {
            task.channelUnregistered(k2.channel(), cause);
        }
        catch (Exception e2) {
            logger.warn("Unexpected exception while running NioTask.channelUnregistered()", e2);
        }
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && this.wakenUp.compareAndSet(false, true)) {
            this.selector.wakeup();
        }
    }

    Selector unwrappedSelector() {
        return this.unwrappedSelector;
    }

    int selectNow() throws IOException {
        try {
            int n2 = this.selector.selectNow();
            return n2;
        }
        finally {
            if (this.wakenUp.get()) {
                this.selector.wakeup();
            }
        }
    }

    private void select(boolean oldWakenUp) throws IOException {
        block11: {
            Selector selector = this.selector;
            try {
                int selectCnt = 0;
                long currentTimeNanos = System.nanoTime();
                long selectDeadLineNanos = currentTimeNanos + this.delayNanos(currentTimeNanos);
                while (true) {
                    long timeoutMillis;
                    if ((timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L) <= 0L) {
                        if (selectCnt != 0) break;
                        selector.selectNow();
                        selectCnt = 1;
                        break;
                    }
                    if (this.hasTasks() && this.wakenUp.compareAndSet(false, true)) {
                        selector.selectNow();
                        selectCnt = 1;
                        break;
                    }
                    int selectedKeys = selector.select(timeoutMillis);
                    ++selectCnt;
                    if (selectedKeys != 0 || oldWakenUp || this.wakenUp.get() || this.hasTasks() || this.hasScheduledTasks()) break;
                    if (Thread.interrupted()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
                        }
                        selectCnt = 1;
                        break;
                    }
                    long time = System.nanoTime();
                    if (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis) >= currentTimeNanos) {
                        selectCnt = 1;
                    } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                        logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", (Object)selectCnt, (Object)selector);
                        this.rebuildSelector();
                        selector = this.selector;
                        selector.selectNow();
                        selectCnt = 1;
                        break;
                    }
                    currentTimeNanos = time;
                }
                if (selectCnt > 3 && logger.isDebugEnabled()) {
                    logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.", (Object)(selectCnt - 1), (Object)selector);
                }
            }
            catch (CancelledKeyException e2) {
                if (!logger.isDebugEnabled()) break block11;
                logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?", (Object)selector, (Object)e2);
            }
        }
    }

    private void selectAgain() {
        this.needsToSelectAgain = false;
        try {
            this.selector.selectNow();
        }
        catch (Throwable t2) {
            logger.warn("Failed to update SelectionKeys.", t2);
        }
    }

    static {
        int selectorAutoRebuildThreshold;
        logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
        DISABLE_KEYSET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
        String key = "sun.nio.ch.bugLevel";
        String buglevel = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
        if (buglevel == null) {
            try {
                AccessController.doPrivileged(new PrivilegedAction<Void>(){

                    @Override
                    public Void run() {
                        System.setProperty("sun.nio.ch.bugLevel", "");
                        return null;
                    }
                });
            }
            catch (SecurityException e2) {
                logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", e2);
            }
        }
        if ((selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512)) < 3) {
            selectorAutoRebuildThreshold = 0;
        }
        SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.noKeySetOptimization: {}", (Object)DISABLE_KEYSET_OPTIMIZATION);
            logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", (Object)SELECTOR_AUTO_REBUILD_THRESHOLD);
        }
    }
}

