package joptsimple.internal;

import java.util.Map;

public interface OptionNameMap<V> {
    public boolean contains(String var1);

    public V get(String var1);

    public void put(String var1, V var2);

    public void putAll(Iterable<String> var1, V var2);

    public void remove(String var1);

    public Map<String, V> toJavaUtilMap();
}

