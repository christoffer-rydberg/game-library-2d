package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.framework.OpenGL;

import java.nio.IntBuffer;

public class OpenGLIntBuffer extends AbstractOpenGLBuffer {
    private final int usage;

    private int[] data;
    private IntBuffer ioBuffer;

    private OpenGLIntBuffer(int target, int usage) {
        super(target);
        this.usage = usage;
    }

    public static OpenGLIntBuffer create(int[] data, int target, int usage, Disposer disposer) {
        var buffer = new OpenGLIntBuffer(target, usage);
        buffer.allocate(data);
        disposer.registerDisposal(buffer);
        return buffer;
    }

    public int[] data() {
        return data;
    }

    public boolean allocate(int[] data) {
        if (this.data != data) {
            this.data = data;
            allocate(data.length);
            return true;
        }

        return false;
    }

    @Override
    public void copy(int offset, int destination, int len) {
        System.arraycopy(data, offset, data, destination, len);
    }

    @Override
    protected void onAllocate(int target) {
        if (ioBuffer == null || ioBuffer.capacity() != data.length) {
            ioBuffer = BufferUtils.createIntBuffer(data.length);
            OpenGL.instance().glBufferData(target, data, usage);
        } else {
            OpenGL.instance().glBufferSubData(target, 0, data);
        }
    }

    @Override
    protected void onUpdateGPU(int target, int offset, int len) {
        ioBuffer.clear();
        ioBuffer.put(data, offset, len);
        ioBuffer.flip();
        OpenGL.instance().glBufferSubData(target, offset * Float.BYTES, ioBuffer);
    }

    @Override
    protected void onUpdateCPU(int target, int offset, int len) {
        ioBuffer.clear();
        ioBuffer.limit(len);
        OpenGL.instance().glGetBufferSubData(target, offset * Float.BYTES, ioBuffer);
        for (int i = offset; i < len; ++i) {
            data[i] = ioBuffer.get(i);
        }
    }
}