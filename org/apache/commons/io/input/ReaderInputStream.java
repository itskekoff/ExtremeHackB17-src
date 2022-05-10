package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class ReaderInputStream
extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final Reader reader;
    private final CharsetEncoder encoder;
    private final CharBuffer encoderIn;
    private final ByteBuffer encoderOut;
    private CoderResult lastCoderResult;
    private boolean endOfInput;

    public ReaderInputStream(Reader reader, CharsetEncoder encoder) {
        this(reader, encoder, 1024);
    }

    public ReaderInputStream(Reader reader, CharsetEncoder encoder, int bufferSize) {
        this.reader = reader;
        this.encoder = encoder;
        this.encoderIn = CharBuffer.allocate(bufferSize);
        this.encoderIn.flip();
        this.encoderOut = ByteBuffer.allocate(128);
        this.encoderOut.flip();
    }

    public ReaderInputStream(Reader reader, Charset charset, int bufferSize) {
        this(reader, charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE), bufferSize);
    }

    public ReaderInputStream(Reader reader, Charset charset) {
        this(reader, charset, 1024);
    }

    public ReaderInputStream(Reader reader, String charsetName, int bufferSize) {
        this(reader, Charset.forName(charsetName), bufferSize);
    }

    public ReaderInputStream(Reader reader, String charsetName) {
        this(reader, charsetName, 1024);
    }

    @Deprecated
    public ReaderInputStream(Reader reader) {
        this(reader, Charset.defaultCharset());
    }

    private void fillBuffer() throws IOException {
        if (!this.endOfInput && (this.lastCoderResult == null || this.lastCoderResult.isUnderflow())) {
            this.encoderIn.compact();
            int position = this.encoderIn.position();
            int c2 = this.reader.read(this.encoderIn.array(), position, this.encoderIn.remaining());
            if (c2 == -1) {
                this.endOfInput = true;
            } else {
                this.encoderIn.position(position + c2);
            }
            this.encoderIn.flip();
        }
        this.encoderOut.compact();
        this.lastCoderResult = this.encoder.encode(this.encoderIn, this.encoderOut, this.endOfInput);
        this.encoderOut.flip();
    }

    @Override
    public int read(byte[] b2, int off, int len) throws IOException {
        if (b2 == null) {
            throw new NullPointerException("Byte array must not be null");
        }
        if (len < 0 || off < 0 || off + len > b2.length) {
            throw new IndexOutOfBoundsException("Array Size=" + b2.length + ", offset=" + off + ", length=" + len);
        }
        int read = 0;
        if (len == 0) {
            return 0;
        }
        while (len > 0) {
            if (this.encoderOut.hasRemaining()) {
                int c2 = Math.min(this.encoderOut.remaining(), len);
                this.encoderOut.get(b2, off, c2);
                off += c2;
                len -= c2;
                read += c2;
                continue;
            }
            this.fillBuffer();
            if (!this.endOfInput || this.encoderOut.hasRemaining()) continue;
        }
        return read == 0 && this.endOfInput ? -1 : read;
    }

    @Override
    public int read(byte[] b2) throws IOException {
        return this.read(b2, 0, b2.length);
    }

    @Override
    public int read() throws IOException {
        do {
            if (this.encoderOut.hasRemaining()) {
                return this.encoderOut.get() & 0xFF;
            }
            this.fillBuffer();
        } while (!this.endOfInput || this.encoderOut.hasRemaining());
        return -1;
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}

