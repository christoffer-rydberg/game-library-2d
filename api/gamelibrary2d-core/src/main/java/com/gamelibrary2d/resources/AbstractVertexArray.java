package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.TransferBuffer;

public abstract class AbstractVertexArray extends AbstractDisposable implements VertexArray {
    private final int glVao;
    private final TransferBuffer buffer;

    protected AbstractVertexArray(TransferBuffer buffer) {
        this(buffer, 4);
    }

    protected AbstractVertexArray(TransferBuffer buffer, int elementSize) {
        OpenGL openGL = OpenGL.instance();
        this.buffer = buffer;
        this.glVao = openGL.glGenVertexArrays();

        bind();
        int stride = buffer.getStride();
        int elements = stride / elementSize;
        int byteStride = stride * Float.BYTES;
        for (int i = 0; i < elements; ++i) {
            openGL.glEnableVertexAttribArray(i);
            openGL.glVertexAttribPointer(i, elementSize, OpenGL.GL_FLOAT, false, byteStride, i * elementSize * Float.BYTES);
        }
        unbind();
    }

    @Override
    public void bind() {
        OpenGL.instance().glBindVertexArray(glVao);
        buffer.bind();
    }

    @Override
    public void unbind() {
        buffer.unbind();
        OpenGL.instance().glBindVertexArray(0);
    }

    @Override
    protected void onDispose() {
        OpenGL.instance().glDeleteVertexArrays(glVao);
    }

    @Override
    public int getGlBuffer() {
        return buffer.getGlBuffer();
    }

    @Override
    public void updateGPU(int offset, int len) {
        buffer.updateGPU(offset, len);
    }

    @Override
    public void updateCPU(int offset, int len) {
        buffer.updateCPU(offset, len);
    }

    @Override
    public int getStride() {
        return buffer.getStride();
    }

    @Override
    public int getCapacity() {
        return buffer.getCapacity();
    }

    @Override
    public void copy(int index, int destinationIndex) {
        buffer.copy(index, destinationIndex);
    }
}