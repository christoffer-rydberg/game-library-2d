package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.objects.GameObject;

public interface KeyReleaseListener {
    void onKeyRelease(GameObject sender, int key, int scanCode, int mods);
}
