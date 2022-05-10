package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

public interface SctpServerChannelConfig
extends ChannelConfig {
    public int getBacklog();

    public SctpServerChannelConfig setBacklog(int var1);

    public int getSendBufferSize();

    public SctpServerChannelConfig setSendBufferSize(int var1);

    public int getReceiveBufferSize();

    public SctpServerChannelConfig setReceiveBufferSize(int var1);

    public SctpStandardSocketOptions.InitMaxStreams getInitMaxStreams();

    public SctpServerChannelConfig setInitMaxStreams(SctpStandardSocketOptions.InitMaxStreams var1);

    @Override
    @Deprecated
    public SctpServerChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public SctpServerChannelConfig setWriteSpinCount(int var1);

    @Override
    public SctpServerChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public SctpServerChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public SctpServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public SctpServerChannelConfig setAutoRead(boolean var1);

    @Override
    public SctpServerChannelConfig setAutoClose(boolean var1);

    @Override
    public SctpServerChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public SctpServerChannelConfig setWriteBufferLowWaterMark(int var1);

    @Override
    public SctpServerChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

    @Override
    public SctpServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}
