package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.disposal.ResourceDisposer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.util.Projection;
import com.gamelibrary2d.renderers.BitmapRenderer;

public abstract class AbstractMouseAwareObject<T extends Renderable> extends AbstractGameObject<T> implements MouseAware {
    private final MouseButtonStates mouseButtonStates = new MouseButtonStates(5);
    private Rectangle bounds;
    private BitmapRenderer bitmapRenderer;
    private ResourceDisposer disposer;

    protected AbstractMouseAwareObject() {

    }

    protected AbstractMouseAwareObject(T content) {
        super(content);
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);
            mouseButtonStates.clear();
        }
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : super.getBounds();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void disablePixelDetection() {
        if (this.disposer != null) {
            this.disposer.dispose();
            this.disposer = null;
            bitmapRenderer = null;
        }
    }

    public void enablePixelDetection(Disposer disposer) {
        if (this.disposer == null || this.disposer.getParentDisposer() != disposer) {
            disablePixelDetection();
            this.disposer = new ResourceDisposer(disposer);
        }
    }

    public boolean pixelDetectionEnabled() {
        return disposer != null;
    }

    private boolean isPixelVisible(float x, float y) {
        var bounds = getBounds();
        if (bounds.isInside(x, y)) {
            if (pixelDetectionEnabled()) {
                if (bitmapRenderer == null || !bitmapRenderer.getArea().equals(bounds)) {
                    disposer.dispose();
                    bitmapRenderer = BitmapRenderer.create(bounds, disposer);
                }
                bitmapRenderer.render(() -> super.onRenderProjected(1f));
                return bitmapRenderer.isVisible(x, y);
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean onMouseButtonDown(int button, int mods, float x, float y) {
        if (isEnabled() && isListeningToMouseClickEvents()) {
            var projected = Projection.projectTo(this, x, y);
            if (isPixelVisible(projected.getX(), projected.getY())) {
                if (handleMouseButtonDown(button, mods, projected.getX(), projected.getY())) {
                    mouseButtonStates.setActive(button, true);
                    return true;
                }
            }

            mouseButtonStates.setActive(button, false);
        }

        return false;
    }

    @Override
    public final boolean onMouseMove(float x, float y) {
        if (isEnabled()) {
            if (mouseButtonStates.hasActiveButtons() && isListeningToMouseDragEvents()) {
                var projected = Projection.projectTo(this, x, y);
                return handleMouseMove(projected.getX(), projected.getY(), true);
            } else if (isListeningToMouseHoverEvents()) {
                var projected = Projection.projectTo(this, x, y);
                if (isPixelVisible(projected.getX(), projected.getY())) {
                    return handleMouseMove(projected.getX(), projected.getY(), false);
                }
            }
        }

        return false;
    }

    @Override
    public final void onMouseButtonRelease(int button, int mods, float x, float y) {
        if (isEnabled() && mouseButtonStates.isActive(button)) {
            mouseButtonStates.setActive(button, false);
            var projected = Projection.projectTo(this, x, y);
            handleMouseButtonRelease(button, mods, projected.getX(), projected.getY());
        }
    }

    protected abstract boolean isListeningToMouseClickEvents();

    protected abstract boolean isListeningToMouseHoverEvents();

    protected abstract boolean isListeningToMouseDragEvents();

    protected abstract boolean handleMouseButtonDown(int button, int mods, float projectedX, float projectedY);

    protected abstract boolean handleMouseMove(float projectedX, float projectedY, boolean drag);

    protected abstract void handleMouseButtonRelease(int button, int mods, float projectedX, float projectedY);

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