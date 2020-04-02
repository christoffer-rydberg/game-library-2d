package com.gamelibrary2d.tools.particlegenerator;

import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.LoadingContext;
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

    public static float PosX;
    public static float PosY;
    private final float WINDOW_MARGIN = 40;
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
        super(game);
    }

    @Override
    protected void onInitialize(FrameInitializer initializer) {
        particleSystem = ParticleSystemModel.create(this);

        screenLayer = new BasicLayer<>();
        particleLayer = new BasicLayer<>();
        particleLayer.add(particleSystem.getDefaultParticleSystem());
        particleLayer.add(particleSystem.getShaderParticleSystem());
        backgroundLayer = new BasicLayer<>();

        particleSettingsPanel = new ParticleSettingsPanel(particleSystem);
        particleSettingsPanel.getPosition().set(WINDOW_MARGIN, getGame().getWindow().height() - WINDOW_MARGIN);

        basicSpawnSettingsPanel = new BasicSpawnSettingsPanel(this, particleSystem);
        basicSpawnSettingsPanel.getPosition().set(WINDOW_MARGIN,
                getGame().getWindow().height() - WINDOW_MARGIN - particleSettingsPanel.getBounds().height());

        ellipsoidSpawnSettingsPanel = new EllipsoidSpawnSettingsPanel(this, particleSystem);
        ellipsoidSpawnSettingsPanel.getPosition().set(WINDOW_MARGIN,
                getGame().getWindow().height() - WINDOW_MARGIN - particleSettingsPanel.getBounds().height());

        spawnSettingsPanel = basicSpawnSettingsPanel;

        renderSettingsPanel = new RenderSettingsPanel(particleSystem, this);
        renderSettingsPanel.getPosition().set(
                getGame().getWindow().width() - renderSettingsPanel.getBounds().width() - WINDOW_MARGIN,
                getGame().getWindow().height() - WINDOW_MARGIN);

        emitterPanel = new EmitterPanel(particleSystem);
        emitterPanel.getPosition().set(getGame().getWindow().width() - WINDOW_MARGIN,
                emitterPanel.getBounds().height() + WINDOW_MARGIN);

        saveLoadResetPanel = new SaveLoadResetPanel(particleSystem, this);
        saveLoadResetPanel.getPosition().set(WINDOW_MARGIN, WINDOW_MARGIN);

        initializer.onLoad(this::onLoad);
    }

    private void onLoad(LoadingContext context) {
        PosX = getGame().getWindow().width() / 2f;
        PosY = getGame().getWindow().height() / 2f;
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
    protected void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
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

            return;
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