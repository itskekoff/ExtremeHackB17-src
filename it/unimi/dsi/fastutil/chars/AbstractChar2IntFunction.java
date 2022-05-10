package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import java.io.Serializable;

public abstract class AbstractChar2IntFunction
implements Char2IntFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected int defRetValue;

    protected AbstractChar2IntFunction() {
    }

    @Override
    public void defaultReturnValue(int rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public int defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public int put(char key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(char key) {
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
    public Integer get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? Integer.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Integer put(Character ok2, Integer ov) {
        char k2 = ok2.charValue();
        boolean containsKey = this.containsKey(k2);
        int v2 = this.put(k2, (int)ov);
        return containsKey ? Integer.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    public Integer remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        int v2 = this.remove(k2);
        return containsKey ? Integer.valueOf(v2) : null;
    }
}

