package com.gamelibrary2d;

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
                ((FocusAware) obj).focused();
            }
        }
    }

    public static void unfocus(Object obj, boolean recursive) {
        if (focusedObjects.remove(obj) && obj instanceof FocusAware) {
            ((FocusAware) obj).unfocused();
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
                iterationList.get(i).keyDown(key, scanCode, repeat, mods);
            }
        } finally {
            iterationList.clear();
        }
    }

    static void keyReleaseEvent(int key, int scanCode, int mods) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                iterationList.get(i).keyReleased(key, scanCode, mods);
            }
        } finally {
            iterationList.clear();
        }
    }

    static void charInputEvent(char charInput) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                iterationList.get(i).charInput(charInput);
            }
        } finally {
            iterationList.clear();
        }
    }

    private static void onMouseButtonEventFinished(int button, int mods, boolean released) {
        try {
            iterationList.addAll(focusedObjects);
            for (int i = 0; i < iterationList.size(); ++i) {
                var obj = iterationList.get(i);
                if (obj instanceof MouseWhenFocusedAware) {
                    if (released) {
                        ((MouseWhenFocusedAware) obj).mouseButtonReleasedWhenFocused(button, mods);
                    } else {
                        ((MouseWhenFocusedAware) obj).mouseButtonDownWhenFocused(button, mods);
                    }
                }
            }
        } finally {
            iterationList.clear();
        }
    }

    static void mouseButtonDownFinished(int button, int mods) {
        onMouseButtonEventFinished(button, mods, false);
    }

    static void mouseButtonReleasedFinished(int button, int mods) {
        onMouseButtonEventFinished(button, mods, true);
    }
}