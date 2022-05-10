package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import java.io.Serializable;

public abstract class AbstractByte2ObjectFunction<V>
implements Byte2ObjectFunction<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected V defRetValue;

    protected AbstractByte2ObjectFunction() {
    }

    @Override
    public void defaultReturnValue(V rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public V defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public V put(byte key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object ok2) {
        if (ok2 == null) {
            return false;
        }
        return this.containsKey((Byte)ok2);
    }

    @Override
    public V get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? (V)this.get(k2) : null;
    }

    @Override
    @Deprecated
    public V put(Byte ok2, V ov) {
        byte k2 = ok2;
        boolean containsKey = this.containsKey(k2);
        V v2 = this.put(k2, ov);
        return (V)(containsKey ? v2 : null);
    }

    @Override
    public V remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        V v2 = this.remove(k2);
        return (V)(containsKey ? v2 : null);
    }
}

