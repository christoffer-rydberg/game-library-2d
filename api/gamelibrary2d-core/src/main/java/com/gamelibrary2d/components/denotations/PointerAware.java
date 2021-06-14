package com.gamelibrary2d.components.denotations;

import com.gamelibrary2d.components.objects.GameObject;

/**
 * {@link GameObject Game objects} implementing this interface will receive pointer actions routed by the parent
 * {@link Parent}. Pointer coordinates are projected to the position, scale and rotation of the object.
 */
public interface PointerAware {

    /**
     * Handles pointer down actions.
     *
     * @param id         The id of the pointer.
     * @param button     The id of the pointer button.
     * @param x          The x-coordinate of the pointer.
     * @param y          The y-coordinate of the pointer.
     * @param projectedX The x-coordinate of the pointer projected to the parent container.
     * @param projectedY The y-coordinate of the pointer projected to the parent container.
     * @return True if the action is handled and should not be routed to other objects, false otherwise.
     */
    boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY);

    /**
     * Handles pointer move actions.
     *
     * @param id         The id of the pointer.
     * @param x          The x-coordinate of the pointer.
     * @param y          The y-coordinate of the pointer.
     * @param projectedX The x-coordinate of the pointer projected to the parent container.
     * @param projectedY The y-coordinate of the pointer projected to the parent container.
     * @return True if the action is handled and should not be routed to other objects, false otherwise.
     */
    boolean pointerMove(int id, float x, float y, float projectedX, float projectedY);

    /**
     * Handles pointer up actions.
     *
     * @param id         The id of the pointer.
     * @param button     The id of the pointer button.
     * @param x          The x-coordinate of the pointer.
     * @param y          The y-coordinate of the pointer.
     * @param projectedX The x-coordinate of the pointer projected to the parent container.
     * @param projectedY The y-coordinate of the pointer projected to the parent container.
     */
    void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY);

}