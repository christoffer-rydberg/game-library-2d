package com.gamelibrary2d.markers;

public interface KeyAware {

    /**
     * Handles char input events.
     *
     * @param charInput The Unicode code point of the character.
     */
    void onCharInput(char charInput);
    
    /**
     * Handles key down events.
     *
     * @param key      The keyboard key that was pressed or released.
     * @param scanCode The system-specific scancode of the key.
     * @param repeat   True if the key action is repeat.
     * @param mods     Describes which modifier keys were held down.
     */
    void onKeyDown(int key, int scanCode, boolean repeat, int mods);

    /**
     * Handles key release events.
     *
     * @param key      The keyboard key that was pressed or released.
     * @param scanCode The system-specific scancode of the key.
     * @param mods     Describes which modifier keys were held down.
     */
    void onKeyReleased(int key, int scanCode, int mods);

}
