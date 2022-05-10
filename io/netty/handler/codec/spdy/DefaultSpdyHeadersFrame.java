package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeaders;
import io.netty.handler.codec.spdy.DefaultSpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.util.internal.StringUtil;
import java.util.Map;

public class DefaultSpdyHeadersFrame
extends DefaultSpdyStreamFrame
implements SpdyHeadersFrame {
    private boolean invalid;
    private boolean truncated;
    private final SpdyHeaders headers;

    public DefaultSpdyHeadersFrame(int streamId) {
        this(streamId, true);
    }

    public DefaultSpdyHeadersFrame(int streamId, boolean validate) {
        super(streamId);
        this.headers = new DefaultSpdyHeaders(validate);
    }

    @Override
    public SpdyHeadersFrame setStreamId(int streamId) {
        super.setStreamId(streamId);
        return this;
    }

    @Override
    public SpdyHeadersFrame setLast(boolean last) {
        super.setLast(last);
        return this;
    }

    @Override
    public boolean isInvalid() {
        return this.invalid;
    }

    @Override
    public SpdyHeadersFrame setInvalid() {
        this.invalid = true;
        return this;
    }

    @Override
    public boolean isTruncated() {
        return this.truncated;
    }

    @Override
    public SpdyHeadersFrame setTruncated() {
        this.truncated = true;
        return this;
    }

    @Override
    public SpdyHeaders headers() {
        return this.headers;
    }

    public String toString() {
        StringBuilder buf2 = new StringBuilder().append(StringUtil.simpleClassName(this)).append("(last: ").append(this.isLast()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(this.streamId()).append(StringUtil.NEWLINE).append("--> Headers:").append(StringUtil.NEWLINE);
        this.appendHeaders(buf2);
        buf2.setLength(buf2.length() - StringUtil.NEWLINE.length());
        return buf2.toString();
    }

    protected void appendHeaders(StringBuilder buf2) {
        for (Map.Entry e2 : this.headers()) {
            buf2.append("    ");
            buf2.append((CharSequence)e2.getKey());
            buf2.append(": ");
            buf2.append((CharSequence)e2.getValue());
            buf2.append(StringUtil.NEWLINE);
        }
    }
}

