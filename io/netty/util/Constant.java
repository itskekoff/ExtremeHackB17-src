package io.netty.util;

public interface Constant<T extends Constant<T>>
extends Comparable<T> {
    public int id();

    public String name();
}

