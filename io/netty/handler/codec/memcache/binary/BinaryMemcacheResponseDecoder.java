package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheDecoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponse;
import io.netty.handler.codec.memcache.binary.DefaultBinaryMemcacheResponse;

public class BinaryMemcacheResponseDecoder
extends AbstractBinaryMemcacheDecoder<BinaryMemcacheResponse> {
    public BinaryMemcacheResponseDecoder() {
        this(8192);
    }

    public BinaryMemcacheResponseDecoder(int chunkSize) {
        super(chunkSize);
    }

    @Override
    protected BinaryMemcacheResponse decodeHeader(ByteBuf in2) {
        DefaultBinaryMemcacheResponse header = new DefaultBinaryMemcacheResponse();
        header.setMagic(in2.readByte());
        header.setOpcode(in2.readByte());
        header.setKeyLength(in2.readShort());
        header.setExtrasLength(in2.readByte());
        header.setDataType(in2.readByte());
        header.setStatus(in2.readShort());
        header.setTotalBodyLength(in2.readInt());
        header.setOpaque(in2.readInt());
        header.setCas(in2.readLong());
        return header;
    }

    @Override
    protected BinaryMemcacheResponse buildInvalidMessage() {
        return new DefaultBinaryMemcacheResponse(Unpooled.EMPTY_BUFFER, Unpooled.EMPTY_BUFFER);
    }
}

