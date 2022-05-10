package io.netty.util;

import io.netty.util.Timeout;

public interface TimerTask {
    public void run(Timeout var1) throws Exception;
}

