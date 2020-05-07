package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.particle.ParticleUpdateListener;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;
import com.gamelibrary2d.particle.settings.ParticleSettings;
import com.gamelibrary2d.particle.settings.ParticleSpawnSettings;
import com.gamelibrary2d.particle.settings.ParticleUpdateSettings;

import java.util.ArrayDeque;
import java.util.Deque;

public class DefaultParticleSystem implements ParticleSystem {

    private static Deque<Particle> ParticlePool = new ArrayDeque<>();

    private final float[] externalSpeed = new float[3];

    private final float[] externalAcceleration = new float[3];
    private final Particle[] particles;
    private ParticleSpawnSettings spawnSettings;
    private ParticleUpdateSettings updateSettings;
    private InternalParticleBuffer vertexBuffer;
    private ParticleUpdateBuffer updateBuffer;
    private int particleCount;

    private Point positionTransform;

    private ParticleUpdateListener updateListener;

    private boolean gpuOutdated = true;

    private ParticleRenderer renderer;

    private DefaultParticleSystem(int capacity, ParticleRenderer renderer, ParticleSpawnSettings spawnSettings,
                                  ParticleUpdateSettings updateSettings, InternalParticleBuffer vertexBuffer,
                                  ParticleUpdateBuffer updateBuffer) {
        this.renderer = renderer;
        this.spawnSettings = spawnSettings;
        this.updateSettings = updateSettings;
        this.vertexBuffer = vertexBuffer;
        this.updateBuffer = updateBuffer;
        particles = new Particle[capacity];
    }

    public static DefaultParticleSystem create(int capacity, ParticleSettings settings, Disposer disposer) {
        return create(capacity, settings.getSpawnSettings(), settings.getUpdateSettings(), new EfficientParticleRenderer(), disposer);
    }

    public static DefaultParticleSystem create(int capacity, ParticleSettings settings, ParticleRenderer renderer,
                                               Disposer disposer) {
        return create(capacity, settings.getSpawnSettings(), settings.getUpdateSettings(), renderer, disposer);
    }

    public static DefaultParticleSystem create(int capacity, ParticleSpawnSettings spawnSettings,
                                               ParticleUpdateSettings updateSettings, Disposer disposer) {
        var vertexBuffer = InternalParticleBuffer.create(capacity, disposer);

        var updateBuffer = ParticleUpdateBuffer.create(capacity, disposer);

        return new DefaultParticleSystem(
                capacity,
                new EfficientParticleRenderer(),
                spawnSettings,
                updateSettings,
                vertexBuffer,
                updateBuffer);
    }

    public static DefaultParticleSystem create(int capacity, ParticleSpawnSettings spawnSettings,
                                               ParticleUpdateSettings updateSettings, ParticleRenderer renderer, Disposer disposer) {
        InternalParticleBuffer vertexBuffer = InternalParticleBuffer.create(capacity, disposer);

        ParticleUpdateBuffer updateBuffer = ParticleUpdateBuffer.create(capacity, disposer);

        return new DefaultParticleSystem(capacity, renderer, spawnSettings, updateSettings, vertexBuffer, updateBuffer);
    }

    public void setExternalSpeed(float x, float y, float z) {
        externalSpeed[0] = x;
        externalSpeed[1] = y;
        externalSpeed[2] = z;
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
    }

    public ParticleRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(ParticleRenderer renderer) {
        this.renderer = renderer;
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
     *
     * @param x The X-coordinate of the emitted particles.
     * @param y The Y-coordinate of the emitted particles.
     * @param z The Z-coordinate of the emitted particles.
     */
    public void emitAll(float x, float y, float z) {
        emitAll(x, y, z, Math.round(
                spawnSettings.getDefaultCount() + spawnSettings.getDefaultCountVar() * RandomInstance.random11()));
    }

    /**
     * Emits all particles at once at the specified coordinates.
     *
     * @param x     The X-coordinate of the emitted particles.
     * @param y     The Y-coordinate of the emitted particles.
     * @param z     The Z-coordinate of the emitted particles.
     * @param count The number of emitted particles.
     */
    public void emitAll(float x, float y, float z, int count) {
        int remaining = getCapacity() - particleCount;
        if (remaining < count) {
            count = remaining;
        }

        for (int i = 0; i < count; ++i) {
            emit(x, y, z);
        }
    }

    /**
     * Sequentially emits particles at the specified coordinates. The interval is
     * specified by {@link ParticleSpawnSettings#getDefaultInterval()}.
     *
     * @param x         The X-coordinate of the emitted particles.
     * @param y         The Y-coordinate of the emitted particles.
     * @param z         The Z-coordinate of the emitted particles.
     * @param time      The current emitter time, in seconds. The delta time will be
     *                  added to this value. Particles will be emitted while the
     *                  time exceeds the emitter interval.
     * @param deltaTime Time since the last update, in seconds.
     * @return The new emitter time, i.e. how much time has passed since a particle
     * was emitted.
     */
    public float emitSequential(float x, float y, float z, float time, float deltaTime) {
        return emitSequential(x, y, z, time, deltaTime, spawnSettings.getDefaultInterval());
    }

    /**
     * Sequentially emits particles at the specified coordinates.
     *
     * @param x         The X-coordinate of the emitted particles.
     * @param y         The Y-coordinate of the emitted particles.
     * @param z         The Z-coordinate of the emitted particles.
     * @param time      The current emitter time, in seconds. The delta time will be
     *                  added to this value. Particles will be emitted while the
     *                  time exceeds the emitter interval.
     * @param deltaTime Time since the last update, in seconds.
     * @param interval  The interval between emitted particles, in seconds.
     * @return The new emitter time, i.e. how much time has passed since a particle
     * was emitted.
     */
    public float emitSequential(float x, float y, float z, float time, float deltaTime, float interval) {
        if (interval > 0) {
            time += deltaTime;
            int iterations = (int) (time / interval);
            float remainingTime = time - (iterations * interval);
            int count = spawnSettings.isPulsating()
                    ? (spawnSettings.getDefaultCount() + spawnSettings.getDefaultCountVar()) * iterations
                    : iterations;
            emitAll(x, y, z, count);
            time = remainingTime;
        }

        return time;
    }

    /**
     * Emits a single particle at the specified coordinates.
     */
    public void emit(float x, float y, float z) {
        if (particleCount == getCapacity()) {
            // Particle capacity exceeded
            return;
        }

        Particle particle = spawnParticle();

        spawnSettings.emit(particle, x, y, z);

        updateSettings.apply(particle);

        particle.onEmitted(externalSpeed);
    }

    public void update(float deltaTime) {
        if (particleCount > 0) {
            for (int i = 0; i < particleCount; ++i)
                particles[i].update(externalAcceleration, deltaTime);

            int index = 0;

            while (index != particleCount) {
                Particle particle = particles[index];

                if (particle.hasExpired()) {
                    destroyParticle(index);
                    continue;
                }

                if (updateListener != null) {
                    final boolean compensateForPosTransform = positionTransform != null;
                    if (compensateForPosTransform) {
                        particle.setPosition(particle.getPosX() + positionTransform.getX(),
                                particle.getPosY() + positionTransform.getY(), particle.getPosZ());
                    }

                    if (!updateListener.updated(this, particle)) {
                        destroyParticle(index);
                        continue;
                    }

                    if (compensateForPosTransform) {
                        particle.setPosition(particle.getPosX() - positionTransform.getX(),
                                particle.getPosY() - positionTransform.getY(), particle.getPosZ());
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
                renderer.render(particles, vertexBuffer, gpuOutdated, 0, particleCount, alpha);
                ModelMatrix.instance().popMatrix();
            } else {
                renderer.render(particles, vertexBuffer, gpuOutdated, 0, particleCount, alpha);
            }

            gpuOutdated = false;
        }
    }

    public void clear() {
        for (int i = 0; i < particleCount; ++i) {
            ParticlePool.addLast(particles[i]);
            particles[i] = null;
        }
        particleCount = 0;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public int getCapacity() {
        return particles.length;
    }

    private Particle spawnParticle() {
        Particle particle = createParticle(particleCount);
        particles[particleCount] = particle;
        ++particleCount;
        return particle;
    }

    private Particle createParticle(int index) {
        return ParticlePool.isEmpty() ? new Particle(vertexBuffer, updateBuffer, index) : getFromPool(index);
    }

    private Particle getFromPool(int index) {
        Particle particle = ParticlePool.pollFirst();
        particle.setRenderBuffer(vertexBuffer);
        particle.setUpdateBuffer(updateBuffer);
        particle.setIndex(index);
        return particle;
    }

    private void destroyParticle(int index) {
        --particleCount;

        if (particleCount > 0) {
            int lastIndex = particleCount;

            vertexBuffer.copy(lastIndex, index, 1);
            updateBuffer.copy(lastIndex, index, 1);

            Particle last = particles[lastIndex];
            Particle current = particles[index];

            particles[index] = last;
            last.setIndex(index);

            ParticlePool.addLast(current);
        }
    }
}