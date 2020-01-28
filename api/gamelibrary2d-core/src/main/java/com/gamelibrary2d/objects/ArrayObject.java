package com.gamelibrary2d.objects;

import com.gamelibrary2d.renderers.ArrayRenderer;
import com.gamelibrary2d.resources.VertexArray;

public class ArrayObject<T extends VertexArray> extends AbstractGameObject {
    private final T array;
    private final ArrayRenderer<T> renderer;

    public ArrayObject(T array, ArrayRenderer<T> renderer) {
        this.array = array;
        this.renderer = renderer;
    }

    @Override
    protected void onRenderProjected(float alpha) {
        renderer.render(alpha, array);
    }
}
