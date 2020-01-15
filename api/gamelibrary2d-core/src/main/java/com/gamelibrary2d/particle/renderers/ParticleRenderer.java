package com.gamelibrary2d.particle.renderers;

import com.gamelibrary2d.resources.VertexArray;
import com.gamelibrary2d.particle.systems.Particle;

public interface ParticleRenderer {

    void render(Particle[] particles, VertexArray vertexBuffer, boolean gpuOutdated, int offset, int len,
                float alpha);

}
