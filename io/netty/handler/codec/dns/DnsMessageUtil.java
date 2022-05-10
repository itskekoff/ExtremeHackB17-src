package io.netty.handler.codec.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.internal.StringUtil;

final class DnsMessageUtil {
    static StringBuilder appendQuery(StringBuilder buf2, DnsQuery query) {
        DnsMessageUtil.appendQueryHeader(buf2, query);
        DnsMessageUtil.appendAllRecords(buf2, query);
        return buf2;
    }

    static StringBuilder appendResponse(StringBuilder buf2, DnsResponse response) {
        DnsMessageUtil.appendResponseHeader(buf2, response);
        DnsMessageUtil.appendAllRecords(buf2, response);
        return buf2;
    }

    static StringBuilder appendRecordClass(StringBuilder buf2, int dnsClass) {
        String name;
        switch (dnsClass &= 0xFFFF) {
            case 1: {
                name = "IN";
                break;
            }
            case 2: {
                name = "CSNET";
                break;
            }
            case 3: {
                name = "CHAOS";
                break;
            }
            case 4: {
                name = "HESIOD";
                break;
            }
            case 254: {
                name = "NONE";
                break;
            }
            case 255: {
                name = "ANY";
                break;
            }
            default: {
                name = null;
            }
        }
        if (name != null) {
            buf2.append(name);
        } else {
            buf2.append("UNKNOWN(").append(dnsClass).append(')');
        }
        return buf2;
    }

    private static void appendQueryHeader(StringBuilder buf2, DnsQuery msg) {
        buf2.append(StringUtil.simpleClassName(msg)).append('(');
        DnsMessageUtil.appendAddresses(buf2, msg).append(msg.id()).append(", ").append(msg.opCode());
        if (msg.isRecursionDesired()) {
            buf2.append(", RD");
        }
        if (msg.z() != 0) {
            buf2.append(", Z: ").append(msg.z());
        }
        buf2.append(')');
    }

    private static void appendResponseHeader(StringBuilder buf2, DnsResponse msg) {
        buf2.append(StringUtil.simpleClassName(msg)).append('(');
        DnsMessageUtil.appendAddresses(buf2, msg).append(msg.id()).append(", ").append(msg.opCode()).append(", ").append(msg.code()).append(',');
        boolean hasComma = true;
        if (msg.isRecursionDesired()) {
            hasComma = false;
            buf2.append(" RD");
        }
        if (msg.isAuthoritativeAnswer()) {
            hasComma = false;
            buf2.append(" AA");
        }
        if (msg.isTruncated()) {
            hasComma = false;
            buf2.append(" TC");
        }
        if (msg.isRecursionAvailable()) {
            hasComma = false;
            buf2.append(" RA");
        }
        if (msg.z() != 0) {
            if (!hasComma) {
                buf2.append(',');
            }
            buf2.append(" Z: ").append(msg.z());
        }
        if (hasComma) {
            buf2.setCharAt(buf2.length() - 1, ')');
        } else {
            buf2.append(')');
        }
    }

    private static StringBuilder appendAddresses(StringBuilder buf2, DnsMessage msg) {
        if (!(msg instanceof AddressedEnvelope)) {
            return buf2;
        }
        AddressedEnvelope envelope = (AddressedEnvelope)((Object)msg);
        Object addr = envelope.sender();
        if (addr != null) {
            buf2.append("from: ").append(addr).append(", ");
        }
        if ((addr = envelope.recipient()) != null) {
            buf2.append("to: ").append(addr).append(", ");
        }
        return buf2;
    }

    private static void appendAllRecords(StringBuilder buf2, DnsMessage msg) {
        DnsMessageUtil.appendRecords(buf2, msg, DnsSection.QUESTION);
        DnsMessageUtil.appendRecords(buf2, msg, DnsSection.ANSWER);
        DnsMessageUtil.appendRecords(buf2, msg, DnsSection.AUTHORITY);
        DnsMessageUtil.appendRecords(buf2, msg, DnsSection.ADDITIONAL);
    }

    private static void appendRecords(StringBuilder buf2, DnsMessage message, DnsSection section) {
        int count = message.count(section);
        for (int i2 = 0; i2 < count; ++i2) {
            buf2.append(StringUtil.NEWLINE).append('\t').append(message.recordAt(section, i2));
        }
    }

    private DnsMessageUtil() {
    }
}

