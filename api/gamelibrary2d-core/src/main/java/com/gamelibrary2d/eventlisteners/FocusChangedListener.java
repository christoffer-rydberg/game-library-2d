package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.markers.Focusable;

public interface FocusChangedListener {
    void onFocusChanged(Focusable object, boolean focused);
}