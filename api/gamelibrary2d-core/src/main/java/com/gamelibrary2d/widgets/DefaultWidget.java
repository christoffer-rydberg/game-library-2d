package com.gamelibrary2d.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.ComposableObject;

public final class DefaultWidget<T extends Renderable> extends AbstractObservableWidget<T> implements ComposableObject<T> {
    private Renderable background;
    private Renderable foreground;

    public DefaultWidget() {

    }

    public DefaultWidget(T content) {
        super(content);
    }

    @Override
    public T getContent() {
        return super.getContent();
    }

    @Override
    public void setContent(T content) {
        super.setContent(content);
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
