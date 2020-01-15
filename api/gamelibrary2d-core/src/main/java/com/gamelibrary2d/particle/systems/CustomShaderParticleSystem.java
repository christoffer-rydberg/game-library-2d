package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.resources.CustomVertexArray;
import com.gamelibrary2d.glUtil.FloatTransferBuffer;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.resources.VertexArray;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;

public class CustomShaderParticleSystem extends AbstractShaderParticleSystem {

    private final FloatTransferBuffer updateBuffer;
    private final VertexArray arrayBuffer;

    protected CustomShaderParticleSystem(ShaderProgram updaterProgram, FloatTransferBuffer updateBuffer,
                                         VertexArray arrayBuffer, EfficientParticleRenderer renderer) {
        super(updaterProgram, renderer);
        this.updateBuffer = updateBuffer;
        this.arrayBuffer = arrayBuffer;
    }

    public static CustomShaderParticleSystem create(float[] state, float[] update, int stride,
                                                    ShaderProgram updateProgram, EfficientParticleRenderer renderer, Disposer disposer) {
        FloatTransferBuffer updateBuffer = new FloatTransferBuffer(update, stride, OpenGL.GL_SHADER_STORAGE_BUFFER,
                OpenGL.GL_STATIC_DRAW, disposer);

        var arrayBuffer = CustomVertexArray.create(
                new FloatTransferBuffer(state, stride, OpenGL.GL_ARRAY_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer),
                4,
                disposer);

        updateBuffer.updateGPU(0, updateBuffer.getCapacity());
        arrayBuffer.updateGPU(0, arrayBuffer.getCapacity());

        return new CustomShaderParticleSystem(updateProgram, updateBuffer, arrayBuffer, renderer);
    }

    protected void bindUdateBuffers() {
        OpenGL openGL = OpenGL.instance();
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 0, arrayBuffer.getGlBuffer());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 1, updateBuffer.getGlBuffer());
    }

    @Override
    public void render(float alpha) {
        getRenderer().render(null, arrayBuffer, false, 0, arrayBuffer.getCapacity(), alpha);
    }

    @Override
    public void clear() {

    }

    @Override
    public int getParticleCount() {
        return arrayBuffer.getCapacity();
    }
}