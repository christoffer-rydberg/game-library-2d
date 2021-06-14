package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.widgets.AbstractWidget;

public class Button<T extends Renderable> extends AbstractWidget<T> {

    private final Action onClick;

    public Button(T content, Action onClick) {
        super.setContent(content);
        this.onClick = onClick;
    }

    @Override
    public T getContent() {
        return super.getContent();
    }

    @Override
    protected void onPointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        super.onPointerUp(id, button, x, y, projectedX, projectedY);
        onClick.perform();
    }

    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
    }
}