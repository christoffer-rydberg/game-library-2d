package com.gamelibrary2d;

import com.gamelibrary2d.components.denotations.*;

import java.util.ArrayList;
import java.util.List;

public class FocusManager {

    private static final List<Object> focused = new ArrayList<>();

    private static final List<Object> focusedByPointer = new ArrayList<>();

    private static final List<Object> iterationList = new ArrayList<>();

    private static boolean pointerActive;

    static void onPointerActive() {
        pointerActive = true;
    }

    static void onPointerInactive() {
        pointerActive = false;
        focused.addAll(focusedByPointer);
        focusedByPointer.clear();
    }

    public static boolean isFocused(Object obj) {
        return focused.contains(obj) || focusedByPointer.contains(obj);
    }

    public static void focus(Object obj, boolean replace) {
        if (replace) {
            replaceFocus(obj);
        }

        if (!isFocused(obj)) {
            if (pointerActive) {
                focusedByPointer.add(obj);
            } else {
                focused.add(obj);
            }

            if (obj instanceof FocusAware) {
                ((FocusAware) obj).focused();
            }
        }
    }

    private static boolean removeFocus(Object obj) {
        return focused.remove(obj) || focusedByPointer.remove(obj);
    }

    public static void unfocus(Object obj, boolean recursive) {
        if (removeFocus(obj) && obj instanceof FocusAware) {
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
        List<Object> iterationList = new ArrayList<>(FocusManager.focused.size() + focusedByPointer.size());
        iterationList.addAll(focused);
        iterationList.addAll(focusedByPointer);
        for (int i = 0; i < iterationList.size(); ++i) {
            unfocus(iterationList.get(i), false);
        }
    }

    private static void replaceFocus(Object obj) {
        List<Object> iterationList = new ArrayList<>(FocusManager.focused.size() + focusedByPointer.size());
        iterationList.addAll(focused);
        iterationList.addAll(focusedByPointer);
        for (int i = 0; i < iterationList.size(); ++i) {
            Object focusedObject = iterationList.get(i);
            if (focusedObject != obj) {
                unfocus(iterationList.get(i), false);
            }
        }
    }

    static void keyDownEvent(int key, boolean repeat) {
        try {
            iterationList.addAll(focused);
            iterationList.addAll(focusedByPointer);
            for (int i = 0; i < iterationList.size(); ++i) {
                Object obj = iterationList.get(i);
                if (obj instanceof KeyAware) {
                    ((KeyAware) obj).keyDown(key, repeat);
                }
            }
        } finally {
            iterationList.clear();
        }
    }

    static void keyUpEvent(int key) {
        try {
            iterationList.addAll(focused);
            iterationList.addAll(focusedByPointer);
            for (int i = 0; i < iterationList.size(); ++i) {
                Object obj = iterationList.get(i);
                if (obj instanceof KeyAware) {
                    ((KeyAware) obj).keyUp(key);
                }
            }
        } finally {
            iterationList.clear();
        }
    }

    static void charInputEvent(char charInput) {
        try {
            iterationList.addAll(focused);
            iterationList.addAll(focusedByPointer);
            for (int i = 0; i < iterationList.size(); ++i) {
                Object obj = iterationList.get(i);
                if (obj instanceof InputAware) {
                    ((InputAware) obj).charInput(charInput);
                }
            }
        } finally {
            iterationList.clear();
        }
    }

    private static void onPointerActionFinished(int id, int button, boolean released) {
        try {
            iterationList.addAll(focused);
            for (int i = 0; i < iterationList.size(); ++i) {
                Object obj = iterationList.get(i);
                if (obj instanceof PointerWhenFocusedAware) {
                    if (released) {
                        ((PointerWhenFocusedAware) obj).pointerUpWhenFocused(id, button);
                    } else {
                        ((PointerWhenFocusedAware) obj).pointerDownWhenFocused(id, button);
                    }
                }
            }
        } finally {
            iterationList.clear();
        }
    }

    static void pointerDownFinished(int id, int button) {
        onPointerActionFinished(id, button, false);
    }

    static void pointerUpFinished(int id, int button) {
        onPointerActionFinished(id, button, true);
    }
}