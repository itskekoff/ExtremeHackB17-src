package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddressStream;
import java.net.InetSocketAddress;

final class SequentialDnsServerAddressStream
implements DnsServerAddressStream {
    private final InetSocketAddress[] addresses;
    private int i;

    SequentialDnsServerAddressStream(InetSocketAddress[] addresses, int startIdx) {
        this.addresses = addresses;
        this.i = startIdx;
    }

    @Override
    public InetSocketAddress next() {
        int i2 = this.i;
        InetSocketAddress next = this.addresses[i2];
        this.i = ++i2 < this.addresses.length ? i2 : 0;
        return next;
    }

    public String toString() {
        return SequentialDnsServerAddressStream.toString("sequential", this.i, this.addresses);
    }

    static String toString(String type, int index, InetSocketAddress[] addresses) {
        StringBuilder buf2 = new StringBuilder(type.length() + 2 + addresses.length * 16);
        buf2.append(type).append("(index: ").append(index);
        buf2.append(", addrs: (");
        for (InetSocketAddress a2 : addresses) {
            buf2.append(a2).append(", ");
        }
        buf2.setLength(buf2.length() - 2);
        buf2.append("))");
        return buf2.toString();
    }
}

