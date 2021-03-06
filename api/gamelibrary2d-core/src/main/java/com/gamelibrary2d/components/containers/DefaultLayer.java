package com.gamelibrary2d.components.containers;

import com.gamelibrary2d.framework.Renderable;

public class DefaultLayer<T extends Renderable> extends AbstractLayer<T> {
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
    public void onRender(float alpha) {
        background.render(alpha);
        super.onRender(alpha);
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