package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.AbstractDnsRecord;
import io.netty.handler.codec.dns.DnsMessageUtil;
import io.netty.handler.codec.dns.DnsPtrRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultDnsPtrRecord
extends AbstractDnsRecord
implements DnsPtrRecord {
    private final String hostname;

    public DefaultDnsPtrRecord(String name, int dnsClass, long timeToLive, String hostname) {
        super(name, DnsRecordType.PTR, dnsClass, timeToLive);
        this.hostname = ObjectUtil.checkNotNull(hostname, "hostname");
    }

    @Override
    public String hostname() {
        return this.hostname;
    }

    @Override
    public String toString() {
        StringBuilder buf2 = new StringBuilder(64).append(StringUtil.simpleClassName(this)).append('(');
        DnsRecordType type = this.type();
        buf2.append(this.name().isEmpty() ? "<root>" : this.name()).append(' ').append(this.timeToLive()).append(' ');
        DnsMessageUtil.appendRecordClass(buf2, this.dnsClass()).append(' ').append(type.name());
        buf2.append(' ').append(this.hostname);
        return buf2.toString();
    }
}

