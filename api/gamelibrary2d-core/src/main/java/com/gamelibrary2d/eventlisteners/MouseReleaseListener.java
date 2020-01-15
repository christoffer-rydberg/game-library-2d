package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.objects.GameObject;

public interface MouseReleaseListener {
    void onMouseRelease(GameObject obj, int button, int mods, float projectedX, float projectedY);
}