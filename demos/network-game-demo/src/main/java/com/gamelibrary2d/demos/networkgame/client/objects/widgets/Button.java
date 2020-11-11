package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.util.HorizontalTextAlignment;
import com.gamelibrary2d.util.VerticalTextAlignment;
import com.gamelibrary2d.widgets.AbstractWidget;
import com.gamelibrary2d.widgets.Label;

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
    protected void onRenderProjected(float alpha) {
        if (background != null) {
            background.render(alpha);
        }
        super.onRenderProjected(alpha);
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        super.onMouseButtonReleased(button, mods, x, y, projectedX, projectedY);
        onClick.invoke();
    }
}