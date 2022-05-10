package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PemReader {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PemReader.class);
    private static final Pattern CERT_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
    private static final Pattern KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);

    static ByteBuf[] readCertificates(File file) throws CertificateException {
        ByteBuf[] arrbyteBuf;
        FileInputStream in2 = new FileInputStream(file);
        try {
            arrbyteBuf = PemReader.readCertificates(in2);
        }
        catch (Throwable throwable) {
            try {
                PemReader.safeClose(in2);
                throw throwable;
            }
            catch (FileNotFoundException e2) {
                throw new CertificateException("could not find certificate file: " + file);
            }
        }
        PemReader.safeClose(in2);
        return arrbyteBuf;
    }

    static ByteBuf[] readCertificates(InputStream in2) throws CertificateException {
        String content;
        try {
            content = PemReader.readContent(in2);
        }
        catch (IOException e2) {
            throw new CertificateException("failed to read certificate input stream", e2);
        }
        ArrayList<ByteBuf> certs = new ArrayList<ByteBuf>();
        Matcher m2 = CERT_PATTERN.matcher(content);
        int start = 0;
        while (m2.find(start)) {
            ByteBuf base64 = Unpooled.copiedBuffer(m2.group(1), CharsetUtil.US_ASCII);
            ByteBuf der = Base64.decode(base64);
            base64.release();
            certs.add(der);
            start = m2.end();
        }
        if (certs.isEmpty()) {
            throw new CertificateException("found no certificates in input stream");
        }
        return certs.toArray(new ByteBuf[certs.size()]);
    }

    static ByteBuf readPrivateKey(File file) throws KeyException {
        ByteBuf byteBuf;
        FileInputStream in2 = new FileInputStream(file);
        try {
            byteBuf = PemReader.readPrivateKey(in2);
        }
        catch (Throwable throwable) {
            try {
                PemReader.safeClose(in2);
                throw throwable;
            }
            catch (FileNotFoundException e2) {
                throw new KeyException("could not fine key file: " + file);
            }
        }
        PemReader.safeClose(in2);
        return byteBuf;
    }

    static ByteBuf readPrivateKey(InputStream in2) throws KeyException {
        String content;
        try {
            content = PemReader.readContent(in2);
        }
        catch (IOException e2) {
            throw new KeyException("failed to read key input stream", e2);
        }
        Matcher m2 = KEY_PATTERN.matcher(content);
        if (!m2.find()) {
            throw new KeyException("could not find a PKCS #8 private key in input stream (see http://netty.io/wiki/sslcontextbuilder-and-private-key.html for more information)");
        }
        ByteBuf base64 = Unpooled.copiedBuffer(m2.group(1), CharsetUtil.US_ASCII);
        ByteBuf der = Base64.decode(base64);
        base64.release();
        return der;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String readContent(InputStream in2) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int ret;
            byte[] buf2 = new byte[8192];
            while ((ret = in2.read(buf2)) >= 0) {
                out.write(buf2, 0, ret);
            }
            String string = out.toString(CharsetUtil.US_ASCII.name());
            return string;
        }
        finally {
            PemReader.safeClose(out);
        }
    }

    private static void safeClose(InputStream in2) {
        try {
            in2.close();
        }
        catch (IOException e2) {
            logger.warn("Failed to close a stream.", e2);
        }
    }

    private static void safeClose(OutputStream out) {
        try {
            out.close();
        }
        catch (IOException e2) {
            logger.warn("Failed to close a stream.", e2);
        }
    }

    private PemReader() {
    }
}

