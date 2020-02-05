package com.gamelibrary2d.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;

/**
 * Defines a {@link Renderable} object that can be positioned, scaled and rotated.
 *
 * @author Christoffer Rydberg
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
     * Sets the rotation of the object in degrees, clockwise, starting from the
     * positive y-axis.
     */
    void setRotation(float rotation);

    boolean isEnabled();

    void setEnabled(boolean enabled);

}