package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.objects.GameObject;

public interface MouseMoveListener {
    void onMouseMove(GameObject obj, float projectedX, float projectedY, boolean drag);
}
