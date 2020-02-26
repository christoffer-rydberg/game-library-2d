package com.gamelibrary2d.glUtil;

public abstract class AbstractInterleavedBuffer<T extends OpenGLBuffer> implements OpenGLBuffer {
    private final int stride;
    private final T buffer;

    protected AbstractInterleavedBuffer(T buffer, int stride) {
        this.buffer = buffer;
        this.stride = stride;
    }

    protected T buffer() {
        return buffer;
    }

    @Override
    public void bind() {
        buffer.bind();
    }

    @Override
    public void unbind() {
        buffer.unbind();
    }

    @Override
    public int bufferId() {
        return buffer.bufferId();
    }

    @Override
    public void updateGPU(int offset, int len) {
        buffer.updateGPU(offset * stride, len * stride);
    }

    @Override
    public void updateCPU(int offset, int len) {
        buffer.updateCPU(offset * stride, len * stride);
    }

    public int stride() {
        return stride;
    }

    @Override
    public int capacity() {
        return buffer.capacity() / stride;
    }

    @Override
    public void copy(int offset, int destination, int len) {
        buffer.copy(offset * stride, destination * stride, len * stride);
    }
}