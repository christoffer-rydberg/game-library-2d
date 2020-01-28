package com.gamelibrary2d;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.eventlisteners.FocusChangedListener;
import com.gamelibrary2d.markers.Focusable;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.Parent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Used to focus or unfocus {@link Focusable} objects and route keyboard events to all focused objects implementing {@link KeyAware}.
 *
 * @author Christoffer Rydberg
 */
public class FocusManager {

    private static final List<Focusable> focusedObjects = new ArrayList<>();

    private static final List<Focusable> iterationList = new ArrayList<>();

    private static final List<Focusable> readOnlyList = Collections.unmodifiableList(focusedObjects);

    private static final List<FocusChangedListener> focusChangedListeners = new CopyOnWriteArrayList<>();

    /**
     * Focuses the specified object.
     *
     * @param obj     The object to focus.
     * @param replace If true, old focus will be cleared.
     */
    public static void focus(Focusable obj, boolean replace) {
        if (replace) {
            replaceFocus(obj);
        }

        if (!focusedObjects.contains(obj)) {
            focusedObjects.add(obj);
            obj.setFocused(true);
            raiseFocusChangedEvent(obj, true);
        } else {
            obj.setFocused(true);
        }
    }

    /**
     * Unfocuses the specified object and optionally all child objects.
     *
     * @param obj       The object to unfocus.
     * @param recursive If is set to true and the object is a {@link Parent} all child
     *                  objects will also be unfocused. Likewise, if a child object is a
     *                  {@link Parent}, the child objects of that object will be
     *                  unfocused, and so on.
     */
    public static void unfocus(Object obj, boolean recursive) {
        if (obj instanceof Focusable) {
            var focusable = (Focusable) obj;

            var focused = focusable.isFocused();
            var removed = focusedObjects.remove(obj);

            if (focused) {
                focusable.setFocused(false);
            }

            if (removed) {
                raiseFocusChangedEvent(focusable, false);
            }
        }

        if (recursive && obj instanceof Parent) {
            unfocus((Parent<?>) obj);
        }
    }

    private static void unfocus(Parent<?> parent) {
        for (Object child : parent.getChildren()) {
            unfocus(child, true);
        }
    }

    /**
     * @return Read-only list of all focused objects. This list is automatically
     * updated when focus changes.
     */
    public static List<Focusable> getFocusedObjects() {
        return readOnlyList;
    }

    /**
     * Unfocus all objects.
     */
    public static void clearFocus() {
        List<Focusable> focused = new ArrayList<>(focusedObjects.size());
        focused.addAll(focusedObjects);
        for (int i = 0; i < focused.size(); ++i) {
            unfocus(focused.get(i), false);
        }
    }

    private static void replaceFocus(Focusable obj) {
        List<Focusable> focused = new ArrayList<>(focusedObjects.size());
        focused.addAll(focusedObjects);
        for (int i = 0; i < focused.size(); ++i) {
            var focusedObject = focused.get(i);
            if (focusedObject != obj) {
                unfocus(focused.get(i), false);
            }
        }
    }

    static void keyDownEvent(int key, int scanCode, boolean repeat, int mods) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                Focusable obj = iterationList.get(i);
                failIfUnfocusedButNotRemoved(obj);
                if (obj instanceof KeyAware) {
                    ((KeyAware) obj).keyDownEvent(key, scanCode, repeat, mods);
                }
            }
        } finally {
            iterationList.clear();
        }
    }

    static void keyReleaseEvent(int key, int scanCode, int mods) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                var obj = iterationList.get(i);
                failIfUnfocusedButNotRemoved(obj);
                if (obj instanceof KeyAware) {
                    ((KeyAware) obj).keyReleaseEvent(key, scanCode, mods);
                }
            }
        } finally {
            iterationList.clear();
        }
    }

    static void charInputEvent(char charInput) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                Focusable obj = iterationList.get(i);
                failIfUnfocusedButNotRemoved(obj);
                if (obj instanceof KeyAware) {
                    ((KeyAware) obj).charInputEvent(charInput);
                }
            }
        } finally {
            iterationList.clear();
        }
    }

    static void mouseButtonEventFinished(int button, int action, int mods) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                Focusable obj = iterationList.get(i);
                failIfUnfocusedButNotRemoved(obj);
                obj.mouseButtonEventFinished(button, action, mods);
            }
        } finally {
            iterationList.clear();
        }
    }

    private static void failIfUnfocusedButNotRemoved(Focusable obj) {
        if (!obj.isFocused() && focusedObjects.contains(obj)) {
            throw new GameLibrary2DRuntimeException("Object must be unfocused using the FocusManager");
        }
    }

    public static void addFocusChangedListener(FocusChangedListener listener) {
        focusChangedListeners.add(listener);
    }

    public static void removeFocusChangedListener(FocusChangedListener listener) {
        focusChangedListeners.remove(listener);
    }

    private static void raiseFocusChangedEvent(Focusable obj, boolean focused) {
        for (FocusChangedListener listener : focusChangedListeners) {
            listener.onFocusChanged(obj, focused);
        }
    }
}