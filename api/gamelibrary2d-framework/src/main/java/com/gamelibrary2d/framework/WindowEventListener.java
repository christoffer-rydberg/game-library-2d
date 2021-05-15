package com.gamelibrary2d.framework;

public interface WindowEventListener {

    /**
     * Handler for key action events.
     *
     * @param key    The keyboard key.
     * @param action The {@link KeyAction}.
     */
    void onKeyAction(int key, KeyAction action);

    /**
     * Handler for character input events.
     *
     * @param codePoint The Unicode code point of the character.
     */
    void onCharInput(char codePoint);

    /**
     * Handler for pointer moved events.
     *
     * @param id   The id of the pointer.
     * @param posX The x-coordinate, relative to the left edge of the window.
     * @param posY The y-coordinate, relative to the bottom edge of the window.
     */
    void onPointerMove(int id, float posX, float posY);

    /**
     * Handler for pointer enter events. Occurs whenever a pointer enters the game window.
     *
     * @param id The id of the pointer.
     */
    void onPointerEnter(int id);

    /**
     * Handler for pointer leave events. Occurs whenever a pointer leaves the game window.
     *
     * @param id The id of the pointer.
     */
    void onPointerLeave(int id);

    /**
     * Handler for pointer action events.
     *
     * @param id     The id of the pointer.
     * @param button The id of the pointer button.
     * @param posX   The x-coordinate, relative to the left edge of the window.
     * @param posY   The y-coordinate, relative to the bottom edge of the window.
     * @param action The {@link PointerAction}.
     */
    void onPointerAction(int id, int button, float posX, float posY, PointerAction action);

    /**
     * Handler for scroll events.
     *
     * @param id      The id of the pointer.
     * @param xOffset The scroll offset along the x-axis
     * @param yOffset The scroll offset along the y-axis.
     */
    void onScroll(int id, float xOffset, float yOffset);
}
