package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.glUtil.ModelMatrix;
import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.particle.ParticleUpdateListener;
import com.gamelibrary2d.particle.parameters.EmitterParameters;
import com.gamelibrary2d.particle.parameters.ParticleSystemParameters;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;

public class DefaultParticleSystem implements ParticleSystem, Clearable {
    private final float[] externalSpeed = new float[2];
    private final float[] externalAcceleration = new float[2];
    private final Particle particle;

    private Point positionTransformation;

    private int particleCount;
    private boolean gpuOutdated = true;
    private ParticleSystemParameters parameters;
    private ParticleRenderer renderer;
    private ParticleRenderBuffer renderBuffer;
    private ParticleUpdateBuffer updateBuffer;
    private ParticleUpdateListener updateListener;

    private DefaultParticleSystem(
            ParticleSystemParameters parameters,
            ParticleRenderer renderer,
            ParticleRenderBuffer renderBuffer,
            ParticleUpdateBuffer updateBuffer) {

        this.parameters = parameters;
        this.renderer = renderer;
        this.renderBuffer = renderBuffer;
        this.updateBuffer = updateBuffer;
        this.particle = new Particle(renderBuffer, updateBuffer, 0);
    }

    public static DefaultParticleSystem create(ParticleSystemParameters parameters, Disposer disposer) {
        return create(parameters, new EfficientParticleRenderer(), disposer);
    }

    public static DefaultParticleSystem create(
            ParticleSystemParameters parameters,
            ParticleRenderer renderer,
            Disposer disposer) {
        return create(parameters, renderer, ParticleSystemParameters.estimateCapacity(parameters), disposer);
    }

    public static DefaultParticleSystem create(
            ParticleSystemParameters parameters,
            int initialCapacity,
            Disposer disposer) {
        return create(parameters, new EfficientParticleRenderer(), initialCapacity, disposer);
    }

    public static DefaultParticleSystem create(
            ParticleSystemParameters parameters,
            ParticleRenderer renderer,
            int initialCapacity,
            Disposer disposer) {

        return new DefaultParticleSystem(
                parameters,
                renderer,
                ParticleRenderBuffer.create(initialCapacity, disposer),
                ParticleUpdateBuffer.create(initialCapacity, disposer));
    }

    public float getParticleTime(int index) {
        return updateBuffer.getTime(index * updateBuffer.getStride());
    }

    public void setExternalSpeed(float x, float y) {
        externalSpeed[0] = x;
        externalSpeed[1] = y;
    }

    public void setExternalAcceleration(float x, float y) {
        externalAcceleration[0] = x;
        externalAcceleration[1] = y;
    }

    public void setSettings(ParticleSystemParameters parameters) {
        this.parameters = parameters;
    }

    public ParticleSystemParameters getParameters() {
        return parameters;
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

    public Point getPositionTransformation() {
        return positionTransformation;
    }

    public void setPositionTransformation(Point positionTransformation) {
        this.positionTransformation = positionTransformation;
    }

    /**
     * Emits all particles at the specified coordinates. The particles count
     * is specified by {@link EmitterParameters#getDefaultCount()}.
     *
     * @param x The X-coordinate of the emitted particles.
     * @param y The Y-coordinate of the emitted particles.
     */
    public void emitAll(float x, float y) {
        var emitterParameters = parameters.getEmitterParameters();
        emit(x, y, Math.round(emitterParameters.getDefaultCount() + emitterParameters.getDefaultCountVar() * RandomInstance.random11()));
    }

    /**
     * Emits particles at the specified coordinates.
     *
     * @param x     The X-coordinate of the emitted particles.
     * @param y     The Y-coordinate of the emitted particles.
     * @param count The number of emitted particles.
     */
    public void emit(float x, float y, int count) {
        var updateParticleCount = particleCount + count;
        renderBuffer.ensureCapacity(updateParticleCount * renderBuffer.getStride());
        updateBuffer.ensureCapacity(updateParticleCount * updateBuffer.getStride());
        var emitterParameters = parameters.getEmitterParameters();
        x += emitterParameters.getOffsetX() + emitterParameters.getOffsetXVar() * RandomInstance.random11();
        y += emitterParameters.getOffsetY() + emitterParameters.getOffsetYVar() * RandomInstance.random11();
        for (int i = 0; i < count; ++i) {
            onEmit(x, y);
        }
    }

    /**
     * Sequentially emits particles at the specified coordinates. The interval is
     * specified by {@link EmitterParameters#getDefaultInterval()}.
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
        return emitSequential(x, y, time, deltaTime, parameters.getEmitterParameters().getDefaultInterval());
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

            var emitterParameters = parameters.getEmitterParameters();

            int count = emitterParameters.isPulsating()
                    ? Math.round(emitterParameters.getDefaultCount() + emitterParameters.getDefaultCountVar() * RandomInstance.random11())
                    : 1;

            var updateParticleCount = particleCount + iterations * count;
            renderBuffer.ensureCapacity(updateParticleCount * renderBuffer.getStride());
            updateBuffer.ensureCapacity(updateParticleCount * updateBuffer.getStride());
            for (int i = 0; i < iterations; ++i) {
                var emittedX = x + emitterParameters.getOffsetX() + emitterParameters.getOffsetXVar() * RandomInstance.random11();
                var emittedY = y + emitterParameters.getOffsetY() + emitterParameters.getOffsetYVar() * RandomInstance.random11();
                for (int j = 0; j < count; ++j) {
                    onEmit(emittedX, emittedY);
                }
            }

            time -= iterations * interval;
        }

        return time;
    }

    /**
     * Emits a single particle at the specified coordinates.
     */
    public void emit(float x, float y) {
        var emitterParameters = parameters.getEmitterParameters();
        x += emitterParameters.getOffsetX() + emitterParameters.getOffsetXVar() * RandomInstance.random11();
        y += emitterParameters.getOffsetY() + emitterParameters.getOffsetYVar() * RandomInstance.random11();
        renderBuffer.ensureCapacity((particleCount + 1) * renderBuffer.getStride());
        updateBuffer.ensureCapacity((particleCount + 1) * updateBuffer.getStride());
        onEmit(x, y);
    }

    private void onEmit(float x, float y) {
        particle.setIndex(particleCount++);

        particle.setInitialized(false);
        parameters.getPositionParameters().apply(particle, x, y);
        parameters.getParticleParameters().apply(particle);

        particle.setExternalSpeedX(externalSpeed[0]);
        particle.setExternalSpeedY(externalSpeed[1]);
        particle.setCustom(0);
    }

    private boolean isTransformingPosition() {
        return positionTransformation != null && (positionTransformation.getX() != 0f || positionTransformation.getY() != 0f);
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
                    final boolean compensateForPosTransform = isTransformingPosition();
                    if (compensateForPosTransform) {
                        particle.setPosition(particle.getPosX() + positionTransformation.getX(),
                                particle.getPosY() + positionTransformation.getY());
                    }

                    if (!updateListener.updated(this, particle)) {
                        destroyParticle(index);
                        continue;
                    }

                    if (compensateForPosTransform) {
                        particle.setPosition(particle.getPosX() - positionTransformation.getX(),
                                particle.getPosY() - positionTransformation.getY());
                    }
                }

                ++index;
            }

            gpuOutdated = true;
        }
    }

    public void render(float alpha) {
        if (particleCount > 0) {
            if (isTransformingPosition()) {
                ModelMatrix.instance().pushMatrix();
                ModelMatrix.instance().translatef(positionTransformation.getX(), positionTransformation.getY(), 0);
                renderer.render(renderBuffer, gpuOutdated, 0, particleCount, alpha);
                ModelMatrix.instance().popMatrix();
            } else {
                renderer.render(renderBuffer, gpuOutdated, 0, particleCount, alpha);
            }

            gpuOutdated = false;
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

    public int getParticleCount() {
        return particleCount;
    }

    private void destroyParticle(int index) {
        --particleCount;

        if (particleCount > 0) {
            int lastIndex = particleCount;
            renderBuffer.copy(lastIndex, index, 1);
            updateBuffer.copy(lastIndex, index, 1);
        }
    }
}