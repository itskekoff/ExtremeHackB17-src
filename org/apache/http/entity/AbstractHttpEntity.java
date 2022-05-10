package org.apache.http.entity;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.message.BasicHeader;

@NotThreadSafe
public abstract class AbstractHttpEntity
implements HttpEntity {
    protected static final int OUTPUT_BUFFER_SIZE = 4096;
    protected Header contentType;
    protected Header contentEncoding;
    protected boolean chunked;

    protected AbstractHttpEntity() {
    }

    public Header getContentType() {
        return this.contentType;
    }

    public Header getContentEncoding() {
        return this.contentEncoding;
    }

    public boolean isChunked() {
        return this.chunked;
    }

    public void setContentType(Header contentType) {
        this.contentType = contentType;
    }

    public void setContentType(String ctString) {
        BasicHeader h2 = null;
        if (ctString != null) {
            h2 = new BasicHeader("Content-Type", ctString);
        }
        this.setContentType(h2);
    }

    public void setContentEncoding(Header contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public void setContentEncoding(String ceString) {
        BasicHeader h2 = null;
        if (ceString != null) {
            h2 = new BasicHeader("Content-Encoding", ceString);
        }
        this.setContentEncoding(h2);
    }

    public void setChunked(boolean b2) {
        this.chunked = b2;
    }

    @Deprecated
    public void consumeContent() throws IOException {
    }
}

