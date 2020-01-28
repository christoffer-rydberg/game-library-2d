package com.gamelibrary2d.layers;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.GameObject;

public interface LayerObject<T extends Renderable> extends Layer<T>, GameObject {

}
