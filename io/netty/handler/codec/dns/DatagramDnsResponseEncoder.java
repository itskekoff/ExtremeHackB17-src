package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordEncoder;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsResponseEncoder
extends MessageToMessageEncoder<AddressedEnvelope<DnsResponse, InetSocketAddress>> {
    private final DnsRecordEncoder recordEncoder;

    public DatagramDnsResponseEncoder() {
        this(DnsRecordEncoder.DEFAULT);
    }

    public DatagramDnsResponseEncoder(DnsRecordEncoder recordEncoder) {
        this.recordEncoder = ObjectUtil.checkNotNull(recordEncoder, "recordEncoder");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<DnsResponse, InetSocketAddress> in2, List<Object> out) throws Exception {
        InetSocketAddress recipient = in2.recipient();
        DnsResponse response = in2.content();
        ByteBuf buf2 = this.allocateBuffer(ctx, in2);
        boolean success = false;
        try {
            DatagramDnsResponseEncoder.encodeHeader(response, buf2);
            this.encodeQuestions(response, buf2);
            this.encodeRecords(response, DnsSection.ANSWER, buf2);
            this.encodeRecords(response, DnsSection.AUTHORITY, buf2);
            this.encodeRecords(response, DnsSection.ADDITIONAL, buf2);
            success = true;
        }
        finally {
            if (!success) {
                buf2.release();
            }
        }
        out.add(new DatagramPacket(buf2, recipient, null));
    }

    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, AddressedEnvelope<DnsResponse, InetSocketAddress> msg) throws Exception {
        return ctx.alloc().ioBuffer(1024);
    }

    private static void encodeHeader(DnsResponse response, ByteBuf buf2) {
        buf2.writeShort(response.id());
        int flags = 32768;
        flags |= (response.opCode().byteValue() & 0xFF) << 11;
        if (response.isAuthoritativeAnswer()) {
            flags |= 0x400;
        }
        if (response.isTruncated()) {
            flags |= 0x200;
        }
        if (response.isRecursionDesired()) {
            flags |= 0x100;
        }
        if (response.isRecursionAvailable()) {
            flags |= 0x80;
        }
        flags |= response.z() << 4;
        buf2.writeShort(flags |= response.code().intValue());
        buf2.writeShort(response.count(DnsSection.QUESTION));
        buf2.writeShort(response.count(DnsSection.ANSWER));
        buf2.writeShort(response.count(DnsSection.AUTHORITY));
        buf2.writeShort(response.count(DnsSection.ADDITIONAL));
    }

    private void encodeQuestions(DnsResponse response, ByteBuf buf2) throws Exception {
        int count = response.count(DnsSection.QUESTION);
        for (int i2 = 0; i2 < count; ++i2) {
            this.recordEncoder.encodeQuestion((DnsQuestion)response.recordAt(DnsSection.QUESTION, i2), buf2);
        }
    }

    private void encodeRecords(DnsResponse response, DnsSection section, ByteBuf buf2) throws Exception {
        int count = response.count(section);
        for (int i2 = 0; i2 < count; ++i2) {
            this.recordEncoder.encodeRecord((DnsRecord)response.recordAt(section, i2), buf2);
        }
    }
}

