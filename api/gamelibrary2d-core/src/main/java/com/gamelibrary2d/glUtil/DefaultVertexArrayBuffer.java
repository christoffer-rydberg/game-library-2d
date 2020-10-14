package com.gamelibrary2d.glUtil;

public class DefaultVertexArrayBuffer<T extends OpenGLBuffer> extends AbstractVertexArrayBuffer<T> {

    public DefaultVertexArrayBuffer(T buffer, int stride, int elementSize) {
        super(buffer, stride, elementSize);
    }

    @Override
    public T getBuffer() {
        return super.getBuffer();
    }
}
