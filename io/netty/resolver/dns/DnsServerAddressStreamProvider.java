package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddressStream;

public interface DnsServerAddressStreamProvider {
    public DnsServerAddressStream nameServerAddressStream(String var1);
}

