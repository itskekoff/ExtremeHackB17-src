package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;

final class HttpHeadersEncoder {
    private HttpHeadersEncoder() {
    }

    public static void encoderHeader(CharSequence name, CharSequence value, ByteBuf buf2) throws Exception {
        int nameLen = name.length();
        int valueLen = value.length();
        int entryLen = nameLen + valueLen + 4;
        buf2.ensureWritable(entryLen);
        int offset = buf2.writerIndex();
        HttpHeadersEncoder.writeAscii(buf2, offset, name, nameLen);
        offset += nameLen;
        buf2.setByte(offset++, 58);
        buf2.setByte(offset++, 32);
        HttpHeadersEncoder.writeAscii(buf2, offset, value, valueLen);
        offset += valueLen;
        buf2.setByte(offset++, 13);
        buf2.setByte(offset++, 10);
        buf2.writerIndex(offset);
    }

    private static void writeAscii(ByteBuf buf2, int offset, CharSequence value, int valueLen) {
        if (value instanceof AsciiString) {
            ByteBufUtil.copy((AsciiString)value, 0, buf2, offset, valueLen);
        } else {
            HttpHeadersEncoder.writeCharSequence(buf2, offset, value, valueLen);
        }
    }

    private static void writeCharSequence(ByteBuf buf2, int offset, CharSequence value, int valueLen) {
        for (int i2 = 0; i2 < valueLen; ++i2) {
            buf2.setByte(offset++, AsciiString.c2b(value.charAt(i2)));
        }
    }
}

