package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.updates.UpdateObject;

/**
 * Interface for all visualized objects in the game.
 *
 * @author Christoffer Rydberg
 */
public interface GameObject extends UpdateObject, Renderable {

    /**
     * Renders the object.
     *
     * @param alpha The opacity factor which will be multiplied to the opacity of the
     *              object when rendering.
     */
    @Override
    void render(float alpha);

    /**
     * @return The object's bounds. This is unaffected by other properties, such as
     * position, scale and rotation. Bounds can used to determine how much
     * space the object takes up in a {@link Container} or to determine if
     * objects are overlapping (collision detection).
     */
    Rectangle getBounds();

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

    /**
     * Sets the rotation of the object in degrees, clockwise, starting from the
     * positive y-axis.
     */
    void setRotation(float rotation);

    /**
     * Determines if the specified pixel coordinate is visible. Used for
     * hit-detection, for example when clicking on the object.
     *
     * @param projectedX The x-coordinate, projected to the orientation of the object so
     *                   that it can be directly compared to the object's bounds.
     * @param projectedY The y-coordinate, projected to the orientation of the object so
     *                   that it can be directly compared to the object's bounds.
     * @return True if the pixel is visible, false otherwise.
     */
    boolean isPixelVisible(float projectedX, float projectedY);

    /**
     * Disabled objects will ignore mouse events and not be updated or rendered by
     * their containers. Additionally, an object cannot be focused and disabled at
     * the same time. Focusing a disabled object will enable it, and disabling a
     * focused object will unfocus it. This is used as an alternative to completely
     * removing an object from its container, so that it can still maintain its
     * position and be enabled at will (without reinserting it). If you just want to
     * hide the object temporarily, but maintain all functionality, consider setting
     * opacity to 0 instead.
     */
    boolean isEnabled();

    /**
     * Sets {@link #isEnabled enabled} to true or false.
     */
    void setEnabled(boolean enabled);

    /**
     * Gets the opacity of the object.
     *
     * @return The opacity of the object. 0 is fully transparent and 1 is fully
     * opaque.
     */
    float getOpacity();

    /**
     * Sets the opacity of the object. 0 is fully transparent and 1 is fully opaque.
     */
    void setOpacity(float opacity);
}