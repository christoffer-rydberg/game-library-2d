package com.gamelibrary2d.glUtil;

public abstract class AbstractMirroredVertexArrayBuffer<T extends MirroredBuffer>
        extends AbstractVertexArrayBuffer<T>
        implements MirroredBuffer {

    protected AbstractMirroredVertexArrayBuffer(T buffer, int stride, int elementSize) {
        super(buffer, stride, elementSize);
    }

    @Override
    public void updateGPU(int offset, int len) {
        var stride = getStride();
        getBuffer().updateGPU(offset * stride, len * stride);
    }

    @Override
    public void updateCPU(int offset, int len) {
        var stride = getStride();
        getBuffer().updateCPU(offset * stride, len * stride);
    }

    @Override
    public void copy(int offset, int destination, int len) {
        var stride = getStride();
        getBuffer().copy(offset * stride, destination * stride, len * stride);
    }
}