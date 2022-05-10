package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.util.CharsetUtil;
import java.util.List;

public class Socks5PasswordAuthRequestDecoder
extends ReplayingDecoder<State> {
    public Socks5PasswordAuthRequestDecoder() {
        super(State.INIT);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        try {
            switch ((State)((Object)this.state())) {
                case INIT: {
                    int startOffset = in2.readerIndex();
                    byte version = in2.getByte(startOffset);
                    if (version != 1) {
                        throw new DecoderException("unsupported subnegotiation version: " + version + " (expected: 1)");
                    }
                    short usernameLength = in2.getUnsignedByte(startOffset + 1);
                    short passwordLength = in2.getUnsignedByte(startOffset + 2 + usernameLength);
                    int totalLength = usernameLength + passwordLength + 3;
                    in2.skipBytes(totalLength);
                    out.add(new DefaultSocks5PasswordAuthRequest(in2.toString(startOffset + 2, usernameLength, CharsetUtil.US_ASCII), in2.toString(startOffset + 3 + usernameLength, passwordLength, CharsetUtil.US_ASCII)));
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
        DefaultSocks5PasswordAuthRequest m2 = new DefaultSocks5PasswordAuthRequest("", "");
        m2.setDecoderResult(DecoderResult.failure(cause));
        out.add(m2);
    }

    static enum State {
        INIT,
        SUCCESS,
        FAILURE;

    }
}

