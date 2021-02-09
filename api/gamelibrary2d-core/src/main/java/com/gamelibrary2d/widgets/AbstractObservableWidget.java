package com.gamelibrary2d.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.widgets.listeners.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractObservableWidget<T extends Renderable> extends AbstractWidget<T> {
    private final List<MouseButtonDownListener> mouseButtonDownListeners = new CopyOnWriteArrayList<>();
    private final List<MouseHoverListener> mouseHoverListeners = new CopyOnWriteArrayList<>();
    private final List<MouseDragListener> mouseDragListeners = new CopyOnWriteArrayList<>();
    private final List<MouseButtonReleasedListener> mouseButtonReleasedListeners = new CopyOnWriteArrayList<>();
    private final List<KeyDownListener> keyDownListeners = new CopyOnWriteArrayList<>();
    private final List<KeyReleasedListener> keyReleasedListeners = new CopyOnWriteArrayList<>();
    private final List<CharInputListener> charInputListeners = new CopyOnWriteArrayList<>();
    private final List<FocusChangedListener> focusChangedListeners = new CopyOnWriteArrayList<>();

    protected AbstractObservableWidget() {

    }

    protected AbstractObservableWidget(T content) {
        super(content);
    }

    public void removeMouseListener(MouseButtonDownListener listener) {
        mouseButtonDownListeners.remove(listener);
    }

    public void addMouseButtonDownListener(MouseButtonDownListener listener) {
        mouseButtonDownListeners.add(listener);
    }

    public void removeMouseButtonDownListener(MouseButtonDownListener listener) {
        mouseButtonDownListeners.remove(listener);
    }

    public void addMouseHoverListener(MouseHoverListener listener) {
        mouseHoverListeners.add(listener);
    }

    public void removeMouseHoverListener(MouseHoverListener listener) {
        mouseHoverListeners.remove(listener);
    }

    public void addMouseDragListener(MouseDragListener listener) {
        mouseDragListeners.add(listener);
    }

    public void removeMouseDragListener(MouseDragListener listener) {
        mouseDragListeners.remove(listener);
    }

    public void addMouseButtonReleasedListener(MouseButtonReleasedListener listener) {
        mouseButtonReleasedListeners.add(listener);
    }

    public void removeMouseButtonReleasedListener(MouseButtonReleasedListener listener) {
        mouseButtonReleasedListeners.remove(listener);
    }

    public void addKeyDownListener(KeyDownListener listener) {
        keyDownListeners.add(listener);
    }

    public void removeKeyDownListener(KeyDownListener listener) {
        keyDownListeners.remove(listener);
    }

    public void addKeyReleasedListener(KeyReleasedListener listener) {
        keyReleasedListeners.add(listener);
    }

    public void removeKeyReleasedListener(KeyReleasedListener listener) {
        keyReleasedListeners.remove(listener);
    }

    public void addCharInputListener(CharInputListener listener) {
        charInputListeners.add(listener);
    }

    public void removeCharInputListener(CharInputListener listener) {
        charInputListeners.remove(listener);
    }

    public void addFocusChangedListener(FocusChangedListener listener) {
        focusChangedListeners.add(listener);
    }

    public void removeFocusChangedListener(FocusChangedListener listener) {
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
    protected void onMouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        super.onMouseButtonDown(button, mods, x, y, projectedX, projectedY);
        for (MouseButtonDownListener listener : mouseButtonDownListeners) {
            listener.onMouseButtonDown(button, mods, x, y, projectedX, projectedY);
        }
    }

    @Override
    protected void onMouseHover(float x, float y, float projectedX, float projectedY) {
        for (MouseHoverListener listener : mouseHoverListeners) {
            listener.onMouseHover(x, y, projectedX, projectedY);
        }
    }

    @Override
    protected void onMouseDrag(float x, float y, float projectedX, float projectedY) {
        for (MouseDragListener listener : mouseDragListeners) {
            listener.onMouseDrag(x, y, projectedX, projectedY);
        }
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        super.onMouseButtonReleased(button, mods, x, y, projectedX, projectedY);
        for (MouseButtonReleasedListener listener : mouseButtonReleasedListeners) {
            listener.onMouseButtonReleased(button, mods, x, y, projectedX, projectedY);
        }
    }

    @Override
    public void onCharInput(char charInput) {
        super.onCharInput(charInput);
        for (CharInputListener listener : charInputListeners) {
            listener.onCharInput(charInput);
        }
    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        super.onKeyDown(key, scanCode, repeat, mods);
        for (KeyDownListener listener : keyDownListeners) {
            listener.onKeyDown(key, scanCode, repeat, mods);
        }
    }

    @Override
    public void onKeyReleased(int key, int scanCode, int mods) {
        super.onKeyReleased(key, scanCode, mods);
        for (KeyReleasedListener listener : keyReleasedListeners) {
            listener.onKeyReleased(key, scanCode, mods);
        }
    }

    @Override
    public void onFocused() {
        for (FocusChangedListener listener : focusChangedListeners) {
            listener.onFocusChanged(true);
        }
    }

    @Override
    public void onUnfocused() {
        for (FocusChangedListener listener : focusChangedListeners) {
            listener.onFocusChanged(false);
        }
    }
}