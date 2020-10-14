package com.gamelibrary2d.glUtil;

public abstract class AbstractInterleavedBuffer<T extends OpenGLBuffer> implements OpenGLBuffer {
    private final T buffer;
    private final int stride;

    protected AbstractInterleavedBuffer(T buffer, int stride) {
        this.buffer = buffer;
        this.stride = stride;
    }

    protected T getBuffer() {
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
    public int getBufferId() {
        return buffer.getBufferId();
    }

    public int getStride() {
        return stride;
    }

    @Override
    public int getCapacity() {
        return buffer.getCapacity() / stride;
    }
}