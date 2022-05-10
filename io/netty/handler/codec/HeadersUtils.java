package io.netty.handler.codec;

import io.netty.handler.codec.Headers;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class HeadersUtils {
    private HeadersUtils() {
    }

    public static <K, V> List<String> getAllAsString(Headers<K, V, ?> headers, K name) {
        final List<V> allNames = headers.getAll(name);
        return new AbstractList<String>(){

            @Override
            public String get(int index) {
                Object value = allNames.get(index);
                return value != null ? value.toString() : null;
            }

            @Override
            public int size() {
                return allNames.size();
            }
        };
    }

    public static <K, V> String getAsString(Headers<K, V, ?> headers, K name) {
        V orig = headers.get(name);
        return orig != null ? orig.toString() : null;
    }

    public static Iterator<Map.Entry<String, String>> iteratorAsString(Iterable<Map.Entry<CharSequence, CharSequence>> headers) {
        return new StringEntryIterator(headers.iterator());
    }

    public static Set<String> namesAsString(Headers<CharSequence, CharSequence, ?> headers) {
        return new CharSequenceDelegatingStringSet(headers.names());
    }

    private static abstract class DelegatingStringSet<T>
    implements Set<String> {
        protected final Set<T> allNames;

        public DelegatingStringSet(Set<T> allNames) {
            this.allNames = ObjectUtil.checkNotNull(allNames, "allNames");
        }

        @Override
        public int size() {
            return this.allNames.size();
        }

        @Override
        public boolean isEmpty() {
            return this.allNames.isEmpty();
        }

        @Override
        public boolean contains(Object o2) {
            return this.allNames.contains(o2.toString());
        }

        @Override
        public Iterator<String> iterator() {
            return new StringIterator<T>(this.allNames.iterator());
        }

        @Override
        public Object[] toArray() {
            Object[] arr2 = new Object[this.size()];
            this.fillArray(arr2);
            return arr2;
        }

        @Override
        public <X> X[] toArray(X[] a2) {
            if (a2 == null || a2.length < this.size()) {
                Object[] arr2 = new Object[this.size()];
                this.fillArray(arr2);
                return arr2;
            }
            this.fillArray(a2);
            return a2;
        }

        private void fillArray(Object[] arr2) {
            Iterator<T> itr = this.allNames.iterator();
            for (int i2 = 0; i2 < this.size(); ++i2) {
                arr2[i2] = itr.next();
            }
        }

        @Override
        public boolean remove(Object o2) {
            return this.allNames.remove(o2);
        }

        @Override
        public boolean containsAll(Collection<?> c2) {
            for (Object o2 : c2) {
                if (this.contains(o2)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c2) {
            boolean modified = false;
            for (Object o2 : c2) {
                if (!this.remove(o2)) continue;
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(Collection<?> c2) {
            boolean modified = false;
            Iterator<String> it2 = this.iterator();
            while (it2.hasNext()) {
                if (c2.contains(it2.next())) continue;
                it2.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public void clear() {
            this.allNames.clear();
        }
    }

    private static final class CharSequenceDelegatingStringSet
    extends DelegatingStringSet<CharSequence> {
        public CharSequenceDelegatingStringSet(Set<CharSequence> allNames) {
            super(allNames);
        }

        @Override
        public boolean add(String e2) {
            return this.allNames.add(e2);
        }

        @Override
        public boolean addAll(Collection<? extends String> c2) {
            return this.allNames.addAll(c2);
        }
    }

    private static final class StringIterator<T>
    implements Iterator<String> {
        private final Iterator<T> iter;

        public StringIterator(Iterator<T> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public String next() {
            T next = this.iter.next();
            return next != null ? next.toString() : null;
        }

        @Override
        public void remove() {
            this.iter.remove();
        }
    }

    private static final class StringEntry
    implements Map.Entry<String, String> {
        private final Map.Entry<CharSequence, CharSequence> entry;
        private String name;
        private String value;

        StringEntry(Map.Entry<CharSequence, CharSequence> entry) {
            this.entry = entry;
        }

        @Override
        public String getKey() {
            if (this.name == null) {
                this.name = this.entry.getKey().toString();
            }
            return this.name;
        }

        @Override
        public String getValue() {
            if (this.value == null && this.entry.getValue() != null) {
                this.value = this.entry.getValue().toString();
            }
            return this.value;
        }

        @Override
        public String setValue(String value) {
            String old = this.getValue();
            this.entry.setValue(value);
            return old;
        }

        public String toString() {
            return this.entry.toString();
        }
    }

    private static final class StringEntryIterator
    implements Iterator<Map.Entry<String, String>> {
        private final Iterator<Map.Entry<CharSequence, CharSequence>> iter;

        public StringEntryIterator(Iterator<Map.Entry<CharSequence, CharSequence>> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Map.Entry<String, String> next() {
            return new StringEntry(this.iter.next());
        }

        @Override
        public void remove() {
            this.iter.remove();
        }
    }
}

