package com.gamelibrary2d.collision;

import com.gamelibrary2d.common.Rectangle;

interface InternalCollidable {

    /**
     * Used together with the position to determine collisions.
     */
    Rectangle getBounds();

    /**
     * Used together with the {@link #getBounds bounds} to determine collisions.
     */
    float getPosX();

    /**
     * Used together with the {@link #getBounds bounds} to determine collisions.
     */
    float getPosY();
}
