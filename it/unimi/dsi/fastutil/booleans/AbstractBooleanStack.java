package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.AbstractStack;
import it.unimi.dsi.fastutil.booleans.BooleanStack;

public abstract class AbstractBooleanStack
extends AbstractStack<Boolean>
implements BooleanStack {
    protected AbstractBooleanStack() {
    }

    @Override
    public void push(Boolean o2) {
        this.push((boolean)o2);
    }

    @Override
    public Boolean pop() {
        return this.popBoolean();
    }

    @Override
    public Boolean top() {
        return this.topBoolean();
    }

    @Override
    public Boolean peek(int i2) {
        return this.peekBoolean(i2);
    }

    @Override
    public void push(boolean k2) {
        this.push((Boolean)k2);
    }

    @Override
    public boolean popBoolean() {
        return this.pop();
    }

    @Override
    public boolean topBoolean() {
        return this.top();
    }

    @Override
    public boolean peekBoolean(int i2) {
        return this.peek(i2);
    }
}

