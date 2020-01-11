package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;

public abstract class AbstractShaderParticleSystem implements ParticleSystem {

    private final static int WORK_GROUP_SIZE = 512;

    private final ShaderProgram updaterProgram;

    private EfficientParticleRenderer renderer;

    private int glUniformDeltaTime;

    private int glUniformParticleCount;

    protected AbstractShaderParticleSystem(ShaderProgram updaterProgram, EfficientParticleRenderer renderer) {

        this.updaterProgram = updaterProgram;

        this.renderer = renderer;

        boolean updateProgramInUse = updaterProgram.inUse();
        if (!updateProgramInUse)
            updaterProgram.bind();

        // Cache uniforms
        glUniformDeltaTime = updaterProgram.getUniformLocation("deltaTime");
        glUniformParticleCount = updaterProgram.getUniformLocation("particleCount");

        if (!updateProgramInUse)
            updaterProgram.unbind();
    }

    public ShaderProgram getUpdaterProgram() {
        return updaterProgram;
    }

    public EfficientParticleRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(EfficientParticleRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void update(float deltaTime) {

        OpenGL openGL = OpenGL.instance();

        int particleCount = getParticleCount();

        updaterProgram.bind();

        // Update uniform variables
        openGL.glUniform1f(glUniformDeltaTime, deltaTime);
        openGL.glUniform1i(glUniformParticleCount, particleCount);

        // Bind buffers
        bindUdateBuffers();

        // Dispatch work to the compute shader
        openGL.glDispatchCompute((int) Math.ceil((double) particleCount / WORK_GROUP_SIZE), 1, 1);
        openGL.glMemoryBarrier(OpenGL.GL_SHADER_STORAGE_BARRIER_BIT | OpenGL.GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT);

        updaterProgram.unbind();
    }

    protected abstract void bindUdateBuffers();
}