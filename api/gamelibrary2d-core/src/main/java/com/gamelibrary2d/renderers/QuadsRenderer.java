package com.gamelibrary2d.renderers;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.PositionBuffer;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.util.QuadShape;

import java.nio.FloatBuffer;

public class QuadsRenderer extends AbstractArrayRenderer<PositionBuffer> {
    private final static String boundsUniformName = "bounds";
    private final static String texturedUniformName = "textured";
    private final static String shapeUniformName = "shape";
    private final FloatBuffer boundsBuffer = BufferUtils.createFloatBuffer(4);

    private Texture texture;
    private Rectangle bounds;
    private QuadShape shape = QuadShape.RECTANGLE;

    public QuadsRenderer(Rectangle bounds) {
        super(DrawMode.POINTS);
        setBounds(bounds);
    }

    public QuadsRenderer(Rectangle bounds, Texture texture) {
        this(bounds);
        this.texture = texture;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
        boundsBuffer.clear();
        boundsBuffer.put(bounds.getLowerX());
        boundsBuffer.put(bounds.getLowerY());
        boundsBuffer.put(bounds.getUpperX());
        boundsBuffer.put(bounds.getUpperY());
        boundsBuffer.flip();
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public QuadShape getShape() {
        return shape;
    }

    public void setShape(QuadShape shape) {
        this.shape = shape;
    }

    @Override
    protected void renderPrepare(ShaderProgram shaderProgram) {
        if (texture != null) {
            texture.bind();
        }

        var glBoundsUniform = shaderProgram.getUniformLocation(boundsUniformName);
        OpenGL.instance().glUniform4fv(glBoundsUniform, boundsBuffer);

        var glTexturedUniform = shaderProgram.getUniformLocation(texturedUniformName);
        OpenGL.instance().glUniform1i(glTexturedUniform, texture != null ? 1 : 0);

        var glShapeUniform = shaderProgram.getUniformLocation(shapeUniformName);
        switch (shape) {
            case RECTANGLE:
                OpenGL.instance().glUniform1i(glShapeUniform, 0);
                break;
            case RADIAL_GRADIENT:
                OpenGL.instance().glUniform1i(glShapeUniform, 1);
                break;
        }
    }

    @Override
    protected void renderCleanup() {
        if (texture != null) {
            texture.unbind();
        }
    }

    @Override
    protected ShaderProgram getShaderProgram() {
        return ShaderProgram.getQuadShaderProgram();
    }
}
