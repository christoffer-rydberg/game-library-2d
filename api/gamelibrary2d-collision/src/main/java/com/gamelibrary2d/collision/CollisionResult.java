package com.gamelibrary2d.collision;

public enum CollisionResult {

    /**
     * Continues collision detection for the {@link Collidable}.
     * This option should not be used if the position or the bounds of the {@link Collidable}
     * has been changed inside {@link CollisionAware#onCollision}, since it can lead to
     * undetected collisions.
     */
    CONTINUE,

    /**
     * Reruns collision detection for the {@link Collidable}. This option should be
     * used when its bounds or position has been changed inside
     * {@link CollisionAware#onCollision}, in order to handle collisions caused
     * by the new position. Collisions with objects handled in previous iterations
     * will need to be handled again (if the new position still causes a collision).
     * A counter will be incremented each time this option is used, and provided when
     * {@link CollisionAware#onCollision} is invoked.
     */
    RERUN,

    /**
     * Further collision detection for the {@link Collidable} is aborted. Note that changes
     * of the bounds or position inside {@link CollisionAware#onCollision} are kept,
     * but might lead to undetected collisions.
     */
    ABORT
}


