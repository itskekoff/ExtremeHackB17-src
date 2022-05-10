package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.SequentialDnsServerAddressStream;
import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.util.Random;

final class ShuffledDnsServerAddressStream
implements DnsServerAddressStream {
    private final InetSocketAddress[] addresses;
    private int i;

    ShuffledDnsServerAddressStream(InetSocketAddress[] addresses) {
        this.addresses = (InetSocketAddress[])addresses.clone();
        this.shuffle();
    }

    private void shuffle() {
        InetSocketAddress[] addresses = this.addresses;
        Random r2 = PlatformDependent.threadLocalRandom();
        for (int i2 = addresses.length - 1; i2 >= 0; --i2) {
            InetSocketAddress tmp = addresses[i2];
            int j2 = r2.nextInt(i2 + 1);
            addresses[i2] = addresses[j2];
            addresses[j2] = tmp;
        }
    }

    @Override
    public InetSocketAddress next() {
        int i2 = this.i;
        InetSocketAddress next = this.addresses[i2];
        if (++i2 < this.addresses.length) {
            this.i = i2;
        } else {
            this.i = 0;
            this.shuffle();
        }
        return next;
    }

    public String toString() {
        return SequentialDnsServerAddressStream.toString("shuffled", this.i, this.addresses);
    }
}

