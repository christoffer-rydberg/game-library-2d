package com.gamelibrary2d.updates;

import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.GameObject;

/**
 * Updates one or more attributes of a {@link GameObject}.
 */
public interface AttributeUpdate extends Updatable {

    /**
     * Invoke this method before the update is applied in order to
     * interpret the update's parameters as absolute values.
     */
    void makeAbsolute();

    /**
     * Invoke this method before the update is applied in order to
     * interpret the update's parameters as values relative to the specified object.
     */
    void makeRelative(GameObject obj);
}