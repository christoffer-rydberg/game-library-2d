package com.gamelibrary2d.objects;

import com.gamelibrary2d.FocusManager;
import com.gamelibrary2d.common.Point;

/**
 * Base class for game objects that can handle keyboard and mouse input.
 * Keyboard events are routed from the {@link com.gamelibrary2d.FocusManager
 * FocusManager} when focused. Mouse events are routed from the parent container
 * but are ignored by default. The different types of mouse events can be
 * enabled individually. Mouse coordinates will automatically be projected to
 * the scale, rotation and position of the object.
 *
 * @author Christoffer Rydberg
 */
public abstract class AbstractInputObject extends AbstractGameObject implements MouseAware, KeyAware {

    /**
     * Used to calculate projected coordinates on mouse events. Since all mouse
     * event are on the GUI thread, this instance can be safely used without locks.
     */
    private static Point mouseProjectionPoint = new Point();

    private boolean focused;

    private boolean mouseEventHandled;

    private boolean listeningToMouseClickEvents;

    private boolean listeningToMouseHoverEvents;

    private boolean listeningToMouseDragEvents;

    /**
     * Keeps track of the mouse buttons that are active on the object.
     */
    private InternalButtonStateArray mouseButtonStates = new InternalButtonStateArray(5);

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

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled)
            return;
        super.setEnabled(enabled);
        setFocused(false);
        mouseButtonStates.clear();
    }

    @Override
    public final boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.focused == focused)
            return;
        this.focused = focused;
        if (focused) {
            FocusManager.focus(this, false);
            onFocused();
        } else {
            FocusManager.unfocus(this, false);
            onUnfocused();
        }

        if (isFocused())
            setEnabled(true);
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

        // Project coordinates to this objects position, rotation and scale:
        mouseProjectionPoint.set(projectedX, projectedY);
        projectTo(mouseProjectionPoint);
        projectedX = mouseProjectionPoint.getX();
        projectedY = mouseProjectionPoint.getY();

        if (isPixelVisible(projectedX, projectedY)) {
            if (onMouseClickEvent(button, mods, projectedX, projectedY)) {
                if (isEnabled()) { // Check is needed in case onMouseClickEvent disabled object.
                    // Disabled objects should not wait for mouse release,
                    // since no more mouse events will be triggered for the object.
                    mouseButtonStates.setActive(button, true);
                }

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

        // Project coordinates to this objects position, rotation and scale:
        mouseProjectionPoint.set(projectedX, projectedY);
        projectTo(mouseProjectionPoint);
        projectedX = mouseProjectionPoint.getX();
        projectedY = mouseProjectionPoint.getY();

        if (!drag && !isPixelVisible(projectedX, projectedY)) {
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
    public void mouseEventFinished(int button, int action, int mods) {
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

        // Project coordinates to this objects position, rotation and scale:
        mouseProjectionPoint.set(projectedX, projectedY);
        projectTo(mouseProjectionPoint);
        projectedX = mouseProjectionPoint.getX();
        projectedY = mouseProjectionPoint.getY();

        mouseButtonStates.setActive(button, false);
        onMouseReleaseEvent(button, mods, projectedX, projectedY);

        return true;
    }

    private void projectTo(Point point) {
        float rotationAndScaleCenterX = getPosition().getX() + getScaleAndRotationCenter().getX();
        float rotationAndScaleCenterY = getPosition().getY() + getScaleAndRotationCenter().getY();

        if (getRotation() != 0) {
            point.rotate(-getRotation(), rotationAndScaleCenterX, rotationAndScaleCenterY);
        }

        if (getScale().getX() != 1 || getScale().getY() != 1) {
            point.setX(rotationAndScaleCenterX + ((point.getX() - rotationAndScaleCenterX) / getScale().getX()));
            point.setY(rotationAndScaleCenterY + ((point.getY() - rotationAndScaleCenterY) / getScale().getY()));
        }

        point.setX(point.getX() - getPosition().getX());
        point.setY(point.getY() - getPosition().getY());
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

}
