package io.netty.handler.codec.http2;

public final class Http2ConnectionPrefaceWrittenEvent {
    static final Http2ConnectionPrefaceWrittenEvent INSTANCE = new Http2ConnectionPrefaceWrittenEvent();

    private Http2ConnectionPrefaceWrittenEvent() {
    }
}

