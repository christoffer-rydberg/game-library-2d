package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.updating.Updatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractModifiableContainer<T extends GameObject> extends AbstractInputObject implements ModifiableContainer<T> {

    private final List<T> objects;
    private final List<T> readonlyObjects;

    private final List<Updatable> activeObjects;
    private final List<Clearable> clearableObjects;

    private final List<MouseAware> mouseAwareObjects;

    private final List<MouseAware> mouseAwareIterationList;

    private boolean autoClearing;
    private Comparator<T> renderOrderComparator;

    protected AbstractModifiableContainer() {
        objects = new ArrayList<>();
        readonlyObjects = Collections.unmodifiableList(objects);
        mouseAwareIterationList = new ArrayList<>();
        activeObjects = new ArrayList<>();
        clearableObjects = new ArrayList<>();
        mouseAwareObjects = new ArrayList<>();
        setListeningToMouseClickEvents(true);
        setListeningToMouseDragEvents(true);
        setListeningToMouseHoverEvents(true);
    }

    public int size() {
        return objects.size();
    }

    public int indexOf(T obj) {
        return objects.indexOf(obj);
    }

    /**
     * @return The comparator used to sort game objects before rendering.
     */
    public Comparator<T> getRenderOrderComparator() {
        return renderOrderComparator;
    }

    /**
     * Sets the comparator used to sort game objects before rendering. If the
     * comparator is null (default) no sorting will be done and objects will be
     * rendered the order they are inserted. However, if a comparator has been set,
     * all objects will be reordered on the first render call. Setting the
     * comparator back to null will not restore the object order.
     *
     * @param renderOrderComparator The comparator used to sort game objects before rendering.
     */
    public void setRenderOrderComparator(Comparator<T> renderOrderComparator) {
        this.renderOrderComparator = renderOrderComparator;
    }

    @Override
    public boolean isAutoClearing() {
        return autoClearing;
    }

    public void setAutoClearing(boolean autoClearing) {
        this.autoClearing = autoClearing;
    }

    @Override
    public void clear() {
        objects.clear();
        activeObjects.clear();
        clearObjects();
        mouseAwareObjects.clear();
    }

    private void clearObjects() {
        for (int i = 0; i < clearableObjects.size(); ++i) {
            Clearable container = clearableObjects.get(i);
            if (container.isAutoClearing()) {
                container.clear();
            }
        }
        clearableObjects.clear();
    }

    public T get(int index) {
        return objects.get(index);
    }

    public void add(T obj) {
        addType(obj);
        objects.add(obj);
    }

    public void add(int index, T obj) {
        addType(obj);
        objects.add(index, obj);
    }

    private void addType(T obj) {
        if (obj instanceof Updatable)
            activeObjects.add((Updatable) obj);
        if (obj instanceof Clearable)
            clearableObjects.add((Clearable) obj);
        if (obj instanceof MouseAware)
            mouseAwareObjects.add((MouseAware) obj);
    }

    public void remove(int index) {
        removeType(objects.get(index));
        objects.remove(index);
    }

    public boolean remove(T obj) {
        if (objects.remove(obj)) {
            removeType(obj);
            return true;
        }
        return false;
    }

    private void removeType(T obj) {
        if (obj instanceof Updatable)
            activeObjects.remove(obj);
        if (obj instanceof Clearable)
            clearableObjects.remove(obj);
        if (obj instanceof MouseAware)
            mouseAwareObjects.remove(obj);
    }

    public List<T> getObjects() {
        return readonlyObjects;
    }

    @Override
    protected boolean onMouseClickEvent(int button, int mods, float projectedX, float projectedY) {
        mouseAwareIterationList.addAll(mouseAwareObjects);
        for (int i = mouseAwareIterationList.size() - 1; i >= 0; --i) {
            MouseAware obj = mouseAwareIterationList.get(i);
            if (obj.mouseButtonDownEvent(button, mods, projectedX, projectedY)) {
                mouseAwareIterationList.clear();
                return true;
            }
        }

        mouseAwareIterationList.clear();
        return false;
    }

    @Override
    protected boolean onMouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        mouseAwareIterationList.addAll(mouseAwareObjects);
        for (int i = mouseAwareIterationList.size() - 1; i >= 0; --i) {
            MouseAware obj = mouseAwareIterationList.get(i);
            if (obj.mouseMoveEvent(projectedX, projectedY, drag)) {
                mouseAwareIterationList.clear();
                return true;
            }
        }

        mouseAwareIterationList.clear();
        return false;
    }

    @Override
    protected void onMouseReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        mouseAwareIterationList.addAll(mouseAwareObjects);
        for (int i = mouseAwareIterationList.size() - 1; i >= 0; --i) {
            MouseAware obj = mouseAwareIterationList.get(i);
            if (obj.mouseButtonReleaseEvent(button, mods, projectedX, projectedY)) {
                mouseAwareIterationList.clear();
                return;
            }
        }

        mouseAwareIterationList.clear();
    }

    @Override
    public void charInputEvent(char charInput) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyDownEvent(int key, int scanCode, boolean repeat, int mods) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleaseEvent(int key, int scanCode, int mods) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onRender(float alpha) {
        if (renderOrderComparator != null) {
            objects.sort(renderOrderComparator);
        }

        int size = objects.size();
        for (int i = 0; i < size; ++i) {
            var obj = objects.get(i);
            if (!obj.isEnabled())
                continue;
            obj.render(alpha);
        }
    }

    @Override
    public void update(float deltaTime) {
        int size = activeObjects.size();
        for (int i = 0; i < size; ++i) {
            Updatable obj = activeObjects.get(i);
            if (((GameObject) obj).isEnabled()) {
                obj.update(deltaTime);
            }
        }
    }
}