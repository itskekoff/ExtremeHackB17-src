package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import java.io.Serializable;

public abstract class AbstractChar2ObjectFunction<V>
implements Char2ObjectFunction<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected V defRetValue;

    protected AbstractChar2ObjectFunction() {
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
    public V put(char key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(char key) {
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
        return this.containsKey(((Character)ok2).charValue());
    }

    @Override
    public V get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? (V)this.get(k2) : null;
    }

    @Override
    @Deprecated
    public V put(Character ok2, V ov) {
        char k2 = ok2.charValue();
        boolean containsKey = this.containsKey(k2);
        V v2 = this.put(k2, ov);
        return (V)(containsKey ? v2 : null);
    }

    @Override
    public V remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        V v2 = this.remove(k2);
        return (V)(containsKey ? v2 : null);
    }
}

