package com.gamelibrary2d.glUtil;

public interface OpenGLBuffer {

    int bufferId();

    void updateGPU(int offset, int len);

    void updateCPU(int offset, int len);

    int capacity();

    void bind();

    void unbind();

    void copy(int offset, int destination, int len);
}