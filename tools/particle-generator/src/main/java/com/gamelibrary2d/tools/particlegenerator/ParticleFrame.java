package com.gamelibrary2d.tools.particlegenerator;

import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.particle.systems.ParticleSystem;
import com.gamelibrary2d.tools.particlegenerator.models.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.panels.EmitterPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.ParticleSystemSettingsPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.RenderingPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.SaveLoadResetPanel;

public class ParticleFrame extends AbstractFrame implements KeyAware {
    private static final float WINDOW_MARGIN = 40;
    private final ParticleGenerator game;
    private int dragging = -1;
    private ParticleSystemModel particleSystem;

    private Layer<Renderable> screenLayer;
    private Layer<ParticleSystem> particleLayer;
    private Layer<Renderable> backgroundLayer;

    private EmitterPanel emitterPanel;

    private ParticleSystemSettingsPanel settingsPanel;

    private RenderingPanel renderingPanel;

    private SaveLoadResetPanel saveLoadResetPanel;

    private boolean interfaceHidden = false;

    private float particleEmitterTime;

    ParticleFrame(ParticleGenerator game) {
        this.game = game;
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        screenLayer = new BasicLayer<>();
        particleLayer = new BasicLayer<>();

        particleSystem = ParticleSystemModel.create(this);
        particleSystem.addToLayer(particleLayer);

        backgroundLayer = new BasicLayer<>();

        settingsPanel = new ParticleSystemSettingsPanel(particleSystem, this);
        settingsPanel.setPosition(160f, game.getWindow().getHeight() - 20f);

        renderingPanel = new RenderingPanel(particleSystem);
        renderingPanel.setPosition(
                game.getWindow().getWidth() - renderingPanel.getBounds().getWidth() - WINDOW_MARGIN,
                game.getWindow().getHeight() - WINDOW_MARGIN);

        emitterPanel = new EmitterPanel(particleSystem);
        emitterPanel.setPosition(game.getWindow().getWidth() - WINDOW_MARGIN,
                emitterPanel.getBounds().getHeight() + WINDOW_MARGIN);

        saveLoadResetPanel = new SaveLoadResetPanel(particleSystem, game);
        saveLoadResetPanel.setPosition(WINDOW_MARGIN, WINDOW_MARGIN);
    }

    @Override
    protected void onLoad(InitializationContext context) {
        particleSystem.setPosition(
                game.getWindow().getWidth() / 2f,
                game.getWindow().getHeight() / 2f
        );
        add(backgroundLayer);
        add(particleLayer);
        add(screenLayer);
        screenLayer.add(settingsPanel);
        screenLayer.add(renderingPanel);
        screenLayer.add(emitterPanel);
        screenLayer.add(saveLoadResetPanel);
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

    @Override
    protected void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        if (emitterPanel.isLaunchingSequential()) {
            particleEmitterTime = particleSystem.emitSequential(particleEmitterTime, deltaTime);
        }
    }

    @Override
    protected boolean onMouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (!super.onMouseButtonDown(button, mods, x, y, projectedX, projectedY)) {
            if (dragging == -1) {
                dragging = button;
                particleSystem.setPosition(projectedX, projectedY);
                return true;
            }

            return false;
        }

        return true;
    }

    @Override
    protected boolean onMouseMove(float x, float y, float projectedX, float projectedY) {
        if (!super.onMouseMove(x, y, projectedX, projectedY)) {
            if (dragging != -1) {
                particleSystem.setPosition(projectedX, projectedY);
                return true;
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (button == dragging) {
            dragging = -1;
        }
        super.onMouseButtonReleased(button, mods, x, y, projectedX, projectedY);
    }

    @Override
    public void keyDown(int key, int scanCode, boolean repeat, int mods) {
        if (!repeat && key == Keyboard.instance().keyEscape()) {
            if (!interfaceHidden) {
                remove(screenLayer);
                interfaceHidden = true;
            } else {
                add(screenLayer);
                interfaceHidden = false;
            }
        }
    }

    @Override
    public void keyReleased(int key, int scanCode, int mods) {

    }
}