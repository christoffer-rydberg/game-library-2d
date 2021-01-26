package com.gamelibrary2d.demos.drawing;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.renderers.ArrayRenderer;

public class Geometry extends AbstractGameObject<Renderable> {
    private final PositionBuffer nodes;
    private final ArrayRenderer<PositionBuffer> renderer;

    public Geometry(PositionBuffer nodes, ArrayRenderer<PositionBuffer> renderer) {
        this.nodes = nodes;
        this.renderer = renderer;
        this.setContent(a -> renderer.render(a, nodes, 0, nodes.size()));
    }

    public PositionBuffer nodes() {
        return nodes;
    }

    public ArrayRenderer<PositionBuffer> getRenderer() {
        return renderer;
    }
}
