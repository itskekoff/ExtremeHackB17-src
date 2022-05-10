package it.unimi.dsi.fastutil.booleans;

import java.util.Comparator;

public interface BooleanComparator
extends Comparator<Boolean> {
    @Override
    public int compare(boolean var1, boolean var2);
}

