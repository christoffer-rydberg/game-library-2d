package com.gamelibrary2d.layers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.disposal.ResourceDisposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;
import com.gamelibrary2d.renderers.BitmapRenderer;

public class Bitmap<T extends Renderable> implements Renderable, Bounded {
    private final ResourceDisposer disposer;
    private final T content;
    private boolean caching;
    private boolean cached;
    private Rectangle bounds;
    private BitmapRenderer bitmapRenderer;

    public Bitmap(T content, Rectangle bounds, Disposer disposer) {
        this.content = content;
        this.bounds = bounds;
        this.disposer = new ResourceDisposer(disposer);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
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
            if (bitmapRenderer == null || !bitmapRenderer.getArea().equals(bounds)) {
                disposer.dispose();
                bitmapRenderer = BitmapRenderer.create(bounds, disposer);
            }
            bitmapRenderer.render(() -> content.render(1f));
            cached = caching;
        }

        bitmapRenderer.renderFrameBuffer(alpha);
    }
}