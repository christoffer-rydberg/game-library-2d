package com.gamelibrary2d.collision.handlers;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.collision.CollidableInfo;

/**
 * Invoked after an object has been updated, right before its new position is checked for collisions.
 */
public interface UpdatedHandler<T extends Collidable> {
    void updated(CollidableInfo<T> info);
}
