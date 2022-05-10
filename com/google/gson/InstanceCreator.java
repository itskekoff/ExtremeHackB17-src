package com.google.gson;

import java.lang.reflect.Type;

public interface InstanceCreator<T> {
    public T createInstance(Type var1);
}

