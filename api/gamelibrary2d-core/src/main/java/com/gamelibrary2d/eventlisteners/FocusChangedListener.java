package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.objects.GameObject;

public interface FocusChangedListener {
    void onFocusChanged(GameObject sender, boolean focused);
}
