package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import java.util.List;

public class Socks5InitialRequestDecoder
extends ReplayingDecoder<State> {
    public Socks5InitialRequestDecoder() {
        super(State.INIT);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        try {
            switch ((State)((Object)this.state())) {
                case INIT: {
                    byte version = in2.readByte();
                    if (version != SocksVersion.SOCKS5.byteValue()) {
                        throw new DecoderException("unsupported version: " + version + " (expected: " + SocksVersion.SOCKS5.byteValue() + ')');
                    }
                    int authMethodCnt = in2.readUnsignedByte();
                    if (this.actualReadableBytes() < authMethodCnt) break;
                    Socks5AuthMethod[] authMethods = new Socks5AuthMethod[authMethodCnt];
                    for (int i2 = 0; i2 < authMethodCnt; ++i2) {
                        authMethods[i2] = Socks5AuthMethod.valueOf(in2.readByte());
                    }
                    out.add(new DefaultSocks5InitialRequest(authMethods));
                    this.checkpoint(State.SUCCESS);
                }
                case SUCCESS: {
                    int readableBytes = this.actualReadableBytes();
                    if (readableBytes <= 0) break;
                    out.add(in2.readRetainedSlice(readableBytes));
                    break;
                }
                case FAILURE: {
                    in2.skipBytes(this.actualReadableBytes());
                }
            }
        }
        catch (Exception e2) {
            this.fail(out, e2);
        }
    }

    private void fail(List<Object> out, Throwable cause) {
        if (!(cause instanceof DecoderException)) {
            cause = new DecoderException(cause);
        }
        this.checkpoint(State.FAILURE);
        DefaultSocks5InitialRequest m2 = new DefaultSocks5InitialRequest(Socks5AuthMethod.NO_AUTH);
        m2.setDecoderResult(DecoderResult.failure(cause));
        out.add(m2);
    }

    static enum State {
        INIT,
        SUCCESS,
        FAILURE;

    }
}

