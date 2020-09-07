package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Texture;

abstract class AbstractSurfaceRenderer extends AbstractShaderRenderer implements TexturedRenderer {

    private Surface surface;
    private Texture texture;

    protected AbstractSurfaceRenderer(RenderingParameters parameters) {
        super(parameters);
    }

    protected AbstractSurfaceRenderer() {
        this(null, null);
    }

    protected AbstractSurfaceRenderer(Surface surface) {
        this(surface, null);
    }

    protected AbstractSurfaceRenderer(Surface surface, Texture texture) {
        setSurface(surface);
        setTexture(texture);
    }

    public Surface getSurface() {
        return surface;
    }

    void setSurface(Surface surface) {
        this.surface = surface;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Rectangle getBounds() {
        return surface == null ? Rectangle.EMPTY : surface.getBounds();
    }

    @Override
    protected void applyParameters(float alpha) {
        getParameters().set(RenderingParameters.IS_TEXTURED, texture != null ? 1 : 0);
        super.applyParameters(alpha);
    }

    @Override
    public void onRender(ShaderProgram shaderProgram) {
        if (surface != null) {
            if (texture != null) {
                texture.bind();
            }
            surface.render(shaderProgram);
        }
    }
}