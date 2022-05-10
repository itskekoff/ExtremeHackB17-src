package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import java.io.Serializable;

public abstract class AbstractByte2CharFunction
implements Byte2CharFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected char defRetValue;

    protected AbstractByte2CharFunction() {
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
    public char put(byte key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(byte key) {
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
    @Deprecated
    public Character get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? Character.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Character put(Byte ok2, Character ov) {
        byte k2 = ok2;
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
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        char v2 = this.remove(k2);
        return containsKey ? Character.valueOf(v2) : null;
    }
}

