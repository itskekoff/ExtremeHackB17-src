package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import java.io.Serializable;

public abstract class AbstractChar2CharFunction
implements Char2CharFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected char defRetValue;

    protected AbstractChar2CharFunction() {
    }

    @Override
    public void defaultReturnValue(char rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public char defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public char put(char key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(char key) {
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
    public Character get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? Character.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Character put(Character ok2, Character ov) {
        char k2 = ok2.charValue();
        boolean containsKey = this.containsKey(k2);
        char v2 = this.put(k2, ov.charValue());
        return containsKey ? Character.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    public Character remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        char v2 = this.remove(k2);
        return containsKey ? Character.valueOf(v2) : null;
    }
}

