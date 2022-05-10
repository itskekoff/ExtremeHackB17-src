package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2FloatFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharIterator;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2FloatMap;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.AbstractFloatIterator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractChar2FloatMap
extends AbstractChar2FloatFunction
implements Char2FloatMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2FloatMap() {
    }

    @Override
    public boolean containsValue(Object ov) {
        if (ov == null) {
            return false;
        }
        return this.containsValue(((Float)ov).floatValue());
    }

    @Override
    public boolean containsValue(float v2) {
        return this.values().contains(v2);
    }

    @Override
    public boolean containsKey(char k2) {
        return this.keySet().contains(k2);
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Float> m2) {
        int n2 = m2.size();
        Iterator<Map.Entry<? extends Character, ? extends Float>> i2 = m2.entrySet().iterator();
        if (m2 instanceof Char2FloatMap) {
            while (n2-- != 0) {
                Char2FloatMap.Entry e2 = (Char2FloatMap.Entry)i2.next();
                this.put(e2.getCharKey(), e2.getFloatValue());
            }
        } else {
            while (n2-- != 0) {
                Map.Entry<? extends Character, ? extends Float> e3 = i2.next();
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
                return AbstractChar2FloatMap.this.containsKey(k2);
            }

            @Override
            public int size() {
                return AbstractChar2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2FloatMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new AbstractCharIterator(){
                    final ObjectIterator<Map.Entry<Character, Float>> i;
                    {
                        this.i = AbstractChar2FloatMap.this.entrySet().iterator();
                    }

                    @Override
                    public char nextChar() {
                        return ((Char2FloatMap.Entry)this.i.next()).getCharKey();
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
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float k2) {
                return AbstractChar2FloatMap.this.containsValue(k2);
            }

            @Override
            public int size() {
                return AbstractChar2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new AbstractFloatIterator(){
                    final ObjectIterator<Map.Entry<Character, Float>> i;
                    {
                        this.i = AbstractChar2FloatMap.this.entrySet().iterator();
                    }

                    @Override
                    @Deprecated
                    public float nextFloat() {
                        return ((Char2FloatMap.Entry)this.i.next()).getFloatValue();
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
    public ObjectSet<Map.Entry<Character, Float>> entrySet() {
        return this.char2FloatEntrySet();
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
            Char2FloatMap.Entry e2 = (Char2FloatMap.Entry)i2.next();
            s2.append(String.valueOf(e2.getCharKey()));
            s2.append("=>");
            s2.append(String.valueOf(e2.getFloatValue()));
        }
        s2.append("}");
        return s2.toString();
    }

    public static class BasicEntry
    implements Char2FloatMap.Entry {
        protected char key;
        protected float value;

        public BasicEntry(Character key, Float value) {
            this.key = key.charValue();
            this.value = value.floatValue();
        }

        public BasicEntry(char key, float value) {
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
        public Float getValue() {
            return Float.valueOf(this.value);
        }

        @Override
        public float getFloatValue() {
            return this.value;
        }

        @Override
        public float setValue(float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Float setValue(Float value) {
            return Float.valueOf(this.setValue(value.floatValue()));
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
            if (e2.getValue() == null || !(e2.getValue() instanceof Float)) {
                return false;
            }
            return this.key == ((Character)e2.getKey()).charValue() && this.value == ((Float)e2.getValue()).floatValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

