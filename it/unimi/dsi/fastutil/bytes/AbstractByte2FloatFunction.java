package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import java.io.Serializable;

public abstract class AbstractByte2FloatFunction
implements Byte2FloatFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected float defRetValue;

    protected AbstractByte2FloatFunction() {
    }

    @Override
    public void defaultReturnValue(float rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public float defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public float put(byte key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(byte key) {
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
    public Float get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? Float.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Float put(Byte ok2, Float ov) {
        byte k2 = ok2;
        boolean containsKey = this.containsKey(k2);
        float v2 = this.put(k2, ov.floatValue());
        return containsKey ? Float.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    public Float remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        float v2 = this.remove(k2);
        return containsKey ? Float.valueOf(v2) : null;
    }
}

