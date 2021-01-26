package com.gamelibrary2d.renderers;

import com.gamelibrary2d.glUtil.OpenGLBuffer;

public interface ArrayRenderer<T extends OpenGLBuffer> {
    void render(float alpha, T array, int offset, int len);

    ShaderParameters getParameters();
}
