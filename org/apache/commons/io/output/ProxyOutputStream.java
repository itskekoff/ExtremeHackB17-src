package org.apache.commons.io.output;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProxyOutputStream
extends FilterOutputStream {
    public ProxyOutputStream(OutputStream proxy) {
        super(proxy);
    }

    @Override
    public void write(int idx) throws IOException {
        try {
            this.beforeWrite(1);
            this.out.write(idx);
            this.afterWrite(1);
        }
        catch (IOException e2) {
            this.handleIOException(e2);
        }
    }

    @Override
    public void write(byte[] bts2) throws IOException {
        try {
            int len = bts2 != null ? bts2.length : 0;
            this.beforeWrite(len);
            this.out.write(bts2);
            this.afterWrite(len);
        }
        catch (IOException e2) {
            this.handleIOException(e2);
        }
    }

    @Override
    public void write(byte[] bts2, int st2, int end) throws IOException {
        try {
            this.beforeWrite(end);
            this.out.write(bts2, st2, end);
            this.afterWrite(end);
        }
        catch (IOException e2) {
            this.handleIOException(e2);
        }
    }

    @Override
    public void flush() throws IOException {
        try {
            this.out.flush();
        }
        catch (IOException e2) {
            this.handleIOException(e2);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.out.close();
        }
        catch (IOException e2) {
            this.handleIOException(e2);
        }
    }

    protected void beforeWrite(int n2) throws IOException {
    }

    protected void afterWrite(int n2) throws IOException {
    }

    protected void handleIOException(IOException e2) throws IOException {
        throw e2;
    }
}

