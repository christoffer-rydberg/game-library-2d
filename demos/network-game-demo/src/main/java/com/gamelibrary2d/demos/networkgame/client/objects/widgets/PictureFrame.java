package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;

import java.util.ArrayList;

public class PictureFrame implements Renderable {
    private final ArrayList<Renderable> sides;

    private PictureFrame(ArrayList<Renderable> sides) {
        this.sides = sides;
    }

    public static PictureFrame create(Rectangle outer, Rectangle inner, Color color, Disposer disposer) {
        var sides = new ArrayList<Renderable>(4);

        if (inner.xMin() > outer.xMin()) {
            var bounds = new Rectangle(outer.xMin(), outer.yMin(), inner.xMin(), outer.yMax());
            sides.add(createSideRenderer(color, bounds, disposer));
        }

        if (outer.xMax() > inner.xMax()) {
            var bounds = new Rectangle(inner.xMax(), outer.yMin(), outer.xMax(), outer.yMax());
            sides.add(createSideRenderer(color, bounds, disposer));
        }

        if (inner.yMin() > outer.yMin()) {
            var bounds = new Rectangle(outer.xMin(), outer.yMin(), outer.xMax(), inner.yMin());
            sides.add(createSideRenderer(color, bounds, disposer));
        }

        if (outer.yMax() > inner.yMax()) {
            var bounds = new Rectangle(outer.xMin(), inner.yMax(), outer.xMax(), outer.yMax());
            sides.add(createSideRenderer(color, bounds, disposer));
        }

        return new PictureFrame(sides);
    }

    private static Renderable createSideRenderer(Color color, Rectangle bounds, Disposer disposer) {
        var renderer = new SurfaceRenderer(Quad.create(bounds, disposer));
        renderer.getParameters().setRgb(color);
        return renderer;
    }

    @Override
    public void render(float alpha) {
        for (int i = 0; i < sides.size(); ++i) {
            sides.get(i).render(alpha);
        }
    }
}
