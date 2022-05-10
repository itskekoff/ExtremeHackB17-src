package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import java.io.Serializable;

public abstract class AbstractByte2ShortFunction
implements Byte2ShortFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected short defRetValue;

    protected AbstractByte2ShortFunction() {
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
    public short put(byte key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(byte key) {
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
    public Short get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? Short.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Short put(Byte ok2, Short ov) {
        byte k2 = ok2;
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
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        short v2 = this.remove(k2);
        return containsKey ? Short.valueOf(v2) : null;
    }
}

