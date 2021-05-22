package com.gamelibrary2d.markers;

import com.gamelibrary2d.framework.Renderable;

public interface Composable<T extends Renderable> {
    T getComposition();
}
