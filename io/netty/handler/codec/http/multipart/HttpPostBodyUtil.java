package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;

final class HttpPostBodyUtil {
    public static final int chunkSize = 8096;
    public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";

    private HttpPostBodyUtil() {
    }

    static int findNonWhitespace(String sb2, int offset) {
        int result;
        for (result = offset; result < sb2.length() && Character.isWhitespace(sb2.charAt(result)); ++result) {
        }
        return result;
    }

    static int findWhitespace(String sb2, int offset) {
        int result;
        for (result = offset; result < sb2.length() && !Character.isWhitespace(sb2.charAt(result)); ++result) {
        }
        return result;
    }

    static int findEndOfString(String sb2) {
        int result;
        for (result = sb2.length(); result > 0 && Character.isWhitespace(sb2.charAt(result - 1)); --result) {
        }
        return result;
    }

    static class SeekAheadOptimize {
        byte[] bytes;
        int readerIndex;
        int pos;
        int origPos;
        int limit;
        ByteBuf buffer;

        SeekAheadOptimize(ByteBuf buffer) throws SeekAheadNoBackArrayException {
            if (!buffer.hasArray()) {
                throw new SeekAheadNoBackArrayException();
            }
            this.buffer = buffer;
            this.bytes = buffer.array();
            this.readerIndex = buffer.readerIndex();
            this.origPos = this.pos = buffer.arrayOffset() + this.readerIndex;
            this.limit = buffer.arrayOffset() + buffer.writerIndex();
        }

        void setReadPosition(int minus) {
            this.pos -= minus;
            this.readerIndex = this.getReadPosition(this.pos);
            this.buffer.readerIndex(this.readerIndex);
        }

        int getReadPosition(int index) {
            return index - this.origPos + this.readerIndex;
        }

        void clear() {
            this.buffer = null;
            this.bytes = null;
            this.limit = 0;
            this.pos = 0;
            this.readerIndex = 0;
        }
    }

    static class SeekAheadNoBackArrayException
    extends Exception {
        private static final long serialVersionUID = -630418804938699495L;

        SeekAheadNoBackArrayException() {
        }
    }

    public static enum TransferEncodingMechanism {
        BIT7("7bit"),
        BIT8("8bit"),
        BINARY("binary");

        private final String value;

        private TransferEncodingMechanism(String value) {
            this.value = value;
        }

        private TransferEncodingMechanism() {
            this.value = this.name();
        }

        public String value() {
            return this.value;
        }

        public String toString() {
            return this.value;
        }
    }
}

