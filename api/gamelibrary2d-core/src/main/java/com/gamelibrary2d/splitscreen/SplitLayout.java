package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.components.objects.GameObject;

public interface SplitLayout {

    void update(GameObject target, Rectangle viewArea, float deltaTime);

    void render(float alpha);
}