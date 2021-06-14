package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.denotations.Bounded;
import com.gamelibrary2d.resources.Quad;

public class RenderCache<T extends Renderable> implements Renderable, Bounded {
    private final T content;
    private final Rectangle bounds;
    private final SurfaceRenderer renderer;
    private final FrameBufferRenderer frameBufferRenderer;
    private boolean caching;
    private boolean cached;

    private RenderCache(T content, Rectangle bounds, Disposer disposer) {
        this.content = content;
        this.bounds = bounds;
        this.frameBufferRenderer = FrameBufferRenderer.create(bounds, disposer);
        this.renderer = new SurfaceRenderer<>(
                Quad.create(bounds, disposer),
                frameBufferRenderer.getFrameBuffer().getTexture());
    }

    public static <T extends Renderable> RenderCache<T> create(T content, Rectangle bounds, Disposer disposer) {
        return new RenderCache<>(content, bounds, disposer);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public void flushCache() {
        cached = false;
    }

    public boolean isCaching() {
        return caching;
    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    @Override
    public void render(float alpha) {
        if (!cached) {
            frameBufferRenderer.render(content, 1f);
            cached = caching;
        }

        renderer.render(alpha);
    }

    public T getContent() {
        return content;
    }
}