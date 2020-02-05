package com.gamelibrary2d.layers;

import com.gamelibrary2d.framework.Renderable;

public class DynamicLayer<T extends Renderable> extends AbstractLayerObject<T> {
    private final Layer<Renderable> background = new BasicLayer<>();
    private final Layer<Renderable> foreground = new BasicLayer<>();
    private Renderable overlay;

    public Layer<Renderable> getBackground() {
        return background;
    }

    public Layer<Renderable> getForeground() {
        return foreground;
    }

    public void setOverlay(Renderable overlay) {
        this.overlay = overlay;
    }

    @Override
    public void onRenderProjected(float alpha) {
        background.render(alpha);
        super.onRenderProjected(alpha);
        foreground.render(alpha);
        if (overlay != null) {
            overlay.render(alpha);
        }
    }

    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        background.update(deltaTime);
        foreground.update(deltaTime);
    }

    @Override
    public void clear() {
        background.clear();
        super.clear();
        foreground.clear();
    }

    /**
     * Does not clear the foreground or background layers.
     */
    public void clearPrimary() {
        super.clear();
    }
}