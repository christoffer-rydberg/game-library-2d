package com.gamelibrary2d.markers;

import com.gamelibrary2d.FocusManager;

public interface PointerWhenFocusedAware {

    /**
     * Invoked by the {@link FocusManager} after a pointer down action,
     * even if the cursor is not over the object.
     *
     * @param id     The id of the pointer.
     * @param button The id of the pointer button.
     */
    void pointerDownWhenFocused(int id, int button);

    /**
     * Invoked by the {@link FocusManager} after a pointer up action,
     * even if the cursor is not over the object.
     *
     * @param id     The id of the pointer.
     * @param button The id of the pointer button.
     */
    void pointerUpWhenFocused(int id, int button);
}
