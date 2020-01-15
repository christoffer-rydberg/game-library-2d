package com.gamelibrary2d.objects;

/**
 * {@link GameObject Game objects} implementing this interface will receive mouse events routed by the parent
 * {@link Container}. Mouse coordinates are projected to the position, scale and rotation of the object.
 *
 * @author Christoffer Rydberg
 */
public interface MouseAware {

    /**
     * Handles mouse button down events.
     *
     * @param button     The mouse button that was pressed.
     * @param mods       Describes which modifier keys were held down.
     * @param projectedX The x-coordinate of the mouse cursor projected to the orientation
     *                   of the object.
     * @param projectedY The y-coordinate of the mouse cursor projected to the orientation
     *                   of the object.
     * @return True if the mouse event is handled and should not be routed to other
     * objects, false otherwise.
     */
    boolean mouseButtonDownEvent(int button, int mods, float projectedX, float projectedY);

    /**
     * Handles mouse move events.
     *
     * @param projectedX The x-coordinate of the mouse cursor projected to the orientation
     *                   of the object.
     * @param projectedY The y-coordinate of the mouse cursor projected to the orientation
     *                   of the object.
     * @return True if the mouse event is handled and should not be routed to other
     * objects, false otherwise.
     */
    boolean mouseMoveEvent(float projectedX, float projectedY, boolean drag);

    /**
     * Handles mouse button release events.
     *
     * @param button     The mouse button that was released.
     * @param mods       Describes which modifier keys were held down.
     * @param projectedX The x-coordinate of the mouse cursor projected to the orientation
     *                   of the object.
     * @param projectedY The y-coordinate of the mouse cursor projected to the orientation
     *                   of the object.
     * @return True if the mouse event is handled and should not be routed to other
     * objects, false otherwise.
     */
    boolean mouseButtonReleaseEvent(int button, int mods, float projectedX, float projectedY);

}