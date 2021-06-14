package com.gamelibrary2d.components.containers;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.objects.GameObject;

public interface LayerObject<T extends Renderable> extends Layer<T>, GameObject {

}
