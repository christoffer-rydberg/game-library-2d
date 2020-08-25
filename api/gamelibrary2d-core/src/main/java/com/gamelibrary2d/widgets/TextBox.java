package com.gamelibrary2d.widgets;

import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.renderers.TextRenderer;

public class TextBox extends Label implements Bounded, Renderable, KeyAware {

    public TextBox() {

    }

    public TextBox(TextRenderer textRenderer) {
        super(textRenderer);
    }

    public TextBox(String text, TextRenderer textRenderer) {
        super(text, textRenderer);
    }

    public void addChar(char c) {
        setText(getText() + c);
    }

    public void removeLast() {
        String oldText = getText();
        int textLength = oldText.length();
        if (textLength == 0)
            return;
        setText(oldText.substring(0, textLength - 1));
    }

    @Override
    public void charInput(char charInput) {
        addChar(charInput);
    }

    @Override
    public void keyDown(int key, int scanCode, boolean repeat, int mods) {
        if (key == Keyboard.instance().keyBackspace()) {
            removeLast();
        }
    }

    @Override
    public void keyReleased(int key, int scanCode, int mods) {

    }
}