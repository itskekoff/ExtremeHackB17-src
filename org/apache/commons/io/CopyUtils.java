package org.apache.commons.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;

@Deprecated
public class CopyUtils {
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static void copy(byte[] input, OutputStream output) throws IOException {
        output.write(input);
    }

    @Deprecated
    public static void copy(byte[] input, Writer output) throws IOException {
        ByteArrayInputStream in2 = new ByteArrayInputStream(input);
        CopyUtils.copy((InputStream)in2, output);
    }

    public static void copy(byte[] input, Writer output, String encoding) throws IOException {
        ByteArrayInputStream in2 = new ByteArrayInputStream(input);
        CopyUtils.copy(in2, output, encoding);
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        int count = 0;
        int n2 = 0;
        while (-1 != (n2 = input.read(buffer))) {
            output.write(buffer, 0, n2);
            count += n2;
        }
        return count;
    }

    public static int copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[4096];
        int count = 0;
        int n2 = 0;
        while (-1 != (n2 = input.read(buffer))) {
            output.write(buffer, 0, n2);
            count += n2;
        }
        return count;
    }

    @Deprecated
    public static void copy(InputStream input, Writer output) throws IOException {
        InputStreamReader in2 = new InputStreamReader(input, Charset.defaultCharset());
        CopyUtils.copy((Reader)in2, output);
    }

    public static void copy(InputStream input, Writer output, String encoding) throws IOException {
        InputStreamReader in2 = new InputStreamReader(input, encoding);
        CopyUtils.copy((Reader)in2, output);
    }

    @Deprecated
    public static void copy(Reader input, OutputStream output) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output, Charset.defaultCharset());
        CopyUtils.copy(input, (Writer)out);
        out.flush();
    }

    public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output, encoding);
        CopyUtils.copy(input, (Writer)out);
        out.flush();
    }

    @Deprecated
    public static void copy(String input, OutputStream output) throws IOException {
        StringReader in2 = new StringReader(input);
        OutputStreamWriter out = new OutputStreamWriter(output, Charset.defaultCharset());
        CopyUtils.copy((Reader)in2, (Writer)out);
        out.flush();
    }

    public static void copy(String input, OutputStream output, String encoding) throws IOException {
        StringReader in2 = new StringReader(input);
        OutputStreamWriter out = new OutputStreamWriter(output, encoding);
        CopyUtils.copy((Reader)in2, (Writer)out);
        out.flush();
    }

    public static void copy(String input, Writer output) throws IOException {
        output.write(input);
    }
}

