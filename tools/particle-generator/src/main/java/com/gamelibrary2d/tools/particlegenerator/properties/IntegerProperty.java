package com.gamelibrary2d.tools.particlegenerator.properties;

public class IntegerProperty {
    private final Getter getter;
    private final Setter setter;

    public IntegerProperty(Getter getter, Setter setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public float get() {
        return getter.get();
    }

    public void set(int value) {
        setter.set(value);
    }

    public interface Getter {
        int get();
    }

    public interface Setter {
        void set(int value);
    }
}
