package io.netty.handler.codec.socksx.v4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandType;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.util.List;

public class Socks4ServerDecoder
extends ReplayingDecoder<State> {
    private static final int MAX_FIELD_LENGTH = 255;
    private Socks4CommandType type;
    private String dstAddr;
    private int dstPort;
    private String userId;

    public Socks4ServerDecoder() {
        super(State.START);
        this.setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        try {
            switch ((State)((Object)this.state())) {
                case START: {
                    short version = in2.readUnsignedByte();
                    if (version != SocksVersion.SOCKS4a.byteValue()) {
                        throw new DecoderException("unsupported protocol version: " + version);
                    }
                    this.type = Socks4CommandType.valueOf(in2.readByte());
                    this.dstPort = in2.readUnsignedShort();
                    this.dstAddr = NetUtil.intToIpAddress(in2.readInt());
                    this.checkpoint(State.READ_USERID);
                }
                case READ_USERID: {
                    this.userId = Socks4ServerDecoder.readString("userid", in2);
                    this.checkpoint(State.READ_DOMAIN);
                }
                case READ_DOMAIN: {
                    if (!"0.0.0.0".equals(this.dstAddr) && this.dstAddr.startsWith("0.0.0.")) {
                        this.dstAddr = Socks4ServerDecoder.readString("dstAddr", in2);
                    }
                    out.add(new DefaultSocks4CommandRequest(this.type, this.dstAddr, this.dstPort, this.userId));
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
        DefaultSocks4CommandRequest m2 = new DefaultSocks4CommandRequest(this.type != null ? this.type : Socks4CommandType.CONNECT, this.dstAddr != null ? this.dstAddr : "", this.dstPort != 0 ? this.dstPort : 65535, this.userId != null ? this.userId : "");
        m2.setDecoderResult(DecoderResult.failure(cause));
        out.add(m2);
        this.checkpoint(State.FAILURE);
    }

    private static String readString(String fieldName, ByteBuf in2) {
        int length = in2.bytesBefore(256, (byte)0);
        if (length < 0) {
            throw new DecoderException("field '" + fieldName + "' longer than " + 255 + " chars");
        }
        String value = in2.readSlice(length).toString(CharsetUtil.US_ASCII);
        in2.skipBytes(1);
        return value;
    }

    static enum State {
        START,
        READ_USERID,
        READ_DOMAIN,
        SUCCESS,
        FAILURE;

    }
}

