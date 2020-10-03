package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;

public final class DefaultGameObject<T extends Renderable> extends AbstractGameObject<T> implements ComposableObject<T> {
    private Renderable background;
    private Renderable foreground;

    public DefaultGameObject() {

    }

    public DefaultGameObject(T content) {
        super(content);
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
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
    }

    @Override
    public T getContent() {
        return super.getContent();
    }

    @Override
    public void setContent(T content) {
        super.setContent(content);
    }

    @Override
    protected void onRenderProjected(float alpha) {
        if (background != null) {
            background.render(alpha);
        }

        super.onRenderProjected(alpha);

        if (foreground != null) {
            foreground.render(alpha);
        }
    }
}