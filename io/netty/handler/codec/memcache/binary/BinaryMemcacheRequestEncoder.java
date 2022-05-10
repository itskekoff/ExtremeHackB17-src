package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheEncoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequest;

public class BinaryMemcacheRequestEncoder
extends AbstractBinaryMemcacheEncoder<BinaryMemcacheRequest> {
    @Override
    protected void encodeHeader(ByteBuf buf2, BinaryMemcacheRequest msg) {
        buf2.writeByte(msg.magic());
        buf2.writeByte(msg.opcode());
        buf2.writeShort(msg.keyLength());
        buf2.writeByte(msg.extrasLength());
        buf2.writeByte(msg.dataType());
        buf2.writeShort(msg.reserved());
        buf2.writeInt(msg.totalBodyLength());
        buf2.writeInt(msg.opaque());
        buf2.writeLong(msg.cas());
    }
}

