package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.FloatTransferBuffer;
import com.gamelibrary2d.glUtil.TransferBuffer;

public class PositionArray extends AbstractVertexArray {
    private final static int STRIDE = 2;
    private final static int ELEMENT_SIZE = 2;

    private PositionArray(TransferBuffer buffer) {
        super(buffer, ELEMENT_SIZE);
    }

    public static PositionArray create(float[] positions, Disposer disposer) {
        var transferBuffer = new FloatTransferBuffer(positions, STRIDE, OpenGL.GL_ARRAY_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);
        var buffer = new PositionArray(transferBuffer);
        buffer.updateGPU(0, buffer.getCapacity());
        disposer.registerDisposal(buffer);
        return buffer;
    }
}
