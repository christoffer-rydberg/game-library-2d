package com.gamelibrary2d.network;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.network.common.Message;

/**
 * Server-side representation of a game object.
 */
public interface ServerObject extends Message {

    /**
     * @return The unique object identifier.
     */
    int getId();

    /**
     * Invoked when the object has been registered.
     *
     * @param id The unique object identifier.
     */
    void onRegistered(int id);

    /**
     * Invoked when the object has been de-registered.
     * The object {@link #getId id} should not be reset when this method is called.
     */
    void onDeregister();

    /**
     * @return The object's position.
     */
    Point getPosition();
}