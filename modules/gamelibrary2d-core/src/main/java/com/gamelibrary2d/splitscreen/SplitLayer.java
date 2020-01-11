package com.gamelibrary2d.splitscreen;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.disposal.ResourceDisposer;
import com.gamelibrary2d.objects.AbstractWrapperObject;
import com.gamelibrary2d.objects.GameObject;

public class SplitLayer<T extends GameObject> extends AbstractWrapperObject<T> {
    private final Rectangle renderArea;
    private final Disposer layoutDisposer;
    private SplitLayout layout;

    public SplitLayer(SplitLayout layout, Rectangle renderArea, Disposer disposer) {
        this.layout = layout;
        this.renderArea = renderArea;
        this.layoutDisposer = new ResourceDisposer(disposer);
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
    public boolean isPixelVisible(float x, float y) {
        return false; // TODO: Handle hit detection
    }
}