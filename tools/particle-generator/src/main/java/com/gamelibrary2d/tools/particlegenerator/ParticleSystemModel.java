package com.gamelibrary2d.tools.particlegenerator;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.particle.systems.DefaultShaderParticleSystem;

public class ParticleSystemModel {
    private final DefaultParticleSystem defaultParticleSystem;
    private final DefaultShaderParticleSystem shaderParticleSystem;

    private boolean updatingOnGpu;
    private ParticleSystemSettings settings;

    public ParticleSystemModel(
            DefaultParticleSystem defaultSystem,
            DefaultShaderParticleSystem shaderSystem,
            ParticleSystemSettings settings) {
        this.defaultParticleSystem = defaultSystem;
        this.shaderParticleSystem = shaderSystem;
        this.settings = settings;
    }

    static ParticleSystemModel create(Disposer disposer) {
        var settings = new ParticleSystemSettings(new ParticlePositioner(), new ParticleParameters());
        var defaultSystem = DefaultParticleSystem.create(1000000, settings, disposer);
        var acceleratedSystem = DefaultShaderParticleSystem.create(1000000, settings, disposer);
        return new ParticleSystemModel(defaultSystem, acceleratedSystem, settings);
    }

    DefaultParticleSystem getDefaultParticleSystem() {
        return defaultParticleSystem;
    }

    DefaultShaderParticleSystem getShaderParticleSystem() {
        return shaderParticleSystem;
    }

    public ParticleParameters getUpdateSettings() {
        return settings.getParticleParameters();
    }

    public void setUpdateSettings(ParticleParameters updateSettings) {
        settings.setParticleParameters(updateSettings);
    }

    public ParticlePositioner getSpawnSettings() {
        return settings.getParticlePositioner();
    }

    public void setSpawnSettings(ParticlePositioner spawnSettings) {
        settings.setParticlePositioner(spawnSettings);
    }

    public ParticleRenderer getRenderer() {
        return settings.getRenderer();
    }

    public void setRenderer(ParticleRenderer renderer) {
        this.settings.setRenderer(renderer);
    }

    float emitSequential(float x, float y, float time, float deltaTime) {
        if (updatingOnGpu) {
            shaderParticleSystem.setPosition(x, y);
            return shaderParticleSystem.emitSequential(time, deltaTime);
        } else {
            return defaultParticleSystem.emitSequential(x, y, time, deltaTime);
        }
    }

    public void emit(float x, float y) {
        if (updatingOnGpu) {
            shaderParticleSystem.setPosition(x, y);
            shaderParticleSystem.emit();
        } else {
            defaultParticleSystem.emit(x, y);
        }
    }

    public void emitAll(float x, float y) {
        if (updatingOnGpu) {
            shaderParticleSystem.setPosition(x, y);
            shaderParticleSystem.emitAll();
        } else {
            defaultParticleSystem.emitAll(x, y);
        }
    }

    public boolean isUpdatingOnGpu() {
        return updatingOnGpu;
    }

    public void setUpdatingOnGpu(boolean updatingOnGpu) {
        this.updatingOnGpu = updatingOnGpu;
    }

    public ParticleSystemSettings getSettings() {
        return settings;
    }

    public void setSettings(ParticleSystemSettings settings) {
        this.settings = settings;
        defaultParticleSystem.setSettings(settings);
        shaderParticleSystem.setSettings(settings);
    }
}