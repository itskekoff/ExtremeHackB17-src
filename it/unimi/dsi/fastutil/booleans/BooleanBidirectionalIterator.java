package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface BooleanBidirectionalIterator
extends BooleanIterator,
ObjectBidirectionalIterator<Boolean> {
    public boolean previousBoolean();

    @Override
    public int back(int var1);
}

