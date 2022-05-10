 * Could not load the following classes:
 *  com.ning.compress.BufferRecycler
 *  com.ning.compress.lzf.ChunkEncoder
 *  com.ning.compress.lzf.LZFEncoder
 *  com.ning.compress.lzf.util.ChunkEncoderFactory
 */
package io.netty.handler.codec.compression;

import com.ning.compress.BufferRecycler;
import com.ning.compress.lzf.ChunkEncoder;
import com.ning.compress.lzf.LZFEncoder;
import com.ning.compress.lzf.util.ChunkEncoderFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LzfEncoder
extends MessageToByteEncoder<ByteBuf> {
    private static final int MIN_BLOCK_TO_COMPRESS = 16;
    private final ChunkEncoder encoder;
    private final BufferRecycler recycler;

    public LzfEncoder() {
        this(false, 65535);
    }

    public LzfEncoder(boolean safeInstance) {
        this(safeInstance, 65535);
    }

    public LzfEncoder(int totalLength) {
        this(false, totalLength);
    }

    public LzfEncoder(boolean safeInstance, int totalLength) {
        super(false);
        if (totalLength < 16 || totalLength > 65535) {
            throw new IllegalArgumentException("totalLength: " + totalLength + " (expected: " + 16 + '-' + 65535 + ')');
        }
        this.encoder = safeInstance ? ChunkEncoderFactory.safeNonAllocatingInstance((int)totalLength) : ChunkEncoderFactory.optimalNonAllocatingInstance((int)totalLength);
        this.recycler = BufferRecycler.instance();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in2, ByteBuf out) throws Exception {
        int inputPtr;
        byte[] input;
        int length = in2.readableBytes();
        int idx = in2.readerIndex();
        if (in2.hasArray()) {
            input = in2.array();
            inputPtr = in2.arrayOffset() + idx;
        } else {
            input = this.recycler.allocInputBuffer(length);
            in2.getBytes(idx, input, 0, length);
            inputPtr = 0;
        }
        int maxOutputLength = LZFEncoder.estimateMaxWorkspaceSize((int)length);
        out.ensureWritable(maxOutputLength);
        byte[] output = out.array();
        int outputPtr = out.arrayOffset() + out.writerIndex();
        int outputLength = LZFEncoder.appendEncoded((ChunkEncoder)this.encoder, (byte[])input, (int)inputPtr, (int)length, (byte[])output, (int)outputPtr) - outputPtr;
        out.writerIndex(out.writerIndex() + outputLength);
        in2.skipBytes(length);
        if (!in2.hasArray()) {
            this.recycler.releaseInputBuffer(input);
        }
    }
}

