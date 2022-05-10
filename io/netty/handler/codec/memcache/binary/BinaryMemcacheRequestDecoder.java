package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheDecoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequest;
import io.netty.handler.codec.memcache.binary.DefaultBinaryMemcacheRequest;

public class BinaryMemcacheRequestDecoder
extends AbstractBinaryMemcacheDecoder<BinaryMemcacheRequest> {
    public BinaryMemcacheRequestDecoder() {
        this(8192);
    }

    public BinaryMemcacheRequestDecoder(int chunkSize) {
        super(chunkSize);
    }

    @Override
    protected BinaryMemcacheRequest decodeHeader(ByteBuf in2) {
        DefaultBinaryMemcacheRequest header = new DefaultBinaryMemcacheRequest();
        header.setMagic(in2.readByte());
        header.setOpcode(in2.readByte());
        header.setKeyLength(in2.readShort());
        header.setExtrasLength(in2.readByte());
        header.setDataType(in2.readByte());
        header.setReserved(in2.readShort());
        header.setTotalBodyLength(in2.readInt());
        header.setOpaque(in2.readInt());
        header.setCas(in2.readLong());
        return header;
    }

    @Override
    protected BinaryMemcacheRequest buildInvalidMessage() {
        return new DefaultBinaryMemcacheRequest(Unpooled.EMPTY_BUFFER, Unpooled.EMPTY_BUFFER);
    }
}

