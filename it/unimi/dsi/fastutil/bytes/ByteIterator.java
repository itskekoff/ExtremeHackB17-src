package it.unimi.dsi.fastutil.bytes;

import java.util.Iterator;

public interface ByteIterator
extends Iterator<Byte> {
    public byte nextByte();

    public int skip(int var1);
}

