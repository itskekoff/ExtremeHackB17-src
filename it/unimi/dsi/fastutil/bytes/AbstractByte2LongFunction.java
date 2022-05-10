package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import java.io.Serializable;

public abstract class AbstractByte2LongFunction
implements Byte2LongFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected long defRetValue;

    protected AbstractByte2LongFunction() {
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
    public long put(byte key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(byte key) {
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
    public Long get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? Long.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Long put(Byte ok2, Long ov) {
        byte k2 = ok2;
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
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        long v2 = this.remove(k2);
        return containsKey ? Long.valueOf(v2) : null;
    }
}

