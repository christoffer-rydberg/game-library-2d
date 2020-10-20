package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.FloatArrayList;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;

public class PositionBuffer extends AbstractMirroredVertexArrayBuffer<MirroredFloatBuffer> {
    private final static int STRIDE = 2;
    private final static int ELEMENT_SIZE = 2;
    private final FloatArrayList data;

    private PositionBuffer(FloatArrayList data, MirroredFloatBuffer buffer) {
        super(buffer, STRIDE, ELEMENT_SIZE);
        this.data = data;
    }

    public static PositionBuffer create(Disposer disposer) {
        return create(new FloatArrayList(), disposer);
    }

    public static PositionBuffer create(float[] data, Disposer disposer) {
        return create(new FloatArrayList(data), disposer);
    }

    public static PositionBuffer create(FloatArrayList data, Disposer disposer) {
        var buffer = MirroredFloatBuffer.create(
                data.getInternalArray(), OpenGL.GL_ARRAY_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);
        var positionBuffer = new PositionBuffer(data, buffer);
        disposer.registerDisposal(positionBuffer);
        return positionBuffer;
    }

    public int size() {
        return data.size() / STRIDE;
    }

    public FloatArrayList getData() {
        return data;
    }

    public void add(float x, float y) {
        data.add(x);
        data.add(y);
        updateGPU(size() - 1, 1);
    }

    public void add(int index, float x, float y) {
        var bufferIndex = index * STRIDE;
        data.add(bufferIndex, x);
        data.add(bufferIndex + 1, y);
        updateGPU(index, size() - index);
    }

    public void set(int index, float x, float y) {
        var bufferIndex = index * STRIDE;
        data.set(bufferIndex, x);
        data.set(bufferIndex + 1, y);
        updateGPU(index, 1);
    }

    public void remove(int index) {
        var bufferIndex = index * STRIDE;
        data.remove(bufferIndex);
        data.remove(bufferIndex);
        updateGPU(index, size() - index);
    }

    @Override
    public void updateGPU(int offset, int len) {
        int requiredSize = offset + len;

        var size = size();
        if (requiredSize > size) {
            throw new IndexOutOfBoundsException("Index: " + (requiredSize - 1) + ", Size: " + size);
        }

        var buffer = getBuffer();
        var bufferData = buffer.getData();
        if (requiredSize <= bufferData.length / STRIDE) {
            super.updateGPU(offset, len);
        } else {
            // Assuming update after array growth. Reallocate entire OpenGL buffer:
            getBuffer().allocate(data.getInternalArray());
        }
    }

    @Override
    public void updateCPU(int offset, int len) {
        super.updateCPU(offset, len);
        var bufferData = getBuffer().getData();
        var internalData = data.getInternalArray();
        if (bufferData != internalData) {
            int bufferOffset = offset * STRIDE;
            var bufferLength = len * STRIDE;
            var requiredSize = bufferOffset + bufferLength;
            if (internalData.length >= requiredSize) {
                System.arraycopy(bufferData, bufferOffset, internalData, bufferOffset, bufferLength);
            } else {
                throw new IndexOutOfBoundsException("Index: " + (requiredSize - 1) + ", Size: " + internalData.length);
            }
        }
    }

    public float getX(int index) {
        return data.get(index * STRIDE);
    }

    public float getY(int index) {
        return data.get(index * STRIDE + 1);
    }
}