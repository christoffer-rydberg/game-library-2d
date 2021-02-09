package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.objects.AbstractGameObjectWrapper;
import com.gamelibrary2d.objects.GameObject;

public class SplitLayer<T extends GameObject> extends AbstractGameObjectWrapper<T> implements Clearable {
    private final Rectangle renderArea;
    private final Disposer layoutDisposer;
    private SplitLayout layout;

    public SplitLayer(SplitLayout layout, Rectangle renderArea, Disposer disposer) {
        this.layout = layout;
        this.renderArea = renderArea;
        this.layoutDisposer = new DefaultDisposer(disposer);
    }

    public T getTarget() {
        return getWrapped();
    }

    public void setTarget(T obj) {
        setWrapped(obj);
    }

    public void setLayout(SplitLayout layout) {
        if (this.layout != layout) {
            layoutDisposer.dispose();
            this.layout = layout;
        }
    }

    @Override
    public void update(float deltaTime) {
        layout.update(getTarget(), renderArea, deltaTime, layoutDisposer);
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