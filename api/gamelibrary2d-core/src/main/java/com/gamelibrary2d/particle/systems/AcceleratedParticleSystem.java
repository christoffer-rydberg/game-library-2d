package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.BufferUtils;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.*;
import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.particle.ParticleUpdateListener;
import com.gamelibrary2d.particle.parameters.EmitterParameters;
import com.gamelibrary2d.particle.parameters.ParticleParameters;
import com.gamelibrary2d.particle.parameters.ParticleSystemParameters;
import com.gamelibrary2d.particle.parameters.PositionParameters;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;

import java.nio.FloatBuffer;

public class AcceleratedParticleSystem extends AbstractGpuBasedParticleSystem implements Clearable {
    private final float[] position = new float[2];
    private final FloatBuffer externalAcceleration = BufferUtils.createFloatBuffer(2);

    private final MirroredIntBuffer atomicBuffer;
    private final MirroredFloatBuffer parametersBuffer;
    private final MirroredFloatBuffer positionbuffer;

    private final DefaultOpenGLBuffer[] updateBuffer;
    private final DefaultVertexArrayBuffer<DefaultOpenGLBuffer>[] renderBuffer;

    private final int capacity;

    private Point positionTransformation;
    private int parameterUpdateCounter;
    private int positionUpdateCounter;
    private int particleCount;
    private int activeBuffer = 0;

    private EfficientParticleRenderer renderer;
    private ParticleSystemParameters parameters;
    private ParticleUpdateListener updateListener;

    private int glUniformPosition;
    private int glUniformExternalAcceleration;
    private int glUniformParticlesInGpu;
    private int glUniformRandomSeed;

    private int particlesInGpuBuffer;

    private AcceleratedParticleSystem(ShaderProgram updaterProgram,
                                      MirroredFloatBuffer positionBuffer,
                                      MirroredFloatBuffer parametersBuffer,
                                      DefaultOpenGLBuffer[] updateBuffer,
                                      DefaultVertexArrayBuffer<DefaultOpenGLBuffer>[] renderBuffer,
                                      ParticleSystemParameters parameters,
                                      EfficientParticleRenderer renderer,
                                      int capacity,
                                      Disposer disposer) {
        super(updaterProgram);
        this.updateBuffer = updateBuffer;
        this.renderBuffer = renderBuffer;
        this.parameters = parameters;
        this.renderer = renderer;
        this.capacity = capacity;
        atomicBuffer = MirroredIntBuffer.create(new int[1], OpenGL.GL_ATOMIC_COUNTER_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);

        boolean updateProgramInUse = updaterProgram.inUse();
        if (!updateProgramInUse) {
            updaterProgram.bind();
        }

        this.positionbuffer = positionBuffer;
        positionUpdateCounter = parameters.getPositionParameters().getUpdateCounter();

        this.parametersBuffer = parametersBuffer;
        parameterUpdateCounter = parameters.getParticleParameters().getUpdateCounter();

        glUniformPosition = updaterProgram.getUniformLocation("position");
        glUniformExternalAcceleration = updaterProgram.getUniformLocation("externalAcceleration");
        glUniformParticlesInGpu = updaterProgram.getUniformLocation("particlesInGpu");
        glUniformRandomSeed = updaterProgram.getUniformLocation("randomSeed");

        if (!updateProgramInUse) {
            updaterProgram.unbind();
        }
    }

    public static AcceleratedParticleSystem create(
            ParticleSystemParameters parameters,
            EfficientParticleRenderer renderer,
            int capacity,
            Disposer disposer) {

        MirroredFloatBuffer positionBuffer = MirroredFloatBuffer.create(
                parameters.getPositionParameters().getInternalStateArray(),
                OpenGL.GL_SHADER_STORAGE_BUFFER,
                OpenGL.GL_DYNAMIC_DRAW,
                disposer);

        MirroredFloatBuffer parametersBuffer = MirroredFloatBuffer.create(
                parameters.getParticleParameters().getInternalStateArray(),
                OpenGL.GL_SHADER_STORAGE_BUFFER,
                OpenGL.GL_DYNAMIC_DRAW,
                disposer);

        DefaultVertexArrayBuffer[] renderBuffer = new DefaultVertexArrayBuffer[2];
        for (int i = 0; i < 2; ++i) {
            DefaultOpenGLBuffer buffer = DefaultOpenGLBuffer.create(
                    OpenGL.GL_ARRAY_BUFFER,
                    OpenGL.GL_DYNAMIC_DRAW,
                    disposer);

            buffer.allocate(capacity * ParticleRenderBuffer.STRIDE);

            renderBuffer[i] = new DefaultVertexArrayBuffer<>(
                    buffer,
                    ParticleRenderBuffer.STRIDE,
                    4);
        }

        DefaultOpenGLBuffer[] updateBuffer = new DefaultOpenGLBuffer[2];
        for (int i = 0; i < 2; ++i) {
            DefaultOpenGLBuffer buffer = DefaultOpenGLBuffer.create(
                    OpenGL.GL_SHADER_STORAGE_BUFFER,
                    OpenGL.GL_DYNAMIC_DRAW,
                    disposer);

            buffer.allocate(capacity * ParticleUpdateBuffer.STRIDE);

            updateBuffer[i] = buffer;
        }

        return new AcceleratedParticleSystem(ShaderProgram.getDefaultParticleUpdaterProgram(),
                positionBuffer, parametersBuffer, updateBuffer, renderBuffer, parameters, renderer, capacity, disposer);
    }

    public void setPosition(float x, float y) {
        position[0] = x;
        position[1] = y;
    }

    public void setExternalAcceleration(float x, float y) {
        externalAcceleration.clear();
        externalAcceleration.put(x);
        externalAcceleration.put(y);
        externalAcceleration.flip();
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

    public Point getPositionTransformation() {
        return positionTransformation;
    }

    public void setPositionTransformation(Point positionTransformation) {
        this.positionTransformation = positionTransformation;
    }

    /**
     * Emits all particles at the specified coordinates. The particles count
     * is specified by {@link EmitterParameters#getDefaultCount()}.
     */
    public void emitAll() {
        EmitterParameters emitterParameters = parameters.getEmitterParameters();
        emit(Math.round(emitterParameters.getDefaultCount() + emitterParameters.getDefaultCountVar() * RandomInstance.random11()));
    }

    /**
     * Emits particles at the specified coordinates.
     *
     * @param count The number of emitted particles.
     */
    public void emit(int count) {
        int remaining = capacity - particleCount;
        if (remaining < count) {
            count = remaining;
        }

        particleCount += count;
    }

    /**
     * Sequentially emits particles at the specified coordinates. The interval is
     * specified by {@link EmitterParameters#getDefaultInterval()}.
     *
     * @param time      The current emitter time, in seconds. The delta time will be
     *                  added to this value. Particles will be emitted while the
     *                  time exceeds the emitter interval.
     * @param deltaTime Time since the last update, in seconds.
     * @return The new emitter time, i.e. how much time has passed since a particle
     * was emitted.
     */
    public float emitSequential(float time, float deltaTime) {
        return emitSequential(time, deltaTime, parameters.getEmitterParameters().getDefaultInterval());
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

            EmitterParameters emitterParameters = parameters.getEmitterParameters();
            int count = emitterParameters.isPulsating()
                    ? (emitterParameters.getDefaultCount() + emitterParameters.getDefaultCountVar()) * iterations
                    : iterations;

            emit(count);

            time = remainingTime;
        }

        return time;
    }

    public void emit() {
        emit(1);
    }

    @Override
    public void update(float deltaTime) {
        if (particleCount > 0) {
            // Reset atomic counter
            atomicBuffer.getData()[0] = 0;
            atomicBuffer.updateGPU(0, 1);

            super.update(deltaTime);

            // Update particle count
            atomicBuffer.updateCPU(0, 1);
            particlesInGpuBuffer = atomicBuffer.getData()[0];
            particleCount = particlesInGpuBuffer;

            activeBuffer = activeBuffer == 1 ? 0 : 1;
        }
    }

    @Override
    protected void bindUpdateBuffers() {
        OpenGL openGL = OpenGL.instance();

        openGL.glUniform1i(glUniformRandomSeed, RandomInstance.get().nextInt());

        openGL.glUniform1i(glUniformParticlesInGpu, particlesInGpuBuffer);

        EmitterParameters emitterParameters = parameters.getEmitterParameters();

        openGL.glUniform2f(
                glUniformPosition,
                position[0] + emitterParameters.getOffsetX(),
                position[1] + emitterParameters.getOffsetY());
        openGL.glUniform2fv(glUniformExternalAcceleration, externalAcceleration);

        PositionParameters positionParameters = parameters.getPositionParameters();
        if (!positionbuffer.allocate(positionParameters.getInternalStateArray())) {
            if (positionUpdateCounter != positionParameters.getUpdateCounter()) {
                positionUpdateCounter = positionParameters.getUpdateCounter();
                positionbuffer.updateGPU(0, positionbuffer.getCapacity());
            }
        }

        ParticleParameters particleParameters = parameters.getParticleParameters();
        if (!parametersBuffer.allocate(particleParameters.getInternalStateArray())) {
            if (parameterUpdateCounter != particleParameters.getUpdateCounter()) {
                parameterUpdateCounter = particleParameters.getUpdateCounter();
                parametersBuffer.updateGPU(0, parametersBuffer.getCapacity());
            }
        }

        openGL.glBindBufferBase(OpenGL.GL_ATOMIC_COUNTER_BUFFER, 0, atomicBuffer.getBufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 1, positionbuffer.getBufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 2, parametersBuffer.getBufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 3, renderBuffer[activeBuffer].getBufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 4, updateBuffer[activeBuffer].getBufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 5,
                renderBuffer[activeBuffer == 1 ? 0 : 1].getBufferId());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 6,
                updateBuffer[activeBuffer == 1 ? 0 : 1].getBufferId());
    }

    private boolean isTransformingPosition() {
        return positionTransformation != null && (positionTransformation.getX() != 0f || positionTransformation.getY() != 0f);
    }

    @Override
    public void render(float alpha) {
        if (particlesInGpuBuffer > 0) {
            if (isTransformingPosition()) {
                ModelMatrix.instance().pushMatrix();
                ModelMatrix.instance().translatef(positionTransformation.getX(), positionTransformation.getY(), 0);
                renderer.render(renderBuffer[activeBuffer], false, 0, particlesInGpuBuffer, alpha);
                ModelMatrix.instance().popMatrix();
            } else {
                renderer.render(renderBuffer[activeBuffer], false, 0, particlesInGpuBuffer, alpha);
            }
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

    public EfficientParticleRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(EfficientParticleRenderer renderer) {
        this.renderer = renderer;
    }

    public ParticleSystemParameters getParameters() {
        return parameters;
    }

    public void setParameters(ParticleSystemParameters parameters) {
        this.parameters = parameters;
    }
}