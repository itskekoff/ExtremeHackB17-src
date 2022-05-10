package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import java.io.Serializable;

public abstract class AbstractChar2ByteFunction
implements Char2ByteFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected byte defRetValue;

    protected AbstractChar2ByteFunction() {
    }

    @Override
    public void defaultReturnValue(byte rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public byte defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public byte put(char key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(char key) {
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
    public Byte get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? Byte.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Byte put(Character ok2, Byte ov) {
        char k2 = ok2.charValue();
        boolean containsKey = this.containsKey(k2);
        byte v2 = this.put(k2, (byte)ov);
        return containsKey ? Byte.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    public Byte remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        byte v2 = this.remove(k2);
        return containsKey ? Byte.valueOf(v2) : null;
    }
}

