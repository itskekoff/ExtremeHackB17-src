package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import java.io.Serializable;

public abstract class AbstractChar2ShortFunction
implements Char2ShortFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected short defRetValue;

    protected AbstractChar2ShortFunction() {
    }

    @Override
    public void defaultReturnValue(short rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public short defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public short put(char key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(char key) {
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
    public Short get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? Short.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Short put(Character ok2, Short ov) {
        char k2 = ok2.charValue();
        boolean containsKey = this.containsKey(k2);
        short v2 = this.put(k2, (short)ov);
        return containsKey ? Short.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    public Short remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        short v2 = this.remove(k2);
        return containsKey ? Short.valueOf(v2) : null;
    }
}

