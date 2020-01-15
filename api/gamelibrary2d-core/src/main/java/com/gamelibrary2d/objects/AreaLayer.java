package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.glUtil.ModelMatrix;

public class AreaLayer<T extends GameObject> extends AbstractModifiableContainer<T> {
    private final Action renderAction;
    private final AreaRenderer areaRenderer;

    private boolean caching;
    private boolean cached;

    public AreaLayer(AreaRenderer areaRenderer) {
        this.areaRenderer = areaRenderer;
        this.renderAction = () -> renderAction(areaRenderer.getArea());
        setAutoClearing(true);
    }

    public void flushCache() {
        cached = false;
    }

    public boolean isCaching() {
        return caching;
    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    @Override
    public void render(float alpha) {
        if (!cached) {
            areaRenderer.render(renderAction);
            cached = caching;
        }
        areaRenderer.renderFrameBuffer(alpha);
    }

    private void renderAction(Rectangle renderArea) {
        ModelMatrix.instance().pushMatrix();
        ModelMatrix.instance().translatef(renderArea.getXMin(), renderArea.getYMin(), 0);
        super.render(1f);
        ModelMatrix.instance().popMatrix();
    }

    @Override
    public boolean isPixelVisible(float x, float y) {
        return areaRenderer.getArea().isInside(x, y) && super.isPixelVisible(x, y);
    }
}