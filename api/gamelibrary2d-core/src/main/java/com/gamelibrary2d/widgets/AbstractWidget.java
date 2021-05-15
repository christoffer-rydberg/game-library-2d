package com.gamelibrary2d.widgets;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.FocusAware;
import com.gamelibrary2d.markers.InputAware;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.PointerWhenFocusedAware;
import com.gamelibrary2d.objects.AbstractPointerAwareGameObject;

public abstract class AbstractWidget<T extends Renderable>
        extends AbstractPointerAwareGameObject<T> implements FocusAware, KeyAware, InputAware, PointerWhenFocusedAware {

    private boolean focused;
    private boolean skipWhenFocusedAction;

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
        T content = getContent();
        if (content instanceof InputAware) {
            ((InputAware) (content)).charInput(charInput);
        }
    }

    @Override
    public final void keyDown(int key, boolean repeat) {
        if (isEnabled()) {
            onKeyDown(key, repeat);
        }
    }

    protected void onKeyDown(int key, boolean repeat) {
        T content = getContent();
        if (content instanceof KeyAware) {
            ((KeyAware) (content)).keyDown(key, repeat);
        }
    }

    @Override
    public final void keyUp(int key) {
        if (isEnabled()) {
            onKeyUp(key);
        }
    }

    protected void onKeyUp(int key) {
        T content = getContent();
        if (content instanceof KeyAware) {
            ((KeyAware) (content)).keyUp(key);
        }
    }

    protected final void pointerActionStarted(float x, float y) {
        super.pointerActionStarted(x, y);
        onPointerActionStarted(x, y);
    }

    protected final void pointerActionFinished(float x, float y) {
        super.pointerActionFinished(x, y);
        onPointerActionFinished(x, y);
        skipWhenFocusedAction = focused;
    }

    /**
     * Invoked before a pointer action is handled.
     *
     * @param x The x-coordinate of the pointer projected to the parent container.
     * @param y The y-coordinate of the pointer projected to the parent container.
     */
    protected void onPointerActionStarted(float x, float y) {

    }

    /**
     * Invoked after after a pointer action is handled.
     *
     * @param x The x-coordinate of the pointer projected to the parent container.
     * @param y The y-coordinate of the pointer projected to the parent container.
     */
    protected void onPointerActionFinished(float x, float y) {

    }

    @Override
    public final void pointerDownWhenFocused(int id, int button) {
        if (!skipWhenFocusedAction) {
            onPointerDownWhenFocused(id, button);
        }

        skipWhenFocusedAction = false;
    }

    protected void onPointerDownWhenFocused(int id, int button) {
        FocusManager.unfocus(this, false);
    }

    @Override
    public final void pointerUpWhenFocused(int id, int button) {
        if (!skipWhenFocusedAction) {
            onPointerUpWhenFocused(id, button);
        }

        skipWhenFocusedAction = false;
    }

    protected void onPointerUpWhenFocused(int id, int button) {
        FocusManager.unfocus(this, false);
    }

    @Override
    public final void focused() {
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
    protected void onPointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        FocusManager.focus(this, false);
    }

    @Override
    protected void onPointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        FocusManager.focus(this, false);
    }

    @Override
    protected boolean isListeningToPointHoverEvents() {
        return false;
    }

    @Override
    protected boolean isListeningToPointDragEvents() {
        return false;
    }

    @Override
    protected void onPointerHover(int id, float x, float y, float projectedX, float projectedY) {

    }

    @Override
    protected void onPointerDrag(int id, float x, float y, float projectedX, float projectedY) {

    }
}