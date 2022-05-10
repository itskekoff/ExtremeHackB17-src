package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.util.internal.ObjectUtil;

public abstract class AbstractHttp2StreamFrame
implements Http2StreamFrame {
    private volatile int streamId = -1;

    @Override
    public AbstractHttp2StreamFrame streamId(int streamId) {
        if (this.streamId != -1) {
            throw new IllegalStateException("Stream identifier may only be set once.");
        }
        this.streamId = ObjectUtil.checkPositiveOrZero(streamId, "streamId");
        return this;
    }

    @Override
    public int streamId() {
        return this.streamId;
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof Http2StreamFrame)) {
            return false;
        }
        Http2StreamFrame other = (Http2StreamFrame)o2;
        return this.streamId == other.streamId();
    }

    public int hashCode() {
        return this.streamId;
    }
}

