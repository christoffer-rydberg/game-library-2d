package com.gamelibrary2d.particle.settings;

public final class ParticleSettings {

    private final ParticleSpawnSettings spawnSettings;

    private final ParticleUpdateSettings updateSettings;

    public ParticleSettings(ParticleSpawnSettings spawnSettings, ParticleUpdateSettings updateSettings) {
        this.spawnSettings = spawnSettings;
        this.updateSettings = updateSettings;
    }

    public ParticleSpawnSettings getSpawnSettings() {
        return spawnSettings;
    }

    public ParticleUpdateSettings getUpdateSettings() {
        return updateSettings;
    }
}