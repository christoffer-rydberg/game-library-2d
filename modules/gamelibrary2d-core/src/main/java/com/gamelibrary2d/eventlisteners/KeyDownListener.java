package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.objects.GameObject;

public interface KeyDownListener {
    void onKeyDown(GameObject sender, int key, int scanCode, boolean repeat, int mods);
}
