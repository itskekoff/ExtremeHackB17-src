package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;
import java.util.Map;

public final class ServerBootstrapConfig
extends AbstractBootstrapConfig<ServerBootstrap, ServerChannel> {
    ServerBootstrapConfig(ServerBootstrap bootstrap) {
        super(bootstrap);
    }

    public EventLoopGroup childGroup() {
        return ((ServerBootstrap)this.bootstrap).childGroup();
    }

    public ChannelHandler childHandler() {
        return ((ServerBootstrap)this.bootstrap).childHandler();
    }

    public Map<ChannelOption<?>, Object> childOptions() {
        return ((ServerBootstrap)this.bootstrap).childOptions();
    }

    public Map<AttributeKey<?>, Object> childAttrs() {
        return ((ServerBootstrap)this.bootstrap).childAttrs();
    }

    @Override
    public String toString() {
        ChannelHandler childHandler;
        Map<AttributeKey<?>, Object> childAttrs;
        Map<ChannelOption<?>, Object> childOptions;
        StringBuilder buf2 = new StringBuilder(super.toString());
        buf2.setLength(buf2.length() - 1);
        buf2.append(", ");
        EventLoopGroup childGroup = this.childGroup();
        if (childGroup != null) {
            buf2.append("childGroup: ");
            buf2.append(StringUtil.simpleClassName(childGroup));
            buf2.append(", ");
        }
        if (!(childOptions = this.childOptions()).isEmpty()) {
            buf2.append("childOptions: ");
            buf2.append(childOptions);
            buf2.append(", ");
        }
        if (!(childAttrs = this.childAttrs()).isEmpty()) {
            buf2.append("childAttrs: ");
            buf2.append(childAttrs);
            buf2.append(", ");
        }
        if ((childHandler = this.childHandler()) != null) {
            buf2.append("childHandler: ");
            buf2.append(childHandler);
            buf2.append(", ");
        }
        if (buf2.charAt(buf2.length() - 1) == '(') {
            buf2.append(')');
        } else {
            buf2.setCharAt(buf2.length() - 2, ')');
            buf2.setLength(buf2.length() - 1);
        }
        return buf2.toString();
    }
}

