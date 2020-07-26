package com.gamelibrary2d.collision;

/**
 * Result of the {@link ActivationArea#onActivation} method.
 */
public enum ActivationResult {

    /**
     * The {@link Collidable} is activated.
     */
    ACTIVATED,

    /**
     * The {@link Collidable} is activated.
     * Search for activated by other {@link ActivationArea}'s will continue.
     */
    ACTIVATED_CONTINUE_SEARCH,

    /**
     * The {@link Collidable} is detected near the edge of the activation area.
     * Search for activation by other {@link ActivationArea}'s will continue.
     * If the object is not activated by other areas, it can be collided with,
     * but it won't be updated (since an update could move it outside the
     * activation area and lead to an undetected collision).
     */
    NEAR_EDGE,

    /**
     * The {@link Collidable} is not activated by the {@link ActivationArea}.
     * Search for activation by other {@link ActivationArea}'s will continue.
     */
    NOT_DETECTED
}