package com.gamelibrary2d.demos.drawing;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.renderers.ArrayRenderer;

public class Geometry extends AbstractGameObject {
    private final PositionBuffer nodes;
    private final ArrayRenderer<PositionBuffer> renderer;

    public Geometry(PositionBuffer nodes, ArrayRenderer<PositionBuffer> renderer) {
        this.nodes = nodes;
        this.renderer = renderer;
    }

    public PositionBuffer nodes() {
        return nodes;
    }

    public ArrayRenderer<PositionBuffer> getRenderer() {
        return renderer;
    }

    @Override
    protected void onRender(float alpha) {
        renderer.render(alpha, nodes, 0, nodes.size());
    }

    @Override
    public Rectangle getBounds() {
        return Rectangle.EMPTY;
    }
}
