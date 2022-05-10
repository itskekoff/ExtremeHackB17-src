package io.netty.handler.codec.socksx;

public enum SocksVersion {
    SOCKS4a(4),
    SOCKS5(5),
    UNKNOWN(-1);

    private final byte b;

    public static SocksVersion valueOf(byte b2) {
        if (b2 == SOCKS4a.byteValue()) {
            return SOCKS4a;
        }
        if (b2 == SOCKS5.byteValue()) {
            return SOCKS5;
        }
        return UNKNOWN;
    }

    private SocksVersion(byte b2) {
        this.b = b2;
    }

    public byte byteValue() {
        return this.b;
    }
}

