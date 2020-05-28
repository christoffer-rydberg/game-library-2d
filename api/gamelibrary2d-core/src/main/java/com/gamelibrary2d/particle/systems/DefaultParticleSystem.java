package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.particle.ParticleUpdateListener;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;

public class DefaultParticleSystem implements ParticleSystem {
    private final int capacity;
    private final float[] externalSpeed = new float[2];
    private final float[] externalAcceleration = new float[2];
    private final Particle particle;

    private int particleCount;
    private boolean gpuOutdated = true;
    private ParticleSystemSettings settings;
    private ParticleRenderBuffer vertexBuffer;
    private ParticleUpdateBuffer updateBuffer;
    private Point positionTransform;
    private ParticleUpdateListener updateListener;

    private DefaultParticleSystem(int capacity, ParticleSystemSettings settings,
                                  ParticleRenderBuffer vertexBuffer, ParticleUpdateBuffer updateBuffer) {
        this.settings = settings;
        this.vertexBuffer = vertexBuffer;
        this.updateBuffer = updateBuffer;
        this.particle = new Particle(vertexBuffer, updateBuffer, 0);
        this.capacity = capacity;
    }

    public static DefaultParticleSystem create(int capacity, ParticleSystemSettings settings, Disposer disposer) {
        return new DefaultParticleSystem(
                capacity,
                settings,
                ParticleRenderBuffer.create(capacity, disposer),
                ParticleUpdateBuffer.create(capacity, disposer));
    }

    public void setExternalSpeed(float x, float y) {
        externalSpeed[0] = x;
        externalSpeed[1] = y;
    }

    public void setExternalAcceleration(float x, float y) {
        externalAcceleration[0] = x;
        externalAcceleration[1] = y;
    }

    public void setSettings(ParticleSystemSettings settings) {
        this.settings = settings;
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

    public ParticleRenderer getRenderer() {
        return settings.getRenderer();
    }

    public void setRenderer(ParticleRenderer renderer) {
        settings.setRenderer(renderer);
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
     *
     * @param x The X-coordinate of the emitted particles.
     * @param y The Y-coordinate of the emitted particles.
     */
    public void emitAll(float x, float y) {
        emitAll(x, y, Math.round(
                settings.getDefaultCount() + settings.getDefaultCountVar() * RandomInstance.random11()));
    }

    /**
     * Emits all particles at once at the specified coordinates.
     *
     * @param x     The X-coordinate of the emitted particles.
     * @param y     The Y-coordinate of the emitted particles.
     * @param count The number of emitted particles.
     */
    public void emitAll(float x, float y, int count) {
        int remaining = getCapacity() - particleCount;
        if (remaining < count) {
            count = remaining;
        }

        for (int i = 0; i < count; ++i) {
            emit(x, y);
        }
    }

    /**
     * Sequentially emits particles at the specified coordinates. The interval is
     * specified by {@link ParticleSystemSettings#getDefaultInterval()}.
     *
     * @param x         The X-coordinate of the emitted particles.
     * @param y         The Y-coordinate of the emitted particles.
     * @param time      The current emitter time, in seconds. The delta time will be
     *                  added to this value. Particles will be emitted while the
     *                  time exceeds the emitter interval.
     * @param deltaTime Time since the last update, in seconds.
     * @return The new emitter time, i.e. how much time has passed since a particle
     * was emitted.
     */
    public float emitSequential(float x, float y, float time, float deltaTime) {
        return emitSequential(x, y, time, deltaTime, settings.getDefaultInterval());
    }

    /**
     * Sequentially emits particles at the specified coordinates.
     *
     * @param x         The X-coordinate of the emitted particles.
     * @param y         The Y-coordinate of the emitted particles.
     * @param time      The current emitter time, in seconds. The delta time will be
     *                  added to this value. Particles will be emitted while the
     *                  time exceeds the emitter interval.
     * @param deltaTime Time since the last update, in seconds.
     * @param interval  The interval between emitted particles, in seconds.
     * @return The new emitter time, i.e. how much time has passed since a particle
     * was emitted.
     */
    public float emitSequential(float x, float y, float time, float deltaTime, float interval) {
        if (interval > 0) {
            time += deltaTime;
            int iterations = (int) (time / interval);
            float remainingTime = time - (iterations * interval);
            int count = settings.isPulsating()
                    ? (settings.getDefaultCount() + settings.getDefaultCountVar()) * iterations
                    : iterations;
            emitAll(x, y, count);
            time = remainingTime;
        }

        return time;
    }

    /**
     * Emits a single particle at the specified coordinates.
     */
    public void emit(float x, float y) {
        if (particleCount < getCapacity()) {
            particle.setIndex(particleCount++);

            particle.setInitialized(false);
            settings.getParticlePositioner().initialize(particle, x, y);
            settings.getParticleParameters().apply(particle);

            particle.setExternalSpeedX(externalSpeed[0]);
            particle.setExternalSpeedY(externalSpeed[1]);
            particle.setCustom(0);
        }
    }

    public void update(float deltaTime) {
        if (particleCount > 0) {
            int index = 0;
            while (index != particleCount) {
                particle.setIndex(index);
                particle.update(externalAcceleration, deltaTime);

                if (particle.hasExpired()) {
                    destroyParticle(index);
                    continue;
                }

                if (updateListener != null) {
                    final boolean compensateForPosTransform = positionTransform != null;
                    if (compensateForPosTransform) {
                        particle.setPosition(particle.getPosX() + positionTransform.getX(),
                                particle.getPosY() + positionTransform.getY());
                    }

                    if (!updateListener.updated(this, particle)) {
                        destroyParticle(index);
                        continue;
                    }

                    if (compensateForPosTransform) {
                        particle.setPosition(particle.getPosX() - positionTransform.getX(),
                                particle.getPosY() - positionTransform.getY());
                    }
                }

                ++index;
            }

            gpuOutdated = true;
        }
    }

    public void render(float alpha) {
        if (particleCount > 0) {
            if (positionTransform != null) {
                ModelMatrix.instance().pushMatrix();
                ModelMatrix.instance().translatef(positionTransform.getX(), positionTransform.getY(), 0);
                settings.getRenderer().render(vertexBuffer, gpuOutdated, 0, particleCount, alpha);
                ModelMatrix.instance().popMatrix();
            } else {
                settings.getRenderer().render(vertexBuffer, gpuOutdated, 0, particleCount, alpha);
            }

            gpuOutdated = false;
        }
    }

    public void clear() {
        particleCount = 0;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public int getCapacity() {
        return capacity;
    }

    private void destroyParticle(int index) {
        --particleCount;

        if (particleCount > 0) {
            int lastIndex = particleCount;
            vertexBuffer.copy(lastIndex, index, 1);
            updateBuffer.copy(lastIndex, index, 1);
        }
    }
}