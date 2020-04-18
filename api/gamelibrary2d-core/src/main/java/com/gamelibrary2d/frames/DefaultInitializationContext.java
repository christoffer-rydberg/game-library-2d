package com.gamelibrary2d.frames;

import java.util.HashMap;
import java.util.Map;

public class DefaultInitializationContext implements InitializationContext {
    private final Map<Object, Object> register;

    public DefaultInitializationContext() {
        register = new HashMap<>();
    }

    public DefaultInitializationContext(DefaultInitializationContext other) {
        register = new HashMap<>(other.register);
    }

    @Override
    public void register(Object key, Object obj) {
        register.put(key, obj);
    }

    @Override
    public <T> T get(Class<T> type, Object key) {
        var obj = register.get(key);
        if (type.isAssignableFrom(obj.getClass())) {
            return type.cast(obj);
        }

        return null;
    }

    /**
     * Clears all registered objects.
     */
    public void clear() {
        register.clear();
    }

    /**
     * Registers all objects from the specified context.
     */
    public void registerAll(DefaultInitializationContext other) {
        register.putAll(other.register);
    }
}
