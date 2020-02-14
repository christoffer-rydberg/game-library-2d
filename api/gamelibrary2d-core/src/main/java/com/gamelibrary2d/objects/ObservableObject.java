package com.gamelibrary2d.objects;

import com.gamelibrary2d.framework.Renderable;

public final class ObservableObject<T extends Renderable> extends AbstractObservableObject<T> implements ComposableObject<T> {

    public ObservableObject() {

    }

    public ObservableObject(T content) {
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
