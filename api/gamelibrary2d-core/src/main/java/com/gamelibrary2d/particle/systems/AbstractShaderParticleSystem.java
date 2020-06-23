package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ShaderProgram;

public abstract class AbstractShaderParticleSystem implements ParticleSystem {

    private final static int WORK_GROUP_SIZE = 512;

    private final ShaderProgram updaterProgram;

    private int glUniformDeltaTime;

    private int glUniformParticleCount;

    protected AbstractShaderParticleSystem(ShaderProgram updaterProgram) {
        this.updaterProgram = updaterProgram;

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

    @Override
    public void update(float deltaTime) {
        OpenGL openGL = OpenGL.instance();

        int particleCount = getParticleCount();

        updaterProgram.bind();

        openGL.glUniform1f(glUniformDeltaTime, deltaTime);
        openGL.glUniform1i(glUniformParticleCount, particleCount);

        bindUpdateBuffers();

        openGL.glDispatchCompute((int) Math.ceil((double) particleCount / WORK_GROUP_SIZE), 1, 1);

        applyMemoryBarriers();

        updaterProgram.unbind();
    }

    protected void applyMemoryBarriers() {
        OpenGL.instance().glMemoryBarrier(
                OpenGL.GL_SHADER_STORAGE_BARRIER_BIT |
                        OpenGL.GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT);
    }

    protected abstract void bindUpdateBuffers();
}