package com.gamelibrary2d.particle.parameters;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;

public final class ParticleSystemParameters implements Serializable {
    private EmitterParameters emitterParameters;
    private PositionParameters positionParameters;
    private ParticleParameters particleParameters;

    public ParticleSystemParameters(
            EmitterParameters emitterParameters,
            PositionParameters positionParameters,
            ParticleParameters particleParameters) {
        this.emitterParameters = emitterParameters;
        this.positionParameters = positionParameters;
        this.particleParameters = particleParameters;
    }

    public ParticleSystemParameters(DataBuffer buffer) {
        this.particleParameters = new ParticleParameters(buffer);
        this.positionParameters = new PositionParameters(buffer);
        this.emitterParameters = new EmitterParameters(buffer);
    }

    public int estimateCapacityFromCount() {
        EmitterParameters emitterParameters = getEmitterParameters();
        return emitterParameters.getDefaultCount() + emitterParameters.getDefaultCountVar();
    }

    public int estimateCapacityFromInterval() {
        float maxLife = getParticleParameters().getLife() + getParticleParameters().getLifeVar();

        EmitterParameters emitterParameters = getEmitterParameters();

        int particleCount = emitterParameters.isPulsating()
                ? emitterParameters.getDefaultCount() + emitterParameters.getDefaultCountVar()
                : 1;

        return (int) Math.ceil(1.2f * maxLife * particleCount / emitterParameters.getDefaultInterval());
    }

    public int estimateCapacity() {
        return Math.max(
                estimateCapacityFromCount(),
                estimateCapacityFromInterval());
    }

    public EmitterParameters getEmitterParameters() {
        return emitterParameters;
    }

    public void setEmitterParameters(EmitterParameters emitterParameters) {
        this.emitterParameters = emitterParameters;
    }

    public PositionParameters getPositionParameters() {
        return positionParameters;
    }

    public void setPositionParameters(PositionParameters positionParameters) {
        this.positionParameters = positionParameters;
    }

    public ParticleParameters getParticleParameters() {
        return particleParameters;
    }

    public void setParticleParameters(ParticleParameters particleParameters) {
        this.particleParameters = particleParameters;
    }

    @Override
    public void serialize(DataBuffer buffer) {
        particleParameters.serialize(buffer);
        positionParameters.serialize(buffer);
        emitterParameters.serialize(buffer);
    }
}