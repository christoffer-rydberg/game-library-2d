package com.gamelibrary2d.markers;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.input.KeyAction;

public interface MouseWhenFocusedAware {

    /**
     * Invoked by the {@link FocusManager} after a mouse button has been pressed or released.
     *
     * @param button The mouse button that was pressed/released.
     * @param action The key action (press or release).
     * @param mods   Describes which modifier keys were held down.
     */
    void onMouseButtonWhenFocused(int button, KeyAction action, int mods);
}
