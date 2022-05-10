package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
    private final Cipher cipher;
    private byte[] inputBuffer = new byte[0];
    private byte[] outputBuffer = new byte[0];

    protected NettyEncryptionTranslator(Cipher cipherIn) {
        this.cipher = cipherIn;
    }

    private byte[] bufToBytes(ByteBuf buf2) {
        int i2 = buf2.readableBytes();
        if (this.inputBuffer.length < i2) {
            this.inputBuffer = new byte[i2];
        }
        buf2.readBytes(this.inputBuffer, 0, i2);
        return this.inputBuffer;
    }

    protected ByteBuf decipher(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException {
        int i2 = buffer.readableBytes();
        byte[] abyte = this.bufToBytes(buffer);
        ByteBuf bytebuf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i2));
        bytebuf.writerIndex(this.cipher.update(abyte, 0, i2, bytebuf.array(), bytebuf.arrayOffset()));
        return bytebuf;
    }

    protected void cipher(ByteBuf in2, ByteBuf out) throws ShortBufferException {
        int i2 = in2.readableBytes();
        byte[] abyte = this.bufToBytes(in2);
        int j2 = this.cipher.getOutputSize(i2);
        if (this.outputBuffer.length < j2) {
            this.outputBuffer = new byte[j2];
        }
        out.writeBytes(this.outputBuffer, 0, this.cipher.update(abyte, 0, i2, this.outputBuffer));
    }
}

