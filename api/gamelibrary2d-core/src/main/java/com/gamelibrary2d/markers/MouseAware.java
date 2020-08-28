package com.gamelibrary2d.markers;

import com.gamelibrary2d.objects.GameObject;

/**
 * {@link GameObject Game objects} implementing this interface will receive mouse events routed by the parent
 * {@link Parent}. Mouse coordinates are projected to the position, scale and rotation of the object.
 */
public interface MouseAware {

    /**
     * Handles mouse button down events.
     *
     * @param button     The mouse button that was pressed.
     * @param mods       Describes which modifier keys were held down.
     * @param x          The x-coordinate of the mouse cursor.
     * @param y          The y-coordinate of the mouse cursor.
     * @param projectedX The x-coordinate of the mouse cursor projected to the parent container.
     * @param projectedY The y-coordinate of the mouse cursor projected to the parent container.
     * @return True if the mouse event is handled and should not be routed to other
     * objects, false otherwise.
     */
    boolean mouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY);

    /**
     * Handles mouse move events.
     *
     * @param x          The x-coordinate of the mouse cursor.
     * @param y          The y-coordinate of the mouse cursor.
     * @param projectedX The x-coordinate of the mouse cursor projected to the parent container.
     * @param projectedY The y-coordinate of the mouse cursor projected to the parent container.
     * @return True if the mouse event is handled and should not be routed to other
     * objects, false otherwise.
     */
    boolean mouseMove(float x, float y, float projectedX, float projectedY);

    /**
     * Handles mouse button release events.
     *
     * @param button     The mouse button that was released.
     * @param mods       Describes which modifier keys were held down.
     * @param x          The x-coordinate of the mouse cursor.
     * @param y          The y-coordinate of the mouse cursor.
     * @param projectedX The x-coordinate of the mouse cursor projected to the parent container.
     * @param projectedY The y-coordinate of the mouse cursor projected to the parent container.
     */
    void mouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY);

}