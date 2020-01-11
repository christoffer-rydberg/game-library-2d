package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.objects.GameObject;

public interface MouseClickListener {
    void onMouseClick(GameObject obj, int button, int mods, float projectedX, float projectedY);
}
