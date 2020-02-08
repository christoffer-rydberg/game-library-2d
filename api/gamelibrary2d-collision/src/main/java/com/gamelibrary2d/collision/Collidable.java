package com.gamelibrary2d.collision;

/**
 * Derived classes are subjects to collision detection.
 */
public interface Collidable extends InternalCollidable {

    /**
     * Invoked each collision detection cycle to update the {@link Collidable} instance and detect collisions.
     *
     * @param deltaTime Time since the last update, in seconds.
     * @return The update result.
     */
    UpdateResult update(float deltaTime);
}