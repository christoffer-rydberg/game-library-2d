package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.renderers.LineRenderer;

public class Line implements Renderable {
    private final Point start = new Point();
    private final Point end = new Point();
    private final PositionBuffer nodes;
    private final LineRenderer renderer;

    private Line(PositionBuffer nodes) {
        this.nodes = nodes;
        this.renderer = new LineRenderer();
    }

    public static Line create(Disposer disposer) {
        return new Line(PositionBuffer.create(new float[4], disposer));
    }

    public LineRenderer getRenderer() {
        return renderer;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public void refresh() {
        nodes.set(0, start.getX(), start.getY());
        nodes.set(1, end.getX(), end.getY());
        nodes.updateGPU(0, 2);
    }

    @Override
    public void render(float alpha) {
        renderer.render(alpha, nodes, 0, nodes.size());
    }
}
