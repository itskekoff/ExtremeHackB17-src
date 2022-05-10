package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.AbstractHttp2StreamStateEvent;

public class Http2StreamClosedEvent
extends AbstractHttp2StreamStateEvent {
    public Http2StreamClosedEvent(int streamId) {
        super(streamId);
    }
}

