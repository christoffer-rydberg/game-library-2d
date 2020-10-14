package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.*;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;

public class CustomParticleSystem extends AbstractGpuBasedParticleSystem {
    private final MirroredFloatBuffer updateBuffer;
    private final OpenGLBuffer renderBuffer;
    private final EfficientParticleRenderer renderer;

    private CustomParticleSystem(
            ShaderProgram updaterProgram,
            MirroredFloatBuffer updateBuffer,
            OpenGLBuffer renderBuffer,
            EfficientParticleRenderer renderer) {

        super(updaterProgram);
        this.updateBuffer = updateBuffer;
        this.renderBuffer = renderBuffer;
        this.renderer = renderer;
    }

    public static CustomParticleSystem create(
            float[] state,
            float[] update,
            int stride,
            ShaderProgram updateProgram,
            EfficientParticleRenderer renderer,
            Disposer disposer) {

        var updateBuffer = MirroredFloatBuffer.create(
                update, OpenGL.GL_SHADER_STORAGE_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);

        var renderBuffer = MirroredFloatBuffer.create(
                state, OpenGL.GL_ARRAY_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);

        var arrayBuffer = CustomParticleBuffer.create(
                renderBuffer,
                stride,
                4,
                disposer);

        updateBuffer.updateGPU(0, updateBuffer.getCapacity());
        arrayBuffer.updateGPU(0, arrayBuffer.getCapacity());

        return new CustomParticleSystem(updateProgram, updateBuffer, arrayBuffer, renderer);
    }

    protected void bindUpdateBuffers() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 0, renderBuffer.getBufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 1, updateBuffer.getBufferId());
    }

    @Override
    public void render(float alpha) {
        renderer.render(renderBuffer, false, 0, renderBuffer.getCapacity(), alpha);
    }

    @Override
    public int getParticleCount() {
        return renderBuffer.getCapacity();
    }

    private static class CustomParticleBuffer extends AbstractMirroredVertexArrayBuffer<MirroredBuffer> {
        private CustomParticleBuffer(MirroredBuffer buffer, int stride, int elementSize) {
            super(buffer, stride, elementSize);
        }

        public static CustomParticleBuffer create(MirroredBuffer buffer, int stride, int elementSize, Disposer disposer) {
            var obj = new CustomParticleBuffer(buffer, stride, elementSize);
            disposer.registerDisposal(obj);
            return obj;
        }
    }
}