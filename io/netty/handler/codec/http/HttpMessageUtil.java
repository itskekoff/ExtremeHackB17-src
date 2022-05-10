package io.netty.handler.codec.http;

import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.internal.StringUtil;
import java.util.Map;

final class HttpMessageUtil {
    static StringBuilder appendRequest(StringBuilder buf2, HttpRequest req) {
        HttpMessageUtil.appendCommon(buf2, req);
        HttpMessageUtil.appendInitialLine(buf2, req);
        HttpMessageUtil.appendHeaders(buf2, req.headers());
        HttpMessageUtil.removeLastNewLine(buf2);
        return buf2;
    }

    static StringBuilder appendResponse(StringBuilder buf2, HttpResponse res) {
        HttpMessageUtil.appendCommon(buf2, res);
        HttpMessageUtil.appendInitialLine(buf2, res);
        HttpMessageUtil.appendHeaders(buf2, res.headers());
        HttpMessageUtil.removeLastNewLine(buf2);
        return buf2;
    }

    private static void appendCommon(StringBuilder buf2, HttpMessage msg) {
        buf2.append(StringUtil.simpleClassName(msg));
        buf2.append("(decodeResult: ");
        buf2.append(msg.decoderResult());
        buf2.append(", version: ");
        buf2.append(msg.protocolVersion());
        buf2.append(')');
        buf2.append(StringUtil.NEWLINE);
    }

    static StringBuilder appendFullRequest(StringBuilder buf2, FullHttpRequest req) {
        HttpMessageUtil.appendFullCommon(buf2, req);
        HttpMessageUtil.appendInitialLine(buf2, req);
        HttpMessageUtil.appendHeaders(buf2, req.headers());
        HttpMessageUtil.appendHeaders(buf2, req.trailingHeaders());
        HttpMessageUtil.removeLastNewLine(buf2);
        return buf2;
    }

    static StringBuilder appendFullResponse(StringBuilder buf2, FullHttpResponse res) {
        HttpMessageUtil.appendFullCommon(buf2, res);
        HttpMessageUtil.appendInitialLine(buf2, res);
        HttpMessageUtil.appendHeaders(buf2, res.headers());
        HttpMessageUtil.appendHeaders(buf2, res.trailingHeaders());
        HttpMessageUtil.removeLastNewLine(buf2);
        return buf2;
    }

    private static void appendFullCommon(StringBuilder buf2, FullHttpMessage msg) {
        buf2.append(StringUtil.simpleClassName(msg));
        buf2.append("(decodeResult: ");
        buf2.append(msg.decoderResult());
        buf2.append(", version: ");
        buf2.append(msg.protocolVersion());
        buf2.append(", content: ");
        buf2.append(msg.content());
        buf2.append(')');
        buf2.append(StringUtil.NEWLINE);
    }

    private static void appendInitialLine(StringBuilder buf2, HttpRequest req) {
        buf2.append(req.method());
        buf2.append(' ');
        buf2.append(req.uri());
        buf2.append(' ');
        buf2.append(req.protocolVersion());
        buf2.append(StringUtil.NEWLINE);
    }

    private static void appendInitialLine(StringBuilder buf2, HttpResponse res) {
        buf2.append(res.protocolVersion());
        buf2.append(' ');
        buf2.append(res.status());
        buf2.append(StringUtil.NEWLINE);
    }

    private static void appendHeaders(StringBuilder buf2, HttpHeaders headers) {
        for (Map.Entry<String, String> e2 : headers) {
            buf2.append(e2.getKey());
            buf2.append(": ");
            buf2.append(e2.getValue());
            buf2.append(StringUtil.NEWLINE);
        }
    }

    private static void removeLastNewLine(StringBuilder buf2) {
        buf2.setLength(buf2.length() - StringUtil.NEWLINE.length());
    }

    private HttpMessageUtil() {
    }
}

