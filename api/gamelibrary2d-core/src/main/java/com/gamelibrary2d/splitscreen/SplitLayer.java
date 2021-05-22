package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.objects.AbstractGameObjectWrapper;
import com.gamelibrary2d.objects.GameObject;

public class SplitLayer<T extends GameObject> extends AbstractGameObjectWrapper<T> implements Clearable {
    private final Rectangle renderArea;
    private SplitLayout layout;

    public SplitLayer(SplitLayout layout, Rectangle renderArea) {
        this.layout = layout;
        this.renderArea = renderArea;
    }

    public T getTarget() {
        return getWrapped();
    }

    public void setTarget(T obj) {
        setWrapped(obj);
    }

    public void setLayout(SplitLayout layout) {
        this.layout = layout;
    }

    @Override
    public void update(float deltaTime) {
        layout.update(getTarget(), renderArea, deltaTime);
        super.update(deltaTime);
    }

    @Override
    public void render(float alpha) {
        layout.render(alpha);
    }

    @Override
    public void clear() {
        T wrapped = getWrapped();
        if (wrapped instanceof Clearable) {
            ((Clearable) wrapped).clear();
        }
    }

    @Override
    public boolean isAutoClearing() {
        T wrapped = getWrapped();
        if (wrapped instanceof Clearable) {
            return ((Clearable) wrapped).isAutoClearing();
        }
        return false;
    }
}