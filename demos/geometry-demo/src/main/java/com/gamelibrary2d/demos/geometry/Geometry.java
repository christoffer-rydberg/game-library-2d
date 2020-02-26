package com.gamelibrary2d.demos.geometry;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.renderers.ArrayRenderer;
import com.gamelibrary2d.glUtil.PositionBuffer;

public class Geometry extends AbstractGameObject<Renderable> {
    private PositionBuffer nodes;

    public Geometry(PositionBuffer nodes, ArrayRenderer<PositionBuffer> renderer) {
        this.nodes = nodes;
        this.setContent(a -> renderer.render(a, nodes, 0, nodes.size()));
    }

    public PositionBuffer nodes() {
        return nodes;
    }
}
