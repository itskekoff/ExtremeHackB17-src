package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.compression.Snappy;

public class SnappyFrameEncoder
extends MessageToByteEncoder<ByteBuf> {
    private static final int MIN_COMPRESSIBLE_LENGTH = 18;
    private static final byte[] STREAM_START = new byte[]{-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
    private final Snappy snappy = new Snappy();
    private boolean started;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in2, ByteBuf out) throws Exception {
        block6: {
            int dataLength;
            if (!in2.isReadable()) {
                return;
            }
            if (!this.started) {
                this.started = true;
                out.writeBytes(STREAM_START);
            }
            if ((dataLength = in2.readableBytes()) > 18) {
                ByteBuf slice;
                int lengthIdx;
                while (true) {
                    lengthIdx = out.writerIndex() + 1;
                    if (dataLength < 18) {
                        slice = in2.readSlice(dataLength);
                        SnappyFrameEncoder.writeUnencodedChunk(slice, out, dataLength);
                        break block6;
                    }
                    out.writeInt(0);
                    if (dataLength <= 32767) break;
                    slice = in2.readSlice(32767);
                    SnappyFrameEncoder.calculateAndWriteChecksum(slice, out);
                    this.snappy.encode(slice, out, 32767);
                    SnappyFrameEncoder.setChunkLength(out, lengthIdx);
                    dataLength -= 32767;
                }
                slice = in2.readSlice(dataLength);
                SnappyFrameEncoder.calculateAndWriteChecksum(slice, out);
                this.snappy.encode(slice, out, dataLength);
                SnappyFrameEncoder.setChunkLength(out, lengthIdx);
            } else {
                SnappyFrameEncoder.writeUnencodedChunk(in2, out, dataLength);
            }
        }
    }

    private static void writeUnencodedChunk(ByteBuf in2, ByteBuf out, int dataLength) {
        out.writeByte(1);
        SnappyFrameEncoder.writeChunkLength(out, dataLength + 4);
        SnappyFrameEncoder.calculateAndWriteChecksum(in2, out);
        out.writeBytes(in2, dataLength);
    }

    private static void setChunkLength(ByteBuf out, int lengthIdx) {
        int chunkLength = out.writerIndex() - lengthIdx - 3;
        if (chunkLength >>> 24 != 0) {
            throw new CompressionException("compressed data too large: " + chunkLength);
        }
        out.setMediumLE(lengthIdx, chunkLength);
    }

    private static void writeChunkLength(ByteBuf out, int chunkLength) {
        out.writeMediumLE(chunkLength);
    }

    private static void calculateAndWriteChecksum(ByteBuf slice, ByteBuf out) {
        out.writeIntLE(Snappy.calculateChecksum(slice));
    }
}

