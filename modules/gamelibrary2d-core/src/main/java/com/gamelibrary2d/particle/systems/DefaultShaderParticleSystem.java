package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.framework.OpenGL;
import com.gamelibrary2d.glUtil.*;
import com.gamelibrary2d.objects.ParticleUpdateListener;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.settings.ParticleSpawnSettings;
import com.gamelibrary2d.particle.settings.ParticleUpdateSettings;

public class DefaultShaderParticleSystem extends AbstractShaderParticleSystem {

    private final float[] position = new float[3];

    private final float[] externalSpeed = new float[3];

    private final float[] externalAcceleration = new float[3];

    private final int[] atomicArray = new int[1];

    private final TransferBuffer atomicBuffer;

    private final FloatTransferBuffer initBuffer;

    private final ParticleVertexBuffer[] vertexBuffer;

    private final ParticleUpdateBuffer[] updateBuffer;

    private int activeBuffer = 0;

    private int particleCount;

    private ParticleSpawnSettings spawnSettings;

    private ParticleUpdateSettings updateSettings;

    private Point positionTransform;

    private ParticleUpdateListener updateListener;

    private int glUniformPosition;

    private int glUniformExternalSpeed;

    private int glUniformExternalAcceleration;

    private int glUniformParticlesInGpu;

    private int glUniformRandomSeed;

    private int particlesInGpuBuffer;

    private int capacity;

    private DefaultShaderParticleSystem(int capacity, ShaderProgram updaterProgram, FloatTransferBuffer initBuffer,
                                        ParticleUpdateBuffer[] updateBuffer, ParticleVertexBuffer[] vertexBuffer,
                                        EfficientParticleRenderer renderer, ParticleSpawnSettings spawnSettings,
                                        ParticleUpdateSettings updateSettings, Disposer disposer) {
        super(updaterProgram, renderer);
        this.capacity = capacity;
        this.vertexBuffer = vertexBuffer;
        this.updateBuffer = updateBuffer;
        this.spawnSettings = spawnSettings;
        this.updateSettings = updateSettings;
        atomicBuffer = new IntTransferBuffer(atomicArray, 1, OpenGL.GL_ATOMIC_COUNTER_BUFFER, OpenGL.GL_DYNAMIC_DRAW,
                disposer);

        boolean updateProgramInUse = updaterProgram.inUse();
        if (!updateProgramInUse)
            updaterProgram.bind();

        this.initBuffer = initBuffer;

        // Cache uniforms
        glUniformPosition = updaterProgram.getUniformLocation("position");
        glUniformExternalSpeed = updaterProgram.getUniformLocation("externalSpeed");
        glUniformExternalAcceleration = updaterProgram.getUniformLocation("externalAcceleration");
        glUniformParticlesInGpu = updaterProgram.getUniformLocation("particlesInGpu");
        glUniformRandomSeed = updaterProgram.getUniformLocation("randomSeed");

        if (!updateProgramInUse)
            updaterProgram.unbind();
    }

    public static DefaultShaderParticleSystem create(int capacity, ParticleSpawnSettings spawnSettings,
                                                     ParticleUpdateSettings updateSettings, EfficientParticleRenderer renderer, Disposer disposer) {

        FloatTransferBuffer initBuffer = new FloatTransferBuffer(updateSettings.getInternalStateArray(),
                ParticleUpdateSettings.STRIDE, OpenGL.GL_SHADER_STORAGE_BUFFER, OpenGL.GL_DYNAMIC_DRAW, disposer);

        float[] vertices = new float[capacity * ParticleVertexBuffer.STRIDE];
        ParticleVertexBuffer[] vertexBuffer = new ParticleVertexBuffer[2];
        vertexBuffer[0] = ParticleVertexBuffer.create(vertices, disposer);
        vertexBuffer[1] = ParticleVertexBuffer.create(vertices, disposer);
        vertexBuffer[0].updateGPU(0, capacity);
        vertexBuffer[1].updateGPU(0, capacity);

        float[] updateArray = new float[capacity * ParticleUpdateBuffer.STRIDE];
        ParticleUpdateBuffer[] updateBuffer = new ParticleUpdateBuffer[2];
        updateBuffer[0] = new ParticleUpdateBuffer(updateArray, disposer);
        updateBuffer[1] = new ParticleUpdateBuffer(updateArray, disposer);
        updateBuffer[0].updateGPU(0, capacity);
        updateBuffer[1].updateGPU(0, capacity);

        return new DefaultShaderParticleSystem(capacity, ShaderProgram.getDefaultParticleUpdaterProgram(), initBuffer,
                updateBuffer, vertexBuffer, renderer, spawnSettings, updateSettings, disposer);
    }

    public void setPosition(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    public void setExternalAcceleration(float x, float y, float z) {
        externalAcceleration[0] = x;
        externalAcceleration[1] = y;
        externalAcceleration[2] = z;
    }

    public ParticleSpawnSettings getSpawnSettings() {
        return spawnSettings;
    }

    public void setSpawnSettings(ParticleSpawnSettings spawnSettings) {
        this.spawnSettings = spawnSettings;
    }

    public ParticleUpdateSettings getUpdateSettings() {
        return updateSettings;
    }

    public void setUpdateSettings(ParticleUpdateSettings updateSettings) {
        this.updateSettings = updateSettings;
        initBuffer.setSource(updateSettings.getInternalStateArray());
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
     * is specified by {@link ParticleSpawnSettings#getDefaultCount()}.
     */
    public void emitAll() {
        emitAll(Math.round(
                spawnSettings.getDefaultCount() + spawnSettings.getDefaultCountVar() * RandomInstance.random11()));
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
     * specified by {@link ParticleSpawnSettings#getDefaultInterval()}.
     *
     * @param time      The current emitter time, in seconds. The delta time will be
     *                  added to this value. Particles will be emitted while the
     *                  time exceeds the emitter interval.
     * @param deltaTime Time since the last update, in seconds.
     * @return The new emitter time, i.e. how much time has passed since a particle
     * was emitted.
     */
    public float emitSequential(float time, float deltaTime) {
        return emitSequential(time, deltaTime, spawnSettings.getDefaultInterval());
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

            int count = spawnSettings.isPulsating()
                    ? (spawnSettings.getDefaultCount() + spawnSettings.getDefaultCountVar()) * iterations
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
    protected void bindUdateBuffers() {

        OpenGL openGL = OpenGL.instance();

        updateGpuSettings(); // TODO: This is only needed if UpdateSettings has changed.

        openGL.glUniform1i(glUniformRandomSeed, RandomInstance.get().nextInt());
        openGL.glUniform1i(glUniformParticlesInGpu, particlesInGpuBuffer);
        openGL.glUniform3fv(glUniformPosition, position);
        openGL.glUniform3fv(glUniformExternalSpeed, externalSpeed);
        openGL.glUniform3fv(glUniformExternalAcceleration, externalAcceleration);

        openGL.glBindBufferBase(OpenGL.GL_ATOMIC_COUNTER_BUFFER, 0, atomicBuffer.getGlBuffer());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 1, initBuffer.getGlBuffer());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 2, vertexBuffer[activeBuffer].getGlBuffer());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 3, updateBuffer[activeBuffer].getGlBuffer());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 4,
                vertexBuffer[activeBuffer == 1 ? 0 : 1].getGlBuffer());
        openGL.glBindBufferBase(OpenGL.GL_SHADER_STORAGE_BUFFER, 5,
                updateBuffer[activeBuffer == 1 ? 0 : 1].getGlBuffer());
    }

    private void updateGpuSettings() {

        var updaterProgram = getUpdaterProgram();

        boolean updateProgramInUse = updaterProgram.inUse();
        if (!updateProgramInUse)
            updaterProgram.bind();

        initBuffer.updateGPU(0, 1);

        if (!updateProgramInUse)
            updaterProgram.unbind();
    }

    @Override
    public void render(float alpha) {

        if (particlesInGpuBuffer == 0)
            return;

        if (positionTransform != null) {

            ModelMatrix.instance().pushMatrix();

            ModelMatrix.instance().translatef(positionTransform.getX(), positionTransform.getY(), 0);

            getRenderer().render(null, vertexBuffer[activeBuffer], false, 0, particlesInGpuBuffer, alpha);

            ModelMatrix.instance().popMatrix();
        } else {
            getRenderer().render(null, vertexBuffer[activeBuffer], false, 0, particlesInGpuBuffer, alpha);
        }
    }

    @Override
    public void clear() {
        particleCount = 0;
    }

    @Override
    public int getParticleCount() {
        return particleCount;
    }
}