package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.AbstractStack;
import it.unimi.dsi.fastutil.bytes.ByteStack;

public abstract class AbstractByteStack
extends AbstractStack<Byte>
implements ByteStack {
    protected AbstractByteStack() {
    }

    @Override
    public void push(Byte o2) {
        this.push((byte)o2);
    }

    @Override
    public Byte pop() {
        return this.popByte();
    }

    @Override
    public Byte top() {
        return this.topByte();
    }

    @Override
    public Byte peek(int i2) {
        return this.peekByte(i2);
    }

    @Override
    public void push(byte k2) {
        this.push((Byte)k2);
    }

    @Override
    public byte popByte() {
        return this.pop();
    }

    @Override
    public byte topByte() {
        return this.top();
    }

    @Override
    public byte peekByte(int i2) {
        return this.peek(i2);
    }
}

