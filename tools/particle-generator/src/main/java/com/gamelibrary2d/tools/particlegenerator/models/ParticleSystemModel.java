package com.gamelibrary2d.tools.particlegenerator.models;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.particle.parameters.EmitterParameters;
import com.gamelibrary2d.particle.parameters.ParticleParameters;
import com.gamelibrary2d.particle.parameters.ParticleSystemParameters;
import com.gamelibrary2d.particle.parameters.PositionParameters;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.systems.AcceleratedParticleSystem;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.particle.systems.ParticleSystem;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.util.BlendMode;

import java.io.IOException;
import java.net.URL;

public class ParticleSystemModel {
    private final Frame frame;
    private final DefaultParticleSystem defaultParticleSystem;
    private final AcceleratedParticleSystem acceleratedParticleSystem;

    private final EfficientParticleRenderer efficientRenderer;
    private final SequentialRendererModel sequentialRenderer;

    private final Disposer textureDisposer;


    private float posX;
    private float posY;
    private BlendMode blendMode;
    private ParticleSystemParameters settings;
    private ParticleSystemType particleSystemType = ParticleSystemType.EFFICIENT;
    private Rectangle bounds;

    private ParticleSystemModel(Frame frame) {
        this.frame = frame;
        textureDisposer = new DefaultDisposer(frame);

        efficientRenderer = new EfficientParticleRenderer();

        settings = new ParticleSystemParameters(
                new EmitterParameters(),
                new PositionParameters(),
                new ParticleParameters());

        defaultParticleSystem = DefaultParticleSystem.create(settings, efficientRenderer, frame);
        acceleratedParticleSystem = AcceleratedParticleSystem.create(settings, efficientRenderer, 10000000, frame);

        sequentialRenderer = new SequentialRendererModel(
                frame,
                defaultParticleSystem,
                efficientRenderer.getBounds());

        updateParticleSize(
                efficientRenderer.getBounds().width(),
                efficientRenderer.getBounds().height());

        setBlendMode(BlendMode.ADDITIVE);
    }

    public static ParticleSystemModel create(Frame frame) {
        return new ParticleSystemModel(frame);
    }

    public void setPosition(float x, float y) {
        this.posX = x;
        this.posY = y;
    }

    public void reset() {
        setSettings(new ParticleSystemParameters(
                new EmitterParameters(),
                new PositionParameters(),
                new ParticleParameters()));
    }

    public void removeTexture() {
        efficientRenderer.setTexture(null);
        sequentialRenderer.setTexture(null);
    }

    public void loadTexture(URL url) throws IOException {
        textureDisposer.dispose();
        if (url.getPath().endsWith(".gif")) {
            var animation = sequentialRenderer.setAnimation(url);
            efficientRenderer.setTexture(
                    animation.getFrame(0).getTexture()
            );
        } else {
            var texture = DefaultTexture.create(url, textureDisposer);
            sequentialRenderer.setTexture(texture);
            efficientRenderer.setTexture(texture);
        }
    }

    public EfficientParticleRenderer getEfficientRenderer() {
        return efficientRenderer;
    }

    public ParticleSystemType getParticleSystemType() {
        return particleSystemType;
    }

    public void setParticleSystemType(ParticleSystemType particleSystemType) {
        frame.invokeLater(() -> {
            this.particleSystemType = particleSystemType;
            switch (particleSystemType) {
                case SEQUENTIAL:
                    defaultParticleSystem.setRenderer(sequentialRenderer.getRenderer());
                    break;
                case EFFICIENT:
                    defaultParticleSystem.setRenderer(efficientRenderer);
                    break;
            }
        });
    }

    public ParticleParameters getParameters() {
        return settings.getParticleParameters();
    }

    public void setParameters(ParticleParameters updateSettings) {
        settings.setParticleParameters(updateSettings);
    }

    public PositionParameters getPositioner() {
        return settings.getPositionParameters();
    }

    public void setPositionParameters(PositionParameters positionParameters) {
        settings.setPositionParameters(positionParameters);
    }

    public float emitSequential(float time, float deltaTime) {
        switch (particleSystemType) {
            case ACCELERATED:
                acceleratedParticleSystem.setPosition(posX, posY);
                return acceleratedParticleSystem.emitSequential(time, deltaTime);
            case EFFICIENT:
            case SEQUENTIAL:
                return defaultParticleSystem.emitSequential(posX, posY, time, deltaTime);
            default:
                throw new IllegalStateException("Unexpected value: " + particleSystemType);
        }
    }

    public void emit() {
        switch (particleSystemType) {
            case ACCELERATED:
                acceleratedParticleSystem.setPosition(posX, posY);
                acceleratedParticleSystem.emit();
                break;
            case EFFICIENT:
            case SEQUENTIAL:
                defaultParticleSystem.emit(posX, posY);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + particleSystemType);
        }
    }

    public void emitAll() {
        switch (particleSystemType) {
            case ACCELERATED:
                acceleratedParticleSystem.setPosition(posX, posY);
                acceleratedParticleSystem.emitAll();
                break;
            case EFFICIENT:
            case SEQUENTIAL:
                defaultParticleSystem.emitAll(posX, posY);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + particleSystemType);
        }
    }

    public ParticleSystemParameters getSettings() {
        return settings;
    }

    public void setSettings(ParticleSystemParameters settings) {
        this.settings = settings;
        defaultParticleSystem.setSettings(settings);
        acceleratedParticleSystem.setParameters(settings);
    }

    public void addToLayer(Layer<ParticleSystem> layer) {
        layer.add(defaultParticleSystem);
        layer.add(acceleratedParticleSystem);
    }

    public float getWidth() {
        return bounds.width();
    }

    public void setWidth(float width) {
        if (getWidth() != width) {
            updateParticleSize(width, getHeight());
        }
    }

    public float getHeight() {
        return bounds.height();
    }

    public void setHeight(float height) {
        if (getHeight() != height) {
            updateParticleSize(getWidth(), height);
        }
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        if (this.blendMode != blendMode) {
            this.blendMode = blendMode;
            efficientRenderer.setBlendMode(blendMode);
            sequentialRenderer.setBlendMode(blendMode);
        }
    }

    private void updateParticleSize(float width, float height) {
        bounds = Rectangle.centered(width, height);
        efficientRenderer.setBounds(bounds);
        sequentialRenderer.setBounds(bounds);
    }

    public enum ParticleSystemType {
        SEQUENTIAL,
        EFFICIENT,
        ACCELERATED
    }
}