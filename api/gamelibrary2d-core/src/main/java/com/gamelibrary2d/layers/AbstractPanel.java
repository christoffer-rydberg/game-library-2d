package com.gamelibrary2d.layers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.util.StackOrientation;

import java.util.List;

public abstract class AbstractPanel<T extends GameObject> extends AbstractLayerObject<T> implements Panel<T> {
    private boolean autoResizing = true;

    protected AbstractPanel() {
        setAutoClearing(false);
    }

    @Override
    public boolean isAutoResizing() {
        return autoResizing;
    }

    @Override
    public void setAutoResizing(boolean autoResizing) {
        this.autoResizing = autoResizing;
    }

    @Override
    public void add(T obj) {
        onAdd(obj, true);
    }

    protected void onAdd(T obj, boolean increaseBounds) {
        super.add(obj);
        if (increaseBounds) {
            expandBounds(obj);
        }
    }

    @Override
    public void add(int index, T obj) {
        onAdd(index, obj, autoResizing);
    }

    protected void onAdd(int index, T obj, boolean increaseBounds) {
        super.add(index, obj);
        if (increaseBounds) {
            expandBounds(obj);
        }
    }

    @Override
    public void remove(int index) {
        onRemove(index, autoResizing);
    }

    protected void onRemove(int index, boolean recalculateBounds) {
        super.remove(index);
        if (recalculateBounds) {
            recalculateBounds();
        }
    }

    @Override
    public boolean remove(Object obj) {
        return onRemove(obj, true);
    }

    protected boolean onRemove(Object obj, boolean recalculateBounds) {
        if (super.remove(obj)) {
            if (recalculateBounds) {
                recalculateBounds();
            }
            return true;
        }

        return false;
    }

    @Override
    public void clear() {
        super.clear();
        if (autoResizing) {
            setBounds(Rectangle.EMPTY);
        }
    }

    @Override
    public void expandBounds(T obj) {
        expandBounds(getExtentInPanel(obj));
    }

    @Override
    public void expandBounds(Rectangle bounds) {
        Rectangle panelBounds = getBounds();

        if (panelBounds.isInfinite() || bounds.isEmpty()) {
            return;
        }

        if (panelBounds.isEmpty() || bounds.isInfinite()) {
            setBounds(bounds);
            return;
        }

        float xMin = Math.min(bounds.getLowerX(), panelBounds.getLowerX());
        float yMin = Math.min(bounds.getLowerY(), panelBounds.getLowerY());
        float xMax = Math.max(bounds.getUpperX(), panelBounds.getUpperX());
        float yMax = Math.max(bounds.getUpperY(), panelBounds.getUpperY());

        setBounds(new Rectangle(xMin, yMin, xMax, yMax));
    }

    @Override
    public void recalculateBounds() {
        setBounds(Rectangle.EMPTY);
        List<T> objects = getChildren();
        for (int i = 0; i < objects.size(); ++i) {
            expandBounds(objects.get(i));
        }
    }

    @Override
    public void stack(T obj, StackOrientation orientation) {
        stack(obj, orientation, 0, 0, false);
    }

    @Override
    public void stack(T obj, StackOrientation orientation, float offset) {
        stack(obj, orientation, offset, 0, false);
    }

    @Override
    public void stack(T obj, StackOrientation orientation, float offset, float padding) {
        stack(obj, orientation, offset, padding, false);
    }

    @Override
    public void stack(T obj, StackOrientation orientation, float offset, float padding, boolean reposition) {
        if (getBounds().isInfinite()) {
            throw new IllegalStateException("Cannot stack object in panel with infinite bounds.");
        }

        if (padding < 0) {
            throw new IllegalStateException("Padding must be zero or greater.");
        }

        stackObject(obj, orientation, offset);

        if (autoResizing) {
            var objectBounds = getExtentInPanel(obj);

            if (!objectBounds.equals(Rectangle.EMPTY)) {
                stackExpandBounds(orientation, objectBounds, padding);
            } else {
                stackExpandPoint(orientation, obj.getPosition().getX(), obj.getPosition().getY(), padding);
            }
        }

        if (!reposition) {
            onAdd(obj, false);
        }
    }

    /**
     * Calculates how much space the specified object will take up inside the panel.
     * This can be used to expand the bounds of the panel when the object is added.
     * The space is calculated from the position, rotation, scale and bounds of the
     * object. This method will return {@link Rectangle#EMPTY} if the panel's bounds
     * are infinite.
     */
    protected Rectangle getExtentInPanel(T obj) {
        if (getBounds().isInfinite()) {
            // Object cannot increase bounds any more.
            return Rectangle.EMPTY;
        }

        Rectangle objectBounds = obj.getBounds();
        if (objectBounds.isEmpty() || objectBounds.isInfinite()) {
            return objectBounds;
        }

        objectBounds = objectBounds.move(obj.getPosition().getX(), obj.getPosition().getY());

        if (obj.getScale().getX() != 1 || obj.getScale().getY() != 1) {
            objectBounds = objectBounds.resize(obj.getScale().getX(), obj.getScale().getY(), obj.getPosition().getX(),
                    obj.getPosition().getY());
        }
        if (obj.getRotation() != 0) {
            objectBounds = objectBounds.rotate(obj.getRotation(), obj.getPosition().getX(),
                    obj.getPosition().getY());
        }
        return objectBounds;
    }

    private void stackObject(T obj, StackOrientation orientation, float offset) {
        float posX = obj.getPosition().getX();
        float posY = obj.getPosition().getY();

        obj.setPosition(0, 0);
        Rectangle objectBounds = getExtentInPanel(obj);
        if (objectBounds.isInfinite()) {
            obj.setPosition(posX, posY);
            throw new IllegalStateException("Stacked object must have valid bounds.");
        }

        switch (orientation) {
            case DOWN:
                posY = stackDown(objectBounds, offset);
                break;
            case LEFT:
                posX = stackLeft(objectBounds, offset);
                break;
            case RIGHT:
                posX = stackRight(objectBounds, offset);
                break;
            case UP:
                posY = stackUp(objectBounds, offset);
                break;
        }

        obj.setPosition(posX, posY);
    }

    private float stackLeft(Rectangle objectBounds, float margin) {
        return getBounds().getLowerX() - objectBounds.getUpperX() - margin;
    }

    private float stackUp(Rectangle objectBounds, float margin) {
        return getBounds().getUpperY() - objectBounds.getLowerY() + margin;
    }

    private float stackRight(Rectangle objectBounds, float margin) {
        return getBounds().getUpperX() - objectBounds.getLowerX() + margin;
    }

    private float stackDown(Rectangle objectBounds, float margin) {
        return getBounds().getLowerY() - objectBounds.getUpperY() - margin;
    }

    private void stackExpandBounds(StackOrientation orientation, Rectangle bounds, float padding) {
        if (getBounds().equals(Rectangle.EMPTY)) {
            bounds = bounds.add(0, 0);
        }

        if (padding != 0) {
            bounds = bounds.pad(orientation == StackOrientation.LEFT ? padding : 0,
                    orientation == StackOrientation.DOWN ? padding : 0,
                    orientation == StackOrientation.RIGHT ? padding : 0,
                    orientation == StackOrientation.UP ? padding : 0);
        }

        expandBounds(bounds);
    }

    private void stackExpandPoint(StackOrientation orientation, float posX, float posY, float padding) {
        switch (orientation) {
            case DOWN:
                posY -= padding;
                break;
            case LEFT:
                posX -= padding;
                break;
            case RIGHT:
                posX += padding;
                break;
            case UP:
                posY += padding;
                break;
        }

        setBounds(getBounds().add(posX, posY));
    }
}