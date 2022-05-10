package org.apache.http.io;

public interface BufferInfo {
    public int length();

    public int capacity();

    public int available();
}

