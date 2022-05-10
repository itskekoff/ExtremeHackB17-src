package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class MessageToByteEncoder<I>
extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;
    private final boolean preferDirect;

    protected MessageToByteEncoder() {
        this(true);
    }

    protected MessageToByteEncoder(Class<? extends I> outboundMessageType) {
        this(outboundMessageType, true);
    }

    protected MessageToByteEncoder(boolean preferDirect) {
        this.matcher = TypeParameterMatcher.find(this, MessageToByteEncoder.class, "I");
        this.preferDirect = preferDirect;
    }

    protected MessageToByteEncoder(Class<? extends I> outboundMessageType, boolean preferDirect) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
        this.preferDirect = preferDirect;
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        block14: {
            ReferenceCounted buf2 = null;
            try {
                if (this.acceptOutboundMessage(msg)) {
                    Object cast = msg;
                    buf2 = this.allocateBuffer(ctx, cast, this.preferDirect);
                    try {
                        this.encode(ctx, cast, (ByteBuf)buf2);
                    }
                    finally {
                        ReferenceCountUtil.release(cast);
                    }
                    if (((ByteBuf)buf2).isReadable()) {
                        ctx.write(buf2, promise);
                    } else {
                        buf2.release();
                        ctx.write(Unpooled.EMPTY_BUFFER, promise);
                    }
                    buf2 = null;
                    break block14;
                }
                ctx.write(msg, promise);
            }
            catch (EncoderException e2) {
                throw e2;
            }
            catch (Throwable e3) {
                throw new EncoderException(e3);
            }
            finally {
                if (buf2 != null) {
                    buf2.release();
                }
            }
        }
    }

    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, I msg, boolean preferDirect) throws Exception {
        if (preferDirect) {
            return ctx.alloc().ioBuffer();
        }
        return ctx.alloc().heapBuffer();
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, ByteBuf var3) throws Exception;

    protected boolean isPreferDirect() {
        return this.preferDirect;
    }
}

