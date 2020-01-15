package com.gamelibrary2d.glUtil;

public interface TransferBuffer {

    int getGlBuffer();

    void updateGPU(int offset, int len);

    void updateCPU(int offset, int len);

    int getStride();

    int getCapacity();

    void bind();

    void unbind();

    void copy(int index, int destinationIndex);
}