package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.framework.OpenGL;

public abstract class AbstractVertexArrayBuffer<T extends OpenGLBuffer> extends AbstractInterleavedBuffer<T>
        implements OpenGLBuffer, Disposable {
    private final int glVao;

    protected AbstractVertexArrayBuffer(T buffer, int stride, int elementSize) {
        super(buffer, stride);

        OpenGL openGL = OpenGL.instance();
        this.glVao = openGL.glGenVertexArrays();

        bind();
        int elements = stride / elementSize;
        int byteStride = stride * Float.BYTES;
        for (int i = 0; i < elements; ++i) {
            openGL.glEnableVertexAttribArray(i);
            openGL.glVertexAttribPointer(
                    i,
                    elementSize,
                    OpenGL.GL_FLOAT,
                    false,
                    byteStride,
                    i * elementSize * Float.BYTES);
        }
        unbind();
    }

    @Override
    public void bind() {
        OpenGL.instance().glBindVertexArray(glVao);
        super.bind();
    }

    @Override
    public void unbind() {
        super.unbind();
        OpenGL.instance().glBindVertexArray(0);
    }

    @Override
    public void dispose() {
        OpenGL.instance().glDeleteVertexArrays(glVao);
    }

}