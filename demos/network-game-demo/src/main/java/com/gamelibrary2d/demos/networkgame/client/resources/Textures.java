package com.gamelibrary2d.demos.networkgame.client.resources;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.client.Settings;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;

public class Textures {
    private static Texture button;
    private static Texture inputField;

    public static void create(Disposer disposer) {
        button = createQuadStackTexture(
                Settings.BUTTON_SIZE,
                Color.WHITE.divide(10),
                Color.WHITE,
                10,
                disposer);

        inputField = button;
    }

    public static Texture button() {
        return button;
    }

    public static Texture inputField() {
        return inputField;
    }

    public static Texture createQuadStackTexture(Rectangle bounds, Color bottom, Color top, int depth, Disposer disposer) {
        var deltaColor = top.subtract(bottom);

        var layers = new Renderable[depth];
        for (int i = 0; i < depth; ++i) {
            var interpolation = i / (float) depth;
            layers[i] = createQuadRenderer(
                    bounds.pad(i * -1),
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

    public static Renderable createQuadRenderer(Rectangle bounds, Color color, Disposer disposer) {
        var renderer = new SurfaceRenderer(Quad.create(bounds, disposer));
        renderer.getParameters().setRgba(color);
        return renderer;
    }
}
