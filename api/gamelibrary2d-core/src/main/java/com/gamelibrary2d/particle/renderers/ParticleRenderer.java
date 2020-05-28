package com.gamelibrary2d.particle.renderers;

import com.gamelibrary2d.glUtil.OpenGLBuffer;

public interface ParticleRenderer<T extends OpenGLBuffer> {

    void render(T renderBuffer, boolean gpuOutdated, int offset, int len, float alpha);

}
