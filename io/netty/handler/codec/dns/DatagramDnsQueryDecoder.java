package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordDecoder;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsQueryDecoder
extends MessageToMessageDecoder<DatagramPacket> {
    private final DnsRecordDecoder recordDecoder;

    public DatagramDnsQueryDecoder() {
        this(DnsRecordDecoder.DEFAULT);
    }

    public DatagramDnsQueryDecoder(DnsRecordDecoder recordDecoder) {
        this.recordDecoder = ObjectUtil.checkNotNull(recordDecoder, "recordDecoder");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
        ByteBuf buf2 = (ByteBuf)packet.content();
        DnsQuery query = DatagramDnsQueryDecoder.newQuery(packet, buf2);
        boolean success = false;
        try {
            int questionCount = buf2.readUnsignedShort();
            int answerCount = buf2.readUnsignedShort();
            int authorityRecordCount = buf2.readUnsignedShort();
            int additionalRecordCount = buf2.readUnsignedShort();
            this.decodeQuestions(query, buf2, questionCount);
            this.decodeRecords(query, DnsSection.ANSWER, buf2, answerCount);
            this.decodeRecords(query, DnsSection.AUTHORITY, buf2, authorityRecordCount);
            this.decodeRecords(query, DnsSection.ADDITIONAL, buf2, additionalRecordCount);
            out.add(query);
            success = true;
        }
        finally {
            if (!success) {
                query.release();
            }
        }
    }

    private static DnsQuery newQuery(DatagramPacket packet, ByteBuf buf2) {
        int id2 = buf2.readUnsignedShort();
        int flags = buf2.readUnsignedShort();
        if (flags >> 15 == 1) {
            throw new CorruptedFrameException("not a query");
        }
        DatagramDnsQuery query = new DatagramDnsQuery((InetSocketAddress)packet.sender(), (InetSocketAddress)packet.recipient(), id2, DnsOpCode.valueOf((byte)(flags >> 11 & 0xF)));
        query.setRecursionDesired((flags >> 8 & 1) == 1);
        query.setZ(flags >> 4 & 7);
        return query;
    }

    private void decodeQuestions(DnsQuery query, ByteBuf buf2, int questionCount) throws Exception {
        for (int i2 = questionCount; i2 > 0; --i2) {
            query.addRecord(DnsSection.QUESTION, this.recordDecoder.decodeQuestion(buf2));
        }
    }

    private void decodeRecords(DnsQuery query, DnsSection section, ByteBuf buf2, int count) throws Exception {
        Object r2;
        for (int i2 = count; i2 > 0 && (r2 = this.recordDecoder.decodeRecord(buf2)) != null; --i2) {
            query.addRecord(section, (DnsRecord)r2);
        }
    }
}

