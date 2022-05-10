package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.memcache.AbstractMemcacheObjectEncoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;

public abstract class AbstractBinaryMemcacheEncoder<M extends BinaryMemcacheMessage>
extends AbstractMemcacheObjectEncoder<M> {
    private static final int MINIMUM_HEADER_SIZE = 24;

    @Override
    protected ByteBuf encodeMessage(ChannelHandlerContext ctx, M msg) {
        ByteBuf buf2 = ctx.alloc().buffer(24 + msg.extrasLength() + msg.keyLength());
        this.encodeHeader(buf2, msg);
        AbstractBinaryMemcacheEncoder.encodeExtras(buf2, msg.extras());
        AbstractBinaryMemcacheEncoder.encodeKey(buf2, msg.key());
        return buf2;
    }

    private static void encodeExtras(ByteBuf buf2, ByteBuf extras) {
        if (extras == null || !extras.isReadable()) {
            return;
        }
        buf2.writeBytes(extras);
    }

    private static void encodeKey(ByteBuf buf2, ByteBuf key) {
        if (key == null || !key.isReadable()) {
            return;
        }
        buf2.writeBytes(key);
    }

    protected abstract void encodeHeader(ByteBuf var1, M var2);
}

