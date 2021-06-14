package com.gamelibrary2d.components.widgets;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;

public final class DefaultWidget<T extends Renderable> extends AbstractObservableWidget<T> {
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
    protected void onRender(float alpha) {
        if (background != null) {
            background.render(alpha);
        }

        super.onRender(alpha);

        if (foreground != null) {
            foreground.render(alpha);
        }
    }

    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
    }
}
