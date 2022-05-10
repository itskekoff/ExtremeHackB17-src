package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.memcache.AbstractMemcacheObjectDecoder;
import io.netty.handler.codec.memcache.DefaultLastMemcacheContent;
import io.netty.handler.codec.memcache.DefaultMemcacheContent;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheContent;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;
import java.util.List;

public abstract class AbstractBinaryMemcacheDecoder<M extends BinaryMemcacheMessage>
extends AbstractMemcacheObjectDecoder {
    public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
    private final int chunkSize;
    private M currentMessage;
    private int alreadyReadChunkSize;
    private State state = State.READ_HEADER;

    protected AbstractBinaryMemcacheDecoder() {
        this(8192);
    }

    protected AbstractBinaryMemcacheDecoder(int chunkSize) {
        if (chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize must be a positive integer: " + chunkSize);
        }
        this.chunkSize = chunkSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        switch (this.state) {
            case READ_HEADER: {
                try {
                    if (in2.readableBytes() < 24) {
                        return;
                    }
                    this.resetDecoder();
                    this.currentMessage = this.decodeHeader(in2);
                    this.state = State.READ_EXTRAS;
                }
                catch (Exception e2) {
                    this.resetDecoder();
                    out.add(this.invalidMessage(e2));
                    return;
                }
            }
            case READ_EXTRAS: {
                try {
                    byte extrasLength = this.currentMessage.extrasLength();
                    if (extrasLength > 0) {
                        if (in2.readableBytes() < extrasLength) {
                            return;
                        }
                        this.currentMessage.setExtras(in2.readRetainedSlice(extrasLength));
                    }
                    this.state = State.READ_KEY;
                }
                catch (Exception e3) {
                    this.resetDecoder();
                    out.add(this.invalidMessage(e3));
                    return;
                }
            }
            case READ_KEY: {
                try {
                    short keyLength = this.currentMessage.keyLength();
                    if (keyLength > 0) {
                        if (in2.readableBytes() < keyLength) {
                            return;
                        }
                        this.currentMessage.setKey(in2.readRetainedSlice(keyLength));
                    }
                    out.add(this.currentMessage.retain());
                    this.state = State.READ_CONTENT;
                }
                catch (Exception e4) {
                    this.resetDecoder();
                    out.add(this.invalidMessage(e4));
                    return;
                }
            }
            case READ_CONTENT: {
                try {
                    int valueLength = this.currentMessage.totalBodyLength() - this.currentMessage.keyLength() - this.currentMessage.extrasLength();
                    int toRead = in2.readableBytes();
                    if (valueLength > 0) {
                        int remainingLength;
                        if (toRead == 0) {
                            return;
                        }
                        if (toRead > this.chunkSize) {
                            toRead = this.chunkSize;
                        }
                        if (toRead > (remainingLength = valueLength - this.alreadyReadChunkSize)) {
                            toRead = remainingLength;
                        }
                        ByteBuf chunkBuffer = in2.readRetainedSlice(toRead);
                        DefaultMemcacheContent chunk = (this.alreadyReadChunkSize += toRead) >= valueLength ? new DefaultLastMemcacheContent(chunkBuffer) : new DefaultMemcacheContent(chunkBuffer);
                        out.add(chunk);
                        if (this.alreadyReadChunkSize < valueLength) {
                            return;
                        }
                    } else {
                        out.add(LastMemcacheContent.EMPTY_LAST_CONTENT);
                    }
                    this.resetDecoder();
                    this.state = State.READ_HEADER;
                    return;
                }
                catch (Exception e5) {
                    this.resetDecoder();
                    out.add(this.invalidChunk(e5));
                    return;
                }
            }
            case BAD_MESSAGE: {
                in2.skipBytes(this.actualReadableBytes());
                return;
            }
        }
        throw new Error("Unknown state reached: " + (Object)((Object)this.state));
    }

    private M invalidMessage(Exception cause) {
        this.state = State.BAD_MESSAGE;
        M message = this.buildInvalidMessage();
        message.setDecoderResult(DecoderResult.failure(cause));
        return message;
    }

    private MemcacheContent invalidChunk(Exception cause) {
        this.state = State.BAD_MESSAGE;
        DefaultLastMemcacheContent chunk = new DefaultLastMemcacheContent(Unpooled.EMPTY_BUFFER);
        chunk.setDecoderResult(DecoderResult.failure(cause));
        return chunk;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.resetDecoder();
    }

    protected void resetDecoder() {
        if (this.currentMessage != null) {
            this.currentMessage.release();
            this.currentMessage = null;
        }
        this.alreadyReadChunkSize = 0;
    }

    protected abstract M decodeHeader(ByteBuf var1);

    protected abstract M buildInvalidMessage();

    static enum State {
        READ_HEADER,
        READ_EXTRAS,
        READ_KEY,
        READ_CONTENT,
        BAD_MESSAGE;

    }
}

