package io.netty.util.concurrent;

public interface ThreadProperties {
    public Thread.State state();

    public int priority();

    public boolean isInterrupted();

    public boolean isDaemon();

    public String name();

    public long id();

    public StackTraceElement[] stackTrace();

    public boolean isAlive();
}

