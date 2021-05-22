package com.gamelibrary2d.widgets;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.ComposableGameObject;

public final class DefaultWidget<T extends Renderable> extends AbstractObservableWidget<T> implements ComposableGameObject<T> {
    private Renderable background;
    private Renderable foreground;

    public DefaultWidget() {

    }

    public DefaultWidget(T composition) {
        super(composition);
    }

    @Override
    public T getComposition() {
        return super.getComposition();
    }

    @Override
    public void setComposition(T composition) {
        super.setComposition(composition);
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
