package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.AbstractReferenceCounted;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class InternalAttribute
extends AbstractReferenceCounted
implements InterfaceHttpData {
    private final List<ByteBuf> value = new ArrayList<ByteBuf>();
    private final Charset charset;
    private int size;

    InternalAttribute(Charset charset) {
        this.charset = charset;
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.InternalAttribute;
    }

    public void addValue(String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        ByteBuf buf2 = Unpooled.copiedBuffer(value, this.charset);
        this.value.add(buf2);
        this.size += buf2.readableBytes();
    }

    public void addValue(String value, int rank) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        ByteBuf buf2 = Unpooled.copiedBuffer(value, this.charset);
        this.value.add(rank, buf2);
        this.size += buf2.readableBytes();
    }

    public void setValue(String value, int rank) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        ByteBuf buf2 = Unpooled.copiedBuffer(value, this.charset);
        ByteBuf old = this.value.set(rank, buf2);
        if (old != null) {
            this.size -= old.readableBytes();
            old.release();
        }
        this.size += buf2.readableBytes();
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof InternalAttribute)) {
            return false;
        }
        InternalAttribute attribute = (InternalAttribute)o2;
        return this.getName().equalsIgnoreCase(attribute.getName());
    }

    @Override
    public int compareTo(InterfaceHttpData o2) {
        if (!(o2 instanceof InternalAttribute)) {
            throw new ClassCastException("Cannot compare " + (Object)((Object)this.getHttpDataType()) + " with " + (Object)((Object)o2.getHttpDataType()));
        }
        return this.compareTo((InternalAttribute)o2);
    }

    @Override
    public int compareTo(InternalAttribute o2) {
        return this.getName().compareToIgnoreCase(o2.getName());
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ByteBuf elt : this.value) {
            result.append(elt.toString(this.charset));
        }
        return result.toString();
    }

    public int size() {
        return this.size;
    }

    public ByteBuf toByteBuf() {
        return Unpooled.compositeBuffer().addComponents(this.value).writerIndex(this.size()).readerIndex(0);
    }

    @Override
    public String getName() {
        return "InternalAttribute";
    }

    @Override
    protected void deallocate() {
    }

    @Override
    public InterfaceHttpData retain() {
        for (ByteBuf buf2 : this.value) {
            buf2.retain();
        }
        return this;
    }

    @Override
    public InterfaceHttpData retain(int increment) {
        for (ByteBuf buf2 : this.value) {
            buf2.retain(increment);
        }
        return this;
    }

    @Override
    public InterfaceHttpData touch() {
        for (ByteBuf buf2 : this.value) {
            buf2.touch();
        }
        return this;
    }

    @Override
    public InterfaceHttpData touch(Object hint) {
        for (ByteBuf buf2 : this.value) {
            buf2.touch(hint);
        }
        return this;
    }
}

