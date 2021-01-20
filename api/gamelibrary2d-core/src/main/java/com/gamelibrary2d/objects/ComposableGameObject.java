package com.gamelibrary2d.objects;

import com.gamelibrary2d.framework.Renderable;

public interface ComposableGameObject<T extends Renderable> extends GameObject {

    T getContent();
}
