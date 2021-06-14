package com.gamelibrary2d.components.containers;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.denotations.Bounded;
import com.gamelibrary2d.components.objects.AbstractGameObject;
import com.gamelibrary2d.Projection;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractLayerObject<T extends Renderable> extends AbstractGameObject implements LayerObject<T> {
    private final Layer<T> layer;
    private final Point projectionOutput = new Point();

    private Rectangle bounds;

    protected AbstractLayerObject() {
        layer = new BasicLayer<>();
    }

    @Override
    public int indexOf(Object obj) {
        return layer.indexOf(obj);
    }

    @Override
    public Comparator<T> getRenderOrderComparator() {
        return layer.getRenderOrderComparator();
    }

    @Override
    public void setRenderOrderComparator(Comparator<T> renderOrderComparator) {
        layer.setRenderOrderComparator(renderOrderComparator);
    }

    @Override
    public boolean isAutoClearing() {
        return layer.isAutoClearing();
    }

    public void setAutoClearing(boolean autoClearing) {
        layer.setAutoClearing(autoClearing);
    }

    @Override
    public void clear() {
        layer.clear();
    }

    @Override
    public T get(int index) {
        return layer.get(index);
    }

    @Override
    public void add(T obj) {
        layer.add(obj);
    }

    @Override
    public void add(int index, T obj) {
        layer.add(index, obj);
    }

    @Override
    public void remove(int index) {
        layer.remove(index);
    }

    @Override
    public boolean remove(Object obj) {
        return layer.remove(obj);
    }

    @Override
    public List<T> getChildren() {
        return layer.getChildren();
    }

    @Override
    public final boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        return isEnabled() && onPointerDown(id, button, x, y, projectedX, projectedY);
    }

    protected boolean onPointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        Projection.projectTo(this, projectedX, projectedY, projectionOutput);
        return layer.pointerDown(id, button, x, y, projectionOutput.getX(), projectionOutput.getY());
    }

    @Override
    public final boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        return isEnabled() && onPointerMove(id, x, y, projectedX, projectedY);
    }

    protected boolean onPointerMove(int id, float x, float y, float projectedX, float projectedY) {
        Projection.projectTo(this, projectedX, projectedY, projectionOutput);
        return layer.pointerMove(id, x, y, projectionOutput.getX(), projectionOutput.getY());
    }

    @Override
    public final void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            onPointerUp(id, button, x, y, projectedX, projectedY);
        }
    }

    protected void onPointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        Projection.projectTo(this, projectedX, projectedY, projectionOutput);
        layer.pointerUp(id, button, x, y, projectionOutput.getX(), projectionOutput.getY());
    }

    @Override
    public final void update(float deltaTime) {
        if (isEnabled()) {
            onUpdate(deltaTime);
        }
    }

    @Override
    protected void onRender(float alpha) {
        layer.render(alpha);
    }

    protected void onUpdate(float deltaTime) {
        layer.update(deltaTime);
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : getLayerBounds();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    private Rectangle getLayerBounds() {
        if (layer instanceof Bounded)
            return ((Bounded) layer).getBounds();
        else
            return Rectangle.EMPTY;
    }
}
