package io.netty.handler.codec;

import io.netty.handler.codec.CodecException;

public class UnsupportedMessageTypeException
extends CodecException {
    private static final long serialVersionUID = 2799598826487038726L;

    public UnsupportedMessageTypeException(Object message, Class<?> ... expectedTypes) {
        super(UnsupportedMessageTypeException.message(message == null ? "null" : message.getClass().getName(), expectedTypes));
    }

    public UnsupportedMessageTypeException() {
    }

    public UnsupportedMessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedMessageTypeException(String s2) {
        super(s2);
    }

    public UnsupportedMessageTypeException(Throwable cause) {
        super(cause);
    }

    private static String message(String actualType, Class<?> ... expectedTypes) {
        StringBuilder buf2 = new StringBuilder(actualType);
        if (expectedTypes != null && expectedTypes.length > 0) {
            Class<?> t2;
            buf2.append(" (expected: ").append(expectedTypes[0].getName());
            for (int i2 = 1; i2 < expectedTypes.length && (t2 = expectedTypes[i2]) != null; ++i2) {
                buf2.append(", ").append(t2.getName());
            }
            buf2.append(')');
        }
        return buf2.toString();
    }
}

