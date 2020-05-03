package com.gamelibrary2d.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.FocusAware;
import com.gamelibrary2d.markers.MouseWhenFocusedAware;
import com.gamelibrary2d.widgets.events.*;

public abstract class AbstractWidget<T extends Renderable>
        extends InternalAbstractWidget<T> implements FocusAware, MouseWhenFocusedAware {
    private MouseButtonDown onMouseButtonDown;
    private MouseMoved onMouseHover;
    private MouseMoved onMouseDrag;
    private MouseButtonReleased onMouseButtonReleased;
    private KeyDown onKeyDown;
    private KeyReleased onKeyReleased;
    private CharInput onCharInput;
    private FocusChanged onFocusChanged;

    protected AbstractWidget() {

    }

    protected AbstractWidget(T content) {
        super(content);
    }

    protected void setMouseButtonDownAction(MouseButtonDown action) {
        onMouseButtonDown = action;
    }

    public void setMouseHoverAction(MouseMoved action) {
        onMouseHover = action;
    }

    public void setMouseDragAction(MouseMoved action) {
        onMouseDrag = action;
    }

    public void setMouseButtonReleasedAction(MouseButtonReleased action) {
        onMouseButtonReleased = action;
    }

    public void setKeyDownAction(KeyDown action) {
        onKeyDown = action;
    }

    public void setKeyReleasedAction(KeyReleased action) {
        onKeyReleased = action;
    }

    public void setCharInputAction(CharInput action) {
        onCharInput = action;
    }

    public void setFocusChangedAction(FocusChanged action) {
        onFocusChanged = action;
    }

    @Override
    protected boolean isListeningToMouseHoverEvents() {
        return onMouseHover != null;
    }

    @Override
    protected boolean isListeningToMouseDragEvents() {
        return onMouseDrag != null;
    }

    @Override
    protected void onHandleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        if (onMouseButtonDown != null) {
            onMouseButtonDown.onMouseButtonDown(button, mods, projectedX, projectedY);
        }
    }

    @Override
    protected void handleMouseHover(float projectedX, float projectedY) {
        onMouseHover.onMouseMoved(projectedX, projectedY, false);
    }

    @Override
    protected void handleMouseDrag(float projectedX, float projectedY) {
        onMouseDrag.onMouseMoved(projectedX, projectedY, true);
    }

    @Override
    protected void onHandleMouseButtonReleased(int button, int mods, float projectedX, float projectedY) {
        if (onMouseButtonReleased != null) {
            onMouseButtonReleased.onMouseButtonReleased(button, mods, projectedX, projectedY);
        }
    }

    @Override
    public void onCharInput(char charInput) {
        super.onCharInput(charInput);
        if (onCharInput != null) {
            onCharInput.onCharInput(charInput);
        }
    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        super.onKeyDown(key, scanCode, repeat, mods);
        if (onKeyDown != null) {
            onKeyDown.onKeyDown(key, scanCode, repeat, mods);
        }
    }

    @Override
    public void onKeyReleased(int key, int scanCode, int mods) {
        super.onKeyReleased(key, scanCode, mods);
        if (onKeyReleased != null) {
            onKeyReleased.onKeyReleased(key, scanCode, mods);
        }
    }

    @Override
    public void onFocused() {
        if (onFocusChanged != null) {
            onFocusChanged.onFocusChanged(true);
        }
    }

    @Override
    public void onUnfocused() {
        if (onFocusChanged != null) {
            onFocusChanged.onFocusChanged(false);
        }
    }
}