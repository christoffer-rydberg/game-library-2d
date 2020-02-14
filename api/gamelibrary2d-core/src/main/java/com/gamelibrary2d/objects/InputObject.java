package com.gamelibrary2d.objects;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.input.KeyAction;
import com.gamelibrary2d.markers.FocusAware;
import com.gamelibrary2d.markers.MouseWhenFocusedAware;

public final class InputObject<T extends Renderable> extends AbstractMouseAwareObject<T>
        implements FocusAware, MouseWhenFocusedAware, ComposableObject<T> {
    private ParameterizedAction<T> onFocused;
    private ParameterizedAction<T> onUnfocused;

    private KeyAction focusAction = KeyAction.PRESSED;

    private boolean mouseEventHandled;

    public InputObject() {

    }

    public InputObject(T content) {
        super(content);
    }

    public void setFocusedHandler(ParameterizedAction<T> onFocused) {
        this.onFocused = onFocused;
    }

    public void setUnfocusedHandler(ParameterizedAction<T> onUnfocused) {
        this.onUnfocused = onUnfocused;
    }

    public void setFocusAction(KeyAction focusAction) {
        this.focusAction = focusAction;
    }

    @Override
    public void onFocused() {
        if (onFocused != null) {
            onFocused.invoke(getContent());
        }
    }

    @Override
    public void onUnfocused() {
        if (onUnfocused != null) {
            onUnfocused.invoke(getContent());
        }
    }

    @Override
    protected boolean isListeningToMouseClickEvents() {
        return true;
    }

    @Override
    protected boolean isListeningToMouseHoverEvents() {
        return false;
    }

    @Override
    protected boolean isListeningToMouseDragEvents() {
        return false;
    }

    @Override
    protected boolean handleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        mouseEventHandled = true;
        if (focusAction == KeyAction.PRESSED) {
            FocusManager.focus(this, false);
        }
        return true;
    }

    @Override
    protected boolean handleMouseHover(float projectedX, float projectedY) {
        return true;
    }

    @Override
    protected boolean handleMouseDrag(float projectedX, float projectedY) {
        return true;
    }

    @Override
    protected void handleMouseButtonRelease(int button, int mods, float projectedX, float projectedY) {
        mouseEventHandled = true;
        if (focusAction == KeyAction.RELEASED) {
            FocusManager.focus(this, false);
        }
    }

    @Override
    public void onMouseButtonWhenFocused(int button, KeyAction action, int mods) {
        if (!mouseEventHandled) {
            FocusManager.unfocus(this, false);
        }
        mouseEventHandled = false;
    }

    @Override
    public T getContent() {
        return super.getContent();
    }

    @Override
    public void setContent(T content) {
        super.setContent(content);
    }
}