package com.gamelibrary2d.particle.renderers;

import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLBuffer;
import com.gamelibrary2d.particle.systems.ParticleRenderBuffer;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.util.RenderSettings;

public class IterativeParticleRenderer implements ParticleRenderer {

    private Renderer renderer;

    public IterativeParticleRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(OpenGLBuffer buffer, boolean gpuOutdated, int offset, int len, float alpha) {
        var renderBuffer = (ParticleRenderBuffer) buffer;
        if (renderer != null) {
            ModelMatrix modelMatrix = ModelMatrix.instance();

            int end = offset + len;
            for (int i = offset; i < end; ++i) {
                var renderOffset = i * renderBuffer.stride();

                modelMatrix.pushMatrix();

                modelMatrix.translatef(
                        renderBuffer.getPosX(renderOffset),
                        renderBuffer.getPosY(renderOffset),
                        0);

                modelMatrix.rotatef(-renderBuffer.getRotation(renderOffset), 0, 0, 1);

                modelMatrix.scalef(
                        renderBuffer.getScale(renderOffset),
                        renderBuffer.getScale(renderOffset),
                        1.0f);

                renderer.updateSettings(
                        RenderSettings.COLOR_R,
                        renderBuffer.getColorR(renderOffset),
                        renderBuffer.getColorG(renderOffset),
                        renderBuffer.getColorB(renderOffset),
                        renderBuffer.getColorA(renderOffset));

                renderer.render(alpha);

                modelMatrix.popMatrix();
            }
        }
    }
}