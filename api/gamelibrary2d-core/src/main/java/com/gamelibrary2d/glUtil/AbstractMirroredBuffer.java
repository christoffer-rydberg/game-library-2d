package com.gamelibrary2d.glUtil;

public abstract class AbstractMirroredBuffer extends AbstractOpenGLBuffer implements MirroredBuffer {
    private final int target;

    protected AbstractMirroredBuffer(int target) {
        super(target);
        this.target = target;
    }

    public void updateCPU(int offset, int len) {
        boolean isBound = isBound();

        if (!isBound) {
            bind();
        }

        onUpdateCPU(target, offset, len);

        if (!isBound) {
            unbind();
        }
    }

    public void updateGPU(int offset, int len) {
        boolean isBound = isBound();

        if (!isBound) {
            bind();
        }

        onUpdateGPU(target, offset, len);

        if (!isBound) {
            unbind();
        }
    }

    protected abstract void onUpdateGPU(int target, int offset, int len);

    protected abstract void onUpdateCPU(int target, int offset, int len);
}