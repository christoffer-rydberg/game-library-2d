package com.gamelibrary2d.markers;

public interface KeyAware {
    /**
     * Handles key down events.
     *
     * @param key    The keyboard key that was pressed.
     * @param repeat True if the key action is repeat.
     */
    void keyDown(int key, boolean repeat);

    /**
     * Handles key up events.
     *
     * @param key The keyboard key that was released.
     */
    void keyUp(int key);

}
