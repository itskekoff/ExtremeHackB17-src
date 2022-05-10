package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I>
extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;

    protected MessageToMessageEncoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
    }

    protected MessageToMessageEncoder(Class<? extends I> outboundMessageType) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        block18: {
            out = null;
            try {
                if (this.acceptOutboundMessage(msg)) {
                    out = CodecOutputList.newInstance();
                    cast = msg;
                    try {
                        this.encode(ctx, cast, out);
                    }
                    finally {
                        ReferenceCountUtil.release(cast);
                    }
                    if (out.isEmpty()) {
                        out.recycle();
                        out = null;
                        throw new EncoderException(StringUtil.simpleClassName(this) + " must produce at least one message.");
                    }
                } else {
                    ctx.write(msg, promise);
                }
                if (out == null) return;
            }
            catch (EncoderException e2) {
                try {
                    throw e2;
                    catch (Throwable t2) {
                        throw new EncoderException(t2);
                    }
                }
                catch (Throwable throwable) {
                    block20: {
                        block21: {
                            block19: {
                                if (out == null) throw throwable;
                                sizeMinusOne = out.size() - 1;
                                if (sizeMinusOne != 0) break block19;
                                ctx.write(out.get(0), promise);
                                break block20;
                            }
                            if (sizeMinusOne <= 0) break block20;
                            voidPromise = ctx.voidPromise();
                            isVoidPromise = promise == voidPromise;
                            break block21;
lbl49:
                            // 2 sources

                            for (i3 = 0; i3 < sizeMinusOne; ++i3) {
                                if (isVoidPromise) {
                                    p3 = voidPromise;
                                } else {
                                    p = ctx.newPromise();
                                }
                                ctx.write(out.getUnsafe(i3), p);
                            }
                            ctx.write(out.getUnsafe(sizeMinusOne), promise);
lbl59:
                            // 3 sources

                            out.recycle();
                            return;
                        }
                        for (i2 = 0; i2 < sizeMinusOne; ++i2) {
                            if (isVoidPromise) {
                                p2 = voidPromise;
                            } else {
                                p = ctx.newPromise();
                            }
                            ctx.write(out.getUnsafe(i2), p);
                        }
                        ctx.write(out.getUnsafe(sizeMinusOne), promise);
                    }
                    out.recycle();
                    throw throwable;
                }
            }
            sizeMinusOne = out.size() - 1;
            if (sizeMinusOne != 0) break block18;
            ctx.write(out.get(0), promise);
            ** GOTO lbl59
        }
        if (sizeMinusOne <= 0) ** GOTO lbl59
        voidPromise = ctx.voidPromise();
        isVoidPromise = promise == voidPromise;
        ** GOTO lbl49
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}

