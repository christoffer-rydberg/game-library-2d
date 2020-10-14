package com.gamelibrary2d.particle.renderers;

import com.gamelibrary2d.glUtil.OpenGLBuffer;

public interface ParticleRenderer {

    void render(OpenGLBuffer renderBuffer, boolean gpuOutdated, int offset, int len, float alpha);

}
