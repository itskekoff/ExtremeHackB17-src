package joptsimple;

import java.util.List;
import joptsimple.OptionSet;

public interface OptionSpec<V> {
    public List<V> values(OptionSet var1);

    public V value(OptionSet var1);

    public List<String> options();

    public boolean isForHelp();
}

