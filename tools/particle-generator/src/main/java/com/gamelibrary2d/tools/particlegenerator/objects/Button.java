package com.gamelibrary2d.tools.particlegenerator.objects;

import com.gamelibrary2d.widgets.AbstractObservableWidget;
import com.gamelibrary2d.objects.ComposableObject;
import com.gamelibrary2d.widgets.Label;

public class Button extends AbstractObservableWidget<Label> implements ComposableObject<Label> {

    public Button() {
        setContent(new Label());
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