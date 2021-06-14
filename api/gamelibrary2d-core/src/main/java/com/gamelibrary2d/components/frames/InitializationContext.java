package com.gamelibrary2d.components.frames;

public interface InitializationContext {

    /**
     * Registers an object with the specified key.
     */
    void register(Object key, Object obj);

    /**
     * Gets the object of the specified type registered with the specified key.
     */
    <T> T get(Class<T> type, Object key);

    /**
     * Registers an object with its {@link Object#getClass() runtime class} as key.
     */
    default void register(Object value) {
        register(value.getClass(), value);
    }

    /**
     * Gets the object registered with the specified type as key.
     */
    default <T> T get(Class<T> type) {
        return get(type, type);
    }
}
