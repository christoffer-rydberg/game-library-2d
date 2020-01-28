package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Point;

/**
 * Defines an entity with a position, scale and rotation.
 */
public interface Transformable {

    /**
     * @return The object's position.
     */
    Point getPosition();

    /**
     * @return The object's scale.
     */
    Point getScale();

    /**
     * @return The center point, relative to the object's position, used when
     * scaling and rotating the object.
     */
    Point getScaleAndRotationCenter();

    /**
     * @return The rotation of the object in degrees, clockwise, starting from the
     * positive y-axis.
     */
    float getRotation();
}
