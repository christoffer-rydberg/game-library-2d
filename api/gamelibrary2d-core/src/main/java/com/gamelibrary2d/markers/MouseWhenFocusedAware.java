package com.gamelibrary2d.markers;

import com.gamelibrary2d.FocusManager;

public interface MouseWhenFocusedAware {

    /**
     * Invoked by the {@link FocusManager} after a mouse button has been pressed,
     * even if the cursor is not over the object.
     *
     * @param button The mouse button that was pressed/released.
     * @param mods   Describes which modifier keys were held down.
     */
    void mouseButtonDownWhenFocused(int button, int mods);

    /**
     * Invoked by the {@link FocusManager} after a mouse button has been released,
     * even if the cursor is not over the object.
     *
     * @param button The mouse button that was pressed/released.
     * @param mods   Describes which modifier keys were held down.
     */
    void mouseButtonReleasedWhenFocused(int button, int mods);
}
