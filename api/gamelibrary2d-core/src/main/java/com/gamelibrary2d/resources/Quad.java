package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.util.QuadShape;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Quad extends AbstractDisposable implements Surface {

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
     * The byte offset of the texture coordinate attribute
     */
    private static final int textureCoordinateByteOffset = positionByteSize + coordinateByteSize;

    /**
     * The byte distance from the beginning of one entity to the beginning of the
     * next entity
     */
    private final static int stride = positionByteSize + coordinateByteSize + textureCoordinateByteSize;

    /**
     * The number of vertices
     */
    private final static int vertices = 4;

    /**
     * The length of the vertex buffer
     */
    private final static int bufferLength = vertices
            * (positionElements + coordinateElements + textureCoordinateElements);

    private final Rectangle bounds;
    private final Rectangle textureBounds;
    private final QuadShape shape;

    private final OpenGL openGL = OpenGL.instance();

    private int vaoId = 0;
    private int vboId = 0;
    private int vboiId = 0;

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
        Quad quad = new Quad(bounds, new Rectangle(0, 0, 1, 1), shape);
        quad.setup();
        disposer.register(quad);
        return quad;
    }

    public static Quad create(Rectangle bounds, QuadShape shape, Rectangle textureBounds, Disposer disposer) {
        Quad quad = new Quad(bounds, textureBounds, shape);
        quad.setup();
        disposer.register(quad);
        return quad;
    }

    public QuadShape getShape() {
        return shape;
    }

    private void setup() {
        FloatBuffer vertices = generateVertices();

        // Create a new Vertex Array Object in memory and select it (bind)
        vaoId = openGL.glGenVertexArrays();
        openGL.glBindVertexArray(vaoId);

        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = {0, 1, 2, 2, 3, 0};
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(6);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        // Create a new Vertex Buffer Object in memory and select it (bind)
        vboId = openGL.glGenBuffers();
        openGL.glBindBuffer(OpenGL.GL_ARRAY_BUFFER, vboId);
        openGL.glBufferData(OpenGL.GL_ARRAY_BUFFER, vertices, OpenGL.GL_STATIC_DRAW);

        // Create a new VBO for the indices
        vboiId = openGL.glGenBuffers();
        openGL.glBindBuffer(OpenGL.GL_ELEMENT_ARRAY_BUFFER, vboiId);
        openGL.glBufferData(OpenGL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, OpenGL.GL_STATIC_DRAW);

        // Unbind
        openGL.glBindBuffer(OpenGL.GL_ELEMENT_ARRAY_BUFFER, 0);
        openGL.glBindBuffer(OpenGL.GL_ARRAY_BUFFER, 0);
        openGL.glBindVertexArray(0);
    }

    @Override
    public void onDispose() {
        // Select the VAO
        openGL.glBindVertexArray(vaoId);

        // Delete the vertex VBO
        openGL.glBindBuffer(OpenGL.GL_ARRAY_BUFFER, 0);
        openGL.glDeleteBuffers(vboId);

        // Delete the index VBO
        openGL.glBindBuffer(OpenGL.GL_ELEMENT_ARRAY_BUFFER, 0);
        openGL.glDeleteBuffers(vboiId);

        // Delete the VAO
        openGL.glBindVertexArray(0);
        openGL.glDeleteVertexArrays(vaoId);
    }

    private FloatBuffer generateVertices() {
        Rectangle bounds = getBounds();

        FloatBuffer vertices = BufferUtils.createFloatBuffer(bufferLength);

        vertices.put(bounds.getXMin()).put(bounds.getYMin());
        vertices.put(0).put(0);
        vertices.put(textureBounds.getXMin()).put(textureBounds.getYMin());

        vertices.put(bounds.getXMax()).put(bounds.getYMin());
        vertices.put(1).put(0);
        vertices.put(textureBounds.getXMax()).put(textureBounds.getYMin());

        vertices.put(bounds.getXMax()).put(bounds.getYMax());
        vertices.put(1).put(1);
        vertices.put(textureBounds.getXMax()).put(textureBounds.getYMax());

        vertices.put(bounds.getXMin()).put(bounds.getYMax());
        vertices.put(0).put(1);
        vertices.put(textureBounds.getXMin()).put(textureBounds.getYMax());

        vertices.flip();

        return vertices;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getTextureBounds() {
        return textureBounds;
    }

    @Override
    public void render(ShaderProgram shaderProgram) {
        // Bind to the VAO
        openGL.glBindVertexArray(vaoId);

        // Bind the VBO
        openGL.glBindBuffer(OpenGL.GL_ARRAY_BUFFER, vboId);

        // Bind to the index VBO that has all the information about the order of the
        // vertices
        openGL.glBindBuffer(OpenGL.GL_ELEMENT_ARRAY_BUFFER, vboiId);

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

        var glShapeUniform = shaderProgram.getUniformLocation(SHAPE_ATTRIBUTE);
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

        openGL.glDrawElements(OpenGL.GL_TRIANGLES, 6, OpenGL.GL_UNSIGNED_BYTE, 0);
    }
}