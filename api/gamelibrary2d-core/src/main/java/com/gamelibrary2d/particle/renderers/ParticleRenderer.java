package com.gamelibrary2d.particle.renderers;

import com.gamelibrary2d.glUtil.OpenGLBuffer;
import com.gamelibrary2d.particle.systems.Particle;

public interface ParticleRenderer {

    void render(Particle[] particles, OpenGLBuffer vertexBuffer, boolean gpuOutdated, int offset, int len,
                float alpha);

}
