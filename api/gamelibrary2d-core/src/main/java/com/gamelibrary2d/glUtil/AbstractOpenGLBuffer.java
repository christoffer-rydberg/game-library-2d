package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.framework.OpenGL;

public abstract class AbstractOpenGLBuffer extends AbstractDisposable implements OpenGLBuffer {
    private final int target;

    private boolean bound;
    private int glBuffer = -1;

    protected AbstractOpenGLBuffer(int target) {
        this.target = target;
    }

    protected void allocate() {
        boolean isBound = bound;

        if (!isBound) {
            bind();
        }

        onAllocate(target);

        if (!isBound) {
            unbind();
        }
    }

    public boolean isBound() {
        return bound;
    }

    public void bind() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBindBuffer(target, getBufferId());
        bound = true;
    }

    public void unbind() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBindBuffer(target, 0);
        openGL.glBindVertexArray(0);
        bound = false;
    }

    public int getBufferId() {
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

    @Override
    public abstract int getCapacity();

    protected abstract void onAllocate(int target);
}