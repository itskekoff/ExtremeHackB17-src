package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class Platform {
    static boolean isInstanceOfThrowableClass(@Nullable Throwable t2, Class<? extends Throwable> expectedClass) {
        return expectedClass.isInstance(t2);
    }

    private Platform() {
    }
}

