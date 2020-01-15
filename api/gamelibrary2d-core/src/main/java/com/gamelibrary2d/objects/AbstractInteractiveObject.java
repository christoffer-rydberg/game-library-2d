package com.gamelibrary2d.objects;

import com.gamelibrary2d.eventlisteners.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is an extension of {@link AbstractInputObject} that will raise
 * events when it receives mouse or keyboard input. It provides functionality to
 * add and remove event listeners to handle different types of input events.
 *
 * @author Christoffer Rydberg
 */
public abstract class AbstractInteractiveObject extends AbstractInputObject {

    private final List<MouseClickListener> mouseClickListeners = new CopyOnWriteArrayList<>();
    private final List<MouseMoveListener> mouseMoveListeners = new CopyOnWriteArrayList<>();
    private final List<MouseReleaseListener> mouseReleaseListeners = new CopyOnWriteArrayList<>();
    private final List<KeyDownListener> keyDownListeners = new CopyOnWriteArrayList<>();
    private final List<KeyReleaseListener> keyReleaseListeners = new CopyOnWriteArrayList<>();
    private final List<CharInputListener> charInputListeners = new CopyOnWriteArrayList<>();

    public void removeMouseListener(MouseClickListener listener) {
        mouseClickListeners.remove(listener);
    }

    public void addMouseClickListener(MouseClickListener listener) {
        mouseClickListeners.add(listener);
    }

    public void removeMouseClickListener(MouseClickListener listener) {
        mouseClickListeners.remove(listener);
    }

    public void addMouseMoveListener(MouseMoveListener listener) {
        mouseMoveListeners.add(listener);
    }

    public void removeMouseMoveListener(MouseMoveListener listener) {
        mouseMoveListeners.remove(listener);
    }

    public void addMouseReleaseListener(MouseReleaseListener listener) {
        mouseReleaseListeners.add(listener);
    }

    public void removeMouseReleaseListener(MouseReleaseListener listener) {
        mouseReleaseListeners.remove(listener);
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

    @Override
    protected boolean onMouseClickEvent(int button, int mods, float projectedX, float projectedY) {
        for (MouseClickListener listener : mouseClickListeners) {
            listener.onMouseClick(this, button, mods, projectedX, projectedY);
        }
        return true;
    }

    @Override
    protected boolean onMouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        for (MouseMoveListener listener : mouseMoveListeners) {
            listener.onMouseMove(this, projectedX, projectedY, drag);
        }
        return true;
    }

    @Override
    protected void onMouseReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        for (MouseReleaseListener listener : mouseReleaseListeners) {
            listener.onMouseRelease(this, button, mods, projectedX, projectedY);
        }
    }

    @Override
    public void charInputEvent(char charInput) {
        for (CharInputListener listener : charInputListeners) {
            listener.onCharInput(this, charInput);
        }
    }

    @Override
    public void keyDownEvent(int key, int scanCode, boolean repeat, int mods) {
        for (KeyDownListener listener : keyDownListeners) {
            listener.onKeyDown(this, key, scanCode, repeat, mods);
        }
    }

    @Override
    public void keyReleaseEvent(int key, int scanCode, int mods) {
        for (KeyReleaseListener listener : keyReleaseListeners) {
            listener.onKeyRelease(this, key, scanCode, mods);
        }
    }
}