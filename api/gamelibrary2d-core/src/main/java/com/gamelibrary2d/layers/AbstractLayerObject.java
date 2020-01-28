package com.gamelibrary2d.layers;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.objects.Projection;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractLayerObject<T extends Renderable> extends AbstractGameObject implements LayerObject<T> {
    private final Layer<T> layer = new BasicLayer<>();

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
    public boolean mouseButtonDownEvent(int button, int mods, float projectedX, float projectedY) {
        var projected = Projection.projectTo(this, projectedX, projectedY);
        return layer.mouseButtonDownEvent(button, mods, projected.getX(), projected.getY());
    }

    @Override
    public boolean mouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        var projected = Projection.projectTo(this, projectedX, projectedY);
        return layer.mouseMoveEvent(projected.getX(), projected.getY(), drag);
    }

    @Override
    public boolean mouseButtonReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        var projected = Projection.projectTo(this, projectedX, projectedY);
        return layer.mouseButtonReleaseEvent(button, mods, projected.getX(), projected.getY());
    }

    @Override
    protected void onRenderProjected(float alpha) {
        layer.render(alpha);
    }

    @Override
    public final void update(float deltaTime) {
        if (isEnabled()) {
            onUpdate(deltaTime);
        }
    }

    protected void onUpdate(float deltaTime) {
        layer.update(deltaTime);
    }
}
