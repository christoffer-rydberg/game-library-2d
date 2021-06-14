package com.gamelibrary2d.components.denotations;

import com.gamelibrary2d.common.Rectangle;

public interface Bounded {
    /**
     * @return The object's bounds unaffected by its position, scale and rotation.
     */
    Rectangle getBounds();
}
