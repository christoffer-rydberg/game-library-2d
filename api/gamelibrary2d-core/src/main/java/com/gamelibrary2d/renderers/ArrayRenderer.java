package com.gamelibrary2d.renderers;

import com.gamelibrary2d.resources.VertexArray;

public interface ArrayRenderer<T extends VertexArray> {
    void render(float alpha, T array);
}
