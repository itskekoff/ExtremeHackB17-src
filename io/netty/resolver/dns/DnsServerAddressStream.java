package io.netty.resolver.dns;

import java.net.InetSocketAddress;

public interface DnsServerAddressStream {
    public InetSocketAddress next();
}

