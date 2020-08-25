package com.gamelibrary2d.widgets;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.input.ButtonAction;
import com.gamelibrary2d.markers.FocusAware;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.MouseWhenFocusedAware;
import com.gamelibrary2d.objects.AbstractMouseAwareObject;

public abstract class AbstractWidget<T extends Renderable>
        extends AbstractMouseAwareObject<T> implements FocusAware, KeyAware, MouseWhenFocusedAware {

    private boolean awareOfMouseEvent;

    protected AbstractWidget() {

    }

    protected AbstractWidget(T content) {
        super(content);
    }

    @Override
    public final void charInput(char charInput) {
        if (isEnabled()) {
            onCharInput(charInput);
        }
    }

    protected void onCharInput(char charInput) {
        var content = getContent();
        if (content instanceof KeyAware) {
            ((KeyAware) (content)).charInput(charInput);
        }
    }

    @Override
    public final void keyDown(int key, int scanCode, boolean repeat, int mods) {
        if (isEnabled()) {
            onKeyDown(key, scanCode, repeat, mods);
        }
    }

    protected void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        var content = getContent();
        if (content instanceof KeyAware) {
            ((KeyAware) (content)).keyDown(key, scanCode, repeat, mods);
        }
    }

    @Override
    public final void keyReleased(int key, int scanCode, int mods) {
        if (isEnabled()) {
            onKeyReleased(key, scanCode, mods);
        }
    }

    protected void onKeyReleased(int key, int scanCode, int mods) {
        var content = getContent();
        if (content instanceof KeyAware) {
            ((KeyAware) (content)).keyReleased(key, scanCode, mods);
        }
    }

    @Override
    protected void onMouseButtonEventStarted() {
        super.onMouseButtonEventStarted();
        awareOfMouseEvent = true;
    }

    @Override
    protected void onMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        if (focusOnMouseButtonAction(ButtonAction.PRESSED, button, mods, projectedX, projectedY)) {
            FocusManager.focus(this, false);
        }
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float projectedX, float projectedY) {
        if (focusOnMouseButtonAction(ButtonAction.RELEASED, button, mods, projectedX, projectedY)) {
            FocusManager.focus(this, false);
        }
    }

    @Override
    public final void mouseButtonWhenFocused(int button, ButtonAction action, int mods) {
        if (!awareOfMouseEvent) {
            onMissedMouseButtonEvent(button, action, mods);
        }
        awareOfMouseEvent = false;
    }

    /**
     * Override in order to change which buttons and actions are used to focus this object.
     */
    protected boolean focusOnMouseButtonAction(
            ButtonAction buttonAction, int button, int mods, float projectedX, float projectedY) {
        return buttonAction == ButtonAction.RELEASED;
    }

    /**
     * Invoked if this object is focused and a mouse button event is detected outside the object.
     *
     * @param button The mouse button that was pressed/released.
     * @param action The key action (press or release).
     * @param mods   Describes which modifier keys were held down.
     */
    protected void onMissedMouseButtonEvent(int button, ButtonAction action, int mods) {
        FocusManager.unfocus(this, false);
    }

    @Override
    public void onFocused() {

    }

    @Override
    public void onUnfocused() {

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
    protected void onMouseHover(float projectedX, float projectedY) {

    }

    @Override
    protected void onMouseDrag(float projectedX, float projectedY) {

    }
}