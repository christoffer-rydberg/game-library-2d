package com.gamelibrary2d.demos.networkgame.client.objects.network.decoration;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.demos.networkgame.client.objects.network.ClientObject;
import com.gamelibrary2d.demos.networkgame.client.urls.Particles;
import com.gamelibrary2d.demos.networkgame.common.ObjectTypes;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.parameters.ParticleSystemParameters;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.util.BlendMode;
import com.gamelibrary2d.util.sound.SoundEffectPlayer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectMap {
    private final SoundMap sounds;
    private final SoundEffectPlayer soundPlayer;
    private final SaveLoadManager saveLoadManager = new SaveLoadManager();
    private final List<ParticleSystemItem> particleSystems = new ArrayList<>();
    private final Map<Byte, Map<Byte, InstantEffect>> destroyedEffects = new HashMap<>();
    private final Map<Byte, Map<Byte, Factory<DurationEffect>>> updateEffects = new HashMap<>();
    private final EfficientParticleRenderer defaultRenderer = new EfficientParticleRenderer();

    public EffectMap(SoundMap sounds, SoundEffectPlayer soundPlayer) {
        this.sounds = sounds;
        this.soundPlayer = soundPlayer;
    }

    private ParticleSystemParameters loadParameters(URL url) throws IOException {
        return saveLoadManager.load(url, ParticleSystemParameters::new);
    }

    private DurationEffect createUpdateEffect(DefaultParticleSystem particleSystem) {
        var emitter = new SequentialParticleEmitter(particleSystem);
        return (obj, deltaTime) -> {
            emitter.getPosition().set(obj.getParticleHotspot());
            emitter.getPosition().rotate(obj.getDirection());
            emitter.getPosition().add(obj.getPosition());
            emitter.update(deltaTime);
        };
    }

    private DefaultParticleSystem createParticleSystem(
            ParticleSystemParameters params,
            int simultaneousEffects,
            Scene scene,
            Disposer disposer) {
        return createParticleSystem(
                params,
                defaultRenderer,
                simultaneousEffects,
                scene,
                disposer);
    }

    private DefaultParticleSystem createParticleSystem(
            ParticleSystemParameters params,
            ParticleRenderer renderer,
            int simultaneousEffects,
            Scene scene,
            Disposer disposer) {
        var initialCapacity = simultaneousEffects * params.estimateCapacity();
        var particleSystem = DefaultParticleSystem.create(params, renderer, initialCapacity, disposer);
        this.particleSystems.add(new ParticleSystemItem(particleSystem, scene));
        return particleSystem;
    }

    private void initializePlayerEffects(Disposer disposer) throws IOException {
        var engineSystem = createParticleSystem(
                loadParameters(Particles.ENGINE),
                8,
                Scene.FOREGROUND,
                disposer);

        var updateEffects = new HashMap<Byte, Factory<DurationEffect>>();
        updateEffects.put((byte) 0, () -> createUpdateEffect(engineSystem));
        this.updateEffects.put(ObjectTypes.PLAYER, updateEffects);

        var explosionSystem = createParticleSystem(
                loadParameters(Particles.EXPLOSION),
                8,
                Scene.FOREGROUND,
                disposer);

        var destroyedEffects = new HashMap<Byte, InstantEffect>();
        destroyedEffects.put((byte) 0, obj -> {
            var pos = obj.getPosition();
            explosionSystem.emitAll(pos.getX(), pos.getY());
        });
        this.destroyedEffects.put(ObjectTypes.PLAYER, destroyedEffects);
    }

    private void initializePortalEffects(Disposer disposer) throws IOException {
        var portalSystem = createParticleSystem(
                loadParameters(Particles.PORTAL),
                1,
                Scene.BACKGROUND,
                disposer);

        var updateEffects = new HashMap<Byte, Factory<DurationEffect>>();
        updateEffects.put((byte) 0, () -> createUpdateEffect(portalSystem));
        this.updateEffects.put(ObjectTypes.PORTAL, updateEffects);
    }

    private void initializeObstacleEffects(TextureMap textures, Disposer disposer) throws IOException {
        var updateSystem = createParticleSystem(
                loadParameters(Particles.OBSTACLE),
                100,
                Scene.BACKGROUND,
                disposer);

        var updateEffects = new HashMap<Byte, Factory<DurationEffect>>();
        for (var key : textures.getKeys(ObjectTypes.OBSTACLE)) {
            updateEffects.put(key, () -> createUpdateEffect(updateSystem));
        }
        this.updateEffects.put(ObjectTypes.OBSTACLE, updateEffects);

        var shockwaveSystem = createParticleSystem(
                loadParameters(Particles.SHOCK_WAVE),
                8,
                Scene.FOREGROUND,
                disposer);

        var destroyedEffects = new HashMap<Byte, InstantEffect>();
        this.destroyedEffects.put(ObjectTypes.OBSTACLE, destroyedEffects);

        var destroyParams = loadParameters(Particles.OBSTACLE_EXPLOSION);
        for (var key : textures.getKeys(ObjectTypes.OBSTACLE)) {
            var particleRenderer = new EfficientParticleRenderer();
            particleRenderer.setBlendMode(BlendMode.TRANSPARENT);
            particleRenderer.setTexture(textures.getTexture(ObjectTypes.OBSTACLE, key));

            var explosionSystem = createParticleSystem(
                    destroyParams,
                    particleRenderer,
                    destroyParams.estimateCapacity(),
                    Scene.FOREGROUND,
                    disposer);

            var soundEffect = sounds.getDestroyedSound(ObjectTypes.OBSTACLE, key);

            destroyedEffects.put(key, obj -> {
                var pos = obj.getPosition();
                shockwaveSystem.emitAll(pos.getX(), pos.getY());
                explosionSystem.emitAll(pos.getX(), pos.getY());
                if (soundEffect != null) {
                    soundPlayer.play(soundEffect, 0.5f);
                }
            });
        }
    }

    public void initialize(TextureMap textures, Disposer disposer) throws IOException {
        initializePlayerEffects(disposer);
        initializePortalEffects(disposer);
        initializeObstacleEffects(textures, disposer);
    }

    public void onLoaded(Layer<Renderable> backgroundEffects, Layer<Renderable> foregroundEffects) {
        for (var particleSystem : particleSystems) {
            if (particleSystem.scene == Scene.BACKGROUND) {
                backgroundEffects.add(particleSystem.particleSystem);
            } else {
                foregroundEffects.add(particleSystem.particleSystem);
            }
        }
    }

    public InstantEffect getDestroyed(ClientObject obj) {
        var effects = destroyedEffects.get(obj.getPrimaryType());
        if (effects != null) {
            var secondaryType = obj.getSecondaryType();
            return secondaryType >= effects.size()
                    ? effects.get((byte) 0)
                    : effects.get(secondaryType);
        }

        return null;
    }

    public DurationEffect getUpdate(ClientObject obj) {
        var effects = updateEffects.get(obj.getPrimaryType());
        if (effects != null) {
            var secondaryType = obj.getSecondaryType();
            var effectFactory = secondaryType >= effects.size()
                    ? effects.get((byte) 0)
                    : effects.get(secondaryType);

            return effectFactory != null ? effectFactory.create() : null;
        }

        return null;
    }

    private enum Scene {
        BACKGROUND,
        FOREGROUND
    }

    private static class ParticleSystemItem {
        private final DefaultParticleSystem particleSystem;
        private final Scene scene;

        ParticleSystemItem(DefaultParticleSystem particleSystem, Scene scene) {
            this.particleSystem = particleSystem;
            this.scene = scene;
        }
    }
}
