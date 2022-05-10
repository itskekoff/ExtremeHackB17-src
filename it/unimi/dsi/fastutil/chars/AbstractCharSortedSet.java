package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;

public abstract class AbstractCharSortedSet
extends AbstractCharSet
implements CharSortedSet {
    protected AbstractCharSortedSet() {
    }

    @Override
    @Deprecated
    public CharSortedSet headSet(Character to2) {
        return this.headSet(to2.charValue());
    }

    @Override
    @Deprecated
    public CharSortedSet tailSet(Character from) {
        return this.tailSet(from.charValue());
    }

    @Override
    @Deprecated
    public CharSortedSet subSet(Character from, Character to2) {
        return this.subSet(from.charValue(), to2.charValue());
    }

    @Override
    @Deprecated
    public Character first() {
        return Character.valueOf(this.firstChar());
    }

    @Override
    @Deprecated
    public Character last() {
        return Character.valueOf(this.lastChar());
    }

    @Override
    @Deprecated
    public CharBidirectionalIterator charIterator() {
        return this.iterator();
    }

    @Override
    public abstract CharBidirectionalIterator iterator();
}

