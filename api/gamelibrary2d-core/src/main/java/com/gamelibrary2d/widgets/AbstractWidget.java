package com.gamelibrary2d.widgets;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.*;
import com.gamelibrary2d.objects.AbstractPointerAwareGameObject;

public abstract class AbstractWidget<T extends Renderable>
        extends AbstractPointerAwareGameObject implements FocusAware, KeyAware, InputAware, PointerWhenFocusedAware {

    private T composition;
    private boolean focused;
    private boolean skipWhenFocusedAction;
    private Rectangle bounds;

    protected AbstractWidget() {

    }

    protected AbstractWidget(T composition) {
        this.composition = composition;
    }

    @Override
    public final void charInput(char charInput) {
        if (isEnabled()) {
            onCharInput(charInput);
        }
    }

    protected void onCharInput(char charInput) {
        if (composition instanceof InputAware) {
            ((InputAware) (composition)).charInput(charInput);
        }
    }

    @Override
    public final void keyDown(int key, boolean repeat) {
        if (isEnabled()) {
            onKeyDown(key, repeat);
        }
    }

    protected void onKeyDown(int key, boolean repeat) {
        if (composition instanceof KeyAware) {
            ((KeyAware) (composition)).keyDown(key, repeat);
        }
    }

    @Override
    public final void keyUp(int key) {
        if (isEnabled()) {
            onKeyUp(key);
        }
    }

    protected void onKeyUp(int key) {
        if (composition instanceof KeyAware) {
            ((KeyAware) (composition)).keyUp(key);
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

    protected T getComposition() {
        return composition;
    }

    protected void setComposition(T composition) {
        this.composition = composition;
    }

    @Override
    protected void onRender(float alpha) {
        if (composition != null) {
            composition.render(alpha);
        }
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : getCompositionBounds();
    }

    protected void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    private Rectangle getCompositionBounds() {
        if (composition instanceof Bounded)
            return ((Bounded) composition).getBounds();
        else
            return Rectangle.EMPTY;
    }
}