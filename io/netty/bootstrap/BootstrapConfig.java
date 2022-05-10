package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.resolver.AddressResolverGroup;
import java.net.SocketAddress;

public final class BootstrapConfig
extends AbstractBootstrapConfig<Bootstrap, Channel> {
    BootstrapConfig(Bootstrap bootstrap) {
        super(bootstrap);
    }

    public SocketAddress remoteAddress() {
        return ((Bootstrap)this.bootstrap).remoteAddress();
    }

    public AddressResolverGroup<?> resolver() {
        return ((Bootstrap)this.bootstrap).resolver();
    }

    @Override
    public String toString() {
        StringBuilder buf2 = new StringBuilder(super.toString());
        buf2.setLength(buf2.length() - 1);
        buf2.append(", resolver: ").append(this.resolver());
        SocketAddress remoteAddress = this.remoteAddress();
        if (remoteAddress != null) {
            buf2.append(", remoteAddress: ").append(remoteAddress);
        }
        return buf2.append(')').toString();
    }
}

