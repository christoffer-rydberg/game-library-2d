package com.gamelibrary2d.collision;

enum InsertionResult {

    /**
     * The {@link Collidable} has been activated by one or more {@link ActivationArea}'s.
     * It has been inserted in the collision quad tree.
     */
    INSERTED_ACTIVE,

    /**
     * The {@link Collidable} was only detected near the edge of one or more {@link ActivationArea}'s.
     * It has been inserted in the collision quad tree but should not be updated.
     */
    INSERTED_NEAR_EDGE,

    /**
     * The {@link Collidable} has not been inserted in the collision quad tree,
     * since it wasn't activated by any {@link ActivationArea}.
     */
    NOT_INSERTED
}
