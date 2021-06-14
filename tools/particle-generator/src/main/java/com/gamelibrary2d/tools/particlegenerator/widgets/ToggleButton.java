package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.components.widgets.AbstractWidget;
import com.gamelibrary2d.components.widgets.Label;

public class ToggleButton extends AbstractWidget<Label> {
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
    protected void onPointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        super.onPointerUp(id, button, x, y, projectedX, projectedY);
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

    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
    }
}