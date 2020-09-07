package com.gamelibrary2d.renderers;

import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Texture;

public class SurfaceRenderer extends AbstractSurfaceRenderer {

    public SurfaceRenderer() {
    }

    public SurfaceRenderer(RenderingParameters parameters) {
        super(parameters);
    }

    public SurfaceRenderer(Surface surface) {
        super(surface);
    }

    public SurfaceRenderer(Surface surface, Texture texture) {
        super(surface, texture);
    }

    @Override
    public Texture getTexture() {
        return super.getTexture();
    }

    @Override
    public void setTexture(Texture texture) {
        super.setTexture(texture);
    }

    @Override
    public Surface getSurface() {
        return super.getSurface();
    }

    @Override
    public void setSurface(Surface surface) {
        super.setSurface(surface);
    }
}