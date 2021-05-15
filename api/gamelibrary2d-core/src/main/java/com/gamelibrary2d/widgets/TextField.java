package com.gamelibrary2d.widgets;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.InputAware;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.renderers.TextRenderer;

public class TextField extends Label implements Renderable, KeyAware, InputAware {

    public TextField() {

    }

    public TextField(TextRenderer textRenderer) {
        super(textRenderer);
    }

    public TextField(String text, TextRenderer textRenderer) {
        super(text, textRenderer);
    }

    public TextField(TextRenderer textRenderer, Color color) {
        super(textRenderer, color);
    }

    public TextField(String text, TextRenderer textRenderer, Color color) {
        super(text, textRenderer, color);
    }

    public void addChar(char c) {
        setText(getText() + c);
    }

    public void removeLast() {
        String oldText = getText();
        int textLength = oldText.length();
        if (textLength > 0) {
            setText(oldText.substring(0, textLength - 1));
        }
    }

    @Override
    public void charInput(char charInput) {
        addChar(charInput);
    }

    @Override
    public void keyDown(int key, boolean repeat) {
        if (key == Keyboard.instance().keyBackspace()) {
            removeLast();
        }
    }

    @Override
    public void keyUp(int key) {

    }
}