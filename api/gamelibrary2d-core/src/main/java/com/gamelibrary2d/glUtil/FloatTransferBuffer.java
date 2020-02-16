package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;

import java.nio.FloatBuffer;

public class FloatTransferBuffer extends AbstractTransferBuffer {
    private final int usage;
    private final FloatBuffer ioBuffer;
    private float[] src;

    public FloatTransferBuffer(float[] src, int stride, int target, int usage, Disposer disposer) {
        super(src.length, stride, target);
        this.src = src;
        this.usage = usage;
        ioBuffer = BufferUtils.createFloatBuffer(src.length);
        disposer.registerDisposal(this);
    }

    public final float[] getSource() {
        return src;
    }

    public final void setSource(float[] src) {
        this.src = src;
    }

    @Override
    public void copy(int index, int destinationIndex) {
        int stride = getStride();
        int origin = index * stride;
        int destination = destinationIndex * stride;
        System.arraycopy(src, origin, src, destination, stride);
    }

    @Override
    protected void onInitializeGPU(int target) {
        OpenGL.instance().glBufferData(target, src, usage);
    }

    @Override
    protected void onUpdateGPU(int target, int offset, int len) {
        ioBuffer.clear();
        ioBuffer.put(src, offset, len);
        ioBuffer.flip();
        OpenGL.instance().glBufferSubData(target, offset * Float.BYTES, ioBuffer);
    }

    @Override
    protected void onUpdateCPU(int target, int offset, int len) {
        ioBuffer.clear();
        ioBuffer.limit(len);
        OpenGL.instance().glGetBufferSubData(target, offset * Float.BYTES, ioBuffer);
        for (int i = offset; i < len; ++i) {
            src[i] = ioBuffer.get(i);
        }
    }
}