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
     * @return True if the collision is handled. The collided object will be notified of a handled collision if it derives from {@link CollidedAware}).
     */
    boolean onCollisionWith(T collidable);
}