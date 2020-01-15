package com.gamelibrary2d.collision;

/**
 * Result of the {@link CollisionDetectionArea#activate} method.
 */
public enum ActivationResult {

    /**
     * The {@link Collidable} is activated by the {@link CollisionDetectionArea}.
     */
    ACTIVATED,

    /**
     * The {@link Collidable} is activated by the {@link CollisionDetectionArea}. Search will continue to allow other
     * detection areas to be alerted of activation.
     */
    ACTIVATED_CONTINUE_SEARCH,

    /**
     * The {@link Collidable} is not activated by the {@link CollisionDetectionArea}.
     */
    NOT_ACTIVATED
}