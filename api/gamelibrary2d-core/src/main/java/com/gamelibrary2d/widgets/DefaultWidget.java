package com.gamelibrary2d.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.FocusAware;
import com.gamelibrary2d.markers.MouseWhenFocusedAware;
import com.gamelibrary2d.objects.ComposableObject;

public final class DefaultWidget<T extends Renderable> extends AbstractWidget<T>
        implements FocusAware, MouseWhenFocusedAware, ComposableObject<T> {

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
}