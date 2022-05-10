package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.input.ProxyInputStream;

public class TeeInputStream
extends ProxyInputStream {
    private final OutputStream branch;
    private final boolean closeBranch;

    public TeeInputStream(InputStream input, OutputStream branch) {
        this(input, branch, false);
    }

    public TeeInputStream(InputStream input, OutputStream branch, boolean closeBranch) {
        super(input);
        this.branch = branch;
        this.closeBranch = closeBranch;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        }
        finally {
            if (this.closeBranch) {
                this.branch.close();
            }
        }
    }

    @Override
    public int read() throws IOException {
        int ch2 = super.read();
        if (ch2 != -1) {
            this.branch.write(ch2);
        }
        return ch2;
    }

    @Override
    public int read(byte[] bts2, int st2, int end) throws IOException {
        int n2 = super.read(bts2, st2, end);
        if (n2 != -1) {
            this.branch.write(bts2, st2, n2);
        }
        return n2;
    }

    @Override
    public int read(byte[] bts2) throws IOException {
        int n2 = super.read(bts2);
        if (n2 != -1) {
            this.branch.write(bts2, 0, n2);
        }
        return n2;
    }
}

