package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;

import java.nio.IntBuffer;

public class IntTransferBuffer extends AbstractTransferBuffer {

    private final int usage;

    private final int[] src;

    private final IntBuffer ioBuffer;

    public IntTransferBuffer(int[] src, int stride, int target, int usage, Disposer disposer) {

        super(src.length, stride, target);

        this.src = src;

        this.usage = usage;

        ioBuffer = BufferUtils.createIntBuffer(src.length);

        disposer.register(this);
    }

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
        OpenGL.instance().glBufferSubData(target, offset * Integer.BYTES, ioBuffer);
    }

    @Override
    protected void onUpdateCPU(int target, int offset, int len) {
        ioBuffer.clear();
        ioBuffer.limit(len);
        OpenGL.instance().glGetBufferSubData(target, offset * Integer.BYTES, ioBuffer);
        for (int i = offset; i < len; ++i) {
            src[i] = ioBuffer.get(i);
        }
    }
}