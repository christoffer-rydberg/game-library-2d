package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.widgets.AbstractWidget;
import com.gamelibrary2d.components.widgets.Label;

public class Button extends AbstractWidget<Label> {
    private final Action onClick;
    private final Renderable background;

    public Button(Label label, Rectangle bounds, Action onClick) {
        this(label, null, bounds, onClick);
    }

    public Button(Label label, Renderable background, Rectangle bounds, Action onClick) {
        this.background = background;
        this.onClick = onClick;
        setContent(label);
        setBounds(bounds);
    }

    @Override
    protected void onRender(float alpha) {
        if (background != null) {
            background.render(alpha);
        }
        super.onRender(alpha);
    }

    @Override
    protected void onPointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        super.onPointerUp(id, button, x, y, projectedX, projectedY);
        onClick.perform();
    }
}