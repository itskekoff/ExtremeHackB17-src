 * Could not load the following classes:
 *  org.tukaani.xz.SingleXZInputStream
 *  org.tukaani.xz.XZ
 *  org.tukaani.xz.XZInputStream
 */
package org.apache.commons.compress.compressors.xz;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.tukaani.xz.SingleXZInputStream;
import org.tukaani.xz.XZ;
import org.tukaani.xz.XZInputStream;

public class XZCompressorInputStream
extends CompressorInputStream {
    private final InputStream in;

    public static boolean matches(byte[] signature, int length) {
        if (length < XZ.HEADER_MAGIC.length) {
            return false;
        }
        for (int i2 = 0; i2 < XZ.HEADER_MAGIC.length; ++i2) {
            if (signature[i2] == XZ.HEADER_MAGIC[i2]) continue;
            return false;
        }
        return true;
    }

    public XZCompressorInputStream(InputStream inputStream) throws IOException {
        this(inputStream, false);
    }

    public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated) throws IOException {
        this.in = decompressConcatenated ? new XZInputStream(inputStream) : new SingleXZInputStream(inputStream);
    }

    public int read() throws IOException {
        int ret = this.in.read();
        this.count(ret == -1 ? -1 : 1);
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

