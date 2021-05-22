package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;

public final class DefaultGameObject<T extends Renderable> extends AbstractGameObject implements ComposableGameObject<T> {
    private T composition;
    private Rectangle bounds;
    private Renderable background;
    private Renderable foreground;

    public DefaultGameObject() {

    }

    public DefaultGameObject(T composition) {
        this.composition = composition;
    }

    public Renderable getBackground() {
        return background;
    }

    public void setBackground(Renderable background) {
        this.background = background;
    }

    public Renderable getForeground() {
        return foreground;
    }

    public void setForeground(Renderable foreground) {
        this.foreground = foreground;
    }

    @Override
    public T getComposition() {
        return composition;
    }

    public void setComposition(T composition) {
        this.composition = composition;
    }

    @Override
    protected void onRender(float alpha) {
        if (background != null) {
            background.render(alpha);
        }

        if (composition != null) {
            composition.render(alpha);
        }

        if (foreground != null) {
            foreground.render(alpha);
        }
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : getCompositionBounds();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    private Rectangle getCompositionBounds() {
        if (composition instanceof Bounded)
            return ((Bounded) composition).getBounds();
        else
            return Rectangle.EMPTY;
    }
}
