package io.netty.util;

public interface HashingStrategy<T> {
    public static final HashingStrategy JAVA_HASHER = new HashingStrategy(){

        public int hashCode(Object obj) {
            return obj != null ? obj.hashCode() : 0;
        }

        public boolean equals(Object a2, Object b2) {
            return a2 == b2 || a2 != null && a2.equals(b2);
        }
    };

    public int hashCode(T var1);

    public boolean equals(T var1, T var2);
}

