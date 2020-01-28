package com.gamelibrary2d.objects;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.markers.Focusable;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.markers.MouseAware;

public abstract class AbstractFocusableObject extends AbstractGameObject implements MouseAware, KeyAware, Focusable {

    private final MouseButtonStates mouseButtonStates = new MouseButtonStates(5);
    private boolean focused;
    private boolean mouseEventHandled;
    private boolean listeningToMouseClickEvents;
    private boolean listeningToMouseHoverEvents;
    private boolean listeningToMouseDragEvents;

    public boolean isListeningToMouseClickEvents() {
        return listeningToMouseClickEvents;
    }

    public void setListeningToMouseClickEvents(boolean listeningToMouseClickEvents) {
        this.listeningToMouseClickEvents = listeningToMouseClickEvents;
    }

    public boolean isListeningToMouseHoverEvents() {
        return listeningToMouseHoverEvents;
    }

    public void setListeningToMouseHoverEvents(boolean listeningToMouseHoverEvents) {
        this.listeningToMouseHoverEvents = listeningToMouseHoverEvents;
    }

    public boolean isListeningToMouseDragEvents() {
        return listeningToMouseDragEvents;
    }

    public void setListeningToMouseDragEvents(boolean listeningToMouseDragEvents) {
        this.listeningToMouseDragEvents = listeningToMouseDragEvents;
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);
            mouseButtonStates.clear();
        }
    }

    @Override
    public final boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.focused == focused) {
            return;
        }

        this.focused = focused;
        if (focused) {
            setEnabled(true);
            FocusManager.focus(this, false);
            onFocused();
        } else {
            FocusManager.unfocus(this, false);
            onUnfocused();
        }
    }

    @Override
    public boolean mouseButtonDownEvent(int button, int mods, float projectedX, float projectedY) {
        if (handleMouseClickEvent(button, mods, projectedX, projectedY)) {
            mouseEventHandled = true;
            return true;
        }

        return false;
    }

    private boolean handleMouseClickEvent(int button, int mods, float projectedX, float projectedY) {
        if (!isEnabled() || !isListeningToMouseClickEvents()) {
            return false;
        }

        var projected = Projection.projectTo(this, projectedX, projectedY);
        if (isPixelVisible(projected.getX(), projected.getY())) {
            if (onMouseClickEvent(button, mods, projected.getX(), projected.getY())) {
                mouseButtonStates.setActive(button, true);
                return true;
            }
        }

        mouseButtonStates.setActive(button, false);

        return false;
    }

    @Override
    public boolean mouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        if (!isEnabled())
            return false;

        if (mouseButtonStates.hasActiveButtons()) {
            if (!isListeningToMouseDragEvents()) {
                // Don't listen to drag event
                return false;
            }

            // This object is being "dragged"
            drag = true;
        } else {
            if (drag) {
                // Another object is being "dragged", skip event.
                return false;
            }

            if (!isListeningToMouseHoverEvents()) {
                // Don't listen to hover event
                return false;
            }
        }

        var projected = Projection.projectTo(this, projectedX, projectedY);
        if (!drag && !isPixelVisible(projected.getX(), projected.getY())) {
            // The object is not dragged and not hovered over.
            return false;
        }

        return onMouseMoveEvent(projectedX, projectedY, drag);
    }

    @Override
    public boolean mouseButtonReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        if (handleMouseReleaseEvent(button, mods, projectedX, projectedY)) {
            mouseEventHandled = true;
            return true;
        }

        return false;
    }

    @Override
    public void mouseButtonEventFinished(int button, int action, int mods) {
        if (mouseEventHandled) {
            mouseEventHandled = false;
            return;
        }

        onUnhandledMouseEvent(button, action, mods);
    }

    private boolean handleMouseReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        if (!mouseButtonStates.isActive(button)) {
            // Can be skipped since no mouse down has been handled.
            return false;
        }

        mouseButtonStates.setActive(button, false);
        var projected = Projection.projectTo(this, projectedX, projectedY);
        onMouseReleaseEvent(button, mods, projected.getX(), projected.getY());

        return true;
    }

    protected void onUnhandledMouseEvent(int button, int action, int mods) {
    }

    protected void onFocused() {
    }

    protected void onUnfocused() {
    }

    protected abstract boolean onMouseClickEvent(int button, int mods, float projectedX, float projectedY);

    protected abstract boolean onMouseMoveEvent(float projectedX, float projectedY, boolean drag);

    protected abstract void onMouseReleaseEvent(int button, int mods, float projectedX, float projectedY);

    private static class MouseButtonStates {

        private int activeButtons;

        private boolean[] buttons;

        private int arraySize = 0;

        public MouseButtonStates(int initialArraySize) {
            this.buttons = new boolean[initialArraySize];
        }

        public void clear() {
            for (int i = 0; i < arraySize; ++i) {
                buttons[i] = false;
            }
            arraySize = 0;
        }

        public void setActive(int button, boolean active) {

            arraySize = Math.max(arraySize, button + 1);

            if (button > buttons.length) {
                buttons = new boolean[arraySize];
            }

            if (active != buttons[button]) {
                activeButtons += active ? 1 : -1;
                buttons[button] = active;
            }
        }

        public boolean hasActiveButtons() {
            return activeButtons > 0;
        }

        public boolean isActive(int button) {
            return button < buttons.length ? buttons[button] : false;
        }
    }
}
