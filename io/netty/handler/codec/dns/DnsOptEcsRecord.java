package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsOptPseudoRecord;

public interface DnsOptEcsRecord
extends DnsOptPseudoRecord {
    public int sourcePrefixLength();

    public int scopePrefixLength();

    public byte[] address();
}

