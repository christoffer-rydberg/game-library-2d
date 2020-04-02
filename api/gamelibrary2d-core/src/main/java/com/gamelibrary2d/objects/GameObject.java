package com.gamelibrary2d.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;

/**
 * Defines a {@link Renderable} object that can be positioned, scaled and rotated.
 */
public interface GameObject extends Transformable, Bounded, Renderable {

    /**
     * The object's opacity.
     */
    float getOpacity();

    /**
     * Sets the object's {@link #getOpacity() opacity}.
     */
    void setOpacity(float opacity);

    /**
     * Checks if the object is enabled. Disabled objects are not rendered or updated.
     */
    boolean isEnabled();

    /**
     * Sets if the object is {@link #isEnabled() enabled}.
     */
    void setEnabled(boolean enabled);

}