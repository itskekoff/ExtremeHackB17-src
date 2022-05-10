package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5AddressDecoder;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import java.util.List;

public class Socks5CommandRequestDecoder
extends ReplayingDecoder<State> {
    private final Socks5AddressDecoder addressDecoder;

    public Socks5CommandRequestDecoder() {
        this(Socks5AddressDecoder.DEFAULT);
    }

    public Socks5CommandRequestDecoder(Socks5AddressDecoder addressDecoder) {
        super(State.INIT);
        if (addressDecoder == null) {
            throw new NullPointerException("addressDecoder");
        }
        this.addressDecoder = addressDecoder;
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
                    Socks5CommandType type = Socks5CommandType.valueOf(in2.readByte());
                    in2.skipBytes(1);
                    Socks5AddressType dstAddrType = Socks5AddressType.valueOf(in2.readByte());
                    String dstAddr = this.addressDecoder.decodeAddress(dstAddrType, in2);
                    int dstPort = in2.readUnsignedShort();
                    out.add(new DefaultSocks5CommandRequest(type, dstAddrType, dstAddr, dstPort));
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
        DefaultSocks5CommandRequest m2 = new DefaultSocks5CommandRequest(Socks5CommandType.CONNECT, Socks5AddressType.IPv4, "0.0.0.0", 1);
        m2.setDecoderResult(DecoderResult.failure(cause));
        out.add(m2);
    }

    static enum State {
        INIT,
        SUCCESS,
        FAILURE;

    }
}

