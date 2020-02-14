package com.gamelibrary2d.objects;

import com.gamelibrary2d.eventlisteners.*;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.FocusAware;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractObservableObject<T extends Renderable> extends AbstractMouseAwareObject<T> implements FocusAware {
    private final List<MouseButtonDownListener> mouseButtonDownListeners = new CopyOnWriteArrayList<>();
    private final List<MouseMoveListener> mouseHoverListeners = new CopyOnWriteArrayList<>();
    private final List<MouseMoveListener> mouseDragListeners = new CopyOnWriteArrayList<>();
    private final List<MouseButtonReleaseListener> mouseButtonReleaseListeners = new CopyOnWriteArrayList<>();
    private final List<KeyDownListener> keyDownListeners = new CopyOnWriteArrayList<>();
    private final List<KeyReleaseListener> keyReleaseListeners = new CopyOnWriteArrayList<>();
    private final List<CharInputListener> charInputListeners = new CopyOnWriteArrayList<>();
    private final List<FocusChangedListener> focusChangedListeners = new CopyOnWriteArrayList<>();

    protected AbstractObservableObject() {

    }

    protected AbstractObservableObject(T content) {
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

    public void addMouseHoverListener(MouseMoveListener listener) {
        mouseHoverListeners.add(listener);
    }

    public void removeMouseHoverListener(MouseMoveListener listener) {
        mouseHoverListeners.remove(listener);
    }

    public void addMouseDragListener(MouseMoveListener listener) {
        mouseDragListeners.add(listener);
    }

    public void removeMouseDragListener(MouseMoveListener listener) {
        mouseDragListeners.remove(listener);
    }

    public void addMouseButtonReleaseListener(MouseButtonReleaseListener listener) {
        mouseButtonReleaseListeners.add(listener);
    }

    public void removeMouseButtonReleaseListener(MouseButtonReleaseListener listener) {
        mouseButtonReleaseListeners.remove(listener);
    }

    public void addKeyDownListener(KeyDownListener listener) {
        keyDownListeners.add(listener);
    }

    public void removeKeyDownListener(KeyDownListener listener) {
        keyDownListeners.remove(listener);
    }

    public void addKeyReleaseListener(KeyReleaseListener listener) {
        keyReleaseListeners.add(listener);
    }

    public void removeKeyReleaseListener(KeyReleaseListener listener) {
        keyReleaseListeners.remove(listener);
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
    protected boolean isListeningToMouseClickEvents() {
        return !mouseButtonDownListeners.isEmpty() ||
                !mouseButtonReleaseListeners.isEmpty() ||
                isListeningToMouseDragEvents();
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
    protected boolean handleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        for (var listener : mouseButtonDownListeners) {
            listener.onMouseButtonDown(this, button, mods, projectedX, projectedY);
        }
        return true;
    }

    @Override
    protected boolean handleMouseHover(float projectedX, float projectedY) {
        for (var listener : mouseHoverListeners) {
            listener.onMouseMove(this, projectedX, projectedY, false);
        }
        return true;
    }

    @Override
    protected boolean handleMouseDrag(float projectedX, float projectedY) {
        for (var listener : mouseDragListeners) {
            listener.onMouseMove(this, projectedX, projectedY, true);
        }
        return true;
    }

    @Override
    protected void handleMouseButtonRelease(int button, int mods, float projectedX, float projectedY) {
        for (var listener : mouseButtonReleaseListeners) {
            listener.onMouseButtonRelease(this, button, mods, projectedX, projectedY);
        }
    }

    @Override
    public void onCharInput(char charInput) {
        super.onCharInput(charInput);
        for (var listener : charInputListeners) {
            listener.onCharInput(this, charInput);
        }
    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        super.onKeyDown(key, scanCode, repeat, mods);
        for (var listener : keyDownListeners) {
            listener.onKeyDown(this, key, scanCode, repeat, mods);
        }
    }

    @Override
    public void onKeyRelease(int key, int scanCode, int mods) {
        super.onKeyRelease(key, scanCode, mods);
        for (var listener : keyReleaseListeners) {
            listener.onKeyRelease(this, key, scanCode, mods);
        }
    }

    @Override
    public void onFocused() {
        for (var listener : focusChangedListeners) {
            listener.onFocusChanged(this, true);
        }
    }

    @Override
    public void onUnfocused() {
        for (var listener : focusChangedListeners) {
            listener.onFocusChanged(this, false);
        }
    }
}