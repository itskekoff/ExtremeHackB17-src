package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2ShortFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharIterator;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2ShortMap;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractChar2ShortMap
extends AbstractChar2ShortFunction
implements Char2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2ShortMap() {
    }

    @Override
    public boolean containsValue(Object ov) {
        if (ov == null) {
            return false;
        }
        return this.containsValue((Short)ov);
    }

    @Override
    public boolean containsValue(short v2) {
        return this.values().contains(v2);
    }

    @Override
    public boolean containsKey(char k2) {
        return this.keySet().contains(k2);
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Short> m2) {
        int n2 = m2.size();
        Iterator<Map.Entry<? extends Character, ? extends Short>> i2 = m2.entrySet().iterator();
        if (m2 instanceof Char2ShortMap) {
            while (n2-- != 0) {
                Char2ShortMap.Entry e2 = (Char2ShortMap.Entry)i2.next();
                this.put(e2.getCharKey(), e2.getShortValue());
            }
        } else {
            while (n2-- != 0) {
                Map.Entry<? extends Character, ? extends Short> e3 = i2.next();
                this.put(e3.getKey(), e3.getValue());
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public CharSet keySet() {
        return new AbstractCharSet(){

            @Override
            public boolean contains(char k2) {
                return AbstractChar2ShortMap.this.containsKey(k2);
            }

            @Override
            public int size() {
                return AbstractChar2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2ShortMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new AbstractCharIterator(){
                    final ObjectIterator<Map.Entry<Character, Short>> i;
                    {
                        this.i = AbstractChar2ShortMap.this.entrySet().iterator();
                    }

                    @Override
                    public char nextChar() {
                        return ((Char2ShortMap.Entry)this.i.next()).getCharKey();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.i.remove();
                    }
                };
            }
        };
    }

    @Override
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short k2) {
                return AbstractChar2ShortMap.this.containsValue(k2);
            }

            @Override
            public int size() {
                return AbstractChar2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new AbstractShortIterator(){
                    final ObjectIterator<Map.Entry<Character, Short>> i;
                    {
                        this.i = AbstractChar2ShortMap.this.entrySet().iterator();
                    }

                    @Override
                    @Deprecated
                    public short nextShort() {
                        return ((Char2ShortMap.Entry)this.i.next()).getShortValue();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }
                };
            }
        };
    }

    @Override
    public ObjectSet<Map.Entry<Character, Short>> entrySet() {
        return this.char2ShortEntrySet();
    }

    @Override
    public int hashCode() {
        int h2 = 0;
        int n2 = this.size();
        ObjectIterator i2 = this.entrySet().iterator();
        while (n2-- != 0) {
            h2 += ((Map.Entry)i2.next()).hashCode();
        }
        return h2;
    }

    @Override
    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (!(o2 instanceof Map)) {
            return false;
        }
        Map m2 = (Map)o2;
        if (m2.size() != this.size()) {
            return false;
        }
        return this.entrySet().containsAll(m2.entrySet());
    }

    public String toString() {
        StringBuilder s2 = new StringBuilder();
        ObjectIterator i2 = this.entrySet().iterator();
        int n2 = this.size();
        boolean first = true;
        s2.append("{");
        while (n2-- != 0) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            Char2ShortMap.Entry e2 = (Char2ShortMap.Entry)i2.next();
            s2.append(String.valueOf(e2.getCharKey()));
            s2.append("=>");
            s2.append(String.valueOf(e2.getShortValue()));
        }
        s2.append("}");
        return s2.toString();
    }

    public static class BasicEntry
    implements Char2ShortMap.Entry {
        protected char key;
        protected short value;

        public BasicEntry(Character key, Short value) {
            this.key = key.charValue();
            this.value = value;
        }

        public BasicEntry(char key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        @Deprecated
        public Character getKey() {
            return Character.valueOf(this.key);
        }

        @Override
        public char getCharKey() {
            return this.key;
        }

        @Override
        @Deprecated
        public Short getValue() {
            return this.value;
        }

        @Override
        public short getShortValue() {
            return this.value;
        }

        @Override
        public short setValue(short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Short setValue(Short value) {
            return this.setValue((short)value);
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            if (e2.getKey() == null || !(e2.getKey() instanceof Character)) {
                return false;
            }
            if (e2.getValue() == null || !(e2.getValue() instanceof Short)) {
                return false;
            }
            return this.key == ((Character)e2.getKey()).charValue() && this.value == (Short)e2.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

