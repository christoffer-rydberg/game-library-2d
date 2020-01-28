package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.renderers.Renderer;

public class InteractiveObject extends AbstractInteractiveObject {

    private Renderer renderer;
    private Renderer focusedRenderer;
    private boolean lazyHitDetection;

    public InteractiveObject() {
        setBounds(null);
    }

    public InteractiveObject(Renderer renderer) {
        setBounds(null);
        setRenderer(renderer);
    }

    public InteractiveObject(Renderer renderer, Rectangle bounds) {
        setRenderer(renderer);
        setBounds(bounds);
    }

    @Override
    public Rectangle getBounds() {
        var bounds = super.getBounds();
        if (bounds != null)
            return bounds;
        if (isFocused() && focusedRenderer != null)
            return focusedRenderer.getBounds();
        return renderer != null ? renderer.getBounds() : Rectangle.EMPTY;
    }

    /**
     * Sets the {@link #getBounds bounds} of the object. Setting the bounds to null
     * will default to the bounds of the active renderer. To specify empty bounds,
     * use {@link Rectangle#EMPTY}
     */
    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public Renderer getFocusedRenderer() {
        return focusedRenderer;
    }

    public void setFocusedRenderer(Renderer focusedRenderer) {
        this.focusedRenderer = focusedRenderer;
    }

    /**
     * If lazy hit detection is enabled, the only requirement for a {@link #isPixelVisible hit detection} is that the
     * coordinates are within the object's {@link #getBounds bounds}. If disabled, the
     * {@link Renderer#isVisible isVisible} method of the object's renderer will be invoked as well.
     */
    public boolean getLazyHitDetection() {
        return lazyHitDetection;
    }

    /**
     * Setter for the {@link #getLazyHitDetection lazyHitDetection} field.
     */
    public void setLazyHitDetection(boolean lazyHitDetection) {
        this.lazyHitDetection = lazyHitDetection;
    }

    @Override
    public boolean isPixelVisible(float x, float y) {
        return lazyHitDetection ? getBounds().isInside(x, y)
                : getBounds().isInside(x, y) && (renderer == null || renderer.isVisible(x, y));
    }

    @Override
    protected void onRenderProjected(float alpha) {
        if (isFocused() && focusedRenderer != null) {
            focusedRenderer.render(alpha);
        } else if (renderer != null) {
            renderer.render(alpha);
        }
    }
}