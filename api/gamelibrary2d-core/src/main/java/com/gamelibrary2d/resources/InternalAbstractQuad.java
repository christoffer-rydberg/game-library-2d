package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;

import java.nio.ByteBuffer;

abstract class InternalAbstractQuad extends AbstractDisposable {
    private final static String POSITION_ATTRIBUTE = "position";
    private final static String VERTEX_COORDINATE_ATTRIBUTE = "coord";
    private final static String TEXTURE_COORDINATE_ATTRIBUTE = "texcoord";
    private final static String SHAPE_ATTRIBUTE = "shape";

    /**
     * The number of bytes per float element
     */
    private static final int elementByteSize = Float.BYTES;

    /**
     * The number of elements in the position attribute
     */
    private static final int positionElements = 2;

    /**
     * The number of bytes per position attribute
     */
    private static final int positionByteSize = positionElements * elementByteSize;

    /**
     * The number of elements in the coordinate attribute
     */
    private static final int coordinateElements = 2;

    /**
     * The number of bytes per coordinate attribute
     */
    private static final int coordinateByteSize = coordinateElements * elementByteSize;

    /**
     * The byte offset of the coordinate attribute
     */
    private static final int coordinateByteOffset = positionByteSize;

    /**
     * The number of elements in the texture coordinate attribute
     */
    private static final int textureCoordinateElements = 2;

    /**
     * The number of bytes per texture coordinate attribute
     */
    private static final int textureCoordinateByteSize = textureCoordinateElements * elementByteSize;
    /**
     * The byte distance from the beginning of one entity to the beginning of the
     * next entity
     */
    protected final static int stride = positionByteSize + coordinateByteSize + textureCoordinateByteSize;
    /**
     * The byte offset of the texture coordinate attribute
     */
    private static final int textureCoordinateByteOffset = positionByteSize + coordinateByteSize;
    /**
     * The number of vertices
     */
    private final static int vertices = 4;

    /**
     * The length of the vertex buffer
     */
    final static int bufferLength = vertices
            * (positionElements + coordinateElements + textureCoordinateElements);

    private final OpenGL openGL = OpenGL.instance();

    private int vboId = 0;
    private int vboiId = 0;

    protected void bind() {
        openGL.glBindBuffer(OpenGL.GL_ARRAY_BUFFER, vboId);
        openGL.glBindBuffer(OpenGL.GL_ELEMENT_ARRAY_BUFFER, vboiId);
    }

    protected void unbind() {
        openGL.glBindBuffer(OpenGL.GL_ELEMENT_ARRAY_BUFFER, 0);
        openGL.glBindBuffer(OpenGL.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void onDispose() {
        openGL.glDeleteBuffers(vboId);
        openGL.glDeleteBuffers(vboiId);
    }

    void setup() {
        byte[] indices = {0, 1, 2, 2, 3, 0};
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(6);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        vboId = openGL.glGenBuffers();
        openGL.glBindBuffer(OpenGL.GL_ARRAY_BUFFER, vboId);

        vboiId = openGL.glGenBuffers();
        openGL.glBindBuffer(OpenGL.GL_ELEMENT_ARRAY_BUFFER, vboiId);
        openGL.glBufferData(OpenGL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, OpenGL.GL_STATIC_DRAW);

        onSetup();

        unbind();
    }

    void setAttributePointersBeforeRender(ShaderProgram shaderProgram) {
        int posAttrib = shaderProgram.getAttributeLocation(POSITION_ATTRIBUTE);
        openGL.glEnableVertexAttribArray(posAttrib);
        openGL.glVertexAttribPointer(posAttrib, positionElements, OpenGL.GL_FLOAT, false, stride, 0);

        int coordAttrib = shaderProgram.getAttributeLocation(VERTEX_COORDINATE_ATTRIBUTE);
        if (coordAttrib >= 0) {
            openGL.glEnableVertexAttribArray(coordAttrib);
            openGL.glVertexAttribPointer(coordAttrib, coordinateElements, OpenGL.GL_FLOAT, false, stride,
                    coordinateByteOffset);
        }

        int texAttrib = shaderProgram.getAttributeLocation(TEXTURE_COORDINATE_ATTRIBUTE);
        if (texAttrib >= 0) {
            openGL.glEnableVertexAttribArray(texAttrib);
            openGL.glVertexAttribPointer(texAttrib, textureCoordinateElements, OpenGL.GL_FLOAT, false, stride,
                    textureCoordinateByteOffset);
        }
    }

    void setShapeBeforeRender(QuadShape shape, ShaderProgram shaderProgram) {
        int glShapeUniform = shaderProgram.getUniformLocation(SHAPE_ATTRIBUTE);
        if (glShapeUniform >= 0) {
            switch (shape) {
                case RECTANGLE:
                    OpenGL.instance().glUniform1i(glShapeUniform, 0);
                    break;
                case RADIAL_GRADIENT:
                    OpenGL.instance().glUniform1i(glShapeUniform, 1);
                    break;
            }
        }
    }

    protected abstract void onSetup();
}
