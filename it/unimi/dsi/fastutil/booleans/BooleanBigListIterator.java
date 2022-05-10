package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;

public interface BooleanBigListIterator
extends BooleanBidirectionalIterator,
BigListIterator<Boolean> {
    public void set(boolean var1);

    public void add(boolean var1);

    public void set(Boolean var1);

    public void add(Boolean var1);
}

