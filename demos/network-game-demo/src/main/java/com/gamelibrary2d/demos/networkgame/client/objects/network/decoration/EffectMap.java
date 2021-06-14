package com.gamelibrary2d.demos.networkgame.client.objects.network.decoration;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.ResourceReader;
import com.gamelibrary2d.demos.networkgame.client.ResourceManager;
import com.gamelibrary2d.demos.networkgame.client.objects.network.ClientObject;
import com.gamelibrary2d.demos.networkgame.client.urls.Particles;
import com.gamelibrary2d.demos.networkgame.common.ObjectTypes;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.containers.Layer;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.parameters.ParticleSystemParameters;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.sound.SoundPlayer;
import com.gamelibrary2d.resources.BlendMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectMap {
    private final ResourceManager resourceManager;
    private final SoundMap sounds;
    private final SoundPlayer soundPlayer;
    private final ResourceReader resourceReader = new ResourceReader();
    private final List<ParticleSystemItem> particleSystems = new ArrayList<>();
    private final Map<Byte, Map<Byte, InstantEffect>> destroyedEffects = new HashMap<>();
    private final Map<Byte, Map<Byte, Factory<DurationEffect>>> updateEffects = new HashMap<>();
    private final EfficientParticleRenderer defaultRenderer = new EfficientParticleRenderer();

    public EffectMap(ResourceManager resourceManager, SoundMap sounds, SoundPlayer soundPlayer) {
        this.resourceManager = resourceManager;
        this.sounds = sounds;
        this.soundPlayer = soundPlayer;
    }

    private DurationEffect createUpdateEffect(DefaultParticleSystem particleSystem) {
        SequentialParticleEmitter emitter = new SequentialParticleEmitter(particleSystem);
        return (obj, deltaTime) -> {
            emitter.getPosition().set(obj.getParticleHotspot());
            emitter.getPosition().rotate(obj.getRotation());
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
        int initialCapacity = simultaneousEffects * params.estimateCapacity();
        DefaultParticleSystem particleSystem = DefaultParticleSystem.create(params, renderer, initialCapacity, disposer);
        this.particleSystems.add(new ParticleSystemItem(particleSystem, scene));
        return particleSystem;
    }

    private ParticleSystemParameters loadParameters(String resource) throws IOException {
        return resourceManager.load(resource, s -> resourceReader.read(s, ParticleSystemParameters::new));
    }

    private void initializePlayerEffects(Disposer disposer) throws IOException {
        DefaultParticleSystem engineSystem = createParticleSystem(
                loadParameters(Particles.ENGINE),
                8,
                Scene.FOREGROUND,
                disposer);

        Map<Byte, Factory<DurationEffect>> updateEffects = new HashMap<>();
        updateEffects.put((byte) 0, () -> createUpdateEffect(engineSystem));
        this.updateEffects.put(ObjectTypes.PLAYER, updateEffects);

        DefaultParticleSystem explosionSystem = createParticleSystem(
                loadParameters(Particles.EXPLOSION),
                8,
                Scene.FOREGROUND,
                disposer);

        Map<Byte, InstantEffect> destroyedEffects = new HashMap<>();
        destroyedEffects.put((byte) 0, obj -> explosionSystem.emitAll(obj.getPosition()));
        this.destroyedEffects.put(ObjectTypes.PLAYER, destroyedEffects);
    }

    private void initializePortalEffects(Disposer disposer) throws IOException {
        DefaultParticleSystem portalSystem = createParticleSystem(
                loadParameters(Particles.PORTAL),
                1,
                Scene.BACKGROUND,
                disposer);

        Map<Byte, Factory<DurationEffect>> updateEffects = new HashMap<>();
        updateEffects.put((byte) 0, () -> createUpdateEffect(portalSystem));
        this.updateEffects.put(ObjectTypes.PORTAL, updateEffects);
    }

    private void initializeObstacleEffects(TextureMap textures, Disposer disposer) throws IOException {
        DefaultParticleSystem updateSystem = createParticleSystem(
                loadParameters(Particles.OBSTACLE),
                100,
                Scene.BACKGROUND,
                disposer);

        Map<Byte, Factory<DurationEffect>> updateEffects = new HashMap<>();
        for (Byte key : textures.getKeys(ObjectTypes.OBSTACLE)) {
            updateEffects.put(key, () -> createUpdateEffect(updateSystem));
        }
        this.updateEffects.put(ObjectTypes.OBSTACLE, updateEffects);

        DefaultParticleSystem shockwaveSystem = createParticleSystem(
                loadParameters(Particles.SHOCK_WAVE),
                8,
                Scene.FOREGROUND,
                disposer);

        Map<Byte, InstantEffect> destroyedEffects = new HashMap<>();
        this.destroyedEffects.put(ObjectTypes.OBSTACLE, destroyedEffects);

        ParticleSystemParameters destroyParams = loadParameters(Particles.OBSTACLE_EXPLOSION);
        for (Byte key : textures.getKeys(ObjectTypes.OBSTACLE)) {
            EfficientParticleRenderer particleRenderer = new EfficientParticleRenderer();
            particleRenderer.setBlendMode(BlendMode.TRANSPARENT);
            particleRenderer.setTexture(textures.getTexture(ObjectTypes.OBSTACLE, key));

            DefaultParticleSystem explosionSystem = createParticleSystem(
                    destroyParams,
                    particleRenderer,
                    destroyParams.estimateCapacity(),
                    Scene.FOREGROUND,
                    disposer);

            Object soundEffect = sounds.getDestroyedSound(ObjectTypes.OBSTACLE, key);

            destroyedEffects.put(key, obj -> {
                Point position = obj.getPosition();
                shockwaveSystem.emitAll(position);
                explosionSystem.emitAll(position);
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
        for (ParticleSystemItem particleSystem : particleSystems) {
            if (particleSystem.scene == Scene.BACKGROUND) {
                backgroundEffects.add(particleSystem.particleSystem);
            } else {
                foregroundEffects.add(particleSystem.particleSystem);
            }
        }
    }

    public InstantEffect getDestroyed(ClientObject obj) {
        Map<Byte, InstantEffect> effects = destroyedEffects.get(obj.getPrimaryType());
        if (effects != null) {
            byte secondaryType = obj.getSecondaryType();
            return secondaryType >= effects.size()
                    ? effects.get((byte) 0)
                    : effects.get(secondaryType);
        }

        return null;
    }

    public DurationEffect getUpdate(ClientObject obj) {
        Map<Byte, Factory<DurationEffect>> effects = updateEffects.get(obj.getPrimaryType());
        if (effects != null) {
            byte secondaryType = obj.getSecondaryType();
            Factory<DurationEffect> effectFactory = secondaryType >= effects.size()
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
