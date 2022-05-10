package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.ComposedLastHttpContent;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpMessage;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public abstract class HttpContentDecoder
extends MessageToMessageDecoder<HttpObject> {
    static final String IDENTITY = HttpHeaderValues.IDENTITY.toString();
    protected ChannelHandlerContext ctx;
    private EmbeddedChannel decoder;
    private boolean continueResponse;

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        if (msg instanceof HttpResponse && ((HttpResponse)msg).status().code() == 100) {
            if (!(msg instanceof LastHttpContent)) {
                this.continueResponse = true;
            }
            out.add(ReferenceCountUtil.retain(msg));
            return;
        }
        if (this.continueResponse) {
            if (msg instanceof LastHttpContent) {
                this.continueResponse = false;
            }
            out.add(ReferenceCountUtil.retain(msg));
            return;
        }
        if (msg instanceof HttpMessage) {
            String targetContentEncoding;
            this.cleanup();
            HttpMessage message = (HttpMessage)msg;
            HttpHeaders headers = message.headers();
            String contentEncoding = headers.get(HttpHeaderNames.CONTENT_ENCODING);
            contentEncoding = contentEncoding != null ? contentEncoding.trim() : IDENTITY;
            this.decoder = this.newContentDecoder(contentEncoding);
            if (this.decoder == null) {
                if (message instanceof HttpContent) {
                    ((HttpContent)((Object)message)).retain();
                }
                out.add(message);
                return;
            }
            if (headers.contains(HttpHeaderNames.CONTENT_LENGTH)) {
                headers.remove(HttpHeaderNames.CONTENT_LENGTH);
                headers.set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
            }
            if (HttpHeaderValues.IDENTITY.contentEquals(targetContentEncoding = this.getTargetContentEncoding(contentEncoding))) {
                headers.remove(HttpHeaderNames.CONTENT_ENCODING);
            } else {
                headers.set((CharSequence)HttpHeaderNames.CONTENT_ENCODING, (Object)targetContentEncoding);
            }
            if (message instanceof HttpContent) {
                DefaultHttpMessage copy;
                if (message instanceof HttpRequest) {
                    HttpRequest r2 = (HttpRequest)message;
                    copy = new DefaultHttpRequest(r2.protocolVersion(), r2.method(), r2.uri());
                } else if (message instanceof HttpResponse) {
                    HttpResponse r3 = (HttpResponse)message;
                    copy = new DefaultHttpResponse(r3.protocolVersion(), r3.status());
                } else {
                    throw new CodecException("Object of class " + message.getClass().getName() + " is not a HttpRequest or HttpResponse");
                }
                copy.headers().set(message.headers());
                copy.setDecoderResult(message.decoderResult());
                out.add(copy);
            } else {
                out.add(message);
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent c2 = (HttpContent)msg;
            if (this.decoder == null) {
                out.add(c2.retain());
            } else {
                this.decodeContent(c2, out);
            }
        }
    }

    private void decodeContent(HttpContent c2, List<Object> out) {
        ByteBuf content = c2.content();
        this.decode(content, out);
        if (c2 instanceof LastHttpContent) {
            this.finishDecode(out);
            LastHttpContent last = (LastHttpContent)c2;
            HttpHeaders headers = last.trailingHeaders();
            if (headers.isEmpty()) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                out.add(new ComposedLastHttpContent(headers));
            }
        }
    }

    protected abstract EmbeddedChannel newContentDecoder(String var1) throws Exception;

    protected String getTargetContentEncoding(String contentEncoding) throws Exception {
        return IDENTITY;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.channelInactive(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.handlerAdded(ctx);
    }

    private void cleanup() {
        if (this.decoder != null) {
            if (this.decoder.finish()) {
                ByteBuf buf2;
                while ((buf2 = (ByteBuf)this.decoder.readInbound()) != null) {
                    buf2.release();
                }
            }
            this.decoder = null;
        }
    }

    private void decode(ByteBuf in2, List<Object> out) {
        this.decoder.writeInbound(in2.retain());
        this.fetchDecoderOutput(out);
    }

    private void finishDecode(List<Object> out) {
        if (this.decoder.finish()) {
            this.fetchDecoderOutput(out);
        }
        this.decoder = null;
    }

    private void fetchDecoderOutput(List<Object> out) {
        ByteBuf buf2;
        while ((buf2 = (ByteBuf)this.decoder.readInbound()) != null) {
            if (!buf2.isReadable()) {
                buf2.release();
                continue;
            }
            out.add(new DefaultHttpContent(buf2));
        }
    }
}

