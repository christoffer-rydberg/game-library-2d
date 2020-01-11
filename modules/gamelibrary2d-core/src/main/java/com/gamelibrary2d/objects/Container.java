package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.updating.Updatable;

import java.util.List;

/**
 * Any game object containing a public list of child objects should implement
 * this interface. Many recursive methods will try to cast a GameObject to a
 * Container to gain access to its child objects, e.g. to forwards input events.
 *
 * @author Christoffer Rydberg
 */
public interface Container<T extends GameObject> extends GameObject, Updatable, MouseAware, KeyAware {

    /**
     * @return The size of the container.
     */
    int size();

    /**
     * @return All objects in the container.
     */
    List<T> getObjects();

}