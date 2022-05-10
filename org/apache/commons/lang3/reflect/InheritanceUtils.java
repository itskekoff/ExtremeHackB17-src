package org.apache.commons.lang3.reflect;

import org.apache.commons.lang3.BooleanUtils;

public class InheritanceUtils {
    public static int distance(Class<?> child, Class<?> parent) {
        if (child == null || parent == null) {
            return -1;
        }
        if (child.equals(parent)) {
            return 0;
        }
        Class<?> cParent = child.getSuperclass();
        int d2 = BooleanUtils.toInteger(parent.equals(cParent));
        if (d2 == 1) {
            return d2;
        }
        return (d2 += InheritanceUtils.distance(cParent, parent)) > 0 ? d2 + 1 : -1;
    }
}

