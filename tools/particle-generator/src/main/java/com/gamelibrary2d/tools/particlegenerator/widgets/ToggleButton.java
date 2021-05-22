package com.gamelibrary2d.tools.particlegenerator.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.objects.ComposableGameObject;
import com.gamelibrary2d.widgets.AbstractWidget;
import com.gamelibrary2d.widgets.Label;

public class ToggleButton extends AbstractWidget<Label> implements ComposableGameObject<Label> {
    private boolean toggled;
    private Color defaultColor;

    public ToggleButton() {
        setComposition(new Label());
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (toggled) {
            defaultColor = getComposition().getColor();
            getComposition().setColor(Color.GREEN);
        } else {
            getComposition().setColor(defaultColor);
        }
    }

    @Override
    protected void onPointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        super.onPointerUp(id, button, x, y, projectedX, projectedY);
        setToggled(!isToggled());
    }

    @Override
    public Label getComposition() {
        return super.getComposition();
    }

    @Override
    public void setComposition(Label composition) {
        super.setComposition(composition);
    }

    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
    }
}