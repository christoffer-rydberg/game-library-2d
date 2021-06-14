package com.gamelibrary2d.components.objects;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.components.denotations.PointerAware;
import com.gamelibrary2d.renderers.FrameBufferRenderer;
import com.gamelibrary2d.Projection;

public abstract class AbstractPointerAwareGameObject extends AbstractGameObject implements PointerAware {
    private final Point projectionOutput = new Point();
    private final PointerInteractionsArray pointerInteractions = new PointerInteractionsArray(10);
    private FrameBufferRenderer frameBufferRenderer;
    private DefaultDisposer disposer;

    protected AbstractPointerAwareGameObject() {

    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);
            pointerInteractions.clear();
        }
    }

    public void disablePixelDetection() {
        if (this.disposer != null) {
            this.disposer.dispose();
            this.disposer = null;
            frameBufferRenderer = null;
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
        Rectangle bounds = getBounds();
        if (bounds.contains(x, y)) {
            if (pixelDetectionEnabled()) {
                if (frameBufferRenderer == null || !frameBufferRenderer.getArea().equals(bounds)) {
                    disposer.dispose();
                    frameBufferRenderer = FrameBufferRenderer.create(bounds, disposer);
                }
                frameBufferRenderer.render(this::onRender, 1f);
                return frameBufferRenderer.isVisible(x, y);
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            Projection.projectTo(this, projectedX, projectedY, projectionOutput);
            if (isPixelVisible(projectionOutput.getX(), projectionOutput.getY()) && pointerInteractions.setActive(id, id)) {
                pointerActionStarted(projectedX, projectedY);
                onPointerDown(id, button, x, y, projectionOutput.getX(), projectionOutput.getY());
                pointerActionFinished(projectedX, projectedY);
                return true;
            }

            pointerInteractions.setInactive(id, id);
        }

        return false;
    }

    @Override
    public final boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            if (pointerInteractions.hasActiveButtons(id) && isListeningToPointDragEvents()) {
                Projection.projectTo(this, projectedX, projectedY, projectionOutput);
                pointerActionStarted(projectedX, projectedY);
                onPointerDrag(id, x, y, projectionOutput.getX(), projectionOutput.getY());
                pointerActionFinished(projectedX, projectedY);
                return true;
            } else if (isListeningToPointHoverEvents()) {
                Projection.projectTo(this, projectedX, projectedY, projectionOutput);
                if (isPixelVisible(projectionOutput.getX(), projectionOutput.getY())) {
                    pointerActionStarted(projectedX, projectedY);
                    onPointerHover(id, x, y, projectionOutput.getX(), projectionOutput.getY());
                    pointerActionFinished(projectedX, projectedY);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public final void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (isEnabled() && pointerInteractions.isActive(id, id)) {
            pointerInteractions.setInactive(id, id);
            Projection.projectTo(this, projectedX, projectedY, projectionOutput);
            pointerActionStarted(projectedX, projectedY);
            onPointerUp(id, button, x, y, projectionOutput.getX(), projectionOutput.getY());
            pointerActionFinished(projectedX, projectedY);
        }
    }

    /**
     * Invoked before a pointer action is handled.
     *
     * @param x The x-coordinate of the pointer projected to the parent container.
     * @param y The y-coordinate of the pointer projected to the parent container.
     */
    protected void pointerActionStarted(float x, float y) {

    }

    /**
     * Invoked after after a pointer action is handled.
     *
     * @param x The x-coordinate of the pointer projected to the parent container.
     * @param y The y-coordinate of the pointer projected to the parent container.
     */
    protected void pointerActionFinished(float x, float y) {

    }

    protected abstract boolean isListeningToPointHoverEvents();

    protected abstract boolean isListeningToPointDragEvents();

    /**
     * Invoked when a pointer down action is handled.
     *
     * @param id         The id of the pointer.
     * @param button     The id of the pointer button.
     * @param x          The x-coordinate of the pointer.
     * @param y          The y-coordinate of the pointer.
     * @param projectedX The x-coordinate of the pointer projected to this object.
     * @param projectedY The y-coordinate of the pointer projected to this object.
     */
    protected abstract void onPointerDown(int id, int button, float x, float y, float projectedX, float projectedY);

    /**
     * Invoked when a pointer up event is handled.
     *
     * @param id         The id of the pointer.
     * @param button     The id of the pointer button.
     * @param x          The x-coordinate of the pointer.
     * @param y          The y-coordinate of the pointer.
     * @param projectedX The x-coordinate of the pointer projected to this object.
     * @param projectedY The y-coordinate of the pointer projected to this object.
     */
    protected abstract void onPointerUp(int id, int button, float x, float y, float projectedX, float projectedY);

    /**
     * Invoked when the pointer hovers over this object.
     *
     * @param id         The id of the pointer.
     * @param x          The x-coordinate of the pointer.
     * @param y          The y-coordinate of the pointer.
     * @param projectedX The x-coordinate of the pointer projected to this object.
     * @param projectedY The y-coordinate of the pointer projected to this object.
     */
    protected abstract void onPointerHover(int id, float x, float y, float projectedX, float projectedY);

    /**
     * Invoked when one or more pointer down actions has been handled by this object,
     * but not yet been released, and the pointer moves.
     *
     * @param id         The id of the pointer.
     * @param x          The x-coordinate of the pointer.
     * @param y          The y-coordinate of the pointer.
     * @param projectedX The x-coordinate of the pointer projected to this object.
     * @param projectedY The y-coordinate of the pointer projected to this object.
     */
    protected abstract void onPointerDrag(int id, float x, float y, float projectedX, float projectedY);

    private static class PointerInteractions {
        private int count;

        private boolean[] active;

        private int capacity = 0;

        PointerInteractions(int initialArraySize) {
            this.active = new boolean[initialArraySize];
        }

        void clear() {
            for (int i = 0; i < capacity; ++i) {
                active[i] = false;
            }
            capacity = 0;
        }

        void setActive(int button, boolean active) {
            capacity = Math.max(capacity, button + 1);

            if (button > this.active.length) {
                this.active = new boolean[capacity];
            }

            if (active != this.active[button]) {
                count += active ? 1 : -1;
                this.active[button] = active;
            }
        }

        boolean hasActiveButtons() {
            return count > 0;
        }

        boolean isActive(int button) {
            return button < active.length && active[button];
        }
    }

    private static class PointerInteractionsArray {
        private final PointerInteractions[] pointerInteractions;

        PointerInteractionsArray(int capacity) {
            pointerInteractions = new PointerInteractions[capacity];
        }

        boolean isActive(int id, int button) {
            PointerInteractions interactions = pointerInteractions[id];
            return interactions != null && interactions.isActive(button);
        }

        boolean setActive(int id, int button) {
            if (id > pointerInteractions.length) {
                return false;
            }

            PointerInteractions interactions = pointerInteractions[id];
            if (interactions == null) {
                interactions = new PointerInteractions(5);
                pointerInteractions[id] = interactions;
            }
            interactions.setActive(button, true);

            return true;
        }

        void setInactive(int id, int button) {
            PointerInteractions interactions = pointerInteractions[id];
            if (interactions != null) {
                interactions.setActive(button, false);
            }
        }

        void clear() {
            for (int i = 0; i < 10; ++i) {
                PointerInteractions interactions = pointerInteractions[i];
                if (interactions != null) {
                    interactions.clear();
                }
            }
        }

        boolean hasActiveButtons(int id) {
            PointerInteractions interactions = pointerInteractions[id];
            return interactions != null && interactions.hasActiveButtons();
        }
    }
}