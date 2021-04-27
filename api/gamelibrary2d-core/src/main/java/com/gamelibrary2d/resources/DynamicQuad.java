package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.util.QuadShape;

import java.nio.FloatBuffer;

public class DynamicQuad extends InternalAbstractQuad implements Surface {
    private QuadShape shape;
    private Rectangle bounds;
    private FloatBuffer verticeBuffer;
    private boolean verticesChanged;

    private DynamicQuad(QuadShape shape) {
        this.shape = shape;
    }

    public static DynamicQuad create(Disposer disposer) {
        return create(QuadShape.RECTANGLE, disposer);
    }

    public static DynamicQuad create(QuadShape shape, Disposer disposer) {
        DynamicQuad quad = new DynamicQuad(shape);
        quad.setup();
        disposer.registerDisposal(quad);
        return quad;
    }

    public void setBounds(float lowerX, float lowerY, float upperX, float upperY) {
        bounds = null;

        verticeBuffer.put(0, lowerX);
        verticeBuffer.put(1, lowerY);

        verticeBuffer.put(6, upperX);
        verticeBuffer.put(7, lowerY);

        verticeBuffer.put(12, upperX);
        verticeBuffer.put(13, upperY);

        verticeBuffer.put(18, lowerX);
        verticeBuffer.put(19, upperY);

        verticesChanged = true;
    }

    private void setCoordinates(float lowerX, float lowerY, float upperX, float upperY) {
        verticeBuffer.put(2, lowerX);
        verticeBuffer.put(3, lowerY);

        verticeBuffer.put(8, upperX);
        verticeBuffer.put(9, lowerY);

        verticeBuffer.put(14, upperX);
        verticeBuffer.put(16, upperY);

        verticeBuffer.put(20, lowerX);
        verticeBuffer.put(21, upperY);

        verticesChanged = true;
    }

    public void setTextureBounds(float lowerX, float lowerY, float upperX, float upperY) {
        verticeBuffer.put(4, lowerX);
        verticeBuffer.put(5, lowerY);

        verticeBuffer.put(10, upperX);
        verticeBuffer.put(11, lowerY);

        verticeBuffer.put(16, upperX);
        verticeBuffer.put(17, upperY);

        verticeBuffer.put(22, lowerX);
        verticeBuffer.put(23, upperY);

        verticesChanged = true;
    }

    public QuadShape getShape() {
        return shape;
    }

    public void setShape(QuadShape shape) {
        this.shape = shape;
    }

    @Override
    protected void onSetup() {
        verticeBuffer = BufferUtils.createFloatBuffer(bufferLength);
        setCoordinates(0, 0, 1, 1);
        setTextureBounds(0, 0, 1, 1);
    }

    @Override
    public Rectangle getBounds() {
        if (bounds == null) {
            bounds = new Rectangle(
                    verticeBuffer.get(0),
                    verticeBuffer.get(1),
                    verticeBuffer.get(12),
                    verticeBuffer.get(13)
            );
        }

        return bounds;
    }

    @Override
    public void render(ShaderProgram shaderProgram) {
        OpenGL openGL = OpenGL.instance();
        bind();
        setShapeBeforeRender(shape, shaderProgram);
        setAttributePointersBeforeRender(shaderProgram);

        if (verticesChanged) {
            openGL.glBufferData(OpenGL.GL_ARRAY_BUFFER, verticeBuffer, OpenGL.GL_DYNAMIC_DRAW);
            verticesChanged = false;
        }

        openGL.glDrawElements(OpenGL.GL_TRIANGLES, 6, OpenGL.GL_UNSIGNED_BYTE, 0);
    }
}
