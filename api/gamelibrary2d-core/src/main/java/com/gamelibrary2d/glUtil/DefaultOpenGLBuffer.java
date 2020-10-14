package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;

public class DefaultOpenGLBuffer extends AbstractOpenGLBuffer {
    private final int usage;
    private int size;

    private DefaultOpenGLBuffer(int target, int usage) {
        super(target);
        this.usage = usage;
    }

    public static DefaultOpenGLBuffer create(int target, int usage, Disposer disposer) {
        var buffer = new DefaultOpenGLBuffer(target, usage);
        disposer.registerDisposal(buffer);
        return buffer;
    }

    public void allocate(int size) {
        this.size = size;
        super.allocate();
    }

    @Override
    public int getCapacity() {
        return size;
    }

    @Override
    protected void onAllocate(int target) {
        OpenGL.instance().glBufferData(target, size, usage);
    }
}
