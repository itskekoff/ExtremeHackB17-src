package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddresses;
import java.net.InetSocketAddress;

abstract class DefaultDnsServerAddresses
extends DnsServerAddresses {
    protected final InetSocketAddress[] addresses;
    private final String strVal;

    DefaultDnsServerAddresses(String type, InetSocketAddress[] addresses) {
        this.addresses = addresses;
        StringBuilder buf2 = new StringBuilder(type.length() + 2 + addresses.length * 16);
        buf2.append(type).append('(');
        for (InetSocketAddress a2 : addresses) {
            buf2.append(a2).append(", ");
        }
        buf2.setLength(buf2.length() - 2);
        buf2.append(')');
        this.strVal = buf2.toString();
    }

    public String toString() {
        return this.strVal;
    }
}

