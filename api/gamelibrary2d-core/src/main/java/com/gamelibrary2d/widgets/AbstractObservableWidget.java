package com.gamelibrary2d.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.widgets.events.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractObservableWidget<T extends Renderable> extends AbstractWidget<T> {
    private final List<MouseButtonDown> mouseButtonDownListeners = new CopyOnWriteArrayList<>();
    private final List<MouseMoved> mouseHoverListeners = new CopyOnWriteArrayList<>();
    private final List<MouseMoved> mouseDragListeners = new CopyOnWriteArrayList<>();
    private final List<MouseButtonReleased> mouseButtonReleasedListeners = new CopyOnWriteArrayList<>();
    private final List<KeyDown> keyDownListeners = new CopyOnWriteArrayList<>();
    private final List<KeyReleased> keyReleasedListeners = new CopyOnWriteArrayList<>();
    private final List<CharInput> charInputListeners = new CopyOnWriteArrayList<>();
    private final List<FocusChanged> focusChangedListeners = new CopyOnWriteArrayList<>();

    protected AbstractObservableWidget() {

    }

    protected AbstractObservableWidget(T content) {
        super(content);
    }

    public void removeMouseListener(MouseButtonDown listener) {
        mouseButtonDownListeners.remove(listener);
    }

    public void addMouseButtonDownListener(MouseButtonDown listener) {
        mouseButtonDownListeners.add(listener);
    }

    public void removeMouseButtonDownListener(MouseButtonDown listener) {
        mouseButtonDownListeners.remove(listener);
    }

    public void addMouseHoverListener(MouseMoved listener) {
        mouseHoverListeners.add(listener);
    }

    public void removeMouseHoverListener(MouseMoved listener) {
        mouseHoverListeners.remove(listener);
    }

    public void addMouseDragListener(MouseMoved listener) {
        mouseDragListeners.add(listener);
    }

    public void removeMouseDragListener(MouseMoved listener) {
        mouseDragListeners.remove(listener);
    }

    public void addMouseButtonReleasedListener(MouseButtonReleased listener) {
        mouseButtonReleasedListeners.add(listener);
    }

    public void removeMouseButtonReleasedListener(MouseButtonReleased listener) {
        mouseButtonReleasedListeners.remove(listener);
    }

    public void addKeyDownListener(KeyDown listener) {
        keyDownListeners.add(listener);
    }

    public void removeKeyDownListener(KeyDown listener) {
        keyDownListeners.remove(listener);
    }

    public void addKeyReleasedListener(KeyReleased listener) {
        keyReleasedListeners.add(listener);
    }

    public void removeKeyReleasedListener(KeyReleased listener) {
        keyReleasedListeners.remove(listener);
    }

    public void addCharInputListener(CharInput listener) {
        charInputListeners.add(listener);
    }

    public void removeCharInputListener(CharInput listener) {
        charInputListeners.remove(listener);
    }

    public void addFocusChangedListener(FocusChanged listener) {
        focusChangedListeners.add(listener);
    }

    public void removeFocusChangedListener(FocusChanged listener) {
        focusChangedListeners.remove(listener);
    }

    @Override
    protected boolean isListeningToMouseHoverEvents() {
        return !mouseHoverListeners.isEmpty();
    }

    @Override
    protected boolean isListeningToMouseDragEvents() {
        return !mouseDragListeners.isEmpty();
    }

    @Override
    protected void onMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        super.onMouseButtonDown(button, mods, projectedX, projectedY);
        for (var listener : mouseButtonDownListeners) {
            listener.onMouseButtonDown(button, mods, projectedX, projectedY);
        }
    }

    @Override
    protected void onMouseHover(float projectedX, float projectedY) {
        for (var listener : mouseHoverListeners) {
            listener.onMouseMoved(projectedX, projectedY, false);
        }
    }

    @Override
    protected void onMouseDrag(float projectedX, float projectedY) {
        for (var listener : mouseDragListeners) {
            listener.onMouseMoved(projectedX, projectedY, true);
        }
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float projectedX, float projectedY) {
        super.onMouseButtonReleased(button, mods, projectedX, projectedY);
        for (var listener : mouseButtonReleasedListeners) {
            listener.onMouseButtonReleased(button, mods, projectedX, projectedY);
        }
    }

    @Override
    public void onCharInput(char charInput) {
        super.charInput(charInput);
        for (var listener : charInputListeners) {
            listener.onCharInput(charInput);
        }
    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        super.keyDown(key, scanCode, repeat, mods);
        for (var listener : keyDownListeners) {
            listener.onKeyDown(key, scanCode, repeat, mods);
        }
    }

    @Override
    public void onKeyReleased(int key, int scanCode, int mods) {
        super.keyReleased(key, scanCode, mods);
        for (var listener : keyReleasedListeners) {
            listener.onKeyReleased(key, scanCode, mods);
        }
    }

    @Override
    public void onFocused() {
        for (var listener : focusChangedListeners) {
            listener.onFocusChanged(true);
        }
    }

    @Override
    public void onUnfocused() {
        for (var listener : focusChangedListeners) {
            listener.onFocusChanged(false);
        }
    }
}