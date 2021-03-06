package com.gamelibrary2d.demos.networkgame.client.resources;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.client.settings.Dimensions;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;

public class Textures {
    private static Texture button;
    private static Texture inputField;

    public static void create(Dimensions dimensions, Disposer disposer) {
        button = createQuadStackTexture(
                dimensions.getButtonSize(),
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
        Color deltaColor = top.subtract(bottom);

        Renderable[] layers = new Renderable[depth];
        for (int i = 0; i < depth; ++i) {
            float interpolation = i / (float) depth;
            layers[i] = createQuadRenderer(
                    bounds.pad(i * -1),
                    bottom.add(deltaColor.multiply(interpolation)),
                    disposer);
        }

        Renderable r = a -> {
            for (Renderable layer : layers) {
                layer.render(1f);
            }
        };

        return DefaultTexture.create(r, 1f, bounds, disposer);
    }

    public static Renderable createQuadRenderer(Rectangle bounds, Color color, Disposer disposer) {
        Renderer renderer = new SurfaceRenderer<>(Quad.create(bounds, disposer));
        renderer.getParameters().setColor(color);
        return renderer;
    }
}
