package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import java.io.Serializable;

public class Byte2BooleanFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Byte2BooleanFunctions() {
    }

    public static Byte2BooleanFunction singleton(byte key, boolean value) {
        return new Singleton(key, value);
    }

    public static Byte2BooleanFunction singleton(Byte key, Boolean value) {
        return new Singleton(key, value);
    }

    public static Byte2BooleanFunction synchronize(Byte2BooleanFunction f2) {
        return new SynchronizedFunction(f2);
    }

    public static Byte2BooleanFunction synchronize(Byte2BooleanFunction f2, Object sync) {
        return new SynchronizedFunction(f2, sync);
    }

    public static Byte2BooleanFunction unmodifiable(Byte2BooleanFunction f2) {
        return new UnmodifiableFunction(f2);
    }

    public static class UnmodifiableFunction
    extends AbstractByte2BooleanFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2BooleanFunction function;

        protected UnmodifiableFunction(Byte2BooleanFunction f2) {
            if (f2 == null) {
                throw new NullPointerException();
            }
            this.function = f2;
        }

        @Override
        public int size() {
            return this.function.size();
        }

        @Override
        public boolean containsKey(byte k2) {
            return this.function.containsKey(k2);
        }

        @Override
        public boolean defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(boolean defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean put(byte k2, boolean v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return this.function.toString();
        }

        @Override
        @Deprecated
        public boolean remove(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean get(byte k2) {
            return this.function.get(k2);
        }

        @Override
        public boolean containsKey(Object ok2) {
            return this.function.containsKey(ok2);
        }
    }

    public static class SynchronizedFunction
    extends AbstractByte2BooleanFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2BooleanFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Byte2BooleanFunction f2, Object sync) {
            if (f2 == null) {
                throw new NullPointerException();
            }
            this.function = f2;
            this.sync = sync;
        }

        protected SynchronizedFunction(Byte2BooleanFunction f2) {
            if (f2 == null) {
                throw new NullPointerException();
            }
            this.function = f2;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.containsKey(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(boolean defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean put(byte k2, boolean v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k2, v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.function.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.toString();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Boolean put(Byte k2, Boolean v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k2, v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Boolean get(Object k2) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.function.get(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Boolean remove(Object k2) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.function.remove(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.remove(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean get(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(Object ok2) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.containsKey(ok2);
            }
        }
    }

    public static class Singleton
    extends AbstractByte2BooleanFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final byte key;
        protected final boolean value;

        protected Singleton(byte key, boolean value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(byte k2) {
            return this.key == k2;
        }

        @Override
        public boolean get(byte k2) {
            if (this.key == k2) {
                return this.value;
            }
            return this.defRetValue;
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptyFunction
    extends AbstractByte2BooleanFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public boolean get(byte k2) {
            return false;
        }

        @Override
        public boolean containsKey(byte k2) {
            return false;
        }

        @Override
        public boolean defaultReturnValue() {
            return false;
        }

        @Override
        public void defaultReturnValue(boolean defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Boolean get(Object k2) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        private Object readResolve() {
            return EMPTY_FUNCTION;
        }

        public Object clone() {
            return EMPTY_FUNCTION;
        }
    }
}

