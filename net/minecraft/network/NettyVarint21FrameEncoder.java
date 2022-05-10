package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

@ChannelHandler.Sharable
public class NettyVarint21FrameEncoder
extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_) throws Exception {
        int i2 = p_encode_2_.readableBytes();
        int j2 = PacketBuffer.getVarIntSize(i2);
        if (j2 > 3) {
            throw new IllegalArgumentException("unable to fit " + i2 + " into " + 3);
        }
        PacketBuffer packetbuffer = new PacketBuffer(p_encode_3_);
        packetbuffer.ensureWritable(j2 + i2);
        packetbuffer.writeVarIntToBuffer(i2);
        packetbuffer.writeBytes(p_encode_2_, p_encode_2_.readerIndex(), i2);
    }
}

