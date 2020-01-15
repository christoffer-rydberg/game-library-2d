package com.gamelibrary2d.collision;

/**
 * Area used to restrict where to update {@link Collidable} objects and to perform collision detection.
 *
 * @author Christoffer Rydberg
 */
public interface CollisionDetectionArea extends InternalCollidable {

    /**
     * Invoked in the beginning of the collision detection cycle for each {@link Collidable} overlapping this
     * {@link CollisionDetectionArea}. The purpose of this method is to decide if the object should be activated or
     * if it's too close to the edge. Object's close to the edge are not activated to avoid undetected collisions with
     * objects outside the detection area. Note that it's enough for one {@link CollisionDetectionArea} to active a
     * {@link Collidable} object. Activation means that the object will be updated. Other {@link Collidable}
     * objects can regardless collide into the object, since it is inside (or partly inside) the activation area.
     *
     * @param collidable The collidable object.
     * @return The activation result.
     */
    ActivationResult activate(Collidable collidable);
}