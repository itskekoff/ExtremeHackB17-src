package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsRecord;

public interface DnsPtrRecord
extends DnsRecord {
    public String hostname();
}

