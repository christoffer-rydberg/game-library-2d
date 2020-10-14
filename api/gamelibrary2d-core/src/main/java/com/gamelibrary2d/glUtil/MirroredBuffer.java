package com.gamelibrary2d.glUtil;

public interface MirroredBuffer extends OpenGLBuffer {

    void updateGPU(int offset, int len);

    void updateCPU(int offset, int len);

    void copy(int offset, int destination, int len);
}