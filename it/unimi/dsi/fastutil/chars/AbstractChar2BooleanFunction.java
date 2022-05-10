package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import java.io.Serializable;

public abstract class AbstractChar2BooleanFunction
implements Char2BooleanFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected boolean defRetValue;

    protected AbstractChar2BooleanFunction() {
    }

    @Override
    public void defaultReturnValue(boolean rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public boolean defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public boolean put(char key, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(char key) {
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
    public Boolean get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? Boolean.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Boolean put(Character ok2, Boolean ov) {
        char k2 = ok2.charValue();
        boolean containsKey = this.containsKey(k2);
        boolean v2 = this.put(k2, (boolean)ov);
        return containsKey ? Boolean.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    public Boolean remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        boolean v2 = this.remove(k2);
        return containsKey ? Boolean.valueOf(v2) : null;
    }
}

