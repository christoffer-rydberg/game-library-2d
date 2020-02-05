package com.gamelibrary2d.layers;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.util.Projection;
import com.gamelibrary2d.objects.AbstractGameObject;

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
    public final boolean onMouseButtonDown(int button, int mods, float x, float y) {
        return isEnabled() ? handleMouseButtonDown(button, mods, x, y) : false;
    }

    protected boolean handleMouseButtonDown(int button, int mods, float x, float y) {
        var projected = Projection.projectTo(this, x, y);
        return getContent().onMouseButtonDown(button, mods, projected.getX(), projected.getY());
    }

    @Override
    public final boolean onMouseMove(float x, float y) {
        return isEnabled() ? handleMouseMove(x, y) : false;
    }

    protected boolean handleMouseMove(float x, float y) {
        var projected = Projection.projectTo(this, x, y);
        return getContent().onMouseMove(projected.getX(), projected.getY());
    }

    @Override
    public final void onMouseButtonRelease(int button, int mods, float x, float y) {
        if (isEnabled()) {
            handleMouseButtonRelease(button, mods, x, y);
        }
    }

    protected void handleMouseButtonRelease(int button, int mods, float x, float y) {
        var projected = Projection.projectTo(this, x, y);
        getContent().onMouseButtonRelease(button, mods, projected.getX(), projected.getY());
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
