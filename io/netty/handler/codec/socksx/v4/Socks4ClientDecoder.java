package io.netty.handler.codec.socksx.v4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.util.NetUtil;
import java.util.List;

public class Socks4ClientDecoder
extends ReplayingDecoder<State> {
    public Socks4ClientDecoder() {
        super(State.START);
        this.setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        try {
            switch ((State)((Object)this.state())) {
                case START: {
                    short version = in2.readUnsignedByte();
                    if (version != 0) {
                        throw new DecoderException("unsupported reply version: " + version + " (expected: 0)");
                    }
                    Socks4CommandStatus status = Socks4CommandStatus.valueOf(in2.readByte());
                    int dstPort = in2.readUnsignedShort();
                    String dstAddr = NetUtil.intToIpAddress(in2.readInt());
                    out.add(new DefaultSocks4CommandResponse(status, dstAddr, dstPort));
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
        DefaultSocks4CommandResponse m2 = new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED);
        m2.setDecoderResult(DecoderResult.failure(cause));
        out.add(m2);
        this.checkpoint(State.FAILURE);
    }

    static enum State {
        START,
        SUCCESS,
        FAILURE;

    }
}

