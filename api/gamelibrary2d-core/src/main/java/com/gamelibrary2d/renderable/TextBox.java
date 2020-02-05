package com.gamelibrary2d.renderable;

import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Bounded;
import com.gamelibrary2d.markers.KeyAware;

public class TextBox extends Label implements Bounded, Renderable, KeyAware {

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
    public void onCharInput(char charInput) {
        addChar(charInput);
    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        if (key == Keyboard.instance().keyBackspace()) {
            removeLast();
        }
    }

    @Override
    public void onKeyRelease(int key, int scanCode, int mods) {

    }
}