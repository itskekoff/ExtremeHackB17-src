package io.netty.buffer;

import io.netty.buffer.ByteBufAllocatorMetric;

public interface ByteBufAllocatorMetricProvider {
    public ByteBufAllocatorMetric metric();
}

