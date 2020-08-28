package com.gamelibrary2d.collision.handlers;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.collision.CollidableInfo;
import com.gamelibrary2d.collision.CollisionResult;

public class DefaultCollisionHandler<T1 extends Collidable, T2 extends Collidable> implements CollisionHandler<T1, T2> {
    private final Class<T2> collidableClass;
    private final CollisionListener<T1, T2> collisionListener;
    private T1 updated;

    public DefaultCollisionHandler(Class<T2> collidableClass, CollisionListener<T1, T2> collisionListener) {
        this.collidableClass = collidableClass;
        this.collisionListener = collisionListener;
    }

    @Override
    public Class<T2> getCollidableClass() {
        return collidableClass;
    }

    @Override
    public void initialize(CollidableInfo<T1> updated) {
        this.updated = updated.getCollidable();
    }

    @Override
    public CollisionResult collision(CollidableInfo<T2> collided) {
        return collisionListener.onCollision(updated, collided.getCollidable());
    }

    @Override
    public void finish() {

    }

    public interface CollisionListener<T1 extends Collidable, T2 extends Collidable> {
        CollisionResult onCollision(T1 obj, T2 collided);
    }
}
