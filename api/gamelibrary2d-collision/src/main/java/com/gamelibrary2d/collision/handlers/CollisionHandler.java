package com.gamelibrary2d.collision.handlers;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.collision.CollidableInfo;
import com.gamelibrary2d.collision.CollisionResult;

public interface CollisionHandler<T1 extends Collidable, T2 extends Collidable> {

    /**
     * The class of the generic {@link Collidable} that this instance handles collisions with.
     */
    Class<T2> getCollidableClass();

    /**
     * Invoked before any collisions are detected for the updated {@link Collidable}.
     *
     * @param updated Parameters for the updated {@link Collidable}.
     */
    void initialize(CollidableInfo<T1> updated);

    /**
     * Invoked when a collision is detected for the updated {@link Collidable}.
     *
     * @param collided Last previous update parameters for the collided {@link Collidable}.
     */
    CollisionResult onCollision(CollidableInfo<T2> collided);

    /**
     * Invoked when all collisions has been detected for the updated {@link Collidable}.
     */
    void finish();
}