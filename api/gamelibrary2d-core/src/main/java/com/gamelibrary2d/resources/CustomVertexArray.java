package com.gamelibrary2d.resources;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.glUtil.TransferBuffer;

public class CustomVertexArray extends AbstractVertexArray {

    private CustomVertexArray(TransferBuffer buffer, int elementSize) {
        super(buffer, elementSize);
    }

    public static CustomVertexArray create(TransferBuffer buffer, int elementSize, Disposer disposer) {
        var obj = new CustomVertexArray(buffer, elementSize);
        disposer.register(obj);
        return obj;
    }
}