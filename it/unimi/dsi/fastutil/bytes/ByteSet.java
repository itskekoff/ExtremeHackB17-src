package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Set;

public interface ByteSet
extends ByteCollection,
Set<Byte> {
    @Override
    public ByteIterator iterator();

    public boolean remove(byte var1);
}

