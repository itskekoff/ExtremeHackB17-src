package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.ExtraObjectsMethodsForWeb;
import java.util.Arrays;
import javax.annotation.Nullable;

@GwtCompatible
public final class Objects
extends ExtraObjectsMethodsForWeb {
    private Objects() {
    }

    public static boolean equal(@Nullable Object a2, @Nullable Object b2) {
        return a2 == b2 || a2 != null && a2.equals(b2);
    }

    public static int hashCode(Object ... objects) {
        return Arrays.hashCode(objects);
    }
}

