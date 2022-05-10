package it.unimi.dsi.fastutil.booleans;

import java.util.Iterator;

public interface BooleanIterator
extends Iterator<Boolean> {
    public boolean nextBoolean();

    public int skip(int var1);
}

