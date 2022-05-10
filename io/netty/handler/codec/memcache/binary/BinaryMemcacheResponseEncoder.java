package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheEncoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponse;

public class BinaryMemcacheResponseEncoder
extends AbstractBinaryMemcacheEncoder<BinaryMemcacheResponse> {
    @Override
    protected void encodeHeader(ByteBuf buf2, BinaryMemcacheResponse msg) {
        buf2.writeByte(msg.magic());
        buf2.writeByte(msg.opcode());
        buf2.writeShort(msg.keyLength());
        buf2.writeByte(msg.extrasLength());
        buf2.writeByte(msg.dataType());
        buf2.writeShort(msg.status());
        buf2.writeInt(msg.totalBodyLength());
        buf2.writeInt(msg.opaque());
        buf2.writeLong(msg.cas());
    }
}

