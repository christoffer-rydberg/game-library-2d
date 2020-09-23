package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.glUtil.OpenGLFloatBuffer;
import com.gamelibrary2d.glUtil.OpenGLIntBuffer;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.particle.ParticleUpdateListener;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;

public class DefaultShaderParticleSystem extends AbstractShaderParticleSystem implements Clearable {
    private final float[] position = new float[2];
    private final float[] externalAcceleration = new float[2];
    private final int[] atomicArray = new int[1];

    private final OpenGLIntBuffer atomicBuffer;
    private final OpenGLFloatBuffer parametersBuffer;
    private final OpenGLFloatBuffer positionbuffer;
    private final ParticleRenderBuffer[] renderBuffer;
    private final ParticleUpdateBuffer[] updateBuffer;

    private Point positionTransform;
    private int parameterUpdateCounter;
    private int positionUpdateCounter;
    private int capacity;
    private int particleCount;
    private int activeBuffer = 0;
    private ParticleSystemSettings settings;
    private ParticleUpdateListener updateListener;

    private int glUniformPosition;
    private int glUniformExternalAcceleration;
    private int glUniformParticlesInGpu;
    private int glUniformRandomSeed;

    private int particlesInGpuBuffer;

    private DefaultShaderParticleSystem(int capacity, ShaderProgram updaterProgram,
                                        OpenGLFloatBuffer positionBuffer, OpenGLFloatBuffer parametersBuffer,
                                        ParticleUpdateBuffer[] updateBuffer, ParticleRenderBuffer[] renderBuffer,
                                        ParticleSystemSettings settings, Disposer disposer) {
        super(updaterProgram);
        this.capacity = capacity;
        this.renderBuffer = renderBuffer;
        this.updateBuffer = updateBuffer;
        this.settings = settings;
        atomicBuffer = OpenGLIntBuffer.create(atomicArray, OpenGL.GL_ATOMIC_COUNTER_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);

        boolean updateProgramInUse = updaterProgram.inUse();
        if (!updateProgramInUse)
            updaterProgram.bind();

        this.positionbuffer = positionBuffer;
        positionUpdateCounter = settings.getParticlePositioner().getUpdateCounter();

        this.parametersBuffer = parametersBuffer;
        parameterUpdateCounter = settings.getParticleParameters().getUpdateCounter();

        // Cache uniforms
        glUniformPosition = updaterProgram.getUniformLocation("position");
        glUniformExternalAcceleration = updaterProgram.getUniformLocation("externalAcceleration");
        glUniformParticlesInGpu = updaterProgram.getUniformLocation("particlesInGpu");
        glUniformRandomSeed = updaterProgram.getUniformLocation("randomSeed");

        if (!updateProgramInUse)
            updaterProgram.unbind();
    }

    public static DefaultShaderParticleSystem create(int capacity, ParticleSystemSettings settings, Disposer disposer) {
        OpenGLFloatBuffer positionBuffer = OpenGLFloatBuffer.create(
                settings.getParticlePositioner().getInternalStateArray(),
                OpenGL.GL_SHADER_STORAGE_BUFFER,
                OpenGL.GL_DYNAMIC_DRAW,
                disposer);


        OpenGLFloatBuffer parametersBuffer = OpenGLFloatBuffer.create(
                settings.getParticleParameters().getInternalStateArray(),
                OpenGL.GL_SHADER_STORAGE_BUFFER,
                OpenGL.GL_DYNAMIC_DRAW,
                disposer);

        var renderBuffer = ParticleRenderBuffer.createWithSharedData(capacity, 2, disposer);
        renderBuffer[0].updateGPU(0, capacity);
        renderBuffer[1].updateGPU(0, capacity);

        var updateBuffer = ParticleUpdateBuffer.createWithSharedData(capacity, 2, disposer);
        updateBuffer[0].updateGPU(0, capacity);
        updateBuffer[1].updateGPU(0, capacity);

        return new DefaultShaderParticleSystem(capacity, ShaderProgram.getDefaultParticleUpdaterProgram(),
                positionBuffer, parametersBuffer, updateBuffer, renderBuffer, settings, disposer);
    }

    public void setPosition(float x, float y) {
        position[0] = x;
        position[1] = y;
    }

    public void setExternalAcceleration(float x, float y) {
        externalAcceleration[0] = x;
        externalAcceleration[1] = y;
    }

    public ParticlePositioner getParticlePositioner() {
        return settings.getParticlePositioner();
    }

    public void setParticlePositioner(ParticlePositioner positioner) {
        settings.setParticlePositioner(positioner);
    }

    public ParticleParameters getParticleParameters() {
        return settings.getParticleParameters();
    }

    public void setParticleParameters(ParticleParameters parameters) {
        settings.setParticleParameters(parameters);
    }

    /**
     * Gets the update listener.
     *
     * @return The update listener, or null if no update listener has been set.
     */
    public ParticleUpdateListener getUpdateListener() {
        return updateListener;
    }

    /**
     * Sets the update listener.
     *
     * @param listener The update listener.
     */
    public void setUpdateListener(ParticleUpdateListener listener) {
        updateListener = listener;
    }

    public Point getPositionTransform() {
        return positionTransform;
    }

    public void setPositionTransform(Point positionTransform) {
        this.positionTransform = positionTransform;
    }

    /**
     * Emits all particles at once at the specified coordinates. The particles count
     * is specified by {@link ParticleSystemSettings#getDefaultCount()}.
     */
    public void emitAll() {
        emitAll(Math.round(
                settings.getDefaultCount() + settings.getDefaultCountVar() * RandomInstance.random11()));
    }

    /**
     * Emits all particles at once at the specified coordinates.
     *
     * @param count The number of emitted particles.
     */
    public void emitAll(int count) {
        int remaining = capacity - particleCount;
        if (remaining < count) {
            count = remaining;
        }

        particleCount += count;
    }

    /**
     * Sequentially emits particles at the specified coordinates. The interval is
     * specified by {@link ParticleSystemSettings#getDefaultInterval()}.
     *
     * @param time      The current emitter time, in seconds. The delta time will be
     *                  added to this value. Particles will be emitted while the
     *                  time exceeds the emitter interval.
     * @param deltaTime Time since the last update, in seconds.
     * @return The new emitter time, i.e. how much time has passed since a particle
     * was emitted.
     */
    public float emitSequential(float time, float deltaTime) {
        return emitSequential(time, deltaTime, settings.getDefaultInterval());
    }

    /**
     * Sequentially emits particles at the specified coordinates.
     *
     * @param time      The current emitter time, in seconds. The delta time will be
     *                  added to this value. Particles will be emitted while the
     *                  time exceeds the emitter interval.
     * @param deltaTime Time since the last update, in seconds.
     * @param interval  The interval between emitted particles, in seconds.
     * @return The new emitter time, i.e. how much time has passed since a particle
     * was emitted.
     */
    public float emitSequential(float time, float deltaTime, float interval) {
        if (interval > 0) {
            time += deltaTime;

            int iterations = (int) (time / interval);

            float remainingTime = time - (iterations * interval);

            int count = settings.isPulsating()
                    ? (settings.getDefaultCount() + settings.getDefaultCountVar()) * iterations
                    : iterations;

            emitAll(count);

            time = remainingTime;
        }

        return time;
    }

    public void emit() {
        if (particleCount < capacity) {
            ++particleCount;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (particleCount > 0) {

            // Reset atomic counter
            atomicArray[0] = 0;
            atomicBuffer.updateGPU(0, 1);

            super.update(deltaTime);

            // Update particle count
            atomicBuffer.updateCPU(0, 1);
            particlesInGpuBuffer = atomicArray[0];
            particleCount = particlesInGpuBuffer;

            activeBuffer = activeBuffer == 1 ? 0 : 1;
        }
    }

    @Override
    protected void bindUpdateBuffers() {
        OpenGL openGL = OpenGL.instance();

        openGL.glUniform1i(glUniformRandomSeed, RandomInstance.get().nextInt());
        openGL.glUniform1i(glUniformParticlesInGpu, particlesInGpuBuffer);
        openGL.glUniform2f(
                glUniformPosition,
                position[0] + settings.getOffsetX(),
                position[1] + settings.getOffsetY());
        openGL.glUniform2fv(glUniformExternalAcceleration, externalAcceleration);

        var particlePositioner = settings.getParticlePositioner();
        if (!positionbuffer.allocate(particlePositioner.getInternalStateArray())) {
            if (positionUpdateCounter != particlePositioner.getUpdateCounter()) {
                positionUpdateCounter = particlePositioner.getUpdateCounter();
                positionbuffer.updateGPU(0, positionbuffer.capacity());
            }
        }

        var particleParameters = settings.getParticleParameters();
        if (!parametersBuffer.allocate(particleParameters.getInternalStateArray())) {
            if (parameterUpdateCounter != particleParameters.getUpdateCounter()) {
                parameterUpdateCounter = particleParameters.getUpdateCounter();
                parametersBuffer.updateGPU(0, parametersBuffer.capacity());
            }
        }

        openGL.glBindBufferBase(OpenGL.GL_ATOMIC_COUNTER_BUFFER, 0, atomicBuffer.bufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 1, positionbuffer.bufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 2, parametersBuffer.bufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 3, renderBuffer[activeBuffer].bufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 4, updateBuffer[activeBuffer].bufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 5,
                renderBuffer[activeBuffer == 1 ? 0 : 1].bufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 6,
                updateBuffer[activeBuffer == 1 ? 0 : 1].bufferId());
    }

    private boolean isTransformingPosition() {
        return positionTransform != null && (positionTransform.getX() != 0f || positionTransform.getY() != 0f);
    }

    @Override
    public void render(float alpha) {
        if (particlesInGpuBuffer == 0)
            return;

        if (isTransformingPosition()) {
            ModelMatrix.instance().pushMatrix();
            ModelMatrix.instance().translatef(positionTransform.getX(), positionTransform.getY(), 0);
            settings.getRenderer().render(renderBuffer[activeBuffer], false, 0, particlesInGpuBuffer, alpha);
            ModelMatrix.instance().popMatrix();
        } else {
            settings.getRenderer().render(renderBuffer[activeBuffer], false, 0, particlesInGpuBuffer, alpha);
        }
    }

    @Override
    public void clear() {
        particleCount = 0;
    }

    @Override
    public boolean isAutoClearing() {
        return true;
    }

    @Override
    public int getParticleCount() {
        return particleCount;
    }

    public void setRenderer(EfficientParticleRenderer renderer) {
        settings.setRenderer(renderer);
    }

    public void setSettings(ParticleSystemSettings settings) {
        this.settings = settings;
    }
}