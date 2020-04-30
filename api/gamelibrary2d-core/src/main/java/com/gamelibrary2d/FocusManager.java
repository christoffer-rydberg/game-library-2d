package com.gamelibrary2d;

import com.gamelibrary2d.input.ButtonAction;
import com.gamelibrary2d.markers.FocusAware;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.MouseWhenFocusedAware;
import com.gamelibrary2d.markers.Parent;

import java.util.ArrayList;
import java.util.List;

public class FocusManager {

    private static final List<KeyAware> focusedObjects = new ArrayList<>();

    private static final List<KeyAware> iterationList = new ArrayList<>();

    public static void focus(KeyAware obj, boolean replace) {
        if (replace) {
            replaceFocus(obj);
        }

        if (!focusedObjects.contains(obj)) {
            focusedObjects.add(obj);
            if (obj instanceof FocusAware) {
                ((FocusAware) obj).onFocused();
            }
        }
    }

    public static void unfocus(Object obj, boolean recursive) {
        if (focusedObjects.remove(obj) && obj instanceof FocusAware) {
            ((FocusAware) obj).onUnfocused();
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

    public static void clearFocus() {
        List<KeyAware> focused = new ArrayList<>(focusedObjects.size());
        focused.addAll(focusedObjects);
        for (int i = 0; i < focused.size(); ++i) {
            unfocus(focused.get(i), false);
        }
    }

    private static void replaceFocus(KeyAware obj) {
        List<KeyAware> focused = new ArrayList<>(focusedObjects.size());
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
                iterationList.get(i).onKeyDown(key, scanCode, repeat, mods);
            }
        } finally {
            iterationList.clear();
        }
    }

    static void keyReleaseEvent(int key, int scanCode, int mods) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                iterationList.get(i).onKeyReleased(key, scanCode, mods);
            }
        } finally {
            iterationList.clear();
        }
    }

    static void charInputEvent(char charInput) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                iterationList.get(i).onCharInput(charInput);
            }
        } finally {
            iterationList.clear();
        }
    }

    static void mouseButtonEventFinished(int button, ButtonAction action, int mods) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                var obj = iterationList.get(i);
                if (obj instanceof MouseWhenFocusedAware) {
                    ((MouseWhenFocusedAware) obj).onMouseButtonWhenFocused(button, action, mods);
                }
            }
        } finally {
            iterationList.clear();
        }
    }
}