package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.framework.OpenGL;

public abstract class AbstractOpenGLBuffer extends AbstractDisposable implements OpenGLBuffer {
    private final int target;

    private int capacity;
    private boolean bound;
    private int glBuffer = -1;

    protected AbstractOpenGLBuffer(int target) {
        this.target = target;
    }

    protected void allocate(int capacity) {
        this.capacity = capacity;

        boolean isBound = bound;

        if (!isBound)
            bind();

        onAllocate(target);

        if (!isBound)
            unbind();
    }

    public int capacity() {
        return capacity;
    }

    public void bind() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBindBuffer(target, bufferId());
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

        onUpdateCPU(target, offset, len);

        if (!isBound)
            unbind();
    }

    public void updateGPU(int offset, int len) {
        boolean isBound = bound;

        if (!isBound)
            bind();

        onUpdateGPU(target, offset, len);

        if (!isBound)
            unbind();
    }

    public int bufferId() {
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

    protected abstract void onAllocate(int target);

    protected abstract void onUpdateGPU(int target, int offset, int len);

    protected abstract void onUpdateCPU(int target, int offset, int len);
}