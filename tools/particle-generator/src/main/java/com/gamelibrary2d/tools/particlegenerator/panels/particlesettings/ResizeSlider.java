package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.objects.Slider;
import com.gamelibrary2d.particle.settings.ParticleSettingsSaveLoadManager;
import com.gamelibrary2d.particle.settings.ParticleSpawnSettings;
import com.gamelibrary2d.particle.settings.ParticleUpdateSettings;
import com.gamelibrary2d.particle.settings.SettingsExtensions;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;

public class ResizeSlider extends Slider {

    private final ParticleSystemModel particleSystem;

    private final ParticleSettingsSaveLoadManager saveLoadManager = new ParticleSettingsSaveLoadManager();

    private ParticleSpawnSettings originalSpawnSettings;

    private ParticleUpdateSettings originalUpdateSettings;

    ResizeSlider(ParticleSystemModel particleSystem, Disposer disposer) {
        this.particleSystem = particleSystem;
        setMinValueX(-100);
        setMaxValueX(100);
        setBounds(Rectangle.centered(200, 32));
        Surface quad = Quad.create(Rectangle.centered(16, 32), disposer);
        setLeverRenderer(new SurfaceRenderer(quad));
    }

    @Override
    protected void onDragStart(float posX, float posY) {
        originalSpawnSettings = saveLoadManager.clone(particleSystem.getSpawnSettings());
        originalUpdateSettings = saveLoadManager.clone(particleSystem.getUpdateSettings());
    }

    @Override
    protected void sliderValueChanged(float posX, float posY, float valueX, float valueY) {
        SettingsExtensions.scaleSpawnSettings(originalSpawnSettings, valueX * 0.01f, particleSystem.getSpawnSettings());
        SettingsExtensions.scaleUpdateSettings(originalUpdateSettings, valueX * 0.01f, particleSystem.getUpdateSettings());
    }

    @Override
    protected void onDragEnd(float posX, float posY) {
        setLeverPos(0, 0, false);
    }

    @Override
    public void charInputEvent(char charInput) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyDownEvent(int key, int scanCode, boolean repeat, int mods) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleaseEvent(int key, int scanCode, int mods) {
        // TODO Auto-generated method stub

    }
}