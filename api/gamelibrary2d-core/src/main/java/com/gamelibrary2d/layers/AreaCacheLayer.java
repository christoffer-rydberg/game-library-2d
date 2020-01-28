package com.gamelibrary2d.layers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.renderers.AreaRenderer;

public class AreaCacheLayer<T extends Renderable> extends BasicLayer<T> {
    private final Action renderAction;
    private final AreaRenderer areaRenderer;

    private boolean caching;
    private boolean cached;

    public AreaCacheLayer(AreaRenderer areaRenderer) {
        this.areaRenderer = areaRenderer;
        this.renderAction = () -> renderAction(areaRenderer.getArea());
    }

    public void flushCache() {
        cached = false;
    }

    public boolean isCaching() {
        return caching;
    }

    public void setCaching(boolean caching) {
        if (this.caching != caching) {
            this.caching = caching;
            this.cached = false;
        }
    }

    @Override
    protected void onRender(float alpha) {
        if (!cached) {
            areaRenderer.render(renderAction);
            cached = caching;
        }
        areaRenderer.renderFrameBuffer(alpha);
    }

    private void renderAction(Rectangle renderArea) {
        ModelMatrix.instance().pushMatrix();
        ModelMatrix.instance().translatef(renderArea.getXMin(), renderArea.getYMin(), 0);
        super.onRender(1f);
        ModelMatrix.instance().popMatrix();
    }
}