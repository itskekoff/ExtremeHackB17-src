package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.AbstractDnsRecord;
import io.netty.handler.codec.dns.DnsMessageUtil;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.internal.StringUtil;

public class DefaultDnsQuestion
extends AbstractDnsRecord
implements DnsQuestion {
    public DefaultDnsQuestion(String name, DnsRecordType type) {
        super(name, type, 0L);
    }

    public DefaultDnsQuestion(String name, DnsRecordType type, int dnsClass) {
        super(name, type, dnsClass, 0L);
    }

    @Override
    public String toString() {
        StringBuilder buf2 = new StringBuilder(64);
        buf2.append(StringUtil.simpleClassName(this)).append('(').append(this.name()).append(' ');
        DnsMessageUtil.appendRecordClass(buf2, this.dnsClass()).append(' ').append(this.type().name()).append(')');
        return buf2.toString();
    }
}

