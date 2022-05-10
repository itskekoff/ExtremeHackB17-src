package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;

public class HttpResponseStatus
implements Comparable<HttpResponseStatus> {
    public static final HttpResponseStatus CONTINUE = HttpResponseStatus.newStatus(100, "Continue");
    public static final HttpResponseStatus SWITCHING_PROTOCOLS = HttpResponseStatus.newStatus(101, "Switching Protocols");
    public static final HttpResponseStatus PROCESSING = HttpResponseStatus.newStatus(102, "Processing");
    public static final HttpResponseStatus OK = HttpResponseStatus.newStatus(200, "OK");
    public static final HttpResponseStatus CREATED = HttpResponseStatus.newStatus(201, "Created");
    public static final HttpResponseStatus ACCEPTED = HttpResponseStatus.newStatus(202, "Accepted");
    public static final HttpResponseStatus NON_AUTHORITATIVE_INFORMATION = HttpResponseStatus.newStatus(203, "Non-Authoritative Information");
    public static final HttpResponseStatus NO_CONTENT = HttpResponseStatus.newStatus(204, "No Content");
    public static final HttpResponseStatus RESET_CONTENT = HttpResponseStatus.newStatus(205, "Reset Content");
    public static final HttpResponseStatus PARTIAL_CONTENT = HttpResponseStatus.newStatus(206, "Partial Content");
    public static final HttpResponseStatus MULTI_STATUS = HttpResponseStatus.newStatus(207, "Multi-Status");
    public static final HttpResponseStatus MULTIPLE_CHOICES = HttpResponseStatus.newStatus(300, "Multiple Choices");
    public static final HttpResponseStatus MOVED_PERMANENTLY = HttpResponseStatus.newStatus(301, "Moved Permanently");
    public static final HttpResponseStatus FOUND = HttpResponseStatus.newStatus(302, "Found");
    public static final HttpResponseStatus SEE_OTHER = HttpResponseStatus.newStatus(303, "See Other");
    public static final HttpResponseStatus NOT_MODIFIED = HttpResponseStatus.newStatus(304, "Not Modified");
    public static final HttpResponseStatus USE_PROXY = HttpResponseStatus.newStatus(305, "Use Proxy");
    public static final HttpResponseStatus TEMPORARY_REDIRECT = HttpResponseStatus.newStatus(307, "Temporary Redirect");
    public static final HttpResponseStatus BAD_REQUEST = HttpResponseStatus.newStatus(400, "Bad Request");
    public static final HttpResponseStatus UNAUTHORIZED = HttpResponseStatus.newStatus(401, "Unauthorized");
    public static final HttpResponseStatus PAYMENT_REQUIRED = HttpResponseStatus.newStatus(402, "Payment Required");
    public static final HttpResponseStatus FORBIDDEN = HttpResponseStatus.newStatus(403, "Forbidden");
    public static final HttpResponseStatus NOT_FOUND = HttpResponseStatus.newStatus(404, "Not Found");
    public static final HttpResponseStatus METHOD_NOT_ALLOWED = HttpResponseStatus.newStatus(405, "Method Not Allowed");
    public static final HttpResponseStatus NOT_ACCEPTABLE = HttpResponseStatus.newStatus(406, "Not Acceptable");
    public static final HttpResponseStatus PROXY_AUTHENTICATION_REQUIRED = HttpResponseStatus.newStatus(407, "Proxy Authentication Required");
    public static final HttpResponseStatus REQUEST_TIMEOUT = HttpResponseStatus.newStatus(408, "Request Timeout");
    public static final HttpResponseStatus CONFLICT = HttpResponseStatus.newStatus(409, "Conflict");
    public static final HttpResponseStatus GONE = HttpResponseStatus.newStatus(410, "Gone");
    public static final HttpResponseStatus LENGTH_REQUIRED = HttpResponseStatus.newStatus(411, "Length Required");
    public static final HttpResponseStatus PRECONDITION_FAILED = HttpResponseStatus.newStatus(412, "Precondition Failed");
    public static final HttpResponseStatus REQUEST_ENTITY_TOO_LARGE = HttpResponseStatus.newStatus(413, "Request Entity Too Large");
    public static final HttpResponseStatus REQUEST_URI_TOO_LONG = HttpResponseStatus.newStatus(414, "Request-URI Too Long");
    public static final HttpResponseStatus UNSUPPORTED_MEDIA_TYPE = HttpResponseStatus.newStatus(415, "Unsupported Media Type");
    public static final HttpResponseStatus REQUESTED_RANGE_NOT_SATISFIABLE = HttpResponseStatus.newStatus(416, "Requested Range Not Satisfiable");
    public static final HttpResponseStatus EXPECTATION_FAILED = HttpResponseStatus.newStatus(417, "Expectation Failed");
    public static final HttpResponseStatus MISDIRECTED_REQUEST = HttpResponseStatus.newStatus(421, "Misdirected Request");
    public static final HttpResponseStatus UNPROCESSABLE_ENTITY = HttpResponseStatus.newStatus(422, "Unprocessable Entity");
    public static final HttpResponseStatus LOCKED = HttpResponseStatus.newStatus(423, "Locked");
    public static final HttpResponseStatus FAILED_DEPENDENCY = HttpResponseStatus.newStatus(424, "Failed Dependency");
    public static final HttpResponseStatus UNORDERED_COLLECTION = HttpResponseStatus.newStatus(425, "Unordered Collection");
    public static final HttpResponseStatus UPGRADE_REQUIRED = HttpResponseStatus.newStatus(426, "Upgrade Required");
    public static final HttpResponseStatus PRECONDITION_REQUIRED = HttpResponseStatus.newStatus(428, "Precondition Required");
    public static final HttpResponseStatus TOO_MANY_REQUESTS = HttpResponseStatus.newStatus(429, "Too Many Requests");
    public static final HttpResponseStatus REQUEST_HEADER_FIELDS_TOO_LARGE = HttpResponseStatus.newStatus(431, "Request Header Fields Too Large");
    public static final HttpResponseStatus INTERNAL_SERVER_ERROR = HttpResponseStatus.newStatus(500, "Internal Server Error");
    public static final HttpResponseStatus NOT_IMPLEMENTED = HttpResponseStatus.newStatus(501, "Not Implemented");
    public static final HttpResponseStatus BAD_GATEWAY = HttpResponseStatus.newStatus(502, "Bad Gateway");
    public static final HttpResponseStatus SERVICE_UNAVAILABLE = HttpResponseStatus.newStatus(503, "Service Unavailable");
    public static final HttpResponseStatus GATEWAY_TIMEOUT = HttpResponseStatus.newStatus(504, "Gateway Timeout");
    public static final HttpResponseStatus HTTP_VERSION_NOT_SUPPORTED = HttpResponseStatus.newStatus(505, "HTTP Version Not Supported");
    public static final HttpResponseStatus VARIANT_ALSO_NEGOTIATES = HttpResponseStatus.newStatus(506, "Variant Also Negotiates");
    public static final HttpResponseStatus INSUFFICIENT_STORAGE = HttpResponseStatus.newStatus(507, "Insufficient Storage");
    public static final HttpResponseStatus NOT_EXTENDED = HttpResponseStatus.newStatus(510, "Not Extended");
    public static final HttpResponseStatus NETWORK_AUTHENTICATION_REQUIRED = HttpResponseStatus.newStatus(511, "Network Authentication Required");
    private final int code;
    private final AsciiString codeAsText;
    private HttpStatusClass codeClass;
    private final String reasonPhrase;
    private final byte[] bytes;

    private static HttpResponseStatus newStatus(int statusCode, String reasonPhrase) {
        return new HttpResponseStatus(statusCode, reasonPhrase, true);
    }

    public static HttpResponseStatus valueOf(int code) {
        switch (code) {
            case 100: {
                return CONTINUE;
            }
            case 101: {
                return SWITCHING_PROTOCOLS;
            }
            case 102: {
                return PROCESSING;
            }
            case 200: {
                return OK;
            }
            case 201: {
                return CREATED;
            }
            case 202: {
                return ACCEPTED;
            }
            case 203: {
                return NON_AUTHORITATIVE_INFORMATION;
            }
            case 204: {
                return NO_CONTENT;
            }
            case 205: {
                return RESET_CONTENT;
            }
            case 206: {
                return PARTIAL_CONTENT;
            }
            case 207: {
                return MULTI_STATUS;
            }
            case 300: {
                return MULTIPLE_CHOICES;
            }
            case 301: {
                return MOVED_PERMANENTLY;
            }
            case 302: {
                return FOUND;
            }
            case 303: {
                return SEE_OTHER;
            }
            case 304: {
                return NOT_MODIFIED;
            }
            case 305: {
                return USE_PROXY;
            }
            case 307: {
                return TEMPORARY_REDIRECT;
            }
            case 400: {
                return BAD_REQUEST;
            }
            case 401: {
                return UNAUTHORIZED;
            }
            case 402: {
                return PAYMENT_REQUIRED;
            }
            case 403: {
                return FORBIDDEN;
            }
            case 404: {
                return NOT_FOUND;
            }
            case 405: {
                return METHOD_NOT_ALLOWED;
            }
            case 406: {
                return NOT_ACCEPTABLE;
            }
            case 407: {
                return PROXY_AUTHENTICATION_REQUIRED;
            }
            case 408: {
                return REQUEST_TIMEOUT;
            }
            case 409: {
                return CONFLICT;
            }
            case 410: {
                return GONE;
            }
            case 411: {
                return LENGTH_REQUIRED;
            }
            case 412: {
                return PRECONDITION_FAILED;
            }
            case 413: {
                return REQUEST_ENTITY_TOO_LARGE;
            }
            case 414: {
                return REQUEST_URI_TOO_LONG;
            }
            case 415: {
                return UNSUPPORTED_MEDIA_TYPE;
            }
            case 416: {
                return REQUESTED_RANGE_NOT_SATISFIABLE;
            }
            case 417: {
                return EXPECTATION_FAILED;
            }
            case 421: {
                return MISDIRECTED_REQUEST;
            }
            case 422: {
                return UNPROCESSABLE_ENTITY;
            }
            case 423: {
                return LOCKED;
            }
            case 424: {
                return FAILED_DEPENDENCY;
            }
            case 425: {
                return UNORDERED_COLLECTION;
            }
            case 426: {
                return UPGRADE_REQUIRED;
            }
            case 428: {
                return PRECONDITION_REQUIRED;
            }
            case 429: {
                return TOO_MANY_REQUESTS;
            }
            case 431: {
                return REQUEST_HEADER_FIELDS_TOO_LARGE;
            }
            case 500: {
                return INTERNAL_SERVER_ERROR;
            }
            case 501: {
                return NOT_IMPLEMENTED;
            }
            case 502: {
                return BAD_GATEWAY;
            }
            case 503: {
                return SERVICE_UNAVAILABLE;
            }
            case 504: {
                return GATEWAY_TIMEOUT;
            }
            case 505: {
                return HTTP_VERSION_NOT_SUPPORTED;
            }
            case 506: {
                return VARIANT_ALSO_NEGOTIATES;
            }
            case 507: {
                return INSUFFICIENT_STORAGE;
            }
            case 510: {
                return NOT_EXTENDED;
            }
            case 511: {
                return NETWORK_AUTHENTICATION_REQUIRED;
            }
        }
        return new HttpResponseStatus(code);
    }

    public static HttpResponseStatus parseLine(CharSequence line) {
        String status = line.toString();
        try {
            int space = status.indexOf(32);
            if (space == -1) {
                return HttpResponseStatus.valueOf(Integer.parseInt(status));
            }
            int code = Integer.parseInt(status.substring(0, space));
            String reasonPhrase = status.substring(space + 1);
            HttpResponseStatus responseStatus = HttpResponseStatus.valueOf(code);
            if (responseStatus.reasonPhrase().contentEquals(reasonPhrase)) {
                return responseStatus;
            }
            return new HttpResponseStatus(code, reasonPhrase);
        }
        catch (Exception e2) {
            throw new IllegalArgumentException("malformed status line: " + status, e2);
        }
    }

    public static HttpResponseStatus parseLine(AsciiString line) {
        try {
            HttpStatusLineProcessor processor = new HttpStatusLineProcessor(line);
            line.forEachByte(processor);
            HttpResponseStatus status = processor.status();
            if (status == null) {
                throw new IllegalArgumentException("unable to get status after parsing input");
            }
            return status;
        }
        catch (Exception e2) {
            throw new IllegalArgumentException("malformed status line: " + line, e2);
        }
    }

    private HttpResponseStatus(int code) {
        this(code, HttpStatusClass.valueOf(code).defaultReasonPhrase() + " (" + code + ')', false);
    }

    public HttpResponseStatus(int code, String reasonPhrase) {
        this(code, reasonPhrase, false);
    }

    private HttpResponseStatus(int code, String reasonPhrase, boolean bytes) {
        if (code < 0) {
            throw new IllegalArgumentException("code: " + code + " (expected: 0+)");
        }
        if (reasonPhrase == null) {
            throw new NullPointerException("reasonPhrase");
        }
        for (int i2 = 0; i2 < reasonPhrase.length(); ++i2) {
            char c2 = reasonPhrase.charAt(i2);
            switch (c2) {
                case '\n': 
                case '\r': {
                    throw new IllegalArgumentException("reasonPhrase contains one of the following prohibited characters: \\r\\n: " + reasonPhrase);
                }
            }
        }
        this.code = code;
        this.codeAsText = new AsciiString(Integer.toString(code));
        this.reasonPhrase = reasonPhrase;
        this.bytes = (byte[])(bytes ? (code + " " + reasonPhrase).getBytes(CharsetUtil.US_ASCII) : null);
    }

    public int code() {
        return this.code;
    }

    public AsciiString codeAsText() {
        return this.codeAsText;
    }

    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    public HttpStatusClass codeClass() {
        HttpStatusClass type = this.codeClass;
        if (type == null) {
            this.codeClass = type = HttpStatusClass.valueOf(this.code);
        }
        return type;
    }

    public int hashCode() {
        return this.code();
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof HttpResponseStatus)) {
            return false;
        }
        return this.code() == ((HttpResponseStatus)o2).code();
    }

    @Override
    public int compareTo(HttpResponseStatus o2) {
        return this.code() - o2.code();
    }

    public String toString() {
        return new StringBuilder(this.reasonPhrase.length() + 5).append(this.code).append(' ').append(this.reasonPhrase).toString();
    }

    void encode(ByteBuf buf2) {
        if (this.bytes == null) {
            HttpUtil.encodeAscii0(String.valueOf(this.code()), buf2);
            buf2.writeByte(32);
            HttpUtil.encodeAscii0(String.valueOf(this.reasonPhrase()), buf2);
        } else {
            buf2.writeBytes(this.bytes);
        }
    }

    private static final class HttpStatusLineProcessor
    implements ByteProcessor {
        private static final byte ASCII_SPACE = 32;
        private final AsciiString string;
        private int i;
        private int state;
        private HttpResponseStatus status;

        public HttpStatusLineProcessor(AsciiString string) {
            this.string = string;
        }

        @Override
        public boolean process(byte value) {
            switch (this.state) {
                case 0: {
                    if (value != 32) break;
                    this.state = 1;
                    break;
                }
                case 1: {
                    this.parseStatus(this.i);
                    this.state = 2;
                    return false;
                }
            }
            ++this.i;
            return true;
        }

        private void parseStatus(int codeEnd) {
            int code = this.string.parseInt(0, codeEnd);
            this.status = HttpResponseStatus.valueOf(code);
            if (codeEnd < this.string.length()) {
                String actualReason = this.string.toString(codeEnd + 1, this.string.length());
                if (!this.status.reasonPhrase().contentEquals(actualReason)) {
                    this.status = new HttpResponseStatus(code, actualReason);
                }
            }
        }

        public HttpResponseStatus status() {
            if (this.state <= 1) {
                this.parseStatus(this.string.length());
                this.state = 3;
            }
            return this.status;
        }
    }
}

