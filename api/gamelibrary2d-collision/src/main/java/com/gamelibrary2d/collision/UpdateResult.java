package com.gamelibrary2d.collision;

/**
 * Used to determine how to proceed with collision detection after an update.
 */
public enum UpdateResult {

    /**
     * The updated {@link Collidable} has moved and must be repositioned in the collision quad tree prior to collision detection.
     */
    MOVED,

    /**
     * The updated {@link Collidable} has not moved and does not need to be repositioned in the collision quad tree. Collision
     * detection is performed in order to capture collisions with other objects that has moved.
     */
    STILL,

    /**
     * The updated {@link Collidable} is removed from the collision quad tree.
     */
    SKIP
}