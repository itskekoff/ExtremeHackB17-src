package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

@GwtIncompatible
public abstract class ForwardingNavigableSet<E>
extends ForwardingSortedSet<E>
implements NavigableSet<E> {
    protected ForwardingNavigableSet() {
    }

    @Override
    protected abstract NavigableSet<E> delegate();

    @Override
    public E lower(E e2) {
        return this.delegate().lower(e2);
    }

    protected E standardLower(E e2) {
        return Iterators.getNext(this.headSet(e2, false).descendingIterator(), null);
    }

    @Override
    public E floor(E e2) {
        return this.delegate().floor(e2);
    }

    protected E standardFloor(E e2) {
        return Iterators.getNext(this.headSet(e2, true).descendingIterator(), null);
    }

    @Override
    public E ceiling(E e2) {
        return this.delegate().ceiling(e2);
    }

    protected E standardCeiling(E e2) {
        return Iterators.getNext(this.tailSet(e2, true).iterator(), null);
    }

    @Override
    public E higher(E e2) {
        return this.delegate().higher(e2);
    }

    protected E standardHigher(E e2) {
        return Iterators.getNext(this.tailSet(e2, false).iterator(), null);
    }

    @Override
    public E pollFirst() {
        return this.delegate().pollFirst();
    }

    protected E standardPollFirst() {
        return Iterators.pollNext(this.iterator());
    }

    @Override
    public E pollLast() {
        return this.delegate().pollLast();
    }

    protected E standardPollLast() {
        return Iterators.pollNext(this.descendingIterator());
    }

    protected E standardFirst() {
        return this.iterator().next();
    }

    protected E standardLast() {
        return this.descendingIterator().next();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return this.delegate().descendingSet();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return this.delegate().descendingIterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return this.delegate().subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Beta
    protected NavigableSet<E> standardSubSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return this.tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
    }

    @Override
    protected SortedSet<E> standardSubSet(E fromElement, E toElement) {
        return this.subSet(fromElement, true, toElement, false);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return this.delegate().headSet(toElement, inclusive);
    }

    protected SortedSet<E> standardHeadSet(E toElement) {
        return this.headSet(toElement, false);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return this.delegate().tailSet(fromElement, inclusive);
    }

    protected SortedSet<E> standardTailSet(E fromElement) {
        return this.tailSet(fromElement, true);
    }

    @Beta
    protected class StandardDescendingSet
    extends Sets.DescendingSet<E> {
        public StandardDescendingSet() {
            super(ForwardingNavigableSet.this);
        }
    }
}

