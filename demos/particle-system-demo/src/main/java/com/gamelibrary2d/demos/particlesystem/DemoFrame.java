package com.gamelibrary2d.demos.particlesystem;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.LoadingContext;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.settings.BasicSpawnSettings;
import com.gamelibrary2d.particle.settings.ParticleSettingsSaveLoadManager;
import com.gamelibrary2d.particle.settings.ParticleSpawnSettings;
import com.gamelibrary2d.particle.settings.ParticleUpdateSettings;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updates.EmptyUpdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DemoFrame extends AbstractFrame {
    private List<SequentialParticleEmitter> emitters = new ArrayList<>();
    private DefaultParticleSystem fireSystem;
    private DefaultParticleSystem explosionSystem;

    DemoFrame(Game game) {
        super(game);
    }

    private ParticleSpawnSettings createSpawnSettings() {
        var spawnSettings = new BasicSpawnSettings();
        spawnSettings.setDefaultInterval(1f / 350f);
        spawnSettings.setPositionVar(75f, 25f, 0);
        return spawnSettings;
    }

    private ParticleUpdateSettings createUpdateSettings() {
        var updateSettings = new ParticleUpdateSettings();
        updateSettings.setLife(1.5f);
        updateSettings.setLifeVar(0.5f);
        updateSettings.setDelay(0.25f);
        updateSettings.setDelayVar(0.25f);
        updateSettings.setSpeed(250f);
        updateSettings.setSpeedVar(100f);
        updateSettings.setAccelerationX(150f);
        updateSettings.setAccelerationXVar(100f);
        updateSettings.setScale(2.5f, 2.5f);
        updateSettings.setScaleVar(2.4f);
        updateSettings.setColor(175f, 50f, 30f);
        updateSettings.setUpdateColor(true);
        updateSettings.setEndColor(127.5f, 127.5f, 127.5f);
        updateSettings.setAlpha(0.75f);
        updateSettings.setAlphaVar(0.25f);
        return updateSettings;
    }

    @Override
    protected void onInitialize() {
        try {
            // Example of particle system created from code:
            fireSystem = DefaultParticleSystem.create(
                    10000, // Particles won't spawn if capacity is exceeded.
                    createSpawnSettings(),
                    createUpdateSettings(),
                    this);

            // Example of particle system loaded from file:
            explosionSystem = DefaultParticleSystem.create(
                    300,
                    new ParticleSettingsSaveLoadManager().load(
                            getClass().getClassLoader().getResource("explosion.particle")),
                    this);

            add(fireSystem);
            add(explosionSystem);
        } catch (IOException e) {
            e.printStackTrace();
            getGame().exit();
        }
    }

    @Override
    protected void onLoad(LoadingContext context) throws InitializationException {

    }

    @Override
    protected void onLoaded(LoadingContext context) {

    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }

    private void createFire(float posX, float posY, float delay) {
        var updater = new SequentialUpdater();
        updater.add(new DurationUpdater(new EmptyUpdate(), delay));
        updater.add(new InstantUpdater((dt, sdt) -> {
            emitters.add(new SequentialParticleEmitter(fireSystem, posX, posY));
        }));
        runUpdater(updater);
    }

    @Override
    protected boolean handleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        explosionSystem.emitAll(projectedX, projectedY, 0);
        createFire(projectedX, projectedY, 1f);
        return true;
    }

    @Override
    public void onUpdate(float deltaTime) {
        for (var emitter : emitters) {
            emitter.update(deltaTime);
        }
        super.onUpdate(deltaTime);
    }
}