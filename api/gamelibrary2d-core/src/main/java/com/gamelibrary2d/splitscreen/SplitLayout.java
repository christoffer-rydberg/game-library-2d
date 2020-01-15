package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.objects.GameObject;

public interface SplitLayout {

    void update(GameObject target, Rectangle viewArea, float deltaTime, Disposer disposer);

    void render(float alpha);
}