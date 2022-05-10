package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.socksx.v5.AbstractSocks5Message;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

public final class DefaultSocks5CommandResponse
extends AbstractSocks5Message
implements Socks5CommandResponse {
    private final Socks5CommandStatus status;
    private final Socks5AddressType bndAddrType;
    private final String bndAddr;
    private final int bndPort;

    public DefaultSocks5CommandResponse(Socks5CommandStatus status, Socks5AddressType bndAddrType) {
        this(status, bndAddrType, null, 0);
    }

    public DefaultSocks5CommandResponse(Socks5CommandStatus status, Socks5AddressType bndAddrType, String bndAddr, int bndPort) {
        if (status == null) {
            throw new NullPointerException("status");
        }
        if (bndAddrType == null) {
            throw new NullPointerException("bndAddrType");
        }
        if (bndAddr != null) {
            if (bndAddrType == Socks5AddressType.IPv4) {
                if (!NetUtil.isValidIpV4Address(bndAddr)) {
                    throw new IllegalArgumentException("bndAddr: " + bndAddr + " (expected: a valid IPv4 address)");
                }
            } else if (bndAddrType == Socks5AddressType.DOMAIN) {
                if ((bndAddr = IDN.toASCII(bndAddr)).length() > 255) {
                    throw new IllegalArgumentException("bndAddr: " + bndAddr + " (expected: less than 256 chars)");
                }
            } else if (bndAddrType == Socks5AddressType.IPv6 && !NetUtil.isValidIpV6Address(bndAddr)) {
                throw new IllegalArgumentException("bndAddr: " + bndAddr + " (expected: a valid IPv6 address)");
            }
        }
        if (bndPort < 0 || bndPort > 65535) {
            throw new IllegalArgumentException("bndPort: " + bndPort + " (expected: 0~65535)");
        }
        this.status = status;
        this.bndAddrType = bndAddrType;
        this.bndAddr = bndAddr;
        this.bndPort = bndPort;
    }

    @Override
    public Socks5CommandStatus status() {
        return this.status;
    }

    @Override
    public Socks5AddressType bndAddrType() {
        return this.bndAddrType;
    }

    @Override
    public String bndAddr() {
        return this.bndAddr;
    }

    @Override
    public int bndPort() {
        return this.bndPort;
    }

    public String toString() {
        StringBuilder buf2 = new StringBuilder(128);
        buf2.append(StringUtil.simpleClassName(this));
        DecoderResult decoderResult = this.decoderResult();
        if (!decoderResult.isSuccess()) {
            buf2.append("(decoderResult: ");
            buf2.append(decoderResult);
            buf2.append(", status: ");
        } else {
            buf2.append("(status: ");
        }
        buf2.append(this.status());
        buf2.append(", bndAddrType: ");
        buf2.append(this.bndAddrType());
        buf2.append(", bndAddr: ");
        buf2.append(this.bndAddr());
        buf2.append(", bndPort: ");
        buf2.append(this.bndPort());
        buf2.append(')');
        return buf2.toString();
    }
}

