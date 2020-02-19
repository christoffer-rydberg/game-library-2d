package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.objects.Slider;
import com.gamelibrary2d.particle.settings.ParticleSettingsSaveLoadManager;
import com.gamelibrary2d.particle.settings.ParticleSpawnSettings;
import com.gamelibrary2d.particle.settings.ParticleUpdateSettings;
import com.gamelibrary2d.particle.settings.ParticleSettingsUtils;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.util.RenderSettings;

class ResizeSlider extends Slider {
    private ParticleSpawnSettings originalSpawnSettings;
    private ParticleUpdateSettings originalUpdateSettings;

    private ResizeSlider(Renderer handle, ParticleSystemModel particleSystem) {
        super(handle, SliderDirection.HORIZONTAL, -50, 50, 3);
        var saveLoadManager = new ParticleSettingsSaveLoadManager();
        addDragBeginListener(value -> {
            originalSpawnSettings = saveLoadManager.clone(particleSystem.getSpawnSettings());
            originalUpdateSettings = saveLoadManager.clone(particleSystem.getUpdateSettings());
        });
        addValueChangedListener(value -> {
            float resizeValue = (value < 0 ? value : value * 2) + 100f;
            ParticleSettingsUtils.scaleSpawnSettings(originalSpawnSettings, resizeValue * 0.01f, particleSystem.getSpawnSettings());
            ParticleSettingsUtils.scaleUpdateSettings(originalUpdateSettings, resizeValue * 0.01f, particleSystem.getUpdateSettings());
        });
        addDragStopListener(v -> setValue(0, false));

        setBounds(Rectangle.centered(handle.getBounds().width() + 200, handle.getBounds().height()));
    }

    static ResizeSlider create(ParticleSystemModel particleSystem, Disposer disposer) {
        var quad = Quad.create(Rectangle.centered(16, 32), disposer);
        var handle = new SurfaceRenderer(quad);
        handle.updateSettings(RenderSettings.COLOR_R, 0.75f, 0.75f, 0);
        return new ResizeSlider(handle, particleSystem);
    }
}
