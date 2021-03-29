package com.gamelibrary2d.tools.particlegenerator.panels;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.markers.Parent;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.parameters.ParticleParameters;
import com.gamelibrary2d.particle.parameters.PositionParameters;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.tools.particlegenerator.models.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.resources.Textures;
import com.gamelibrary2d.tools.particlegenerator.widgets.Slider;

public class ParticleSystemSettingsPanel extends AbstractPanel<GameObject> implements Parent<GameObject> {
    public ParticleSystemSettingsPanel(ParticleSystemModel particleSystem, Disposer disposer) {
        PanelUtil.stack(this, new ParticleParametersPanel(particleSystem), 0f);
        PanelUtil.stack(this, new EmitterParametersPanel(particleSystem), PanelUtil.DEFAULT_STACK_MARGIN * 5);
        PanelUtil.stack(this, new PositionParametersPanel(particleSystem), PanelUtil.DEFAULT_STACK_MARGIN * 5);

        ResizeSlider slider = ResizeSlider.create(particleSystem, disposer);
        PanelUtil.stack(this, slider, PanelUtil.DEFAULT_STACK_MARGIN * 5);
    }

    private static class ResizeSlider extends Slider {
        private PositionParameters originalPositionParameters;
        private ParticleParameters originalParticleParameters;

        private ResizeSlider(Renderer handle, ParticleSystemModel particleSystem) {
            super(handle, SliderDirection.HORIZONTAL, -50, 50, 2);
            SaveLoadManager saveLoadManager = new SaveLoadManager();
            addDragBeginListener(value -> {
                originalPositionParameters = saveLoadManager.clone(particleSystem.getPositioner(), PositionParameters::new);
                originalParticleParameters = saveLoadManager.clone(particleSystem.getParameters(), ParticleParameters::new);
            });
            addValueChangedListener(value -> {
                float resizeValue = ((value < 0 ? value : value * 2) + 100f) * 0.01f;
                PositionParameters updatedParticlePositioner
                        = saveLoadManager.clone(originalPositionParameters, PositionParameters::new);
                ParticleParameters updatedParticleParameters =
                        saveLoadManager.clone(originalParticleParameters, ParticleParameters::new);

                updatedParticlePositioner.scale(resizeValue);
                updatedParticleParameters.scale(resizeValue);

                particleSystem.setPositionParameters(updatedParticlePositioner);
                particleSystem.setParameters(updatedParticleParameters);
            });
            addDragStopListener(v -> setValue(0, false));

            setBounds(Rectangle.create(handle.getBounds().getWidth() + 200, handle.getBounds().getHeight()));
        }

        static ResizeSlider create(ParticleSystemModel particleSystem, Disposer disposer) {
            Surface quad = Quad.create(Rectangle.create(32, 16), disposer);
            Renderer handle = new SurfaceRenderer<>(quad, Textures.sliderHandle());
            return new ResizeSlider(handle, particleSystem);
        }
    }
}