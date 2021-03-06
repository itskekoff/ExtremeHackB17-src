 * Could not load the following classes:
 *  org.tukaani.xz.LZMAInputStream
 */
package org.apache.commons.compress.compressors.lzma;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.tukaani.xz.LZMAInputStream;

public class LZMACompressorInputStream
extends CompressorInputStream {
    private final InputStream in;

    public LZMACompressorInputStream(InputStream inputStream) throws IOException {
        this.in = new LZMAInputStream(inputStream);
    }

    public int read() throws IOException {
        int ret = this.in.read();
        this.count(ret == -1 ? 0 : 1);
        return ret;
    }

    public int read(byte[] buf2, int off, int len) throws IOException {
        int ret = this.in.read(buf2, off, len);
        this.count(ret);
        return ret;
    }

    public long skip(long n2) throws IOException {
        return this.in.skip(n2);
    }

    public int available() throws IOException {
        return this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }
}

