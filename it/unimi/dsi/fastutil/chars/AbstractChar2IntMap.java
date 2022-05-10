package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2IntFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharIterator;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.AbstractIntIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractChar2IntMap
extends AbstractChar2IntFunction
implements Char2IntMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2IntMap() {
    }

    @Override
    public boolean containsValue(Object ov) {
        if (ov == null) {
            return false;
        }
        return this.containsValue((Integer)ov);
    }

    @Override
    public boolean containsValue(int v2) {
        return this.values().contains(v2);
    }

    @Override
    public boolean containsKey(char k2) {
        return this.keySet().contains(k2);
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Integer> m2) {
        int n2 = m2.size();
        Iterator<Map.Entry<? extends Character, ? extends Integer>> i2 = m2.entrySet().iterator();
        if (m2 instanceof Char2IntMap) {
            while (n2-- != 0) {
                Char2IntMap.Entry e2 = (Char2IntMap.Entry)i2.next();
                this.put(e2.getCharKey(), e2.getIntValue());
            }
        } else {
            while (n2-- != 0) {
                Map.Entry<? extends Character, ? extends Integer> e3 = i2.next();
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
                return AbstractChar2IntMap.this.containsKey(k2);
            }

            @Override
            public int size() {
                return AbstractChar2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2IntMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new AbstractCharIterator(){
                    final ObjectIterator<Map.Entry<Character, Integer>> i;
                    {
                        this.i = AbstractChar2IntMap.this.entrySet().iterator();
                    }

                    @Override
                    public char nextChar() {
                        return ((Char2IntMap.Entry)this.i.next()).getCharKey();
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
    public IntCollection values() {
        return new AbstractIntCollection(){

            @Override
            public boolean contains(int k2) {
                return AbstractChar2IntMap.this.containsValue(k2);
            }

            @Override
            public int size() {
                return AbstractChar2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2IntMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new AbstractIntIterator(){
                    final ObjectIterator<Map.Entry<Character, Integer>> i;
                    {
                        this.i = AbstractChar2IntMap.this.entrySet().iterator();
                    }

                    @Override
                    @Deprecated
                    public int nextInt() {
                        return ((Char2IntMap.Entry)this.i.next()).getIntValue();
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
    public ObjectSet<Map.Entry<Character, Integer>> entrySet() {
        return this.char2IntEntrySet();
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
            Char2IntMap.Entry e2 = (Char2IntMap.Entry)i2.next();
            s2.append(String.valueOf(e2.getCharKey()));
            s2.append("=>");
            s2.append(String.valueOf(e2.getIntValue()));
        }
        s2.append("}");
        return s2.toString();
    }

    public static class BasicEntry
    implements Char2IntMap.Entry {
        protected char key;
        protected int value;

        public BasicEntry(Character key, Integer value) {
            this.key = key.charValue();
            this.value = value;
        }

        public BasicEntry(char key, int value) {
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
        public Integer getValue() {
            return this.value;
        }

        @Override
        public int getIntValue() {
            return this.value;
        }

        @Override
        public int setValue(int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer setValue(Integer value) {
            return this.setValue((int)value);
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
            if (e2.getValue() == null || !(e2.getValue() instanceof Integer)) {
                return false;
            }
            return this.key == ((Character)e2.getKey()).charValue() && this.value == (Integer)e2.getValue();
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

