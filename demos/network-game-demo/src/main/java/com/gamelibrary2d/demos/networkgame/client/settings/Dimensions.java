package com.gamelibrary2d.demos.networkgame.client.settings;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Window;

public class Dimensions {
    private final Rectangle buttonSize;
    private final Rectangle inputFieldSize;

    public Dimensions(Window window) {
        buttonSize = resize(Rectangle.create(.2f, .05f), window);
        inputFieldSize = resize(Rectangle.create(.2f, .05f), window);
    }

    private Rectangle resize(Rectangle rect, Window window) {
        return rect.resize(window.getWidth(), window.getHeight());
    }

    public Rectangle getInputFieldSize() {
        return inputFieldSize;
    }

    public Rectangle getButtonSize() {
        return buttonSize;
    }
}
