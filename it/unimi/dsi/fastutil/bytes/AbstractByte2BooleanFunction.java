package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import java.io.Serializable;

public abstract class AbstractByte2BooleanFunction
implements Byte2BooleanFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected boolean defRetValue;

    protected AbstractByte2BooleanFunction() {
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
    public boolean put(byte key, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(byte key) {
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
    public Boolean get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? Boolean.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Boolean put(Byte ok2, Boolean ov) {
        byte k2 = ok2;
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
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        boolean v2 = this.remove(k2);
        return containsKey ? Boolean.valueOf(v2) : null;
    }
}

