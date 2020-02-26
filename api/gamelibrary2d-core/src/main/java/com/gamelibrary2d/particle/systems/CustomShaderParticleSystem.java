package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.AbstractVertexArrayBuffer;
import com.gamelibrary2d.glUtil.OpenGLFloatBuffer;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.OpenGLBuffer;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;

public class CustomShaderParticleSystem extends AbstractShaderParticleSystem {

    private final OpenGLFloatBuffer updateBuffer;
    private final OpenGLBuffer arrayBuffer;

    protected CustomShaderParticleSystem(ShaderProgram updaterProgram, OpenGLFloatBuffer updateBuffer,
                                         OpenGLBuffer arrayBuffer, EfficientParticleRenderer renderer) {
        super(updaterProgram, renderer);
        this.updateBuffer = updateBuffer;
        this.arrayBuffer = arrayBuffer;
    }

    public static CustomShaderParticleSystem create(float[] state, float[] update, int stride,
                                                    ShaderProgram updateProgram, EfficientParticleRenderer renderer, Disposer disposer) {
        var updateBuffer = OpenGLFloatBuffer.create(
                update, OpenGL.GL_SHADER_STORAGE_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);

        var stateBuffer = OpenGLFloatBuffer.create(
                state, OpenGL.GL_ARRAY_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);

        var arrayBuffer = CustomParticleBuffer.create(
                stateBuffer,
                stride,
                4,
                disposer);

        updateBuffer.updateGPU(0, updateBuffer.capacity());
        arrayBuffer.updateGPU(0, arrayBuffer.capacity());

        return new CustomShaderParticleSystem(updateProgram, updateBuffer, arrayBuffer, renderer);
    }

    protected void bindUdateBuffers() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 0, arrayBuffer.bufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 1, updateBuffer.bufferId());
    }

    @Override
    public void render(float alpha) {
        getRenderer().render(null, arrayBuffer, false, 0, arrayBuffer.capacity(), alpha);
    }

    @Override
    public void clear() {

    }

    @Override
    public int getParticleCount() {
        return arrayBuffer.capacity();
    }

    private static class CustomParticleBuffer extends AbstractVertexArrayBuffer {
        private CustomParticleBuffer(OpenGLFloatBuffer buffer, int stride, int elementSize) {
            super(buffer, stride, elementSize);
        }

        public static CustomParticleBuffer create(OpenGLFloatBuffer buffer, int stride, int elementSize, Disposer disposer) {
            var obj = new CustomParticleBuffer(buffer, stride, elementSize);
            disposer.registerDisposal(obj);
            return obj;
        }
    }
}