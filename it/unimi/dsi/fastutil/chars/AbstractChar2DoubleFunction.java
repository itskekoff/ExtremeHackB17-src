package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import java.io.Serializable;

public abstract class AbstractChar2DoubleFunction
implements Char2DoubleFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected double defRetValue;

    protected AbstractChar2DoubleFunction() {
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
    public double put(char key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(char key) {
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
    public Double get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? Double.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Double put(Character ok2, Double ov) {
        char k2 = ok2.charValue();
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
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        double v2 = this.remove(k2);
        return containsKey ? Double.valueOf(v2) : null;
    }
}

