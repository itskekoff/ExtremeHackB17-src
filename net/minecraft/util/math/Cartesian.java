package net.minecraft.util.math;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public class Cartesian {
    public static <T> Iterable<T[]> cartesianProduct(Class<T> clazz, Iterable<? extends Iterable<? extends T>> sets) {
        return new Product(clazz, Cartesian.toArray(Iterable.class, sets));
    }

    public static <T> Iterable<List<T>> cartesianProduct(Iterable<? extends Iterable<? extends T>> sets) {
        return Cartesian.arraysAsLists(Cartesian.cartesianProduct(Object.class, sets));
    }

    private static <T> Iterable<List<T>> arraysAsLists(Iterable<Object[]> arrays) {
        return Iterables.transform(arrays, new GetList());
    }

    private static <T> T[] toArray(Class<? super T> clazz, Iterable<? extends T> it2) {
        ArrayList<T> list = Lists.newArrayList();
        for (T t2 : it2) {
            list.add(t2);
        }
        return list.toArray(Cartesian.createArray(clazz, list.size()));
    }

    private static <T> T[] createArray(Class<? super T> elementType, int length) {
        return (Object[])Array.newInstance(elementType, length);
    }

    static class GetList<T>
    implements Function<Object[], List<T>> {
        private GetList() {
        }

        @Override
        public List<T> apply(@Nullable Object[] p_apply_1_) {
            return Arrays.asList(p_apply_1_);
        }
    }

    static class Product<T>
    implements Iterable<T[]> {
        private final Class<T> clazz;
        private final Iterable<? extends T>[] iterables;

        private Product(Class<T> clazz, Iterable<? extends T>[] iterables) {
            this.clazz = clazz;
            this.iterables = iterables;
        }

        @Override
        public Iterator<T[]> iterator() {
            return this.iterables.length <= 0 ? Collections.singletonList(Cartesian.createArray(this.clazz, 0)).iterator() : new ProductIterator(this.clazz, this.iterables);
        }

        static class ProductIterator<T>
        extends UnmodifiableIterator<T[]> {
            private int index = -2;
            private final Iterable<? extends T>[] iterables;
            private final Iterator<? extends T>[] iterators;
            private final T[] results;

            private ProductIterator(Class<T> clazz, Iterable<? extends T>[] iterables) {
                this.iterables = iterables;
                this.iterators = (Iterator[])Cartesian.createArray(Iterator.class, this.iterables.length);
                for (int i2 = 0; i2 < this.iterables.length; ++i2) {
                    this.iterators[i2] = iterables[i2].iterator();
                }
                this.results = Cartesian.createArray(clazz, this.iterators.length);
            }

            private void endOfData() {
                this.index = -1;
                Arrays.fill(this.iterators, null);
                Arrays.fill(this.results, null);
            }

            @Override
            public boolean hasNext() {
                if (this.index == -2) {
                    this.index = 0;
                    Iterator<? extends T>[] arriterator = this.iterators;
                    int n2 = this.iterators.length;
                    for (int i2 = 0; i2 < n2; ++i2) {
                        Iterator<T> iterator1 = arriterator[i2];
                        if (iterator1.hasNext()) continue;
                        this.endOfData();
                        break;
                    }
                    return true;
                }
                if (this.index >= this.iterators.length) {
                    this.index = this.iterators.length - 1;
                    while (this.index >= 0) {
                        Iterator<T> iterator = this.iterators[this.index];
                        if (iterator.hasNext()) break;
                        if (this.index == 0) {
                            this.endOfData();
                            break;
                        }
                        iterator = this.iterables[this.index].iterator();
                        this.iterators[this.index] = iterator;
                        if (!iterator.hasNext()) {
                            this.endOfData();
                            break;
                        }
                        --this.index;
                    }
                }
                return this.index >= 0;
            }

            /*
             * Unable to fully structure code
             * Enabled aggressive block sorting
             * Lifted jumps to return sites
             */
            @Override
            public T[] next() {
                if (this.hasNext()) ** GOTO lbl5
                throw new NoSuchElementException();
lbl-1000:
                // 1 sources

                {
                    this.results[this.index] = this.iterators[this.index].next();
                    ++this.index;
lbl5:
                    // 2 sources

                    ** while (this.index < this.iterators.length)
                }
lbl6:
                // 1 sources

                return (Object[])this.results.clone();
            }
        }
    }
}

