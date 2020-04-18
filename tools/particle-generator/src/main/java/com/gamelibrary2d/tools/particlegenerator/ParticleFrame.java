package com.gamelibrary2d.tools.particlegenerator;

import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.markers.KeyAware;
import com.gamelibrary2d.particle.settings.ParticleSettingsUtils;
import com.gamelibrary2d.particle.systems.ParticleSystem;
import com.gamelibrary2d.tools.particlegenerator.panels.emitter.EmitterPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.particlesettings.*;
import com.gamelibrary2d.tools.particlegenerator.panels.renderSettings.RenderSettingsPanel;

public class ParticleFrame extends AbstractFrame implements KeyAware {

    private static final float WINDOW_MARGIN = 40;
    public static float PosX;
    public static float PosY;
    private final ParticleGenerator game;
    private int dragging = -1;
    private ParticleSystemModel particleSystem;

    private Layer<Renderable> screenLayer;
    private Layer<ParticleSystem> particleLayer;
    private Layer<Renderable> backgroundLayer;

    private EmitterPanel emitterPanel;

    private ParticleSettingsPanel particleSettingsPanel;

    private AbstractSpawnSettingsPanel spawnSettingsPanel;

    private BasicSpawnSettingsPanel basicSpawnSettingsPanel;

    private EllipsoidSpawnSettingsPanel ellipsoidSpawnSettingsPanel;

    private RenderSettingsPanel renderSettingsPanel;

    private SaveLoadResetPanel saveLoadResetPanel;

    private boolean interfaceHidden = false;

    private float particleEmitterTime;

    ParticleFrame(ParticleGenerator game) {
        this.game = game;
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        particleSystem = ParticleSystemModel.create(this);

        screenLayer = new BasicLayer<>();
        particleLayer = new BasicLayer<>();
        particleLayer.add(particleSystem.getDefaultParticleSystem());
        particleLayer.add(particleSystem.getShaderParticleSystem());
        backgroundLayer = new BasicLayer<>();

        particleSettingsPanel = new ParticleSettingsPanel(particleSystem);
        particleSettingsPanel.setPosition(WINDOW_MARGIN, game.getWindow().height() - WINDOW_MARGIN);

        basicSpawnSettingsPanel = new BasicSpawnSettingsPanel(this, particleSystem);
        basicSpawnSettingsPanel.setPosition(WINDOW_MARGIN,
                game.getWindow().height() - WINDOW_MARGIN - particleSettingsPanel.getBounds().height());

        ellipsoidSpawnSettingsPanel = new EllipsoidSpawnSettingsPanel(this, particleSystem);
        ellipsoidSpawnSettingsPanel.setPosition(WINDOW_MARGIN,
                game.getWindow().height() - WINDOW_MARGIN - particleSettingsPanel.getBounds().height());

        spawnSettingsPanel = basicSpawnSettingsPanel;

        renderSettingsPanel = new RenderSettingsPanel(particleSystem, game, this);
        renderSettingsPanel.setPosition(
                game.getWindow().width() - renderSettingsPanel.getBounds().width() - WINDOW_MARGIN,
                game.getWindow().height() - WINDOW_MARGIN);

        emitterPanel = new EmitterPanel(particleSystem);
        emitterPanel.setPosition(game.getWindow().width() - WINDOW_MARGIN,
                emitterPanel.getBounds().height() + WINDOW_MARGIN);

        saveLoadResetPanel = new SaveLoadResetPanel(particleSystem, game, this);
        saveLoadResetPanel.setPosition(WINDOW_MARGIN, WINDOW_MARGIN);
    }

    @Override
    protected void onLoad(InitializationContext context) {
        PosX = game.getWindow().width() / 2f;
        PosY = game.getWindow().height() / 2f;
        add(backgroundLayer);
        add(particleLayer);
        add(screenLayer);
        screenLayer.add(particleSettingsPanel);
        screenLayer.add(spawnSettingsPanel);
        screenLayer.add(renderSettingsPanel);
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
    protected void handleUpdate(float deltaTime) {
        super.handleUpdate(deltaTime);
        if (emitterPanel.isLaunchingSequential()) {
            particleEmitterTime = particleSystem.emitSequential(PosX, PosY, 0, particleEmitterTime, deltaTime);
        }
    }

    @Override
    protected boolean handleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        if (!super.handleMouseButtonDown(button, mods, projectedX, projectedY)) {
            if (dragging == -1) {
                dragging = button;
                PosX = projectedX;
                PosY = projectedY;
                return true;
            }

            return false;
        }

        return true;
    }

    @Override
    protected boolean handleMouseMove(float projectedX, float projectedY) {
        if (!super.handleMouseMove(projectedX, projectedY)) {
            if (dragging != -1) {
                PosX = projectedX;
                PosY = projectedY;
                return true;
            }

            return false;
        }

        return true;
    }

    @Override
    protected void handleMouseButtonRelease(int button, int mods, float projectedX, float projectedY) {
        if (button == dragging) {
            dragging = -1;
        }
        super.handleMouseButtonRelease(button, mods, projectedX, projectedY);
    }

    @Override
    public void onCharInput(char charInput) {

    }

    @Override
    public void onKeyDown(int key, int scanCode, boolean repeat, int mods) {
        if (key == Keyboard.instance().keyTab()) {
            // TODO: Focus next textbox
            return;
        }

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
    public void onKeyRelease(int key, int scanCode, int mods) {

    }

    public void changeSpawnSettings(SpawnSettingsType type) {
        switch (type) {
            case BASIC:
                screenLayer.remove(spawnSettingsPanel);
                particleSystem.setSpawnSettings(ParticleSettingsUtils.convertToBasic(particleSystem.getSpawnSettings()));
                spawnSettingsPanel = basicSpawnSettingsPanel;
                screenLayer.add(spawnSettingsPanel);
                break;
            case ELLIPSOID:
                screenLayer.remove(spawnSettingsPanel);
                particleSystem.setSpawnSettings(ParticleSettingsUtils.convertToEllipsoid(particleSystem.getSpawnSettings()));
                spawnSettingsPanel = ellipsoidSpawnSettingsPanel;
                screenLayer.add(spawnSettingsPanel);
                break;
        }
    }
}