package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.AbstractStack;
import it.unimi.dsi.fastutil.chars.CharStack;

public abstract class AbstractCharStack
extends AbstractStack<Character>
implements CharStack {
    protected AbstractCharStack() {
    }

    @Override
    public void push(Character o2) {
        this.push(o2.charValue());
    }

    @Override
    public Character pop() {
        return Character.valueOf(this.popChar());
    }

    @Override
    public Character top() {
        return Character.valueOf(this.topChar());
    }

    @Override
    public Character peek(int i2) {
        return Character.valueOf(this.peekChar(i2));
    }

    @Override
    public void push(char k2) {
        this.push(Character.valueOf(k2));
    }

    @Override
    public char popChar() {
        return this.pop().charValue();
    }

    @Override
    public char topChar() {
        return this.top().charValue();
    }

    @Override
    public char peekChar(int i2) {
        return this.peek(i2).charValue();
    }
}

