package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.objects.KeyAware;

public interface FocusChangedListener {
    void onFocusChanged(KeyAware object, boolean focused);
}