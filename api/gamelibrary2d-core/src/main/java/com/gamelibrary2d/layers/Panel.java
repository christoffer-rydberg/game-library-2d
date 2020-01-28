package com.gamelibrary2d.layers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.objects.GameObject;

/**
 * A panel is a {@link LayerObject} with dynamically updated bounds. By
 * default, bounds are extended whenever an object is added and recalculated
 * whenever an object is removed. Panels also provide methods to stack objects.
 * This is done by adding and aligning objects to the edge of the current
 * bounds. You can create a customized panel by extending {@link AbstractPanel}.
 * This abstract base class holds all basic panel functionality. The
 * {@link DefaultPanel} class can be used to instantiate a basic panel.
 *
 * @author Christoffer Rydberg
 */
public interface Panel<T extends GameObject> extends LayerObject<T> {

    /**
     * Determines if the panel {@link #getBounds() bounds} should resize
     * automatically when objects are added or removed. Defaults to true.
     */
    boolean isAutoResizing();

    /**
     * Sets if the panel should {@link #isAutoResizing() auto resize}
     */
    void setAutoResizing(boolean autoResizing);

    /**
     * Expands the panel {@link #getBounds() bounds} to overlap the rotated and
     * scaled bounds of the specified object.
     */
    void expandBounds(T obj);

    /**
     * Expands the panel {@link #getBounds() bounds} to overlap the specified
     * bounds.
     */
    void expandBounds(Rectangle bounds);

    /**
     * Sets the panel {@link #getBounds() bounds}. Note that removing or adding
     * objects will affect the bounds unless {@link #isAutoResizing() auto resizing}
     * is set to false.
     */
    void setBounds(Rectangle bounds);

    /**
     * Recalculates and sets the {@link #getBounds() bounds} based on the rotated
     * and scaled bounds of all objects added to the panel.
     */
    void recalculateBounds();

    /**
     * Same as invoking {@link #stack(T, StackOrientation, float, float)} with the
     * offset and padding set to 0, and reposition set to false.
     */
    void stack(T obj, StackOrientation orientation);

    /**
     * Same as invoking {@link #stack(T, StackOrientation, float, float)} with the
     * padding set to 0, and reposition set to false.
     */
    void stack(T obj, StackOrientation orientation, float offset);

    /**
     * Same as invoking {@link #stack(T, StackOrientation, float, float, boolean)} with
     * reposition set to false.
     */
    void stack(T obj, StackOrientation orientation, float offset, float padding);

    /**
     * Adds the specified object to the panel aligned to its current bounds. The
     * bounds will increase as the new object is added. The purpose is to provide an
     * easy way to stack objects, horizontally or vertically, based on the specified
     * orientation. If stacked vertically, the x-coordinate of the object will
     * remain, and if stacked horizontally, the y-coordinate will remain. It is
     * important for all objects to have valid {@link GameObject#getBounds()
     * bounds}, since the bounds are used to calculate positions and increase the
     * panel bounds.
     *
     * @param obj         The added object.
     * @param orientation The stack orientation.
     * @param offset      The distance from the edge of the panel's bounds to the
     *                    opposite edge of the added object. A positive offset will
     *                    position the object away from the panel, while a negative
     *                    offset will position the object inwards towards the panel.
     * @param padding     Additional padding to the panel's bounds, applied from the
     *                    added object along the specified {@link StackOrientation}.
     * @param reposition  Indicates if the method is invoked in order to reposition
     *                    an object that has already been added to the panel. When
     *                    this flag is true, the object will not be added again,
     *                    only the position of the object and the bounds of the
     *                    panel will be updated (extended if needed).
     */
    void stack(T obj, StackOrientation orientation, float offset, float padding, boolean reposition);
}