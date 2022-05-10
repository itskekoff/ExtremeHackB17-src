package io.netty.channel.pool;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;

public abstract class AbstractChannelPoolHandler
implements ChannelPoolHandler {
    @Override
    public void channelAcquired(Channel ch2) throws Exception {
    }

    @Override
    public void channelReleased(Channel ch2) throws Exception {
    }
}

