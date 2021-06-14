package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;

import java.nio.FloatBuffer;

public class Quad extends InternalAbstractQuad implements Surface {
    private static final Rectangle DEFAULT_TEXTURE_BOUNDS = new Rectangle(0, 0, 1, 1);

    private final Rectangle bounds;
    private final Rectangle textureBounds;
    private final QuadShape shape;

    private Quad(Rectangle bounds, Rectangle textureBounds, QuadShape shape) {
        this.bounds = bounds;
        this.textureBounds = textureBounds;
        this.shape = shape;
    }

    public static Quad create(Rectangle bounds, Disposer disposer) {
        return create(bounds, QuadShape.RECTANGLE, disposer);
    }

    public static Quad create(Rectangle bounds, Rectangle textureBounds, Disposer disposer) {
        return create(bounds, QuadShape.RECTANGLE, textureBounds, disposer);
    }

    public static Quad create(Rectangle bounds, QuadShape shape, Disposer disposer) {
        Quad quad = new Quad(bounds, DEFAULT_TEXTURE_BOUNDS, shape);
        quad.setup();
        disposer.registerDisposal(quad);
        return quad;
    }

    public static Quad create(Rectangle bounds, QuadShape shape, Rectangle textureBounds, Disposer disposer) {
        Quad quad = new Quad(bounds, textureBounds, shape);
        quad.setup();
        disposer.registerDisposal(quad);
        return quad;
    }

    private FloatBuffer generateVertices() {
        Rectangle bounds = getBounds();

        FloatBuffer vertices = BufferUtils.createFloatBuffer(bufferLength);

        vertices.put(bounds.getLowerX()).put(bounds.getLowerY());
        vertices.put(0).put(0);
        vertices.put(textureBounds.getLowerX()).put(textureBounds.getLowerY());

        vertices.put(bounds.getUpperX()).put(bounds.getLowerY());
        vertices.put(1).put(0);
        vertices.put(textureBounds.getUpperX()).put(textureBounds.getLowerY());

        vertices.put(bounds.getUpperX()).put(bounds.getUpperY());
        vertices.put(1).put(1);
        vertices.put(textureBounds.getUpperX()).put(textureBounds.getUpperY());

        vertices.put(bounds.getLowerX()).put(bounds.getUpperY());
        vertices.put(0).put(1);
        vertices.put(textureBounds.getLowerX()).put(textureBounds.getUpperY());

        vertices.flip();

        return vertices;
    }

    @Override
    protected void onSetup() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBufferData(OpenGL.GL_ARRAY_BUFFER, generateVertices(), OpenGL.GL_STATIC_DRAW);
    }

    public QuadShape getShape() {
        return shape;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getTextureBounds() {
        return textureBounds;
    }

    @Override
    public void render(ShaderProgram shaderProgram) {
        OpenGL openGL = OpenGL.instance();
        bind();
        setShapeBeforeRender(shape, shaderProgram);
        setAttributePointersBeforeRender(shaderProgram);
        openGL.glDrawElements(OpenGL.GL_TRIANGLES, 6, OpenGL.GL_UNSIGNED_BYTE, 0);
    }
}