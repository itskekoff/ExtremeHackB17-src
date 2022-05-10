package io.netty.util.internal;

import java.lang.reflect.AccessibleObject;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static Throwable trySetAccessible(AccessibleObject object) {
        try {
            object.setAccessible(true);
            return null;
        }
        catch (SecurityException e2) {
            return e2;
        }
        catch (RuntimeException e3) {
            return ReflectionUtil.handleInaccessibleObjectException(e3);
        }
    }

    private static RuntimeException handleInaccessibleObjectException(RuntimeException e2) {
        if ("java.lang.reflect.InaccessibleObjectException".equals(e2.getClass().getName())) {
            return e2;
        }
        throw e2;
    }
}

