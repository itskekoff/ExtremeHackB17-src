package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

public class HttpRequestEncoder
extends HttpObjectEncoder<HttpRequest> {
    private static final char SLASH = '/';
    private static final char QUESTION_MARK = '?';

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) && !(msg instanceof HttpResponse);
    }

    @Override
    protected void encodeInitialLine(ByteBuf buf2, HttpRequest request) throws Exception {
        AsciiString method = request.method().asciiName();
        ByteBufUtil.copy(method, method.arrayOffset(), buf2, method.length());
        buf2.writeByte(32);
        String uri = request.uri();
        if (uri.isEmpty()) {
            uri = uri + '/';
        } else {
            int start = uri.indexOf("://");
            if (start != -1 && uri.charAt(0) != '/') {
                int startIndex = start + 3;
                int index = uri.indexOf(63, startIndex);
                if (index == -1) {
                    if (uri.lastIndexOf(47) <= startIndex) {
                        uri = uri + '/';
                    }
                } else if (uri.lastIndexOf(47, index) <= startIndex) {
                    int len = uri.length();
                    StringBuilder sb2 = new StringBuilder(len + 1);
                    sb2.append(uri, 0, index).append('/').append(uri, index, len);
                    uri = sb2.toString();
                }
            }
        }
        buf2.writeBytes(uri.getBytes(CharsetUtil.UTF_8));
        buf2.writeByte(32);
        request.protocolVersion().encode(buf2);
        buf2.writeBytes(CRLF);
    }
}

