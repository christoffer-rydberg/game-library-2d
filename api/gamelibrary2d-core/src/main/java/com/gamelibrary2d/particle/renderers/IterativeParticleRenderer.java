package com.gamelibrary2d.particle.renderers;

import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLBuffer;
import com.gamelibrary2d.particle.systems.Particle;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.util.RenderSettings;

public class IterativeParticleRenderer implements ParticleRenderer {

    @Override
    public void render(Particle[] particles, OpenGLBuffer vertexBuffer, boolean gpuOutdated, int offset, int len,
                       float alpha) {
        int end = offset + len;
        for (int i = offset; i < end; ++i) {
            render(particles[i], alpha);
        }
    }

    private void render(Particle particle, float alpha) {

        Renderer renderer = particle.getRenderer();
        if (renderer == null)
            return;

        ModelMatrix modelMatrix = ModelMatrix.instance();

        modelMatrix.pushMatrix();

        modelMatrix.translatef(particle.getPosX(), particle.getPosY(), 0);

        modelMatrix.rotatef(-particle.getRotation(), 0, 0, 1);

        modelMatrix.scalef(particle.getScaleX(), particle.getScaleY(), 1.0f);

        renderer.updateSettings(RenderSettings.TIME, particle.getTime());
        renderer.updateSettings(RenderSettings.COLOR_R, particle.getColorR(), particle.getColorG(),
                particle.getColorB(), particle.getColorA());
        renderer.render(alpha);

        modelMatrix.popMatrix();
    }
}