package com.gamelibrary2d.layers;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.util.Projection;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractLayerObject<T extends Renderable> extends AbstractGameObject<Layer<T>> implements LayerObject<T> {
    protected AbstractLayerObject() {
        setContent(new BasicLayer<>());
    }

    @Override
    public int indexOf(Object obj) {
        return getContent().indexOf(obj);
    }

    @Override
    public Comparator<T> getRenderOrderComparator() {
        return getContent().getRenderOrderComparator();
    }

    @Override
    public void setRenderOrderComparator(Comparator<T> renderOrderComparator) {
        getContent().setRenderOrderComparator(renderOrderComparator);
    }

    @Override
    public boolean isAutoClearing() {
        return getContent().isAutoClearing();
    }

    public void setAutoClearing(boolean autoClearing) {
        getContent().setAutoClearing(autoClearing);
    }

    @Override
    public void clear() {
        getContent().clear();
    }

    @Override
    public T get(int index) {
        return getContent().get(index);
    }

    @Override
    public void add(T obj) {
        getContent().add(obj);
    }

    @Override
    public void add(int index, T obj) {
        getContent().add(index, obj);
    }

    @Override
    public void remove(int index) {
        getContent().remove(index);
    }

    @Override
    public boolean remove(Object obj) {
        return getContent().remove(obj);
    }

    @Override
    public List<T> getChildren() {
        return getContent().getChildren();
    }

    @Override
    public final boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        return isEnabled() ? onMouseButtonDown(id, button, x, y, projectedX, projectedY) : false;
    }

    protected boolean onMouseButtonDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        Point projected = Projection.projectTo(this, projectedX, projectedY);
        return getContent().pointerDown(id, button, x, y, projected.getX(), projected.getY());
    }

    @Override
    public final boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        return isEnabled() ? onMouseMove(id, x, y, projectedX, projectedY) : false;
    }

    protected boolean onMouseMove(int id, float x, float y, float projectedX, float projectedY) {
        Point projected = Projection.projectTo(this, projectedX, projectedY);
        return getContent().pointerMove(id, x, y, projected.getX(), projected.getY());
    }

    @Override
    public final void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            onMouseButtonReleased(id, button, x, y, projectedX, projectedY);
        }
    }

    protected void onMouseButtonReleased(int id, int button, float x, float y, float projectedX, float projectedY) {
        Point projected = Projection.projectTo(this, projectedX, projectedY);
        getContent().pointerUp(id, button, x, y, projected.getX(), projected.getY());
    }

    @Override
    public final void update(float deltaTime) {
        if (isEnabled()) {
            onUpdate(deltaTime);
        }
    }

    protected void onUpdate(float deltaTime) {
        getContent().update(deltaTime);
    }
}
