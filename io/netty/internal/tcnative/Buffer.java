package io.netty.internal.tcnative;

import java.nio.ByteBuffer;

public final class Buffer {
    private Buffer() {
    }

    public static native long address(ByteBuffer var0);

    public static native long size(ByteBuffer var0);
}

