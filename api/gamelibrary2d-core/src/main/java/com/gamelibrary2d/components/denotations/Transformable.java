package com.gamelibrary2d.components.denotations;

import com.gamelibrary2d.common.Point;

/**
 * Defines an object with a position, scale and rotation.
 */
public interface Transformable {

    Point getPosition();

    default void setPosition(Point position) {
        getPosition().set(position);
    }

    default void setPosition(float x, float y) {
        getPosition().set(x, y);
    }

    Point getScale();

    default void setScale(Point scale) {
        getScale().set(scale);
    }

    default void setScale(float scale) {
        getScale().set(scale, scale);
    }

    default void setScale(float x, float y) {
        getScale().set(x, y);
    }

    /**
     * @return The center point, relative to the object's position, used when
     * scaling and rotating the object.
     */
    Point getScaleAndRotationCenter();

    default void setScaleAndRotationCenter(Point scaleAndRotationCenter) {
        getScaleAndRotationCenter().set(scaleAndRotationCenter);
    }

    default void setScaleAndRotationCenter(float x, float y) {
        getScaleAndRotationCenter().set(x, y);
    }

    /**
     * @return The rotation of the object in degrees, clockwise, starting from the
     * positive y-axis.
     */
    float getRotation();

    /**
     * Sets the {@link #getRotation() rotation}.
     */
    void setRotation(float rotation);
}
