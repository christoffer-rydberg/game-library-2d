package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.objects.ComposableObject;
import com.gamelibrary2d.widgets.AbstractWidget;
import com.gamelibrary2d.widgets.Label;

public class ToggleButton extends AbstractWidget<Label> implements ComposableObject<Label> {
    private boolean toggled;
    private Color defaultColor;

    public ToggleButton() {
        setContent(new Label());
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (toggled) {
            defaultColor = getContent().getColor();
            getContent().setColor(Color.GREEN);
        } else {
            getContent().setColor(defaultColor);
        }
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        super.onMouseButtonReleased(button, mods, x, y, projectedX, projectedY);
        setToggled(!isToggled());
    }

    @Override
    public Label getContent() {
        return super.getContent();
    }

    @Override
    public void setContent(Label content) {
        super.setContent(content);
    }
}