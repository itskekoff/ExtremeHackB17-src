package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostStandardRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpPostRequestDecoder;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;
import java.util.List;

public class HttpPostRequestDecoder
implements InterfaceHttpPostRequestDecoder {
    static final int DEFAULT_DISCARD_THRESHOLD = 0xA00000;
    private final InterfaceHttpPostRequestDecoder decoder;

    public HttpPostRequestDecoder(HttpRequest request) {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request) {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        if (request == null) {
            throw new NullPointerException("request");
        }
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.decoder = HttpPostRequestDecoder.isMultipart(request) ? new HttpPostMultipartRequestDecoder(factory, request, charset) : new HttpPostStandardRequestDecoder(factory, request, charset);
    }

    public static boolean isMultipart(HttpRequest request) {
        if (request.headers().contains(HttpHeaderNames.CONTENT_TYPE)) {
            return HttpPostRequestDecoder.getMultipartDataBoundary(request.headers().get(HttpHeaderNames.CONTENT_TYPE)) != null;
        }
        return false;
    }

    protected static String[] getMultipartDataBoundary(String contentType) {
        String[] headerContentType = HttpPostRequestDecoder.splitHeaderContentType(contentType);
        if (headerContentType[0].toLowerCase().startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString())) {
            String charset;
            int index;
            String bound;
            int crank;
            int mrank;
            if (headerContentType[1].toLowerCase().startsWith(HttpHeaderValues.BOUNDARY.toString())) {
                mrank = 1;
                crank = 2;
            } else if (headerContentType[2].toLowerCase().startsWith(HttpHeaderValues.BOUNDARY.toString())) {
                mrank = 2;
                crank = 1;
            } else {
                return null;
            }
            String boundary = StringUtil.substringAfter(headerContentType[mrank], '=');
            if (boundary == null) {
                throw new ErrorDataDecoderException("Needs a boundary value");
            }
            if (boundary.charAt(0) == '\"' && (bound = boundary.trim()).charAt(index = bound.length() - 1) == '\"') {
                boundary = bound.substring(1, index);
            }
            if (headerContentType[crank].toLowerCase().startsWith(HttpHeaderValues.CHARSET.toString()) && (charset = StringUtil.substringAfter(headerContentType[crank], '=')) != null) {
                return new String[]{"--" + boundary, charset};
            }
            return new String[]{"--" + boundary};
        }
        return null;
    }

    @Override
    public boolean isMultipart() {
        return this.decoder.isMultipart();
    }

    @Override
    public void setDiscardThreshold(int discardThreshold) {
        this.decoder.setDiscardThreshold(discardThreshold);
    }

    @Override
    public int getDiscardThreshold() {
        return this.decoder.getDiscardThreshold();
    }

    @Override
    public List<InterfaceHttpData> getBodyHttpDatas() {
        return this.decoder.getBodyHttpDatas();
    }

    @Override
    public List<InterfaceHttpData> getBodyHttpDatas(String name) {
        return this.decoder.getBodyHttpDatas(name);
    }

    @Override
    public InterfaceHttpData getBodyHttpData(String name) {
        return this.decoder.getBodyHttpData(name);
    }

    @Override
    public InterfaceHttpPostRequestDecoder offer(HttpContent content) {
        return this.decoder.offer(content);
    }

    @Override
    public boolean hasNext() {
        return this.decoder.hasNext();
    }

    @Override
    public InterfaceHttpData next() {
        return this.decoder.next();
    }

    @Override
    public InterfaceHttpData currentPartialHttpData() {
        return this.decoder.currentPartialHttpData();
    }

    @Override
    public void destroy() {
        this.decoder.destroy();
    }

    @Override
    public void cleanFiles() {
        this.decoder.cleanFiles();
    }

    @Override
    public void removeHttpDataFromClean(InterfaceHttpData data) {
        this.decoder.removeHttpDataFromClean(data);
    }

    private static String[] splitHeaderContentType(String sb2) {
        int bEnd;
        int aStart = HttpPostBodyUtil.findNonWhitespace(sb2, 0);
        int aEnd = sb2.indexOf(59);
        if (aEnd == -1) {
            return new String[]{sb2, "", ""};
        }
        int bStart = HttpPostBodyUtil.findNonWhitespace(sb2, aEnd + 1);
        if (sb2.charAt(aEnd - 1) == ' ') {
            --aEnd;
        }
        if ((bEnd = sb2.indexOf(59, bStart)) == -1) {
            bEnd = HttpPostBodyUtil.findEndOfString(sb2);
            return new String[]{sb2.substring(aStart, aEnd), sb2.substring(bStart, bEnd), ""};
        }
        int cStart = HttpPostBodyUtil.findNonWhitespace(sb2, bEnd + 1);
        if (sb2.charAt(bEnd - 1) == ' ') {
            --bEnd;
        }
        int cEnd = HttpPostBodyUtil.findEndOfString(sb2);
        return new String[]{sb2.substring(aStart, aEnd), sb2.substring(bStart, bEnd), sb2.substring(cStart, cEnd)};
    }

    public static class ErrorDataDecoderException
    extends DecoderException {
        private static final long serialVersionUID = 5020247425493164465L;

        public ErrorDataDecoderException() {
        }

        public ErrorDataDecoderException(String msg) {
            super(msg);
        }

        public ErrorDataDecoderException(Throwable cause) {
            super(cause);
        }

        public ErrorDataDecoderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class EndOfDataDecoderException
    extends DecoderException {
        private static final long serialVersionUID = 1336267941020800769L;
    }

    public static class NotEnoughDataDecoderException
    extends DecoderException {
        private static final long serialVersionUID = -7846841864603865638L;

        public NotEnoughDataDecoderException() {
        }

        public NotEnoughDataDecoderException(String msg) {
            super(msg);
        }

        public NotEnoughDataDecoderException(Throwable cause) {
            super(cause);
        }

        public NotEnoughDataDecoderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    protected static enum MultiPartStatus {
        NOTSTARTED,
        PREAMBLE,
        HEADERDELIMITER,
        DISPOSITION,
        FIELD,
        FILEUPLOAD,
        MIXEDPREAMBLE,
        MIXEDDELIMITER,
        MIXEDDISPOSITION,
        MIXEDFILEUPLOAD,
        MIXEDCLOSEDELIMITER,
        CLOSEDELIMITER,
        PREEPILOGUE,
        EPILOGUE;

    }
}

