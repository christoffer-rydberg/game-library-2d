package com.gamelibrary2d.widgets;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.MouseEventState;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.FocusAware;
import com.gamelibrary2d.markers.InputAware;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.MouseWhenFocusedAware;
import com.gamelibrary2d.objects.AbstractMouseAwareObject;

public abstract class AbstractWidget<T extends Renderable>
        extends AbstractMouseAwareObject<T> implements FocusAware, KeyAware, InputAware, MouseWhenFocusedAware {

    private boolean focused;
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
        if (content instanceof InputAware) {
            ((InputAware) (content)).charInput(charInput);
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

    protected final void mouseEventStarted(float x, float y) {
        super.mouseEventStarted(x, y);
        onMouseEventStarted(x, y);
    }

    protected final void mouseEventFinished(float x, float y) {
        super.mouseEventFinished(x, y);
        onMouseEventFinished(x, y);
        awareOfMouseEvent = focused;
    }

    /**
     * Invoked before a mouse event is handled.
     *
     * @param x The x-coordinate of the mouse cursor projected to the parent container.
     * @param y The y-coordinate of the mouse cursor projected to the parent container.
     */
    protected void onMouseEventStarted(float x, float y) {

    }

    /**
     * Invoked after after a mouse event is handled.
     *
     * @param x The x-coordinate of the mouse cursor projected to the parent container.
     * @param y The y-coordinate of the mouse cursor projected to the parent container.
     */
    protected void onMouseEventFinished(float x, float y) {

    }

    @Override
    protected void onMouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        FocusManager.focus(this, false);
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        FocusManager.focus(this, false);
    }

    @Override
    public final void mouseButtonDownWhenFocused(int button, int mods) {
        if (!awareOfMouseEvent) {
            onMouseButtonDownWhenFocused(button, mods);
        }
        awareOfMouseEvent = false;
    }

    @Override
    public final void mouseButtonReleasedWhenFocused(int button, int mods) {
        if (!awareOfMouseEvent) {
            onMouseButtonReleasedWhenFocused(button, mods);
        }

        awareOfMouseEvent = false;
    }

    protected void onMouseButtonDownWhenFocused(int button, int mods) {
        FocusManager.unfocus(this, false);
    }

    protected void onMouseButtonReleasedWhenFocused(int button, int mods) {
        FocusManager.unfocus(this, false);
    }

    @Override
    public final void focused() {
        awareOfMouseEvent = MouseEventState.isHandlingEvent();
        focused = true;
        onFocused();
    }

    @Override
    public final void unfocused() {
        focused = false;
        onUnfocused();
    }

    public boolean isFocused() {
        return focused;
    }

    protected void onFocused() {

    }

    protected void onUnfocused() {

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
    protected void onMouseHover(float x, float y, float projectedX, float projectedY) {

    }

    @Override
    protected void onMouseDrag(float x, float y, float projectedX, float projectedY) {

    }
}