package com.gamelibrary2d;

import com.gamelibrary2d.eventlisteners.FocusChangedListener;
import com.gamelibrary2d.objects.Container;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.objects.KeyAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The purpose of this class is to keep track of all focused objects. It
 * contains methods to focus or unfocus objects, and events when this happens.
 * All focused objects will be notified of keyboard events. Mouse events,
 * however, are unrelated to focus. They are typically routed to the concerned
 * objects by the holding containers. Nevertheless, focused objects will be
 * notified when a mouse button event has finished so that it, for example, can
 * unfocus itself.
 *
 * @author Christoffer Rydberg
 */
public class FocusManager {

    /**
     * All focused objects. This list should never be exposed outside this class.
     */
    private static final List<KeyAware> focusedObjects = new ArrayList<>();

    /**
     * Used when iterating during input events. This object can be reused since the
     * mouse/keyboard methods are always called sequentially from the main thread.
     */
    private static final List<KeyAware> focusedObjectsIteration = new ArrayList<>();

    /**
     * Used as return value so that the original list is not exposed.
     */
    private static final List<KeyAware> focusedObjectsReadonly = Collections.unmodifiableList(focusedObjects);

    /**
     * Listeners for focus changes.
     */
    private static final List<FocusChangedListener> focusChangedListeners = new CopyOnWriteArrayList<>();

    /**
     * Focuses the specified object.
     *
     * @param obj     The object to focus.
     * @param replace If true, old focus will be cleared.
     */
    public static void focus(KeyAware obj, boolean replace) {

        if (replace)
            clearFocus();

        if (!obj.isFocused()) {
            // Focus the object first. Doing this will call this method again
            // (if the focus is accepted). Wait until then to add it to the list of
            // focused objects.
            obj.setFocused(true);
        } else if (!focusedObjects.contains(obj)) {
            focusedObjects.add(obj);
            raiseFocusChangedEvent(obj, true);
        }
    }

    /**
     * Unfocuses the specified object and optionally all child objects.
     *
     * @param obj       The object to unfocus.
     * @param recursive If is set to true and the object is of type Container, all child
     *                  objects will also be unfocused. Likewise, if a child object is of
     *                  type Container, the child objects of that object will be
     *                  unfocused, and so on.
     */
    public static void unfocus(KeyAware obj, boolean recursive) {

        if (obj.isFocused()) {
            // Unfocus the object first. Doing this will call this method again
            // (if the unfocus is accepted). Wait until then to add it to the list of
            // focused objects.
            obj.setFocused(false);
        } else if (focusedObjects.remove(obj)) {
            raiseFocusChangedEvent(obj, false);
        }

        if (recursive && obj instanceof Container) {
            unfocus((Container<?>) obj);
        }
    }

    private static void unfocus(Container<?> container) {
        var objects = container.getObjects();
        for (int i = 0; i < objects.size(); ++i) {
            GameObject obj = objects.get(i);
            if (obj instanceof KeyAware)
                unfocus((KeyAware) obj, true);
        }
    }

    /**
     * @return Read-only list of all focused objects. This list is automatically
     * updated when focus changes.
     */
    public static List<KeyAware> getFocusedObjects() {
        return focusedObjectsReadonly;
    }

    /**
     * Unfocus all objects.
     */
    public static void clearFocus() {
        List<KeyAware> focused = new ArrayList<>(focusedObjects.size());
        focused.addAll(focusedObjects);
        for (int i = 0; i < focused.size(); ++i) {
            unfocus(focused.get(i), false);
        }
    }

    static void keyDownEvent(int key, int scanCode, boolean repeat, int mods) {
        try {
            focusedObjectsIteration.addAll(focusedObjects);
            for (int i = 0; i < focusedObjectsIteration.size(); ++i) {
                KeyAware obj = focusedObjectsIteration.get(i);
                obj.keyDownEvent(key, scanCode, repeat, mods);
            }
        } finally {
            focusedObjectsIteration.clear();
        }
    }

    static void keyReleaseEvent(int key, int scanCode, int mods) {
        try {
            focusedObjectsIteration.addAll(focusedObjects);
            for (int i = 0; i < focusedObjectsIteration.size(); ++i) {
                KeyAware obj = focusedObjectsIteration.get(i);
                obj.keyReleaseEvent(key, scanCode, mods);
            }
        } finally {
            focusedObjectsIteration.clear();
        }
    }

    static void charInputEvent(char charInput) {
        try {
            focusedObjectsIteration.addAll(focusedObjects);
            for (int i = 0; i < focusedObjectsIteration.size(); ++i) {
                KeyAware obj = focusedObjectsIteration.get(i);
                obj.charInputEvent(charInput);
            }
        } finally {
            focusedObjectsIteration.clear();
        }
    }

    static void mouseButtonEventFinished(int button, int action, int mods) {
        try {
            focusedObjectsIteration.addAll(focusedObjects);
            for (int i = 0; i < focusedObjectsIteration.size(); ++i) {
                KeyAware obj = focusedObjectsIteration.get(i);
                obj.mouseEventFinished(button, action, mods);
            }
        } finally {
            focusedObjectsIteration.clear();
        }
    }

    public static void addFocusChangedListener(FocusChangedListener listener) {
        focusChangedListeners.add(listener);
    }

    public static void removeFocusChangedListener(FocusChangedListener listener) {
        focusChangedListeners.remove(listener);
    }

    private static void raiseFocusChangedEvent(KeyAware obj, boolean focused) {
        for (FocusChangedListener listener : focusChangedListeners) {
            listener.onFocusChanged(obj, focused);
        }
    }
}