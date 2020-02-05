package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.objects.GameObject;

public interface MouseButtonDownListener {
    void onMouseButtonDown(GameObject obj, int button, int mods, float x, float y);
}
