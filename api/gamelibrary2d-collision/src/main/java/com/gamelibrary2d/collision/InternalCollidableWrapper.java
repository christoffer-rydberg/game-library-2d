package com.gamelibrary2d.collision;

import com.gamelibrary2d.collision.handlers.CollisionHandler;
import com.gamelibrary2d.collision.handlers.UpdatedHandler;

import java.util.ArrayList;

class InternalCollidableWrapper<T1 extends Collidable> {
    final T1 collidable;
    final CollidableInfo<T1> info;
    private final UpdatedHandler<T1> updatedHandler;
    private final ArrayList<CollisionHandler<T1, ?>> collisionHandlers;

    InternalCollidableWrapper(T1 collidable) {
        this.collidable = collidable;
        this.updatedHandler = null;
        this.collisionHandlers = null;
        this.info = new CollidableInfo<>(collidable);
    }

    InternalCollidableWrapper(T1 collidable, CollisionHandler<T1, ?> collisionHandler) {
        this.collidable = collidable;
        if (collisionHandler != null) {
            this.collisionHandlers = new ArrayList<>(1);
            collisionHandlers.add(collisionHandler);
        } else {
            this.collisionHandlers = null;
        }
        this.info = new CollidableInfo<>(collidable);
        updatedHandler = null;
    }

    InternalCollidableWrapper(T1 collidable, ArrayList<CollisionHandler<T1, ?>> collisionHandlers) {
        this.collidable = collidable;
        this.collisionHandlers = collisionHandlers;
        this.info = new CollidableInfo<>(collidable);
        updatedHandler = null;
    }

    InternalCollidableWrapper(T1 collidable, UpdatedHandler<T1> updatedHandler) {
        this.collidable = collidable;
        this.updatedHandler = updatedHandler;
        collisionHandlers = null;
        this.info = new CollidableInfo<>(collidable);
    }

    InternalCollidableWrapper(
            T1 collidable,
            UpdatedHandler<T1> updatedHandler,
            CollisionHandler<T1, ?> collisionHandler) {
        this.collidable = collidable;
        this.updatedHandler = updatedHandler;
        if (collisionHandler != null) {
            this.collisionHandlers = new ArrayList<>(1);
            collisionHandlers.add(collisionHandler);
        } else {
            collisionHandlers = null;
        }
        this.info = new CollidableInfo<>(collidable);
    }

    InternalCollidableWrapper(
            T1 collidable,
            UpdatedHandler<T1> updatedHandler,
            ArrayList<CollisionHandler<T1, ?>> collisionHandlers) {
        this.collidable = collidable;
        this.updatedHandler = updatedHandler;
        this.collisionHandlers = collisionHandlers;
        this.info = new CollidableInfo<>(collidable);
    }

    void update(float deltaTime) {
        var xBeforeUpdate = collidable.getPosX();
        var yBeforeUpdate = collidable.getPosY();
        var boundsBeforeUpdate = collidable.getBounds();

        collidable.update(deltaTime);

        info.reset(
                deltaTime,
                xBeforeUpdate,
                yBeforeUpdate,
                boundsBeforeUpdate);

        if (updatedHandler != null) {
            updatedHandler.onUpdated(info);
        }
    }

    @SuppressWarnings("unchecked")
    private CollisionResult onCollision(
            CollisionHandler collisionHandler, CollidableInfo other) {
        Class<?> type = collisionHandler.getCollidableClass();
        return type.isAssignableFrom(other.getCollidable().getClass())
                ? collisionHandler.onCollision(other)
                : CollisionResult.CONTINUE;
    }

    boolean isHandlingCollisions() {
        return collisionHandlers != null && collisionHandlers.size() > 0;
    }

    CollisionResult handleCollision(InternalCollidableWrapper other) {
        for (int i = 0; i < collisionHandlers.size(); ++i) {
            var result = onCollision(collisionHandlers.get(i), other.info);
            if (result == CollisionResult.ABORT || !collidable.canCollide()) {
                return CollisionResult.ABORT;
            }
        }

        return CollisionResult.CONTINUE;
    }

    void initializeCollisionHandlers() {
        for (int i = 0; i < collisionHandlers.size(); ++i) {
            collisionHandlers.get(i).initialize(info);
        }
    }

    void finishCollisionHandlers() {
        for (int i = 0; i < collisionHandlers.size(); ++i) {
            collisionHandlers.get(i).finish();
        }
    }
}