package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.renderers.Renderer;

public class BasicGameObject extends AbstractGameObject {

    private Renderer renderer;

    public BasicGameObject() {
        this(null, null);
    }

    public BasicGameObject(Renderer renderer) {
        this(renderer, null);
    }

    public BasicGameObject(Renderer renderer, Rectangle bounds) {
        setRenderer(renderer);
        setBounds(bounds);
    }

    @Override
    public Rectangle getBounds() {
        var bounds = super.getBounds();
        if (bounds != null)
            return bounds;
        else if (renderer != null)
            return renderer.getBounds();
        else
            return Rectangle.EMPTY;
    }

    /**
     * Sets the {@link #getBounds bounds} of the object. Setting the bounds to null
     * will default to the bounds of the {@link #getRenderer renderer}. To specify
     * empty bounds, use {@link Rectangle#EMPTY}
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

    @Override
    protected void onRenderProjected(float alpha) {
        if (renderer != null) {
            renderer.render(alpha);
        }
    }
}