package io.netty.util.concurrent;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.StringUtil;
import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory
implements ThreadFactory {
    private static final AtomicInteger poolId = new AtomicInteger();
    private final AtomicInteger nextId = new AtomicInteger();
    private final String prefix;
    private final boolean daemon;
    private final int priority;
    protected final ThreadGroup threadGroup;

    public DefaultThreadFactory(Class<?> poolType) {
        this(poolType, false, 5);
    }

    public DefaultThreadFactory(String poolName) {
        this(poolName, false, 5);
    }

    public DefaultThreadFactory(Class<?> poolType, boolean daemon) {
        this(poolType, daemon, 5);
    }

    public DefaultThreadFactory(String poolName, boolean daemon) {
        this(poolName, daemon, 5);
    }

    public DefaultThreadFactory(Class<?> poolType, int priority) {
        this(poolType, false, priority);
    }

    public DefaultThreadFactory(String poolName, int priority) {
        this(poolName, false, priority);
    }

    public DefaultThreadFactory(Class<?> poolType, boolean daemon, int priority) {
        this(DefaultThreadFactory.toPoolName(poolType), daemon, priority);
    }

    public static String toPoolName(Class<?> poolType) {
        if (poolType == null) {
            throw new NullPointerException("poolType");
        }
        String poolName = StringUtil.simpleClassName(poolType);
        switch (poolName.length()) {
            case 0: {
                return "unknown";
            }
            case 1: {
                return poolName.toLowerCase(Locale.US);
            }
        }
        if (Character.isUpperCase(poolName.charAt(0)) && Character.isLowerCase(poolName.charAt(1))) {
            return Character.toLowerCase(poolName.charAt(0)) + poolName.substring(1);
        }
        return poolName;
    }

    public DefaultThreadFactory(String poolName, boolean daemon, int priority, ThreadGroup threadGroup) {
        if (poolName == null) {
            throw new NullPointerException("poolName");
        }
        if (priority < 1 || priority > 10) {
            throw new IllegalArgumentException("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
        }
        this.prefix = poolName + '-' + poolId.incrementAndGet() + '-';
        this.daemon = daemon;
        this.priority = priority;
        this.threadGroup = threadGroup;
    }

    public DefaultThreadFactory(String poolName, boolean daemon, int priority) {
        this(poolName, daemon, priority, System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup());
    }

    @Override
    public Thread newThread(Runnable r2) {
        Thread t2 = this.newThread(new DefaultRunnableDecorator(r2), this.prefix + this.nextId.incrementAndGet());
        try {
            if (t2.isDaemon()) {
                if (!this.daemon) {
                    t2.setDaemon(false);
                }
            } else if (this.daemon) {
                t2.setDaemon(true);
            }
            if (t2.getPriority() != this.priority) {
                t2.setPriority(this.priority);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return t2;
    }

    protected Thread newThread(Runnable r2, String name) {
        return new FastThreadLocalThread(this.threadGroup, r2, name);
    }

    private static final class DefaultRunnableDecorator
    implements Runnable {
        private final Runnable r;

        DefaultRunnableDecorator(Runnable r2) {
            this.r = r2;
        }

        @Override
        public void run() {
            try {
                this.r.run();
            }
            finally {
                FastThreadLocal.removeAll();
            }
        }
    }
}

