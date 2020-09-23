package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;

public final class ParticleSystemSettings implements Serializable {
    private ParticlePositioner particlePositioner;
    private ParticleParameters particleParameters;
    private ParticleRenderer renderer;

    private int defaultCount = 1;
    private int defaultCountVar = 0;
    private float defaultInterval = 1;
    private boolean pulsating;
    private float offsetX;
    private float offsetXVar;
    private float offsetY;
    private float offsetYVar;

    public ParticleSystemSettings(ParticlePositioner particlePositioner, ParticleParameters particleParameters,
                                  ParticleRenderer renderer) {
        this.particlePositioner = particlePositioner;
        this.particleParameters = particleParameters;
        this.renderer = renderer;
    }

    public ParticleSystemSettings(ParticlePositioner particlePositioner, ParticleParameters particleParameters) {
        this(particlePositioner, particleParameters, new EfficientParticleRenderer());
    }

    public ParticleSystemSettings(DataBuffer buffer) {
        this(buffer, new EfficientParticleRenderer());
    }

    public ParticleSystemSettings(DataBuffer buffer, ParticleRenderer renderer) {
        this.particleParameters = new ParticleParameters(buffer);
        this.particlePositioner = new ParticlePositioner(buffer);
        defaultCount = buffer.getInt();
        defaultCountVar = buffer.getInt();
        defaultInterval = buffer.getFloat();
        pulsating = buffer.getBool();
        offsetX = buffer.getFloat();
        offsetXVar = buffer.getFloat();
        offsetY = buffer.getFloat();
        offsetYVar = buffer.getFloat();
        this.renderer = renderer;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetXVar() {
        return offsetXVar;
    }

    public void setOffsetXVar(float offsetXVar) {
        this.offsetXVar = offsetXVar;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetYVar() {
        return offsetYVar;
    }

    public void setOffsetYVar(float offsetYVar) {
        this.offsetYVar = offsetYVar;
    }

    public int getDefaultCount() {
        return defaultCount;
    }

    public void setDefaultCount(int defaultCount) {
        this.defaultCount = defaultCount;
    }

    public int getDefaultCountVar() {
        return defaultCountVar;
    }

    public void setDefaultCountVar(int defaultCountVar) {
        this.defaultCountVar = defaultCountVar;
    }

    /**
     * @return The default interval, in seconds, between sequentially emitted particles.
     */
    public float getDefaultInterval() {
        return defaultInterval;
    }

    /**
     * Sets the {@link #getDefaultInterval default interval}.
     *
     * @param defaultInterval
     */
    public void setDefaultInterval(float defaultInterval) {
        this.defaultInterval = defaultInterval;
    }

    /**
     * @return True if all particles, specified by {@link #getDefaultCount() getCount},
     * are emitted at the interval specified by {@link #getDefaultInterval() getInterval},
     * when launching sequentially. If false, only one particle gets emitted at each interval.
     */
    public boolean isPulsating() {
        return pulsating;
    }

    /**
     * Sets the {@link #isPulsating() pulsating} field.
     */
    public void setPulsating(boolean pulsating) {
        this.pulsating = pulsating;
    }

    public ParticlePositioner getParticlePositioner() {
        return particlePositioner;
    }

    public void setParticlePositioner(ParticlePositioner particlePositioner) {
        this.particlePositioner = particlePositioner;
    }

    public ParticleParameters getParticleParameters() {
        return particleParameters;
    }

    public void setParticleParameters(ParticleParameters particleParameters) {
        this.particleParameters = particleParameters;
    }

    public ParticleRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(ParticleRenderer particleRenderer) {
        this.renderer = particleRenderer;
    }

    @Override
    public void serialize(DataBuffer buffer) {
        particleParameters.serialize(buffer);
        particlePositioner.serialize(buffer);
        buffer.putInt(defaultCount);
        buffer.putInt(defaultCountVar);
        buffer.putFloat(defaultInterval);
        buffer.putBool(pulsating);
        buffer.putFloat(offsetX);
        buffer.putFloat(offsetXVar);
        buffer.putFloat(offsetY);
        buffer.putFloat(offsetYVar);
    }
}