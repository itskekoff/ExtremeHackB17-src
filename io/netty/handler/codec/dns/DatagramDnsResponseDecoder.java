package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordDecoder;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsResponseDecoder
extends MessageToMessageDecoder<DatagramPacket> {
    private final DnsRecordDecoder recordDecoder;

    public DatagramDnsResponseDecoder() {
        this(DnsRecordDecoder.DEFAULT);
    }

    public DatagramDnsResponseDecoder(DnsRecordDecoder recordDecoder) {
        this.recordDecoder = ObjectUtil.checkNotNull(recordDecoder, "recordDecoder");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
        ByteBuf buf2 = (ByteBuf)packet.content();
        DnsResponse response = DatagramDnsResponseDecoder.newResponse(packet, buf2);
        boolean success = false;
        try {
            int questionCount = buf2.readUnsignedShort();
            int answerCount = buf2.readUnsignedShort();
            int authorityRecordCount = buf2.readUnsignedShort();
            int additionalRecordCount = buf2.readUnsignedShort();
            this.decodeQuestions(response, buf2, questionCount);
            this.decodeRecords(response, DnsSection.ANSWER, buf2, answerCount);
            this.decodeRecords(response, DnsSection.AUTHORITY, buf2, authorityRecordCount);
            this.decodeRecords(response, DnsSection.ADDITIONAL, buf2, additionalRecordCount);
            out.add(response);
            success = true;
        }
        finally {
            if (!success) {
                response.release();
            }
        }
    }

    private static DnsResponse newResponse(DatagramPacket packet, ByteBuf buf2) {
        int id2 = buf2.readUnsignedShort();
        int flags = buf2.readUnsignedShort();
        if (flags >> 15 == 0) {
            throw new CorruptedFrameException("not a response");
        }
        DatagramDnsResponse response = new DatagramDnsResponse((InetSocketAddress)packet.sender(), (InetSocketAddress)packet.recipient(), id2, DnsOpCode.valueOf((byte)(flags >> 11 & 0xF)), DnsResponseCode.valueOf((byte)(flags & 0xF)));
        response.setRecursionDesired((flags >> 8 & 1) == 1);
        response.setAuthoritativeAnswer((flags >> 10 & 1) == 1);
        response.setTruncated((flags >> 9 & 1) == 1);
        response.setRecursionAvailable((flags >> 7 & 1) == 1);
        response.setZ(flags >> 4 & 7);
        return response;
    }

    private void decodeQuestions(DnsResponse response, ByteBuf buf2, int questionCount) throws Exception {
        for (int i2 = questionCount; i2 > 0; --i2) {
            response.addRecord(DnsSection.QUESTION, this.recordDecoder.decodeQuestion(buf2));
        }
    }

    private void decodeRecords(DnsResponse response, DnsSection section, ByteBuf buf2, int count) throws Exception {
        Object r2;
        for (int i2 = count; i2 > 0 && (r2 = this.recordDecoder.decodeRecord(buf2)) != null; --i2) {
            response.addRecord(section, (DnsRecord)r2);
        }
    }
}

