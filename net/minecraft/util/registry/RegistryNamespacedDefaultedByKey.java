package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.registry.RegistryNamespaced;
import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V>
extends RegistryNamespaced<K, V> {
    private final K defaultValueKey;
    private V defaultValue;

    public RegistryNamespacedDefaultedByKey(K defaultValueKeyIn) {
        this.defaultValueKey = defaultValueKeyIn;
    }

    @Override
    public void register(int id2, K key, V value) {
        if (this.defaultValueKey.equals(key)) {
            this.defaultValue = value;
        }
        super.register(id2, key, value);
    }

    public void validateKey() {
        Validate.notNull(this.defaultValue, "Missing default of DefaultedMappedRegistry: " + this.defaultValueKey, new Object[0]);
    }

    @Override
    public int getIDForObject(V value) {
        int i2 = super.getIDForObject(value);
        return i2 == -1 ? super.getIDForObject(this.defaultValue) : i2;
    }

    @Override
    @Nonnull
    public K getNameForObject(V value) {
        Object k2 = super.getNameForObject(value);
        return k2 == null ? this.defaultValueKey : k2;
    }

    @Override
    @Nonnull
    public V getObject(@Nullable K name) {
        Object v2 = super.getObject(name);
        return v2 == null ? this.defaultValue : v2;
    }

    @Override
    @Nonnull
    public V getObjectById(int id2) {
        Object v2 = super.getObjectById(id2);
        return v2 == null ? this.defaultValue : v2;
    }

    @Override
    @Nonnull
    public V getRandomObject(Random random) {
        Object v2 = super.getRandomObject(random);
        return v2 == null ? this.defaultValue : v2;
    }
}

