package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteIterator;

public interface ByteIterable
extends Iterable<Byte> {
    public ByteIterator iterator();
}

