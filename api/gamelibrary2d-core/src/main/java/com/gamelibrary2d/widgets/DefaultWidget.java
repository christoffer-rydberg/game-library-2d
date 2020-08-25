package com.gamelibrary2d.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.ComposableObject;

public final class DefaultWidget<T extends Renderable> extends AbstractObservableWidget<T> implements ComposableObject<T> {

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
}
