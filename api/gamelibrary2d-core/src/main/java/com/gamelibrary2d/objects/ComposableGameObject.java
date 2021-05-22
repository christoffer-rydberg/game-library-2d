package com.gamelibrary2d.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Composable;

public interface ComposableGameObject<T extends Renderable> extends Composable<T>, GameObject {
    
}
