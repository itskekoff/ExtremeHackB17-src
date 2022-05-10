package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.DefaultDnsPtrRecord;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordDecoder;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.CharsetUtil;

public class DefaultDnsRecordDecoder
implements DnsRecordDecoder {
    static final String ROOT = ".";

    protected DefaultDnsRecordDecoder() {
    }

    @Override
    public final DnsQuestion decodeQuestion(ByteBuf in2) throws Exception {
        String name = DefaultDnsRecordDecoder.decodeName(in2);
        DnsRecordType type = DnsRecordType.valueOf(in2.readUnsignedShort());
        int qClass = in2.readUnsignedShort();
        return new DefaultDnsQuestion(name, type, qClass);
    }

    @Override
    public final <T extends DnsRecord> T decodeRecord(ByteBuf in2) throws Exception {
        int startOffset = in2.readerIndex();
        String name = DefaultDnsRecordDecoder.decodeName(in2);
        int endOffset = in2.writerIndex();
        if (endOffset - startOffset < 10) {
            in2.readerIndex(startOffset);
            return null;
        }
        DnsRecordType type = DnsRecordType.valueOf(in2.readUnsignedShort());
        int aClass = in2.readUnsignedShort();
        long ttl = in2.readUnsignedInt();
        int length = in2.readUnsignedShort();
        int offset = in2.readerIndex();
        if (endOffset - offset < length) {
            in2.readerIndex(startOffset);
            return null;
        }
        DnsRecord record = this.decodeRecord(name, type, aClass, ttl, in2, offset, length);
        in2.readerIndex(offset + length);
        return (T)record;
    }

    protected DnsRecord decodeRecord(String name, DnsRecordType type, int dnsClass, long timeToLive, ByteBuf in2, int offset, int length) throws Exception {
        if (type == DnsRecordType.PTR) {
            return new DefaultDnsPtrRecord(name, dnsClass, timeToLive, this.decodeName0(in2.duplicate().setIndex(offset, offset + length)));
        }
        return new DefaultDnsRawRecord(name, type, dnsClass, timeToLive, in2.retainedDuplicate().setIndex(offset, offset + length));
    }

    protected String decodeName0(ByteBuf in2) {
        return DefaultDnsRecordDecoder.decodeName(in2);
    }

    public static String decodeName(ByteBuf in2) {
        int position = -1;
        int checked = 0;
        int end = in2.writerIndex();
        int readable = in2.readableBytes();
        if (readable == 0) {
            return ROOT;
        }
        StringBuilder name = new StringBuilder(readable << 1);
        while (in2.isReadable()) {
            boolean pointer;
            short len = in2.readUnsignedByte();
            boolean bl2 = pointer = (len & 0xC0) == 192;
            if (pointer) {
                if (position == -1) {
                    position = in2.readerIndex() + 1;
                }
                if (!in2.isReadable()) {
                    throw new CorruptedFrameException("truncated pointer in a name");
                }
                int next = (len & 0x3F) << 8 | in2.readUnsignedByte();
                if (next >= end) {
                    throw new CorruptedFrameException("name has an out-of-range pointer");
                }
                in2.readerIndex(next);
                if ((checked += 2) < end) continue;
                throw new CorruptedFrameException("name contains a loop.");
            }
            if (len == 0) break;
            if (!in2.isReadable(len)) {
                throw new CorruptedFrameException("truncated label in a name");
            }
            name.append(in2.toString(in2.readerIndex(), len, CharsetUtil.UTF_8)).append('.');
            in2.skipBytes(len);
        }
        if (position != -1) {
            in2.readerIndex(position);
        }
        if (name.length() == 0) {
            return ROOT;
        }
        if (name.charAt(name.length() - 1) != '.') {
            name.append('.');
        }
        return name.toString();
    }
}

