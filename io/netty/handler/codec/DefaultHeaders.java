package io.netty.handler.codec;

import io.netty.handler.codec.Headers;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class DefaultHeaders<K, V, T extends Headers<K, V, T>>
implements Headers<K, V, T> {
    static final int HASH_CODE_SEED = -1028477387;
    private final HeaderEntry<K, V>[] entries;
    protected final HeaderEntry<K, V> head;
    private final byte hashMask;
    private final ValueConverter<V> valueConverter;
    private final NameValidator<K> nameValidator;
    private final HashingStrategy<K> hashingStrategy;
    int size;

    public DefaultHeaders(ValueConverter<V> valueConverter) {
        this(HashingStrategy.JAVA_HASHER, valueConverter);
    }

    public DefaultHeaders(ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
        this(HashingStrategy.JAVA_HASHER, valueConverter, nameValidator);
    }

    public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter) {
        this(nameHashingStrategy, valueConverter, NameValidator.NOT_NULL);
    }

    public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
        this(nameHashingStrategy, valueConverter, nameValidator, 16);
    }

    public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator, int arraySizeHint) {
        this.valueConverter = ObjectUtil.checkNotNull(valueConverter, "valueConverter");
        this.nameValidator = ObjectUtil.checkNotNull(nameValidator, "nameValidator");
        this.hashingStrategy = ObjectUtil.checkNotNull(nameHashingStrategy, "nameHashingStrategy");
        this.entries = new HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(arraySizeHint, 128)))];
        this.hashMask = (byte)(this.entries.length - 1);
        this.head = new HeaderEntry();
    }

    @Override
    public V get(K name) {
        ObjectUtil.checkNotNull(name, "name");
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        HeaderEntry<K, V> e2 = this.entries[i2];
        V value = null;
        while (e2 != null) {
            if (e2.hash == h2 && this.hashingStrategy.equals(name, e2.key)) {
                value = e2.value;
            }
            e2 = e2.next;
        }
        return value;
    }

    @Override
    public V get(K name, V defaultValue) {
        V value = this.get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public V getAndRemove(K name) {
        int h2 = this.hashingStrategy.hashCode(name);
        return this.remove0(h2, this.index(h2), ObjectUtil.checkNotNull(name, "name"));
    }

    @Override
    public V getAndRemove(K name, V defaultValue) {
        V value = this.getAndRemove(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public List<V> getAll(K name) {
        ObjectUtil.checkNotNull(name, "name");
        LinkedList<V> values = new LinkedList<V>();
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        HeaderEntry<K, V> e2 = this.entries[i2];
        while (e2 != null) {
            if (e2.hash == h2 && this.hashingStrategy.equals(name, e2.key)) {
                values.addFirst(e2.getValue());
            }
            e2 = e2.next;
        }
        return values;
    }

    @Override
    public List<V> getAllAndRemove(K name) {
        List<V> all2 = this.getAll(name);
        this.remove(name);
        return all2;
    }

    @Override
    public boolean contains(K name) {
        return this.get(name) != null;
    }

    @Override
    public boolean containsObject(K name, Object value) {
        return this.contains(name, this.valueConverter.convertObject(ObjectUtil.checkNotNull(value, "value")));
    }

    @Override
    public boolean containsBoolean(K name, boolean value) {
        return this.contains(name, this.valueConverter.convertBoolean(value));
    }

    @Override
    public boolean containsByte(K name, byte value) {
        return this.contains(name, this.valueConverter.convertByte(value));
    }

    @Override
    public boolean containsChar(K name, char value) {
        return this.contains(name, this.valueConverter.convertChar(value));
    }

    @Override
    public boolean containsShort(K name, short value) {
        return this.contains(name, this.valueConverter.convertShort(value));
    }

    @Override
    public boolean containsInt(K name, int value) {
        return this.contains(name, this.valueConverter.convertInt(value));
    }

    @Override
    public boolean containsLong(K name, long value) {
        return this.contains(name, this.valueConverter.convertLong(value));
    }

    @Override
    public boolean containsFloat(K name, float value) {
        return this.contains(name, this.valueConverter.convertFloat(value));
    }

    @Override
    public boolean containsDouble(K name, double value) {
        return this.contains(name, this.valueConverter.convertDouble(value));
    }

    @Override
    public boolean containsTimeMillis(K name, long value) {
        return this.contains(name, this.valueConverter.convertTimeMillis(value));
    }

    @Override
    public boolean contains(K name, V value) {
        return this.contains(name, value, HashingStrategy.JAVA_HASHER);
    }

    public final boolean contains(K name, V value, HashingStrategy<? super V> valueHashingStrategy) {
        ObjectUtil.checkNotNull(name, "name");
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        HeaderEntry<K, V> e2 = this.entries[i2];
        while (e2 != null) {
            if (e2.hash == h2 && this.hashingStrategy.equals(name, e2.key) && valueHashingStrategy.equals(value, e2.value)) {
                return true;
            }
            e2 = e2.next;
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.head == this.head.after;
    }

    @Override
    public Set<K> names() {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet names = new LinkedHashSet(this.size());
        HeaderEntry e2 = this.head.after;
        while (e2 != this.head) {
            names.add(e2.getKey());
            e2 = e2.after;
        }
        return names;
    }

    @Override
    public T add(K name, V value) {
        this.nameValidator.validateName(name);
        ObjectUtil.checkNotNull(value, "value");
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        this.add0(h2, i2, name, value);
        return this.thisT();
    }

    @Override
    public T add(K name, Iterable<? extends V> values) {
        this.nameValidator.validateName(name);
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        for (V v2 : values) {
            this.add0(h2, i2, name, v2);
        }
        return this.thisT();
    }

    @Override
    public T add(K name, V ... values) {
        this.nameValidator.validateName(name);
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        for (V v2 : values) {
            this.add0(h2, i2, name, v2);
        }
        return this.thisT();
    }

    @Override
    public T addObject(K name, Object value) {
        return this.add(name, this.valueConverter.convertObject(ObjectUtil.checkNotNull(value, "value")));
    }

    @Override
    public T addObject(K name, Iterable<?> values) {
        for (Object value : values) {
            this.addObject(name, value);
        }
        return this.thisT();
    }

    @Override
    public T addObject(K name, Object ... values) {
        for (Object value : values) {
            this.addObject(name, value);
        }
        return this.thisT();
    }

    @Override
    public T addInt(K name, int value) {
        return this.add(name, this.valueConverter.convertInt(value));
    }

    @Override
    public T addLong(K name, long value) {
        return this.add(name, this.valueConverter.convertLong(value));
    }

    @Override
    public T addDouble(K name, double value) {
        return this.add(name, this.valueConverter.convertDouble(value));
    }

    @Override
    public T addTimeMillis(K name, long value) {
        return this.add(name, this.valueConverter.convertTimeMillis(value));
    }

    @Override
    public T addChar(K name, char value) {
        return this.add(name, this.valueConverter.convertChar(value));
    }

    @Override
    public T addBoolean(K name, boolean value) {
        return this.add(name, this.valueConverter.convertBoolean(value));
    }

    @Override
    public T addFloat(K name, float value) {
        return this.add(name, this.valueConverter.convertFloat(value));
    }

    @Override
    public T addByte(K name, byte value) {
        return this.add(name, this.valueConverter.convertByte(value));
    }

    @Override
    public T addShort(K name, short value) {
        return this.add(name, this.valueConverter.convertShort(value));
    }

    @Override
    public T add(Headers<? extends K, ? extends V, ?> headers) {
        if (headers == this) {
            throw new IllegalArgumentException("can't add to itself.");
        }
        this.addImpl(headers);
        return this.thisT();
    }

    protected void addImpl(Headers<? extends K, ? extends V, ?> headers) {
        if (headers instanceof DefaultHeaders) {
            DefaultHeaders defaultHeaders = (DefaultHeaders)headers;
            HeaderEntry e2 = defaultHeaders.head.after;
            if (defaultHeaders.hashingStrategy == this.hashingStrategy && defaultHeaders.nameValidator == this.nameValidator) {
                while (e2 != defaultHeaders.head) {
                    this.add0(e2.hash, this.index(e2.hash), e2.key, e2.value);
                    e2 = e2.after;
                }
            } else {
                while (e2 != defaultHeaders.head) {
                    this.add((K)e2.key, (V)e2.value);
                    e2 = e2.after;
                }
            }
        } else {
            for (Map.Entry<K, V> header : headers) {
                this.add(header.getKey(), header.getValue());
            }
        }
    }

    @Override
    public T set(K name, V value) {
        this.nameValidator.validateName(name);
        ObjectUtil.checkNotNull(value, "value");
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        this.remove0(h2, i2, name);
        this.add0(h2, i2, name, value);
        return this.thisT();
    }

    @Override
    public T set(K name, Iterable<? extends V> values) {
        this.nameValidator.validateName(name);
        ObjectUtil.checkNotNull(values, "values");
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        this.remove0(h2, i2, name);
        for (V v2 : values) {
            if (v2 == null) break;
            this.add0(h2, i2, name, v2);
        }
        return this.thisT();
    }

    @Override
    public T set(K name, V ... values) {
        this.nameValidator.validateName(name);
        ObjectUtil.checkNotNull(values, "values");
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        this.remove0(h2, i2, name);
        for (V v2 : values) {
            if (v2 == null) break;
            this.add0(h2, i2, name, v2);
        }
        return this.thisT();
    }

    @Override
    public T setObject(K name, Object value) {
        ObjectUtil.checkNotNull(value, "value");
        V convertedValue = ObjectUtil.checkNotNull(this.valueConverter.convertObject(value), "convertedValue");
        return this.set(name, convertedValue);
    }

    @Override
    public T setObject(K name, Iterable<?> values) {
        this.nameValidator.validateName(name);
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        this.remove0(h2, i2, name);
        for (Object v2 : values) {
            if (v2 == null) break;
            this.add0(h2, i2, name, this.valueConverter.convertObject(v2));
        }
        return this.thisT();
    }

    @Override
    public T setObject(K name, Object ... values) {
        this.nameValidator.validateName(name);
        int h2 = this.hashingStrategy.hashCode(name);
        int i2 = this.index(h2);
        this.remove0(h2, i2, name);
        for (Object v2 : values) {
            if (v2 == null) break;
            this.add0(h2, i2, name, this.valueConverter.convertObject(v2));
        }
        return this.thisT();
    }

    @Override
    public T setInt(K name, int value) {
        return this.set(name, this.valueConverter.convertInt(value));
    }

    @Override
    public T setLong(K name, long value) {
        return this.set(name, this.valueConverter.convertLong(value));
    }

    @Override
    public T setDouble(K name, double value) {
        return this.set(name, this.valueConverter.convertDouble(value));
    }

    @Override
    public T setTimeMillis(K name, long value) {
        return this.set(name, this.valueConverter.convertTimeMillis(value));
    }

    @Override
    public T setFloat(K name, float value) {
        return this.set(name, this.valueConverter.convertFloat(value));
    }

    @Override
    public T setChar(K name, char value) {
        return this.set(name, this.valueConverter.convertChar(value));
    }

    @Override
    public T setBoolean(K name, boolean value) {
        return this.set(name, this.valueConverter.convertBoolean(value));
    }

    @Override
    public T setByte(K name, byte value) {
        return this.set(name, this.valueConverter.convertByte(value));
    }

    @Override
    public T setShort(K name, short value) {
        return this.set(name, this.valueConverter.convertShort(value));
    }

    @Override
    public T set(Headers<? extends K, ? extends V, ?> headers) {
        if (headers != this) {
            this.clear();
            this.addImpl(headers);
        }
        return this.thisT();
    }

    @Override
    public T setAll(Headers<? extends K, ? extends V, ?> headers) {
        if (headers != this) {
            for (K key : headers.names()) {
                this.remove(key);
            }
            this.addImpl(headers);
        }
        return this.thisT();
    }

    @Override
    public boolean remove(K name) {
        return this.getAndRemove(name) != null;
    }

    @Override
    public T clear() {
        Arrays.fill(this.entries, null);
        this.head.after = this.head;
        this.head.before = this.head.after;
        this.size = 0;
        return this.thisT();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new HeaderIterator();
    }

    @Override
    public Boolean getBoolean(K name) {
        V v2 = this.get(name);
        return v2 != null ? Boolean.valueOf(this.valueConverter.convertToBoolean(v2)) : null;
    }

    @Override
    public boolean getBoolean(K name, boolean defaultValue) {
        Boolean v2 = this.getBoolean(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Byte getByte(K name) {
        V v2 = this.get(name);
        return v2 != null ? Byte.valueOf(this.valueConverter.convertToByte(v2)) : null;
    }

    @Override
    public byte getByte(K name, byte defaultValue) {
        Byte v2 = this.getByte(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Character getChar(K name) {
        V v2 = this.get(name);
        return v2 != null ? Character.valueOf(this.valueConverter.convertToChar(v2)) : null;
    }

    @Override
    public char getChar(K name, char defaultValue) {
        Character v2 = this.getChar(name);
        return v2 != null ? v2.charValue() : defaultValue;
    }

    @Override
    public Short getShort(K name) {
        V v2 = this.get(name);
        return v2 != null ? Short.valueOf(this.valueConverter.convertToShort(v2)) : null;
    }

    @Override
    public short getShort(K name, short defaultValue) {
        Short v2 = this.getShort(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Integer getInt(K name) {
        V v2 = this.get(name);
        return v2 != null ? Integer.valueOf(this.valueConverter.convertToInt(v2)) : null;
    }

    @Override
    public int getInt(K name, int defaultValue) {
        Integer v2 = this.getInt(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Long getLong(K name) {
        V v2 = this.get(name);
        return v2 != null ? Long.valueOf(this.valueConverter.convertToLong(v2)) : null;
    }

    @Override
    public long getLong(K name, long defaultValue) {
        Long v2 = this.getLong(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Float getFloat(K name) {
        V v2 = this.get(name);
        return v2 != null ? Float.valueOf(this.valueConverter.convertToFloat(v2)) : null;
    }

    @Override
    public float getFloat(K name, float defaultValue) {
        Float v2 = this.getFloat(name);
        return v2 != null ? v2.floatValue() : defaultValue;
    }

    @Override
    public Double getDouble(K name) {
        V v2 = this.get(name);
        return v2 != null ? Double.valueOf(this.valueConverter.convertToDouble(v2)) : null;
    }

    @Override
    public double getDouble(K name, double defaultValue) {
        Double v2 = this.getDouble(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Long getTimeMillis(K name) {
        V v2 = this.get(name);
        return v2 != null ? Long.valueOf(this.valueConverter.convertToTimeMillis(v2)) : null;
    }

    @Override
    public long getTimeMillis(K name, long defaultValue) {
        Long v2 = this.getTimeMillis(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Boolean getBooleanAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        return v2 != null ? Boolean.valueOf(this.valueConverter.convertToBoolean(v2)) : null;
    }

    @Override
    public boolean getBooleanAndRemove(K name, boolean defaultValue) {
        Boolean v2 = this.getBooleanAndRemove(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Byte getByteAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        return v2 != null ? Byte.valueOf(this.valueConverter.convertToByte(v2)) : null;
    }

    @Override
    public byte getByteAndRemove(K name, byte defaultValue) {
        Byte v2 = this.getByteAndRemove(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Character getCharAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        if (v2 == null) {
            return null;
        }
        try {
            return Character.valueOf(this.valueConverter.convertToChar(v2));
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public char getCharAndRemove(K name, char defaultValue) {
        Character v2 = this.getCharAndRemove(name);
        return v2 != null ? v2.charValue() : defaultValue;
    }

    @Override
    public Short getShortAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        return v2 != null ? Short.valueOf(this.valueConverter.convertToShort(v2)) : null;
    }

    @Override
    public short getShortAndRemove(K name, short defaultValue) {
        Short v2 = this.getShortAndRemove(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Integer getIntAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        return v2 != null ? Integer.valueOf(this.valueConverter.convertToInt(v2)) : null;
    }

    @Override
    public int getIntAndRemove(K name, int defaultValue) {
        Integer v2 = this.getIntAndRemove(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Long getLongAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        return v2 != null ? Long.valueOf(this.valueConverter.convertToLong(v2)) : null;
    }

    @Override
    public long getLongAndRemove(K name, long defaultValue) {
        Long v2 = this.getLongAndRemove(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Float getFloatAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        return v2 != null ? Float.valueOf(this.valueConverter.convertToFloat(v2)) : null;
    }

    @Override
    public float getFloatAndRemove(K name, float defaultValue) {
        Float v2 = this.getFloatAndRemove(name);
        return v2 != null ? v2.floatValue() : defaultValue;
    }

    @Override
    public Double getDoubleAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        return v2 != null ? Double.valueOf(this.valueConverter.convertToDouble(v2)) : null;
    }

    @Override
    public double getDoubleAndRemove(K name, double defaultValue) {
        Double v2 = this.getDoubleAndRemove(name);
        return v2 != null ? v2 : defaultValue;
    }

    @Override
    public Long getTimeMillisAndRemove(K name) {
        V v2 = this.getAndRemove(name);
        return v2 != null ? Long.valueOf(this.valueConverter.convertToTimeMillis(v2)) : null;
    }

    @Override
    public long getTimeMillisAndRemove(K name, long defaultValue) {
        Long v2 = this.getTimeMillisAndRemove(name);
        return v2 != null ? v2 : defaultValue;
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof Headers)) {
            return false;
        }
        return this.equals((Headers)o2, HashingStrategy.JAVA_HASHER);
    }

    public int hashCode() {
        return this.hashCode(HashingStrategy.JAVA_HASHER);
    }

    public final boolean equals(Headers<K, V, ?> h2, HashingStrategy<V> valueHashingStrategy) {
        if (h2.size() != this.size()) {
            return false;
        }
        if (this == h2) {
            return true;
        }
        for (K name : this.names()) {
            List<V> otherValues = h2.getAll(name);
            List<V> values = this.getAll(name);
            if (otherValues.size() != values.size()) {
                return false;
            }
            for (int i2 = 0; i2 < otherValues.size(); ++i2) {
                if (valueHashingStrategy.equals(otherValues.get(i2), values.get(i2))) continue;
                return false;
            }
        }
        return true;
    }

    public final int hashCode(HashingStrategy<V> valueHashingStrategy) {
        int result = -1028477387;
        for (K name : this.names()) {
            result = 31 * result + this.hashingStrategy.hashCode(name);
            List<V> values = this.getAll(name);
            for (int i2 = 0; i2 < values.size(); ++i2) {
                result = 31 * result + valueHashingStrategy.hashCode(values.get(i2));
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName()).append('[');
        String separator = "";
        for (K name : this.names()) {
            List<V> values = this.getAll(name);
            for (int i2 = 0; i2 < values.size(); ++i2) {
                builder.append(separator);
                builder.append(name).append(": ").append(values.get(i2));
                separator = ", ";
            }
        }
        return builder.append(']').toString();
    }

    protected HeaderEntry<K, V> newHeaderEntry(int h2, K name, V value, HeaderEntry<K, V> next) {
        return new HeaderEntry<K, V>(h2, name, value, next, this.head);
    }

    protected ValueConverter<V> valueConverter() {
        return this.valueConverter;
    }

    private int index(int hash) {
        return hash & this.hashMask;
    }

    private void add0(int h2, int i2, K name, V value) {
        this.entries[i2] = this.newHeaderEntry(h2, name, value, this.entries[i2]);
        ++this.size;
    }

    private V remove0(int h2, int i2, K name) {
        HeaderEntry<K, V> e2 = this.entries[i2];
        if (e2 == null) {
            return null;
        }
        V value = null;
        HeaderEntry next = e2.next;
        while (next != null) {
            if (next.hash == h2 && this.hashingStrategy.equals(name, next.key)) {
                value = next.value;
                e2.next = next.next;
                next.remove();
                --this.size;
            } else {
                e2 = next;
            }
            next = e2.next;
        }
        e2 = this.entries[i2];
        if (e2.hash == h2 && this.hashingStrategy.equals(name, e2.key)) {
            if (value == null) {
                value = e2.value;
            }
            this.entries[i2] = e2.next;
            e2.remove();
            --this.size;
        }
        return value;
    }

    private T thisT() {
        return (T)this;
    }

    protected static class HeaderEntry<K, V>
    implements Map.Entry<K, V> {
        protected final int hash;
        protected final K key;
        protected V value;
        protected HeaderEntry<K, V> next;
        protected HeaderEntry<K, V> before;
        protected HeaderEntry<K, V> after;

        protected HeaderEntry(int hash, K key) {
            this.hash = hash;
            this.key = key;
        }

        HeaderEntry(int hash, K key, V value, HeaderEntry<K, V> next, HeaderEntry<K, V> head) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
            this.after = head;
            this.before = head.before;
            this.pointNeighborsToThis();
        }

        HeaderEntry() {
            this.hash = -1;
            this.key = null;
            this.before = this.after = this;
        }

        protected final void pointNeighborsToThis() {
            this.before.after = this;
            this.after.before = this;
        }

        public final HeaderEntry<K, V> before() {
            return this.before;
        }

        public final HeaderEntry<K, V> after() {
            return this.after;
        }

        protected void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
        }

        @Override
        public final K getKey() {
            return this.key;
        }

        @Override
        public final V getValue() {
            return this.value;
        }

        @Override
        public final V setValue(V value) {
            ObjectUtil.checkNotNull(value, "value");
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public final String toString() {
            return this.key.toString() + '=' + this.value.toString();
        }
    }

    private final class HeaderIterator
    implements Iterator<Map.Entry<K, V>> {
        private HeaderEntry<K, V> current;

        private HeaderIterator() {
            this.current = DefaultHeaders.this.head;
        }

        @Override
        public boolean hasNext() {
            return this.current.after != DefaultHeaders.this.head;
        }

        @Override
        public Map.Entry<K, V> next() {
            this.current = this.current.after;
            if (this.current == DefaultHeaders.this.head) {
                throw new NoSuchElementException();
            }
            return this.current;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("read only");
        }
    }

    public static interface NameValidator<K> {
        public static final NameValidator NOT_NULL = new NameValidator(){

            public void validateName(Object name) {
                ObjectUtil.checkNotNull(name, "name");
            }
        };

        public void validateName(K var1);
    }
}

