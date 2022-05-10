package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import java.util.ListIterator;

public interface ByteListIterator
extends ListIterator<Byte>,
ByteBidirectionalIterator {
    @Override
    public void set(byte var1);

    @Override
    public void add(byte var1);
}

