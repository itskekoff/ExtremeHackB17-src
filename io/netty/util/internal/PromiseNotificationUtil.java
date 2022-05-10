package io.netty.util.internal;

import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;

public final class PromiseNotificationUtil {
    private PromiseNotificationUtil() {
    }

    public static void tryCancel(Promise<?> p2, InternalLogger logger) {
        if (!p2.cancel(false) && logger != null) {
            Throwable err = p2.cause();
            if (err == null) {
                logger.warn("Failed to cancel promise because it has succeeded already: {}", (Object)p2);
            } else {
                logger.warn("Failed to cancel promise because it has failed already: {}, unnotified cause:", (Object)p2, (Object)err);
            }
        }
    }

    public static <V> void trySuccess(Promise<? super V> p2, V result, InternalLogger logger) {
        if (!p2.trySuccess(result) && logger != null) {
            Throwable err = p2.cause();
            if (err == null) {
                logger.warn("Failed to mark a promise as success because it has succeeded already: {}", (Object)p2);
            } else {
                logger.warn("Failed to mark a promise as success because it has failed already: {}, unnotified cause:", (Object)p2, (Object)err);
            }
        }
    }

    public static void tryFailure(Promise<?> p2, Throwable cause, InternalLogger logger) {
        if (!p2.tryFailure(cause) && logger != null) {
            Throwable err = p2.cause();
            if (err == null) {
                logger.warn("Failed to mark a promise as failure because it has succeeded already: {}", (Object)p2, (Object)cause);
            } else {
                logger.warn("Failed to mark a promise as failure because it has failed already: {}, unnotified cause: {}", p2, ThrowableUtil.stackTraceToString(err), cause);
            }
        }
    }
}

