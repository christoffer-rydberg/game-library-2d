package com.gamelibrary2d.components.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.denotations.Bounded;

public final class DefaultGameObject<T extends Renderable> extends AbstractGameObject {
    private T content;
    private Rectangle bounds;
    private Renderable background;
    private Renderable foreground;

    public DefaultGameObject() {

    }

    public DefaultGameObject(T content) {
        this.content = content;
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

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    protected void onRender(float alpha) {
        if (background != null) {
            background.render(alpha);
        }

        if (content != null) {
            content.render(alpha);
        }

        if (foreground != null) {
            foreground.render(alpha);
        }
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : getContentBounds();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    private Rectangle getContentBounds() {
        if (content instanceof Bounded)
            return ((Bounded) content).getBounds();
        else
            return Rectangle.EMPTY;
    }
}
