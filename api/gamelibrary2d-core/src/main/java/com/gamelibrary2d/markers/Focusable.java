package com.gamelibrary2d.markers;

import com.gamelibrary2d.objects.GameObject;

/**
 * {@link GameObject Game objects} implementing this interface can receive keyboard events.
 * Keyboard events are routed by the {@link com.gamelibrary2d.FocusManager FocusManager} to all focused instances of {@link Focusable}.
 *
 * @author Christoffer Rydberg
 */
public interface Focusable {

    /**
     * @return True if the object is focused, false otherwise.
     */
    boolean isFocused();

    /**
     * Sets the object to focused or unfocused. This can be used to indicate that the
     * visualization and/or behavior of the object should change. Any object focused
     * through the FocusManager will automatically be notified of keyboard events
     * and when mouse button events has finished.
     */
    void setFocused(boolean focused);

    /**
     * The purpose of this method is to notify focused objects of mouse events, even
     * if the events were handled by other objects. This can, for example, be used
     * to unfocus the object if the user clicks anywhere else.
     *
     * @param button The mouse button that was pressed/released.
     * @param action The key action (press or release).
     * @param mods   Describes which modifier keys were held down.
     */
    void mouseButtonEventFinished(int button, int action, int mods);
}