package com.gamelibrary2d.tools.particlegenerator;

import com.gamelibrary2d.framework.Keyboard;
import com.gamelibrary2d.objects.AbstractFrame;
import com.gamelibrary2d.objects.FrameLayer;
import com.gamelibrary2d.particle.settings.SettingsExtensions;
import com.gamelibrary2d.tools.particlegenerator.panels.emitter.EmitterPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.particlesettings.*;
import com.gamelibrary2d.tools.particlegenerator.panels.renderSettings.RenderSettingsPanel;

public class ParticleFrame extends AbstractFrame {

    public static float PosX;
    public static float PosY;
    private final float WINDOW_MARGIN = 40;
    private int dragging = -1;

    private ParticleSystemModel particleSystem;

    private FrameLayer screenLayer;
    private FrameLayer worldLayer;
    private FrameLayer backgroundLayer;

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
    protected void onPrepare() {

        particleSystem = ParticleSystemModel.create(this);

        screenLayer = new FrameLayer();
        worldLayer = new FrameLayer();
        worldLayer.foregroundParticles().add(particleSystem.getDefaultParticleSystem());
        worldLayer.foregroundParticles().add(particleSystem.getShaderParticleSystem());
        backgroundLayer = new FrameLayer();

        particleSettingsPanel = new ParticleSettingsPanel(particleSystem);
        particleSettingsPanel.getPosition().set(WINDOW_MARGIN, getGame().getWindow().getHeight() - WINDOW_MARGIN);

        basicSpawnSettingsPanel = new BasicSpawnSettingsPanel(this, particleSystem);
        basicSpawnSettingsPanel.getPosition().set(WINDOW_MARGIN,
                getGame().getWindow().getHeight() - WINDOW_MARGIN - particleSettingsPanel.getBounds().getHeight());

        ellipsoidSpawnSettingsPanel = new EllipsoidSpawnSettingsPanel(this, particleSystem);
        ellipsoidSpawnSettingsPanel.getPosition().set(WINDOW_MARGIN,
                getGame().getWindow().getHeight() - WINDOW_MARGIN - particleSettingsPanel.getBounds().getHeight());

        spawnSettingsPanel = basicSpawnSettingsPanel;

        renderSettingsPanel = new RenderSettingsPanel(particleSystem, this);
        renderSettingsPanel.getPosition().set(
                getGame().getWindow().getWidth() - renderSettingsPanel.getBounds().getWidth() - WINDOW_MARGIN,
                getGame().getWindow().getHeight() - WINDOW_MARGIN);

        emitterPanel = new EmitterPanel(particleSystem);
        emitterPanel.getPosition().set(getGame().getWindow().getWidth() - WINDOW_MARGIN,
                emitterPanel.getBounds().getHeight() + WINDOW_MARGIN);

        saveLoadResetPanel = new SaveLoadResetPanel(particleSystem, this);
        saveLoadResetPanel.getPosition().set(WINDOW_MARGIN, WINDOW_MARGIN);
    }

    @Override
    protected void onLoad() {
        PosX = getGame().getWindow().getWidth() / 2f;
        PosY = getGame().getWindow().getHeight() / 2f;
        add(backgroundLayer);
        add(worldLayer);
        add(screenLayer);
        screenLayer.add(particleSettingsPanel);
        screenLayer.add(spawnSettingsPanel);
        screenLayer.add(renderSettingsPanel);
        screenLayer.add(emitterPanel);
        screenLayer.add(saveLoadResetPanel);
    }

    @Override
    protected void onFinish() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBegin() {
    }

    @Override
    public void onEnd() {
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (emitterPanel.isLaunchingSequential()) {
            particleEmitterTime = particleSystem.emitSequential(PosX, PosY, 0, particleEmitterTime, deltaTime);
        }
    }

    @Override
    protected boolean onMouseClickEvent(int button, int mods, float projectedX, float projectedY) {
        if (!super.onMouseClickEvent(button, mods, projectedX, projectedY)) {
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
    protected boolean onMouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        if (!super.onMouseMoveEvent(projectedX, projectedY, drag)) {
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
    protected void onMouseReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        super.onMouseReleaseEvent(button, mods, projectedX, projectedY);
        if (button == dragging) {
            dragging = -1;
        }
    }

    @Override
    public void keyDownEvent(int key, int scanCode, boolean repeat, int mods) {
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

        super.keyDownEvent(key, scanCode, repeat, mods);
    }

    @Override
    protected void onReset() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDispose() {
        // TODO Auto-generated method stub
    }

    public void changeSpawnSettings(SpawnSettingsType type) {
        switch (type) {
            case BASIC:
                screenLayer.remove(spawnSettingsPanel);
                particleSystem.setSpawnSettings(SettingsExtensions.convertToBasic(particleSystem.getSpawnSettings()));
                spawnSettingsPanel = basicSpawnSettingsPanel;
                screenLayer.add(spawnSettingsPanel);
                break;
            case ELLIPSOID:
                screenLayer.remove(spawnSettingsPanel);
                particleSystem.setSpawnSettings(SettingsExtensions.convertToEllipsoid(particleSystem.getSpawnSettings()));
                spawnSettingsPanel = ellipsoidSpawnSettingsPanel;
                screenLayer.add(spawnSettingsPanel);
                break;
        }
    }
}