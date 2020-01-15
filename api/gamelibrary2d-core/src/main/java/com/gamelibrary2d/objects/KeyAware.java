package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;

/**
 * {@link GameObject Game objects} implementing this interface can receive keyboard events.
 * Keyboard events are routed by the {@link com.gamelibrary2d.FocusManager FocusManager} to all focused instances of {@link KeyAware}.
 *
 * @author Christoffer Rydberg
 */
public interface KeyAware {

    /**
     * @return True if the object is focused, false otherwise.
     */
    boolean isFocused();

    /**
     * Sets the object to focused. This can be used to indicate that the
     * visualization and/or behavior of the object should change. Any object focused
     * through the FocusManager will automatically be notified of keyboard events
     * and when mouse events has finished.
     */
    void setFocused(boolean focused);

    /**
     * @return The object's bounds. This is unaffected by other properties, such as
     * position, scale and rotation.
     */
    Rectangle getBounds();

    /**
     * Handles char input events.
     *
     * @param charInput The Unicode code point of the character.
     */
    void charInputEvent(char charInput);

    /**
     * Handles key down events.
     *
     * @param key      The keyboard key that was pressed or released.
     * @param scanCode The system-specific scancode of the key.
     * @param repeat   True if the key action is repeat.
     * @param mods     Describes which modifier keys were held down.
     */
    void keyDownEvent(int key, int scanCode, boolean repeat, int mods);

    /**
     * Handles key release events.
     *
     * @param key      The keyboard key that was pressed or released.
     * @param scanCode The system-specific scancode of the key.
     * @param mods     Describes which modifier keys were held down.
     */
    void keyReleaseEvent(int key, int scanCode, int mods);

    /**
     * The purpose of this method is to notify focused objects of mouse events, even
     * if the events were handled by other objects. This can, for example, be used
     * to unfocus the object if the user clicks anywhere else.
     *
     * @param button The mouse button that was pressed/released.
     * @param action The key action (press or release).
     * @param mods   Describes which modifier keys were held down.
     */
    void mouseEventFinished(int button, int action, int mods);
}