package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.StringUtil;
import java.util.Map;

public class DefaultLastHttpContent
extends DefaultHttpContent
implements LastHttpContent {
    private final HttpHeaders trailingHeaders;
    private final boolean validateHeaders;

    public DefaultLastHttpContent() {
        this(Unpooled.buffer(0));
    }

    public DefaultLastHttpContent(ByteBuf content) {
        this(content, true);
    }

    public DefaultLastHttpContent(ByteBuf content, boolean validateHeaders) {
        super(content);
        this.trailingHeaders = new TrailingHttpHeaders(validateHeaders);
        this.validateHeaders = validateHeaders;
    }

    @Override
    public LastHttpContent copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public LastHttpContent duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public LastHttpContent retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public LastHttpContent replace(ByteBuf content) {
        DefaultLastHttpContent dup = new DefaultLastHttpContent(content, this.validateHeaders);
        dup.trailingHeaders().set(this.trailingHeaders());
        return dup;
    }

    @Override
    public LastHttpContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public LastHttpContent retain() {
        super.retain();
        return this;
    }

    @Override
    public LastHttpContent touch() {
        super.touch();
        return this;
    }

    @Override
    public LastHttpContent touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    @Override
    public String toString() {
        StringBuilder buf2 = new StringBuilder(super.toString());
        buf2.append(StringUtil.NEWLINE);
        this.appendHeaders(buf2);
        buf2.setLength(buf2.length() - StringUtil.NEWLINE.length());
        return buf2.toString();
    }

    private void appendHeaders(StringBuilder buf2) {
        for (Map.Entry<String, String> e2 : this.trailingHeaders()) {
            buf2.append(e2.getKey());
            buf2.append(": ");
            buf2.append(e2.getValue());
            buf2.append(StringUtil.NEWLINE);
        }
    }

    private static final class TrailingHttpHeaders
    extends DefaultHttpHeaders {
        private static final DefaultHeaders.NameValidator<CharSequence> TrailerNameValidator = new DefaultHeaders.NameValidator<CharSequence>(){

            @Override
            public void validateName(CharSequence name) {
                DefaultHttpHeaders.HttpNameValidator.validateName(name);
                if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(name) || HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(name) || HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(name)) {
                    throw new IllegalArgumentException("prohibited trailing header: " + name);
                }
            }
        };

        TrailingHttpHeaders(boolean validate) {
            super(validate, validate ? TrailerNameValidator : DefaultHeaders.NameValidator.NOT_NULL);
        }
    }
}

