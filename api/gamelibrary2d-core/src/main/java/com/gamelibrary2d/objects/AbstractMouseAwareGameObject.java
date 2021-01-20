package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.renderers.BitmapRenderer;
import com.gamelibrary2d.util.Projection;

public abstract class AbstractMouseAwareGameObject<T extends Renderable> extends AbstractGameObject<T> implements MouseAware {

    private final MouseButtonStates mouseButtonStates = new MouseButtonStates(5);
    private BitmapRenderer bitmapRenderer;
    private DefaultDisposer disposer;

    protected AbstractMouseAwareGameObject() {

    }

    protected AbstractMouseAwareGameObject(T content) {
        super(content);
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);
            mouseButtonStates.clear();
        }
    }

    public void disablePixelDetection() {
        if (this.disposer != null) {
            this.disposer.dispose();
            this.disposer = null;
            bitmapRenderer = null;
        }
    }

    public void enablePixelDetection(Disposer disposer) {
        if (this.disposer == null || this.disposer.getParent() != disposer) {
            disablePixelDetection();
            this.disposer = new DefaultDisposer(disposer);
        }
    }

    public boolean pixelDetectionEnabled() {
        return disposer != null;
    }

    private boolean isPixelVisible(float x, float y) {
        var bounds = getBounds();
        if (bounds.contains(x, y)) {
            if (pixelDetectionEnabled()) {
                if (bitmapRenderer == null || !bitmapRenderer.getArea().equals(bounds)) {
                    disposer.dispose();
                    bitmapRenderer = BitmapRenderer.create(bounds, disposer);
                }
                bitmapRenderer.render(() -> onRenderProjected(1f));
                return bitmapRenderer.isVisible(x, y);
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean mouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            var projected = Projection.projectTo(this, projectedX, projectedY);
            if (isPixelVisible(projected.getX(), projected.getY())) {
                mouseEventStarted(projectedX, projectedY);
                onMouseButtonDown(button, mods, x, y, projected.getX(), projected.getY());
                mouseEventFinished(projectedX, projectedY);
                mouseButtonStates.setActive(button, true);
                return true;
            }

            mouseButtonStates.setActive(button, false);
        }

        return false;
    }

    @Override
    public final boolean mouseMove(float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            if (mouseButtonStates.hasActiveButtons() && isListeningToMouseDragEvents()) {
                var projected = Projection.projectTo(this, projectedX, projectedY);
                mouseEventStarted(projectedX, projectedY);
                onMouseDrag(x, y, projected.getX(), projected.getY());
                mouseEventFinished(projectedX, projectedY);
                return true;
            } else if (isListeningToMouseHoverEvents()) {
                var projected = Projection.projectTo(this, projectedX, projectedY);
                if (isPixelVisible(projected.getX(), projected.getY())) {
                    mouseEventStarted(projectedX, projectedY);
                    onMouseHover(x, y, projected.getX(), projected.getY());
                    mouseEventFinished(projectedX, projectedY);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public final void mouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (isEnabled() && mouseButtonStates.isActive(button)) {
            mouseButtonStates.setActive(button, false);
            var projected = Projection.projectTo(this, projectedX, projectedY);
            mouseEventStarted(projectedX, projectedY);
            onMouseButtonReleased(button, mods, x, y, projected.getX(), projected.getY());
            mouseEventFinished(projectedX, projectedY);
        }
    }

    /**
     * Invoked before a mouse event is handled.
     *
     * @param x The x-coordinate of the mouse cursor projected to the parent container.
     * @param y The y-coordinate of the mouse cursor projected to the parent container.
     */
    protected void mouseEventStarted(float x, float y) {

    }

    /**
     * Invoked after after a mouse event is handled.
     *
     * @param x The x-coordinate of the mouse cursor projected to the parent container.
     * @param y The y-coordinate of the mouse cursor projected to the parent container.
     */
    protected void mouseEventFinished(float x, float y) {

    }

    protected abstract boolean isListeningToMouseHoverEvents();

    protected abstract boolean isListeningToMouseDragEvents();

    /**
     * Invoked when a  mouse button down event is handled.
     *
     * @param button     The mouse button that was pressed.
     * @param mods       Describes which modifier keys were held down.
     * @param x          The x-coordinate of the mouse cursor.
     * @param y          The y-coordinate of the mouse cursor.
     * @param projectedX The x-coordinate of the mouse cursor projected to this object.
     * @param projectedY The y-coordinate of the mouse cursor projected to this object.
     */
    protected abstract void onMouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY);

    /**
     * Invoked when a  mouse button release event is handled.
     *
     * @param button     The mouse button that was released.
     * @param mods       Describes which modifier keys were held down.
     * @param x          The x-coordinate of the mouse cursor.
     * @param y          The y-coordinate of the mouse cursor.
     * @param projectedX The x-coordinate of the mouse cursor projected to this object.
     * @param projectedY The y-coordinate of the mouse cursor projected to this object.
     */
    protected abstract void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY);

    /**
     * Invoked when the mouse hovers over this object.
     *
     * @param x          The x-coordinate of the mouse cursor.
     * @param y          The y-coordinate of the mouse cursor.
     * @param projectedX The x-coordinate of the mouse cursor projected to this object.
     * @param projectedY The y-coordinate of the mouse cursor projected to this object.
     */
    protected abstract void onMouseHover(float x, float y, float projectedX, float projectedY);

    /**
     * Invoked when one or more mouse button-down events has been handled by this object,
     * but not yet been released, and the mouse moves.
     *
     * @param x          The x-coordinate of the mouse cursor.
     * @param y          The y-coordinate of the mouse cursor.
     * @param projectedX The x-coordinate of the mouse cursor projected to this object.
     * @param projectedY The y-coordinate of the mouse cursor projected to this object.
     */
    protected abstract void onMouseDrag(float x, float y, float projectedX, float projectedY);

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