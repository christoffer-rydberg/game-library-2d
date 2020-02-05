package com.gamelibrary2d.tools.particlegenerator.objects;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.objects.AbstractObservableObject;
import com.gamelibrary2d.renderable.Label;

public class ToggleButton extends AbstractObservableObject<Label> {
    private boolean toggled;

    public ToggleButton() {
        setContent(new Label());
    }

    @Override
    public void setContent(Label content) {
        super.setContent(content);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        getContent().setFontColor(toggled ? Color.GREEN : Color.WHITE);
    }
}