package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;

public final class BasicObject<T extends Renderable> extends AbstractGameObject<T> {

    public BasicObject() {

    }

    public BasicObject(T content) {
        super(content);
    }

    public BasicObject(T content, Rectangle bounds) {
        super(content);
        setBounds(bounds);
    }

    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
    }

    @Override
    public void setContent(T content) {
        super.setContent(content);
    }
}