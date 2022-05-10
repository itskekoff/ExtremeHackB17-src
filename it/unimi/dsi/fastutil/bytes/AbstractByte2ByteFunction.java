package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2ByteFunction;
import java.io.Serializable;

public abstract class AbstractByte2ByteFunction
implements Byte2ByteFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected byte defRetValue;

    protected AbstractByte2ByteFunction() {
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
    public byte put(byte key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(byte key) {
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
    public Byte get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? Byte.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Byte put(Byte ok2, Byte ov) {
        byte k2 = ok2;
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
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        byte v2 = this.remove(k2);
        return containsKey ? Byte.valueOf(v2) : null;
    }
}

