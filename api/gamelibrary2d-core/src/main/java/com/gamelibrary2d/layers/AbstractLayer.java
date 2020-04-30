package com.gamelibrary2d.layers;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.markers.Updatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractLayer<T extends Renderable> implements Layer<T> {
    private final List<T> objects = new ArrayList<>();
    private final List<T> readonlyObjects = Collections.unmodifiableList(objects);
    private final List<Updatable> updatableObjects = new ArrayList<>();
    private final List<Clearable> clearableObjects = new ArrayList<>();
    private final List<MouseAware> mouseAwareObjects = new ArrayList<>();
    private final List<MouseAware> mouseAwareIterationList = new ArrayList<>();

    private Comparator<T> renderOrderComparator;

    private boolean autoClearing = true;
    private boolean enabled = true;
    private float opacity = 1f;

    @Override
    public int indexOf(Object obj) {
        return objects.indexOf(obj);
    }

    @Override
    public Comparator<T> getRenderOrderComparator() {
        return renderOrderComparator;
    }

    @Override
    public void setRenderOrderComparator(Comparator<T> renderOrderComparator) {
        this.renderOrderComparator = renderOrderComparator;
    }

    @Override
    public T get(int index) {
        return objects.get(index);
    }

    @Override
    public void add(T obj) {
        addType(obj);
        objects.add(obj);
    }

    @Override
    public void add(int index, T obj) {
        addType(obj);
        objects.add(index, obj);
    }

    private void addType(T obj) {
        if (obj instanceof Updatable)
            updatableObjects.add((Updatable) obj);
        if (obj instanceof Clearable)
            clearableObjects.add((Clearable) obj);
        if (obj instanceof MouseAware)
            mouseAwareObjects.add((MouseAware) obj);
    }

    @Override
    public void remove(int index) {
        removeType(objects.get(index));
        objects.remove(index);
    }

    @Override
    public boolean remove(Object obj) {
        if (objects.remove(obj)) {
            removeType(obj);
            return true;
        }
        return false;
    }

    private void removeType(Object obj) {
        if (obj instanceof Updatable)
            updatableObjects.remove(obj);
        if (obj instanceof Clearable)
            clearableObjects.remove(obj);
        if (obj instanceof MouseAware)
            mouseAwareObjects.remove(obj);
    }

    @Override
    public void clear() {
        objects.clear();
        updatableObjects.clear();
        mouseAwareObjects.clear();
        for (Clearable clearable : clearableObjects) {
            clear(clearable);
        }
        clearableObjects.clear();
    }

    private void clear(Clearable clearable) {
        if (clearable.isAutoClearing()) {
            clearable.clear();
        }
    }

    @Override
    public boolean isAutoClearing() {
        return autoClearing;
    }

    @Override
    public void setAutoClearing(boolean autoClearing) {
        this.autoClearing = autoClearing;
    }

    @Override
    public List<T> getChildren() {
        return readonlyObjects;
    }

    @Override
    public final boolean onMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        return isEnabled() ? handleMouseButtonDown(button, mods, projectedX, projectedY) : false;
    }

    protected boolean handleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        mouseAwareIterationList.addAll(mouseAwareObjects);
        for (int i = mouseAwareIterationList.size() - 1; i >= 0; --i) {
            MouseAware obj = mouseAwareIterationList.get(i);
            if (obj.onMouseButtonDown(button, mods, projectedX, projectedY)) {
                mouseAwareIterationList.clear();
                return true;
            }
        }

        mouseAwareIterationList.clear();

        return false;
    }

    @Override
    public final boolean onMouseMove(float projectedX, float projectedY) {
        return isEnabled() ? handleMouseMove(projectedX, projectedY) : false;
    }

    protected boolean handleMouseMove(float projectedX, float projectedY) {
        mouseAwareIterationList.addAll(mouseAwareObjects);
        for (int i = mouseAwareIterationList.size() - 1; i >= 0; --i) {
            MouseAware obj = mouseAwareIterationList.get(i);
            if (obj.onMouseMove(projectedX, projectedY)) {
                mouseAwareIterationList.clear();
                return true;
            }
        }

        mouseAwareIterationList.clear();

        return false;
    }

    @Override
    public final void onMouseButtonReleased(int button, int mods, float projectedX, float projectedY) {
        if (isEnabled()) {
            handleMouseButtonReleased(button, mods, projectedX, projectedY);
        }
    }

    protected void handleMouseButtonReleased(int button, int mods, float projectedX, float projectedY) {
        mouseAwareIterationList.addAll(mouseAwareObjects);
        for (int i = mouseAwareIterationList.size() - 1; i >= 0; --i) {
            mouseAwareIterationList.get(i).onMouseButtonReleased(button, mods, projectedX, projectedY);
        }

        mouseAwareIterationList.clear();
    }

    @Override
    public final void render(float alpha) {
        if (isEnabled()) {
            onRender(alpha * opacity);
        }
    }

    protected void onRender(float alpha) {
        if (renderOrderComparator != null) {
            objects.sort(renderOrderComparator);
        }

        int size = objects.size();
        for (int i = 0; i < size; ++i) {
            objects.get(i).render(alpha);
        }
    }

    @Override
    public final void update(float deltaTime) {
        if (isEnabled()) {
            handleUpdate(deltaTime);
        }
    }

    protected void handleUpdate(float deltaTime) {
        int size = updatableObjects.size();
        for (int i = 0; i < size; ++i) {
            updatableObjects.get(i).update(deltaTime);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public float getOpacity() {
        return opacity;
    }

    @Override
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
}