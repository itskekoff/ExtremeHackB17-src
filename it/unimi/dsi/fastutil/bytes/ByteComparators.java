package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import java.io.Serializable;

public class ByteComparators {
    public static final ByteComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final ByteComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private ByteComparators() {
    }

    public static ByteComparator oppositeComparator(ByteComparator c2) {
        return new OppositeComparator(c2);
    }

    protected static class OppositeComparator
    extends AbstractByteComparator
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private final ByteComparator comparator;

        protected OppositeComparator(ByteComparator c2) {
            this.comparator = c2;
        }

        @Override
        public final int compare(byte a2, byte b2) {
            return this.comparator.compare(b2, a2);
        }
    }

    protected static class OppositeImplicitComparator
    extends AbstractByteComparator
    implements Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        @Override
        public final int compare(byte a2, byte b2) {
            return -Byte.compare(a2, b2);
        }

        private Object readResolve() {
            return OPPOSITE_COMPARATOR;
        }
    }

    protected static class NaturalImplicitComparator
    extends AbstractByteComparator
    implements Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        @Override
        public final int compare(byte a2, byte b2) {
            return Byte.compare(a2, b2);
        }

        private Object readResolve() {
            return NATURAL_COMPARATOR;
        }
    }
}

