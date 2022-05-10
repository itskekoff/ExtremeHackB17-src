package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import java.io.Serializable;

public abstract class AbstractChar2LongFunction
implements Char2LongFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected long defRetValue;

    protected AbstractChar2LongFunction() {
    }

    @Override
    public void defaultReturnValue(long rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public long defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public long put(char key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(char key) {
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
    @Deprecated
    public Long get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? Long.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Long put(Character ok2, Long ov) {
        char k2 = ok2.charValue();
        boolean containsKey = this.containsKey(k2);
        long v2 = this.put(k2, (long)ov);
        return containsKey ? Long.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    public Long remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        long v2 = this.remove(k2);
        return containsKey ? Long.valueOf(v2) : null;
    }
}

