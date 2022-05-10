package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ByteFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ByteFunction;
import java.io.Serializable;

public class Byte2ByteFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Byte2ByteFunctions() {
    }

    public static Byte2ByteFunction singleton(byte key, byte value) {
        return new Singleton(key, value);
    }

    public static Byte2ByteFunction singleton(Byte key, Byte value) {
        return new Singleton(key, value);
    }

    public static Byte2ByteFunction synchronize(Byte2ByteFunction f2) {
        return new SynchronizedFunction(f2);
    }

    public static Byte2ByteFunction synchronize(Byte2ByteFunction f2, Object sync) {
        return new SynchronizedFunction(f2, sync);
    }

    public static Byte2ByteFunction unmodifiable(Byte2ByteFunction f2) {
        return new UnmodifiableFunction(f2);
    }

    public static class UnmodifiableFunction
    extends AbstractByte2ByteFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ByteFunction function;

        protected UnmodifiableFunction(Byte2ByteFunction f2) {
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
        public byte defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(byte defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte put(byte k2, byte v2) {
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
        public byte remove(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public byte get(byte k2) {
            return this.function.get(k2);
        }

        @Override
        public boolean containsKey(Object ok2) {
            return this.function.containsKey(ok2);
        }
    }

    public static class SynchronizedFunction
    extends AbstractByte2ByteFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ByteFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Byte2ByteFunction f2, Object sync) {
            if (f2 == null) {
                throw new NullPointerException();
            }
            this.function = f2;
            this.sync = sync;
        }

        protected SynchronizedFunction(Byte2ByteFunction f2) {
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
        public byte defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(byte defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte put(byte k2, byte v2) {
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
        public Byte put(Byte k2, Byte v2) {
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
        public Byte get(Object k2) {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.function.get(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Byte remove(Object k2) {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.function.remove(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte remove(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.remove(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte get(byte k2) {
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
    extends AbstractByte2ByteFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final byte key;
        protected final byte value;

        protected Singleton(byte key, byte value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(byte k2) {
            return this.key == k2;
        }

        @Override
        public byte get(byte k2) {
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
    extends AbstractByte2ByteFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public byte get(byte k2) {
            return 0;
        }

        @Override
        public boolean containsKey(byte k2) {
            return false;
        }

        @Override
        public byte defaultReturnValue() {
            return 0;
        }

        @Override
        public void defaultReturnValue(byte defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Byte get(Object k2) {
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

