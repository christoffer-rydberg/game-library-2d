package com.gamelibrary2d.tools.particlegenerator.models;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.particle.renderers.EfficientParticleRenderer;
import com.gamelibrary2d.particle.renderers.ParticleRenderer;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.particle.systems.DefaultShaderParticleSystem;
import com.gamelibrary2d.particle.systems.ParticleSystem;
import com.gamelibrary2d.resources.DefaultTexture;
import com.gamelibrary2d.util.BlendMode;

import java.io.IOException;
import java.net.URL;

public class ParticleSystemModel {
    private final DefaultParticleSystem defaultParticleSystem;
    private final DefaultShaderParticleSystem shaderParticleSystem;

    private final EfficientParticleRenderer efficientRenderer;
    private final IterativeRendererModel iterativeRenderer;

    private final Disposer textureDisposer;

    private float posX;
    private float posY;
    private BlendMode blendMode;
    private ParticleSystemSettings settings;
    private ParticleSystemType particleSystemType = ParticleSystemType.EFFICIENT;
    private Rectangle bounds;

    private ParticleSystemModel(Disposer disposer) {
        textureDisposer = new DefaultDisposer(disposer);

        efficientRenderer = new EfficientParticleRenderer();

        settings = new ParticleSystemSettings(
                new ParticlePositioner(),
                new ParticleParameters(),
                efficientRenderer);

        defaultParticleSystem = DefaultParticleSystem.create(1000000, settings, disposer);
        shaderParticleSystem = DefaultShaderParticleSystem.create(1000000, settings, disposer);

        iterativeRenderer = new IterativeRendererModel(
                disposer,
                defaultParticleSystem,
                efficientRenderer.getBounds());

        updateParticleSize(
                efficientRenderer.getBounds().width(),
                efficientRenderer.getBounds().height());

        setBlendMode(BlendMode.ADDITIVE);
    }

    public static ParticleSystemModel create(Disposer disposer) {
        return new ParticleSystemModel(disposer);
    }

    public void setPosition(float x, float y) {
        this.posX = x;
        this.posY = y;
    }

    public void reset() {
        setSettings(new ParticleSystemSettings(
                new ParticlePositioner(),
                new ParticleParameters(),
                getRenderer()));
    }

    public void removeTexture() {
        efficientRenderer.setTexture(null);
        iterativeRenderer.setTexture(null);
    }

    public void loadTexture(URL url) throws IOException {
        textureDisposer.dispose();
        if (url.getPath().endsWith(".gif")) {
            var animation = iterativeRenderer.setAnimation(url);
            efficientRenderer.setTexture(
                    animation.getFrame(0).getTexture()
            );
        } else {
            var texture = DefaultTexture.create(url, textureDisposer);
            iterativeRenderer.setTexture(texture);
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
        this.particleSystemType = particleSystemType;
        switch (particleSystemType) {
            case ITERATIVE:
                defaultParticleSystem.setRenderer(iterativeRenderer.getRenderer());
                break;
            case EFFICIENT:
                defaultParticleSystem.setRenderer(efficientRenderer);
                break;
        }
    }

    public ParticleParameters getParameters() {
        return settings.getParticleParameters();
    }

    public void setParameters(ParticleParameters updateSettings) {
        settings.setParticleParameters(updateSettings);
    }

    public ParticlePositioner getPositioner() {
        return settings.getParticlePositioner();
    }

    public void setPositioner(ParticlePositioner positioner) {
        settings.setParticlePositioner(positioner);
    }

    public ParticleRenderer getRenderer() {
        return settings.getRenderer();
    }

    public float emitSequential(float time, float deltaTime) {
        switch (particleSystemType) {
            case EFFICIENT_GPU:
                shaderParticleSystem.setPosition(posX, posY);
                return shaderParticleSystem.emitSequential(time, deltaTime);
            case EFFICIENT:
            case ITERATIVE:
                return defaultParticleSystem.emitSequential(posX, posY, time, deltaTime);
            default:
                throw new IllegalStateException("Unexpected value: " + particleSystemType);
        }
    }

    public void emit() {
        switch (particleSystemType) {
            case EFFICIENT_GPU:
                shaderParticleSystem.setPosition(posX, posY);
                shaderParticleSystem.emit();
                break;
            case EFFICIENT:
            case ITERATIVE:
                defaultParticleSystem.emit(posX, posY);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + particleSystemType);
        }
    }

    public void emitAll() {
        switch (particleSystemType) {
            case EFFICIENT_GPU:
                shaderParticleSystem.setPosition(posX, posY);
                shaderParticleSystem.emitAll();
                break;
            case EFFICIENT:
            case ITERATIVE:
                defaultParticleSystem.emitAll(posX, posY);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + particleSystemType);
        }
    }

    public ParticleSystemSettings getSettings() {
        return settings;
    }

    public void setSettings(ParticleSystemSettings settings) {
        this.settings = settings;
        defaultParticleSystem.setSettings(settings);
        shaderParticleSystem.setSettings(settings);
    }

    public void addToLayer(Layer<ParticleSystem> layer) {
        layer.add(defaultParticleSystem);
        layer.add(shaderParticleSystem);
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
            iterativeRenderer.setBlendMode(blendMode);
        }
    }

    private void updateParticleSize(float width, float height) {
        bounds = Rectangle.centered(width, height);
        efficientRenderer.setBounds(bounds);
        iterativeRenderer.setBounds(bounds);
    }

    public enum ParticleSystemType {
        ITERATIVE,
        EFFICIENT,
        EFFICIENT_GPU
    }
}