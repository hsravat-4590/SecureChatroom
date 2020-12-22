package com.ravat.hanzalah.securechat.utils;

public class Tuple<T,V> {

    private final T object1;
    private final V object2;

    public Tuple(T object1, V object2){
        this.object1 = object1;
        this.object2 = object2;
    }

    public T getObject1() {
        return object1;
    }

    public V getObject2() {
        return object2;
    }
}
