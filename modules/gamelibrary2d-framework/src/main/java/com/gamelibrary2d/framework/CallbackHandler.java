package com.gamelibrary2d.framework;

public interface CallbackHandler {
    /**
     * Callback listener for keyboard keys.
     *
     * @param key      The keyboard key that was pressed or released.
     * @param scancode The system-specific scancode of the key.
     * @param action   The key action (press, repeat or release).
     * @param mods     Describes which modifier keys were held down.
     */
    void onKeyCallback(int key, int scancode, int action, int mods);

    /**
     * Callback listener for characters.
     *
     * @param codePoint The Unicode code point of the character.
     */
    void onCharCallback(char codePoint);

    /**
     * Callback listener for mouse cursor.
     *
     * @param xCoordinate The x-coordinate, relative to the left edge of the window.
     * @param yCoordinate The y-coordinate, relative to the bottom edge of the
     *                    window.
     */
    void onCursorPosCallback(double xCoordinate, double yCoordinate);

    /**
     * Callback listener for mouse cursor entering or leaving the game window.
     *
     * @param cursorEnter True if the cursor entered the window's area, or false if
     *                    it left it.
     */
    void onCursorEnterCallback(boolean cursorEnter);

    /**
     * @param button The mouse button that was pressed or released.
     * @param action The key action (press or release).
     * @param mods   Describes which modifier keys were held down.
     */
    void onMouseButtonCallback(int button, int action, int mods);

    /**
     * Callback listener for mouse scrolling.
     *
     * @param xoffset The scroll offset along the x-axis
     * @param yoffset The scroll offset along the y-axis.
     */
    void onScrollCallback(double xoffset, double yoffset);
}
