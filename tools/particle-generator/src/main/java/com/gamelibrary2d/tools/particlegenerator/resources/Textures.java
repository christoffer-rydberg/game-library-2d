package com.gamelibrary2d.tools.particlegenerator.resources;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.resources.DefaultTexture;

import java.io.IOException;

public class Textures {
    private static Texture propertyBaseLine;
    private static Texture sliderHandle;

    public static void create(Disposer disposer) throws IOException {
        propertyBaseLine = createQuadStackTexture(
                Bounds.PROPERTY_BASE_LINE,
                Color.WHITE.divide(2),
                Color.WHITE,
                2,
                disposer);

        sliderHandle = DefaultTexture.create(
                Textures.class.getResource("/Images/sliderHandle.png"),
                disposer
        );
    }

    public static Texture propertyBaseLine() {
        return propertyBaseLine;
    }

    public static Texture sliderHandle() {
        return sliderHandle;
    }

    private static Texture createQuadStackTexture(Rectangle bounds, Color bottom, Color top, int depth, Disposer disposer) {
        var deltaColor = top.subtract(bottom);

        var layers = new Renderable[depth];
        for (int i = 0; i < depth; ++i) {
            var interpolation = (i + 1) / (float) depth;
            layers[i] = createQuadRenderer(
                    bounds.pad(0, -i),
                    bottom.add(deltaColor.multiply(interpolation)),
                    disposer);
        }

        Renderable r = a -> {
            for (var layer : layers) {
                layer.render(1f);
            }
        };

        return DefaultTexture.create(r, 1f, bounds, disposer);
    }

    private static Renderable createQuadRenderer(Rectangle bounds, Color color, Disposer disposer) {
        var renderer = new SurfaceRenderer(Quad.create(bounds, disposer));
        renderer.getParameters().setRgba(color);
        return renderer;
    }
}
