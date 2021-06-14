package com.gamelibrary2d.components.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.widgets.listeners.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractObservableWidget<T extends Renderable> extends AbstractWidget<T> {
    private final List<PointerDownListener> pointerDownListeners = new CopyOnWriteArrayList<>();
    private final List<PointerHoverListener> pointerHoverListeners = new CopyOnWriteArrayList<>();
    private final List<PointerDragListener> pointerDragListeners = new CopyOnWriteArrayList<>();
    private final List<PointerUpListener> pointerUpListeners = new CopyOnWriteArrayList<>();
    private final List<KeyDownListener> keyDownListeners = new CopyOnWriteArrayList<>();
    private final List<KeyUpListener> keyUpListeners = new CopyOnWriteArrayList<>();
    private final List<CharInputListener> charInputListeners = new CopyOnWriteArrayList<>();
    private final List<FocusChangedListener> focusChangedListeners = new CopyOnWriteArrayList<>();

    protected AbstractObservableWidget() {

    }

    protected AbstractObservableWidget(T content) {
        super(content);
    }

    public void addPointerDownListener(PointerDownListener listener) {
        pointerDownListeners.add(listener);
    }

    public void removePointerDownListener(PointerDownListener listener) {
        pointerDownListeners.remove(listener);
    }

    public void addPointerHoverListener(PointerHoverListener listener) {
        pointerHoverListeners.add(listener);
    }

    public void removePointerHoverListener(PointerHoverListener listener) {
        pointerHoverListeners.remove(listener);
    }

    public void addPointerDragListener(PointerDragListener listener) {
        pointerDragListeners.add(listener);
    }

    public void removePointerDragListener(PointerDragListener listener) {
        pointerDragListeners.remove(listener);
    }

    public void addPointerUpListener(PointerUpListener listener) {
        pointerUpListeners.add(listener);
    }

    public void removePointerUpListener(PointerUpListener listener) {
        pointerUpListeners.remove(listener);
    }

    public void addKeyDownListener(KeyDownListener listener) {
        keyDownListeners.add(listener);
    }

    public void removeKeyDownListener(KeyDownListener listener) {
        keyDownListeners.remove(listener);
    }

    public void addKeyUpListener(KeyUpListener listener) {
        keyUpListeners.add(listener);
    }

    public void removeKeyUpListener(KeyUpListener listener) {
        keyUpListeners.remove(listener);
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
    protected boolean isListeningToPointHoverEvents() {
        return !pointerHoverListeners.isEmpty();
    }

    @Override
    protected boolean isListeningToPointDragEvents() {
        return !pointerDragListeners.isEmpty();
    }

    @Override
    protected void onPointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        super.onPointerDown(id, button, x, y, projectedX, projectedY);
        for (PointerDownListener listener : pointerDownListeners) {
            listener.onPointerDown(id, button, x, y, projectedX, projectedY);
        }
    }

    @Override
    protected void onPointerHover(int id, float x, float y, float projectedX, float projectedY) {
        for (PointerHoverListener listener : pointerHoverListeners) {
            listener.onPointerHover(id, x, y, projectedX, projectedY);
        }
    }

    @Override
    protected void onPointerDrag(int id, float x, float y, float projectedX, float projectedY) {
        for (PointerDragListener listener : pointerDragListeners) {
            listener.onPointerDrag(id, x, y, projectedX, projectedY);
        }
    }

    @Override
    protected void onPointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        super.onPointerUp(id, button, x, y, projectedX, projectedY);
        for (PointerUpListener listener : pointerUpListeners) {
            listener.onPointerUp(id, button, x, y, projectedX, projectedY);
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
    public void onKeyDown(int key, boolean repeat) {
        super.onKeyDown(key, repeat);
        for (KeyDownListener listener : keyDownListeners) {
            listener.onKeyDown(key, repeat);
        }
    }

    @Override
    public void onKeyUp(int key) {
        super.onKeyUp(key);
        for (KeyUpListener listener : keyUpListeners) {
            listener.onKeyUp(key);
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