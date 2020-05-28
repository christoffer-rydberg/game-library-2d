package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.util.RenderSettings;
import com.gamelibrary2d.widgets.Slider;

class ResizeSlider extends Slider {
    private ParticlePositioner originalParticlePositioner;
    private ParticleParameters originalParticleParameters;

    private ResizeSlider(Renderer handle, ParticleSystemModel particleSystem) {
        super(handle, SliderDirection.HORIZONTAL, -50, 50, 3);
        var saveLoadManager = new SaveLoadManager();
        addDragBeginListener(value -> {
            originalParticlePositioner = saveLoadManager.clone(particleSystem.getSpawnSettings(), ParticlePositioner::new);
            originalParticleParameters = saveLoadManager.clone(particleSystem.getUpdateSettings(), ParticleParameters::new);
        });
        addValueChangedListener(value -> {
            float resizeValue = ((value < 0 ? value : value * 2) + 100f) * 0.01f;
            var updatedParticlePositioner = saveLoadManager.clone(originalParticlePositioner, ParticlePositioner::new);
            var updatedParticleParameters = saveLoadManager.clone(originalParticleParameters, ParticleParameters::new);

            updatedParticlePositioner.scale(resizeValue);
            updatedParticleParameters.scale(resizeValue);

            particleSystem.setSpawnSettings(updatedParticlePositioner);
            particleSystem.setUpdateSettings(updatedParticleParameters);
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
