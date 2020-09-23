package com.gamelibrary2d.tools.particlegenerator.properties;

public class GenericProperty<T> {
    private final Getter<T> getter;
    private final Setter<T> setter;

    public GenericProperty(Getter<T> getter, Setter<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public T get() {
        return getter.get();
    }

    public void set(T value) {
        setter.set(value);
    }

    public interface Getter<T> {
        T get();
    }

    public interface Setter<T> {
        void set(T value);
    }
}
