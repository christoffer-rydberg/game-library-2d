package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.framework.OpenGL;

public abstract class AbstractTransferBuffer extends AbstractDisposable implements TransferBuffer {

    private final int stride;

    private final int target;

    private final int capacity;

    private boolean bound;

    private boolean gpuInitialized;

    private int glBuffer = -1;

    protected AbstractTransferBuffer(int bufferSize, int stride, int target) {
        this.stride = stride;
        this.target = target;
        capacity = bufferSize / stride;
    }

    private void initializeGPU() {
        onInitializeGPU(target);
        gpuInitialized = true;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStride() {
        return stride;
    }

    public void bind() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBindBuffer(target, getGlBuffer());
        bound = true;
    }

    public void unbind() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBindBuffer(target, 0);
        openGL.glBindVertexArray(0);
        bound = false;
    }

    public void updateCPU(int offset, int len) {

        boolean isBound = bound;

        if (!isBound)
            bind();

        onUpdateCPU(target, offset * stride, len * stride);

        if (!isBound)
            unbind();
    }

    public void updateGPU(int offset, int len) {

        boolean isBound = bound;

        if (!isBound)
            bind();

        if (!gpuInitialized) {
            initializeGPU();
        } else {
            onUpdateGPU(target, offset * stride, len * stride);
        }

        if (!isBound)
            unbind();
    }

    public int getGlBuffer() {

        if (glBuffer == -1) {
            OpenGL openGL = OpenGL.instance();
            glBuffer = openGL.glGenBuffers();
        }

        return glBuffer;
    }

    @Override
    protected void onDispose() {
        OpenGL openGL = OpenGL.instance();
        openGL.glDeleteBuffers(glBuffer);
    }

    protected abstract void onInitializeGPU(int target);

    protected abstract void onUpdateGPU(int target, int offset, int len);

    protected abstract void onUpdateCPU(int target, int offset, int len);
}