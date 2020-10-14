package com.gamelibrary2d.glUtil;

public interface OpenGLBuffer {

    void bind();

    void unbind();

    int getBufferId();

    int getCapacity();
}