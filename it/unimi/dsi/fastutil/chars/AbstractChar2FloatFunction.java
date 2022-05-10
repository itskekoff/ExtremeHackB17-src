package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import java.io.Serializable;

public abstract class AbstractChar2FloatFunction
implements Char2FloatFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected float defRetValue;

    protected AbstractChar2FloatFunction() {
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
    public float put(char key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(char key) {
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
    public Float get(Object ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ((Character)ok2).charValue();
        return this.containsKey(k2) ? Float.valueOf(this.get(k2)) : null;
    }

    @Override
    @Deprecated
    public Float put(Character ok2, Float ov) {
        char k2 = ok2.charValue();
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
        char k2 = ((Character)ok2).charValue();
        boolean containsKey = this.containsKey(k2);
        float v2 = this.remove(k2);
        return containsKey ? Float.valueOf(v2) : null;
    }
}

