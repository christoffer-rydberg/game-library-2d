package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Enableable;
import com.gamelibrary2d.markers.Parent;
import com.gamelibrary2d.updates.UpdateObject;

/**
 * Defines a {@link Renderable} object that can be positioned, scaled and rotated.
 *
 * @author Christoffer Rydberg
 */
public interface GameObject extends Transformable, Renderable, Enableable, UpdateObject {

    /**
     * The object's opacity.
     */
    float getOpacity();

    /**
     * Sets the object's {@link #getOpacity() opacity}.
     */
    void setOpacity(float opacity);

    /**
     * @return The object's bounds. This is unaffected by other properties, such as
     * position, scale and rotation. Bounds can used to determine how much
     * space the object takes up in a {@link Parent} or to determine if
     * objects are overlapping (collision detection).
     */
    Rectangle getBounds();

    /**
     * Sets the rotation of the object in degrees, clockwise, starting from the
     * positive y-axis.
     */
    void setRotation(float rotation);

}