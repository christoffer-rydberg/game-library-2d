package com.gamelibrary2d.objects;

import com.gamelibrary2d.eventlisteners.*;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Mouse;

public class TextBox extends TextObject implements KeyDownListener, KeyReleaseListener, MouseClickListener, MouseMoveListener, MouseReleaseListener, CharInputListener {

    private int focusAction = Mouse.instance().actionRelease();

    private int focusButton = Mouse.instance().mouseButton1();

    public TextBox() {
        setListeningToMouseClickEvents(true);
        addKeyDownListener(this);
        addKeyReleaseListener(this);
        addMouseReleaseListener(this);
        addMouseClickListener(this);
        addCharInputListener(this);
    }

    public int getFocusAction() {
        return focusAction;
    }

    public void setFocusAction(int focusAction) {
        this.focusAction = focusAction;
    }

    public int getFocusButton() {
        return focusButton;
    }

    public void setFocusButton(int focusButton) {
        this.focusButton = focusButton;
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
    public void onKeyDown(GameObject sender, int key, int scanCode, boolean repeat, int mods) {
        if (key == Keyboard.instance().keyBackspace()) {
            removeLast();
        }
    }

    @Override
    public void onKeyRelease(GameObject sender, int key, int scanCode, int mods) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCharInput(GameObject sender, char charInput) {
        addChar(charInput);
    }

    @Override
    public void onMouseClick(GameObject obj, int button, int mods, float projectedX, float projectedY) {
        if (isMouseFocusEvent(button, Mouse.instance().actionPress())) {
            setFocused(true);
        }
    }

    @Override
    public void onMouseMove(GameObject obj, float projectedX, float projectedY, boolean drag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMouseRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {
        if (isPixelVisible(projectedX, projectedY)) {
            if (isMouseFocusEvent(button, Mouse.instance().actionRelease())) {
                setFocused(true);
            }
        }
    }

    @Override
    protected void onUnhandledMouseEvent(int button, int action, int mods) {
        if (isMouseFocusEvent(button, action)) {
            setFocused(false);
        }
    }

    private boolean isMouseFocusEvent(int button, int action) {
        return button == focusButton && action == focusAction;
    }
}