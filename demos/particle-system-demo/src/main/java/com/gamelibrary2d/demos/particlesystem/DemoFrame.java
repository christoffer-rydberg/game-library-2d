package com.gamelibrary2d.demos.particlesystem;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updates.EmptyUpdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DemoFrame extends AbstractFrame {
    private final Game game;
    private List<SequentialParticleEmitter> emitters = new ArrayList<>();
    private DefaultParticleSystem fireSystem;
    private DefaultParticleSystem explosionSystem;

    DemoFrame(Game game) {
        this.game = game;
    }

    private ParticlePositioner createSpawnSettings() {
        var spawnSettings = new ParticlePositioner();
        spawnSettings.setSpawnAreaWidthVar(75f);
        spawnSettings.setSpawnAreaHeightVar(25f);
        return spawnSettings;
    }

    private ParticleParameters createUpdateSettings() {
        var updateSettings = new ParticleParameters();
        updateSettings.setLife(1.5f);
        updateSettings.setLifeVar(0.5f);
        updateSettings.setDelay(0.25f);
        updateSettings.setDelayVar(0.25f);
        updateSettings.setSpeed(250f);
        updateSettings.setSpeedVar(100f);
        updateSettings.setHorizontalAcceleration(150f);
        updateSettings.setHorizontalAccelerationVar(100f);
        updateSettings.setScale(2.5f);
        updateSettings.setScaleVar(2.4f);
        updateSettings.setColor(175f, 50f, 30f);
        updateSettings.setUpdateColor(true);
        updateSettings.setEndColor(127.5f, 127.5f, 127.5f);
        updateSettings.setAlpha(0.75f);
        updateSettings.setAlphaVar(0.25f);
        return updateSettings;
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        try {
            // Example of particle system settings created from code:
            var fireSystemSettings = new ParticleSystemSettings(createSpawnSettings(), createUpdateSettings());
            fireSystemSettings.setDefaultInterval(1f / 350f);

            fireSystem = DefaultParticleSystem.create(10000, fireSystemSettings, this);

            // Example of particle system settings loaded from file:
            var explosionSystemSettings = new SaveLoadManager().load(
                    getClass().getClassLoader().getResource("explosion.particle"),
                    ParticleSystemSettings::new);

            explosionSystem = DefaultParticleSystem.create(300, explosionSystemSettings, this);

            add(fireSystem);
            add(explosionSystem);
        } catch (IOException e) {
            e.printStackTrace();
            game.exit();
        }
    }

    @Override
    protected void onLoad(InitializationContext context) throws InitializationException {

    }

    @Override
    protected void onLoaded(InitializationContext context) {

    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }

    private void createFire(float posX, float posY, float delay) {
        var updater = new SequentialUpdater();
        updater.add(new DurationUpdater(delay, new EmptyUpdate()));
        updater.add(new InstantUpdater(dt ->
                emitters.add(new SequentialParticleEmitter(fireSystem, posX, posY))));
        runUpdater(updater);
    }

    @Override
    protected boolean handleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        explosionSystem.emitAll(projectedX, projectedY);
        createFire(projectedX, projectedY, 1f);
        return true;
    }

    @Override
    public void handleUpdate(float deltaTime) {
        for (var emitter : emitters) {
            emitter.update(deltaTime);
        }
        super.handleUpdate(deltaTime);
    }
}