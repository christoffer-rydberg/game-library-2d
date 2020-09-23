package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.widgets.AbstractWidget;

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
    protected void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        super.onMouseButtonReleased(button, mods, x, y, projectedX, projectedY);
        onClick.invoke();
    }

}