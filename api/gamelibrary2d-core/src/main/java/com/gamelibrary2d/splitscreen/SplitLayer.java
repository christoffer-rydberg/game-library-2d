package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.components.denotations.Clearable;
import com.gamelibrary2d.components.denotations.PointerAware;
import com.gamelibrary2d.components.denotations.Updatable;
import com.gamelibrary2d.components.objects.AbstractComposedGameObject;
import com.gamelibrary2d.components.objects.GameObject;

public class SplitLayer<T extends GameObject> extends AbstractComposedGameObject<T>
        implements Clearable, PointerAware, Updatable {

    private final Rectangle renderArea;
    private SplitLayout layout;

    public SplitLayer(SplitLayout layout, Rectangle renderArea) {
        this.layout = layout;
        this.renderArea = renderArea;
    }

    public T getTarget() {
        return getContent();
    }

    public void setTarget(T obj) {
        setContent(obj);
    }

    public void setLayout(SplitLayout layout) {
        this.layout = layout;
    }

    @Override
    public void update(float deltaTime) {
        layout.update(getTarget(), renderArea, deltaTime);
        T content = getContent();
        if (content instanceof Updatable) {
            ((Updatable) content).update(deltaTime);
        }
    }

    @Override
    public void render(float alpha) {
        layout.render(alpha);
    }

    @Override
    public void clear() {
        T content = getContent();
        if (content instanceof Clearable) {
            ((Clearable) content).clear();
        }
    }

    @Override
    public boolean isAutoClearing() {
        T content = getContent();
        if (content instanceof Clearable) {
            return ((Clearable) content).isAutoClearing();
        }
        return false;
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        T content = getContent();
        if (content instanceof PointerAware) {
            return ((PointerAware) content).pointerDown(id, button, x, y, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        T content = getContent();
        if (content instanceof PointerAware) {
            return ((PointerAware) content).pointerMove(id, x, y, projectedX, projectedY);
        }
        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        T content = getContent();
        if (content instanceof PointerAware) {
            ((PointerAware) content).pointerUp(id, button, x, y, projectedX, projectedY);
        }
    }
}