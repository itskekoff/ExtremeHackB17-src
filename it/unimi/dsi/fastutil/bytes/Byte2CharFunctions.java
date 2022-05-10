package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import java.io.Serializable;

public class Byte2CharFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Byte2CharFunctions() {
    }

    public static Byte2CharFunction singleton(byte key, char value) {
        return new Singleton(key, value);
    }

    public static Byte2CharFunction singleton(Byte key, Character value) {
        return new Singleton(key, value.charValue());
    }

    public static Byte2CharFunction synchronize(Byte2CharFunction f2) {
        return new SynchronizedFunction(f2);
    }

    public static Byte2CharFunction synchronize(Byte2CharFunction f2, Object sync) {
        return new SynchronizedFunction(f2, sync);
    }

    public static Byte2CharFunction unmodifiable(Byte2CharFunction f2) {
        return new UnmodifiableFunction(f2);
    }

    public static class UnmodifiableFunction
    extends AbstractByte2CharFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2CharFunction function;

        protected UnmodifiableFunction(Byte2CharFunction f2) {
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
        public char defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(char defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char put(byte k2, char v2) {
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
        public char remove(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public char get(byte k2) {
            return this.function.get(k2);
        }

        @Override
        public boolean containsKey(Object ok2) {
            return this.function.containsKey(ok2);
        }
    }

    public static class SynchronizedFunction
    extends AbstractByte2CharFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2CharFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Byte2CharFunction f2, Object sync) {
            if (f2 == null) {
                throw new NullPointerException();
            }
            this.function = f2;
            this.sync = sync;
        }

        protected SynchronizedFunction(Byte2CharFunction f2) {
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
        public char defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(char defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char put(byte k2, char v2) {
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
        public Character put(Byte k2, Character v2) {
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
        public Character get(Object k2) {
            Object object = this.sync;
            synchronized (object) {
                return (Character)this.function.get(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Character remove(Object k2) {
            Object object = this.sync;
            synchronized (object) {
                return (Character)this.function.remove(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char remove(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.remove(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char get(byte k2) {
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
    extends AbstractByte2CharFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final byte key;
        protected final char value;

        protected Singleton(byte key, char value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(byte k2) {
            return this.key == k2;
        }

        @Override
        public char get(byte k2) {
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
    extends AbstractByte2CharFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public char get(byte k2) {
            return '\u0000';
        }

        @Override
        public boolean containsKey(byte k2) {
            return false;
        }

        @Override
        public char defaultReturnValue() {
            return '\u0000';
        }

        @Override
        public void defaultReturnValue(char defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character get(Object k2) {
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

