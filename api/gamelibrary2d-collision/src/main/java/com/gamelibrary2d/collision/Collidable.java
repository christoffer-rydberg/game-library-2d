package com.gamelibrary2d.collision;

/**
 * Derived classes are subjects to collision detection.
 */
public interface Collidable extends InternalCollidable {

    /**
     * Invoked each collision detection cycle to update this {@link Collidable} instance and detect collisions.
     *
     * @param deltaTime Time since the last update, in seconds.
     */
    void update(float deltaTime);

    /**
     * @return True if the {@link Collidable} instance can currently collide. Collision detection will be skipped
     * while this method returns false. Typically objects that can't collide shouldn't be included in the
     * collision detection search. However, if the object is destroyed, or somehow deactivated, during an update or collision,
     * this flag can return false during the rest of the update cycle to prevent further collision detection.
     */
    boolean canCollide();

    /**
     * Invoked after {@link #update} and collision detection.
     */
    void updated();
}