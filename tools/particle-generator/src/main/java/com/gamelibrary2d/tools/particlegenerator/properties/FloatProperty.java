package com.gamelibrary2d.tools.particlegenerator.properties;

public class FloatProperty {
    private final Getter getter;
    private final Setter setter;

    public FloatProperty(Getter getter, Setter setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public float get() {
        return getter.get();
    }

    public void set(float value) {
        setter.set(value);
    }

    public interface Getter {
        float get();
    }

    public interface Setter {
        void set(float value);
    }
}
