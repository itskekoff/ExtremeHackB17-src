package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

public interface Socks5AddressDecoder {
    public static final Socks5AddressDecoder DEFAULT = new Socks5AddressDecoder(){
        private static final int IPv6_LEN = 16;

        @Override
        public String decodeAddress(Socks5AddressType addrType, ByteBuf in2) throws Exception {
            if (addrType == Socks5AddressType.IPv4) {
                return NetUtil.intToIpAddress(in2.readInt());
            }
            if (addrType == Socks5AddressType.DOMAIN) {
                short length = in2.readUnsignedByte();
                String domain = in2.toString(in2.readerIndex(), length, CharsetUtil.US_ASCII);
                in2.skipBytes(length);
                return domain;
            }
            if (addrType == Socks5AddressType.IPv6) {
                if (in2.hasArray()) {
                    int readerIdx = in2.readerIndex();
                    in2.readerIndex(readerIdx + 16);
                    return NetUtil.bytesToIpAddress(in2.array(), in2.arrayOffset() + readerIdx, 16);
                }
                byte[] tmp = new byte[16];
                in2.readBytes(tmp);
                return NetUtil.bytesToIpAddress(tmp);
            }
            throw new DecoderException("unsupported address type: " + (addrType.byteValue() & 0xFF));
        }
    };

    public String decodeAddress(Socks5AddressType var1, ByteBuf var2) throws Exception;
}

