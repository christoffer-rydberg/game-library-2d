package com.gamelibrary2d.updates;

import com.gamelibrary2d.components.denotations.Updatable;

/**
 * Updates one or more attributes of the generic target.
 */
public interface AttributeUpdate<T> extends Updatable {

    /**
     * Invoke this method before the update is applied in order to
     * interpret the update's parameters as absolute values.
     */
    void makeAbsolute();

    /**
     * Invoke this method before the update is applied in order to
     * interpret the update's parameters as values relative to the specified object.
     */
    void makeRelative(T obj);
}