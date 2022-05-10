package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import java.io.Serializable;

public abstract class AbstractByte2IntFunction
implements Byte2IntFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected int defRetValue;

    protected AbstractByte2IntFunction() {
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
    public int put(byte key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(byte key) {
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
    public Integer get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? Integer.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Integer put(Byte ok2, Integer ov) {
        byte k2 = ok2;
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
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        int v2 = this.remove(k2);
        return containsKey ? Integer.valueOf(v2) : null;
    }
}

