package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Exception;

public interface Http2FrameSizePolicy {
    public void maxFrameSize(int var1) throws Http2Exception;

    public int maxFrameSize();
}

