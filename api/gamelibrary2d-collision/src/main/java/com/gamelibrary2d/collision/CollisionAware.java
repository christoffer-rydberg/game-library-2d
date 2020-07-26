package com.gamelibrary2d.collision;

/**
 * Derived classes are made aware of collisions when updated.
 *
 * @param <T> The generic {@link Collidable} type that will be monitored for collisions.
 */
public interface CollisionAware<T extends Collidable> extends Collidable {

    /**
     * The class of the generic {@link Collidable} that this instance is aware of.
     */
    Class<T> getCollidableClass();

    /**
     * Invoked when a collision is detected with an instance of the generic {@link #getCollidableClass collidable class}.
     *
     * @param collidable The other object in the collision.
     * @param deltaTime  The time since the last update in seconds.
     * @param prevX      The x-position before the update causing to the collision.
     * @param prevY      The y-position before the update causing to the collision.
     * @param attempt    Collision detection attempt. Incremented each time {@link CollisionResult#RERUN} is used.
     */
    CollisionResult onCollision(T collidable, CollisionParameters params);
}