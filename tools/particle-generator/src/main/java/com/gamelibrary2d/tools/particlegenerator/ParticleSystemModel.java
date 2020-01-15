package com.gamelibrary2d.tools.particlegenerator;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;
import com.gamelibrary2d.particle.settings.BasicSpawnSettings;
import com.gamelibrary2d.particle.settings.ParticleSpawnSettings;
import com.gamelibrary2d.particle.settings.ParticleUpdateSettings;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.particle.systems.DefaultShaderParticleSystem;

public class ParticleSystemModel {

    private final DefaultParticleSystem defaultParticleSystem;

    private final DefaultShaderParticleSystem shaderParticleSystem;

    private ParticleSpawnSettings spawnSettings;

    private ParticleUpdateSettings updateSettings;

    private ParticleRenderer renderer;

    private boolean updatingOnGpu;

    public ParticleSystemModel(DefaultParticleSystem defaultSystem, DefaultShaderParticleSystem shaderSystem,
                               ParticleSpawnSettings spawnSettings, ParticleUpdateSettings updateSettings, ParticleRenderer renderer) {
        this.defaultParticleSystem = defaultSystem;
        this.shaderParticleSystem = shaderSystem;
        this.spawnSettings = spawnSettings;
        this.updateSettings = updateSettings;
        this.renderer = renderer;
    }

    static ParticleSystemModel create(Disposer disposer) {

        var spawnSettings = new BasicSpawnSettings();

        var updateSettings = new ParticleUpdateSettings();

        var renderer = new EfficientParticleRenderer();

        var defaultSystem = DefaultParticleSystem.create(1000000, spawnSettings, updateSettings, renderer, disposer);

        var acceleratedSystem = DefaultShaderParticleSystem.create(1000000, spawnSettings, updateSettings, renderer,
                disposer);

        return new ParticleSystemModel(defaultSystem, acceleratedSystem, spawnSettings, updateSettings, renderer);
    }

    DefaultParticleSystem getDefaultParticleSystem() {
        return defaultParticleSystem;
    }

    DefaultShaderParticleSystem getShaderParticleSystem() {
        return shaderParticleSystem;
    }

    public ParticleUpdateSettings getUpdateSettings() {
        return updateSettings;
    }

    public void setUpdateSettings(ParticleUpdateSettings updateSettings) {
        this.updateSettings = updateSettings;
        defaultParticleSystem.setUpdateSettings(updateSettings);
        shaderParticleSystem.setUpdateSettings(updateSettings);
    }

    public ParticleSpawnSettings getSpawnSettings() {
        return spawnSettings;
    }

    public void setSpawnSettings(ParticleSpawnSettings spawnSettings) {
        this.spawnSettings = spawnSettings;
        defaultParticleSystem.setSpawnSettings(spawnSettings);
        shaderParticleSystem.setSpawnSettings(spawnSettings);
    }

    public ParticleRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(ParticleRenderer renderer) {

        try {
            setRendererOnActiveSystem(updatingOnGpu, renderer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        this.renderer = renderer;
    }

    float emitSequential(float x, float y, int z, float time, float deltaTime) {
        if (updatingOnGpu) {
            shaderParticleSystem.setPosition(x, y, z);
            return shaderParticleSystem.emitSequential(time, deltaTime);
        } else {
            return defaultParticleSystem.emitSequential(x, y, z, time, deltaTime);
        }
    }

    public void emit(float x, float y, int z) {
        if (updatingOnGpu) {
            shaderParticleSystem.setPosition(x, y, z);
            shaderParticleSystem.emit();
        } else {
            defaultParticleSystem.emit(x, y, z);
        }
    }

    public void emitAll(float x, float y, int z) {
        if (updatingOnGpu) {
            shaderParticleSystem.setPosition(x, y, z);
            shaderParticleSystem.emitAll();
        } else {
            defaultParticleSystem.emitAll(x, y, z);
        }
    }

    public boolean isUpdatingOnGpu() {
        return updatingOnGpu;
    }

    public void setUpdatingOnGpu(boolean updatingOnGpu) {

        try {
            setRendererOnActiveSystem(updatingOnGpu, renderer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        this.updatingOnGpu = updatingOnGpu;
    }

    private void setRendererOnActiveSystem(boolean updatingOnGpu, ParticleRenderer renderer) {
        if (updatingOnGpu) {
            shaderParticleSystem.setRenderer((EfficientParticleRenderer) renderer);
        } else {
            defaultParticleSystem.setRenderer(renderer);
        }
    }
}