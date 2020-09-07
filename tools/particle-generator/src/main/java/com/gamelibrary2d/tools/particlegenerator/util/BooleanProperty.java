package com.gamelibrary2d.tools.particlegenerator.util;

public class BooleanProperty {
    private final Getter getter;
    private final Setter setter;

    public BooleanProperty(Getter getter, Setter setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public boolean get() {
        return getter.get();
    }

    public void set(boolean value) {
        setter.set(value);
    }

    public interface Getter {
        boolean get();
    }

    public interface Setter {
        void set(boolean value);
    }
}
