package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import java.io.Serializable;

public abstract class AbstractByte2DoubleFunction
implements Byte2DoubleFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected double defRetValue;

    protected AbstractByte2DoubleFunction() {
    }

    @Override
    public void defaultReturnValue(double rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public double defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public double put(byte key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(byte key) {
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
    public Double get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        return this.containsKey(k2) ? Double.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Double put(Byte ok2, Double ov) {
        byte k2 = ok2;
        boolean containsKey = this.containsKey(k2);
        double v2 = this.put(k2, (double)ov);
        return containsKey ? Double.valueOf(v2) : null;
    }

    @Override
    @Deprecated
    public Double remove(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = (Byte)ok2;
        boolean containsKey = this.containsKey(k2);
        double v2 = this.remove(k2);
        return containsKey ? Double.valueOf(v2) : null;
    }
}

