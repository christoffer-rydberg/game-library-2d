package com.gamelibrary2d.network.common.initialization;

public interface CommunicationContext {

    /**
     * Registers an object with its {@link Object#getClass() runtime class} as key.
     */
    default void register(Object value) {
        register(value.getClass(), value);
    }

    /**
     * Registers an object with the specified key.
     */
    void register(Object key, Object value);

    /**
     * Gets the object registered with the specified type as key.
     */
    default <T> T get(Class<T> type) {
        return get(type, type);
    }

    /**
     * Gets the object of the specified type registered with the specified key.
     */
    <T> T get(Class<T> type, Object key);
}
