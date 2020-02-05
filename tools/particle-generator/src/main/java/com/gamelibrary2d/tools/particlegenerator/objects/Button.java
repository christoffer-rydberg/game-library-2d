package com.gamelibrary2d.tools.particlegenerator.objects;

import com.gamelibrary2d.objects.AbstractObservableObject;
import com.gamelibrary2d.renderable.Label;

public class Button extends AbstractObservableObject<Label> {

    public Button() {
        setContent(new Label());
    }

    @Override
    public void setContent(Label content) {
        super.setContent(content);
    }

}