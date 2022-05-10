package io.netty.handler.codec.socksx.v4;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.socksx.v4.AbstractSocks4Message;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandType;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

public class DefaultSocks4CommandRequest
extends AbstractSocks4Message
implements Socks4CommandRequest {
    private final Socks4CommandType type;
    private final String dstAddr;
    private final int dstPort;
    private final String userId;

    public DefaultSocks4CommandRequest(Socks4CommandType type, String dstAddr, int dstPort) {
        this(type, dstAddr, dstPort, "");
    }

    public DefaultSocks4CommandRequest(Socks4CommandType type, String dstAddr, int dstPort, String userId) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (dstAddr == null) {
            throw new NullPointerException("dstAddr");
        }
        if (dstPort <= 0 || dstPort >= 65536) {
            throw new IllegalArgumentException("dstPort: " + dstPort + " (expected: 1~65535)");
        }
        if (userId == null) {
            throw new NullPointerException("userId");
        }
        this.userId = userId;
        this.type = type;
        this.dstAddr = IDN.toASCII(dstAddr);
        this.dstPort = dstPort;
    }

    @Override
    public Socks4CommandType type() {
        return this.type;
    }

    @Override
    public String dstAddr() {
        return this.dstAddr;
    }

    @Override
    public int dstPort() {
        return this.dstPort;
    }

    @Override
    public String userId() {
        return this.userId;
    }

    public String toString() {
        StringBuilder buf2 = new StringBuilder(128);
        buf2.append(StringUtil.simpleClassName(this));
        DecoderResult decoderResult = this.decoderResult();
        if (!decoderResult.isSuccess()) {
            buf2.append("(decoderResult: ");
            buf2.append(decoderResult);
            buf2.append(", type: ");
        } else {
            buf2.append("(type: ");
        }
        buf2.append(this.type());
        buf2.append(", dstAddr: ");
        buf2.append(this.dstAddr());
        buf2.append(", dstPort: ");
        buf2.append(this.dstPort());
        buf2.append(", userId: ");
        buf2.append(this.userId());
        buf2.append(')');
        return buf2.toString();
    }
}

