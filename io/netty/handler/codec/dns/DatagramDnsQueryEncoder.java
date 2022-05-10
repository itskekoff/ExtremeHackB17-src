package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordEncoder;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsQueryEncoder
extends MessageToMessageEncoder<AddressedEnvelope<DnsQuery, InetSocketAddress>> {
    private final DnsRecordEncoder recordEncoder;

    public DatagramDnsQueryEncoder() {
        this(DnsRecordEncoder.DEFAULT);
    }

    public DatagramDnsQueryEncoder(DnsRecordEncoder recordEncoder) {
        this.recordEncoder = ObjectUtil.checkNotNull(recordEncoder, "recordEncoder");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<DnsQuery, InetSocketAddress> in2, List<Object> out) throws Exception {
        InetSocketAddress recipient = in2.recipient();
        DnsQuery query = in2.content();
        ByteBuf buf2 = this.allocateBuffer(ctx, in2);
        boolean success = false;
        try {
            DatagramDnsQueryEncoder.encodeHeader(query, buf2);
            this.encodeQuestions(query, buf2);
            this.encodeRecords(query, DnsSection.ADDITIONAL, buf2);
            success = true;
        }
        finally {
            if (!success) {
                buf2.release();
            }
        }
        out.add(new DatagramPacket(buf2, recipient, null));
    }

    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, AddressedEnvelope<DnsQuery, InetSocketAddress> msg) throws Exception {
        return ctx.alloc().ioBuffer(1024);
    }

    private static void encodeHeader(DnsQuery query, ByteBuf buf2) {
        buf2.writeShort(query.id());
        int flags = 0;
        flags |= (query.opCode().byteValue() & 0xFF) << 14;
        if (query.isRecursionDesired()) {
            flags |= 0x100;
        }
        buf2.writeShort(flags);
        buf2.writeShort(query.count(DnsSection.QUESTION));
        buf2.writeShort(0);
        buf2.writeShort(0);
        buf2.writeShort(query.count(DnsSection.ADDITIONAL));
    }

    private void encodeQuestions(DnsQuery query, ByteBuf buf2) throws Exception {
        int count = query.count(DnsSection.QUESTION);
        for (int i2 = 0; i2 < count; ++i2) {
            this.recordEncoder.encodeQuestion((DnsQuestion)query.recordAt(DnsSection.QUESTION, i2), buf2);
        }
    }

    private void encodeRecords(DnsQuery query, DnsSection section, ByteBuf buf2) throws Exception {
        int count = query.count(section);
        for (int i2 = 0; i2 < count; ++i2) {
            this.recordEncoder.encodeRecord((DnsRecord)query.recordAt(section, i2), buf2);
        }
    }
}

