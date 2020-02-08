package com.gamelibrary2d.collision;

/**
 * Derived classes are notified when collided into by a {@link CollisionAware} instance, i.e. when a collision has been
 * detected and the instance's {@link CollisionAware#onCollisionWith onCollisionWith} method returns true.
 *
 * @param <T> The generic {@link CollisionAware} type that will be monitored for collisions.
 */
public interface CollidedAware<T extends CollisionAware<?>> extends Collidable {

    /**
     * The class of the generic {@link CollisionAware} that this instance is aware of.
     */
    Class<T> getCollisionAwareClass();

    /**
     * Invoked when a collision is detected with an instance of the generic {@link #getCollisionAwareClass collision aware class}.
     *
     * @param collidable The other object in the collision.
     */
    void onCollidedBy(T collidable);
}