package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.markers.Bounded;
import com.gamelibrary2d.renderers.LineRenderer;

public class Box implements Renderable, Bounded {
    private final Rectangle bounds;
    private final LineRenderer renderer;
    private final PositionBuffer nodes;

    public Box(Rectangle bounds, LineRenderer renderer) {
        this.bounds = bounds;
        this.renderer = renderer;
        this.nodes = PositionBuffer.create(new DefaultDisposer()); // TODO: Add real disposer
        nodes.add(bounds.xMin(), bounds.yMin());
        nodes.add(bounds.xMin(), bounds.yMax());
        nodes.add(bounds.xMax(), bounds.yMax());
        nodes.add(bounds.xMax(), bounds.yMin());
        nodes.add(bounds.xMin(), bounds.yMin());
    }

    public LineRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void render(float alpha) {
        renderer.render(alpha * 0.5f, nodes, 0, nodes.size());
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }
}
