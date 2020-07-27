package com.gamelibrary2d.collision;

/**
 * Area used to restrict where to update {@link Collidable} objects and to perform collision detection.
 */
public interface ActivationArea extends InternalArea {

    /**
     * Invoked in the beginning of the collision detection cycle for each {@link Collidable} overlapping
     * this {@link ActivationArea}.
     *
     * @param collidable The collidable.
     * @return The activation result.
     */
    ActivationResult onActivation(Collidable collidable);
}