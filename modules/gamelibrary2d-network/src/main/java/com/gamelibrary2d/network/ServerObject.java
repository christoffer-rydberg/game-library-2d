package com.gamelibrary2d.network;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.network.common.Message;

/**
 * Server-side representation of a game object.
 *
 * @author Christoffer Rydberg
 */
public interface ServerObject extends Message {

    /**
     * Gets the object identifier. The object identifier is set when the object is
     * registered, i.e. when {@link #onRegistered} is invoked by the
     * {@link GameLogic}. This method should return {@link Integer#MAX_VALUE} if the
     * object has not been registered.
     */
    int getId();

    /**
     * The {@link GameLogic} is responsible for registration of server objects. When
     * an object is registered, it will be assigned a unique identifier. If the
     * object has been registered before, it will be re-registered with its previous
     * identifier.
     *
     * @param id The object identifier.
     */
    void onRegistered(int id);

    /**
     * Invoked when the object is unregistered from the {@link GameLogic}. The
     * object can at any time be registered again. Note that this method should not
     * reset the object's {@link #getId identifier}. If the object is re-registered,
     * the old identifier will be reused.
     */
    void onUnregistered();

    /**
     * Gets the position of the object.
     */
    Point getPosition();
}