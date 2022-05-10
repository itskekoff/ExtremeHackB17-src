package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanIterator;

public interface BooleanIterable
extends Iterable<Boolean> {
    public BooleanIterator iterator();
}

