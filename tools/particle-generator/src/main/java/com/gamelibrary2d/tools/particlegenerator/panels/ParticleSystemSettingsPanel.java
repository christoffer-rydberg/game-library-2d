package com.gamelibrary2d.tools.particlegenerator.panels;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.markers.Parent;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.tools.particlegenerator.models.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.properties.BooleanProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.FloatProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.IntegerProperty;
import com.gamelibrary2d.tools.particlegenerator.resources.Textures;
import com.gamelibrary2d.tools.particlegenerator.widgets.Slider;

public class ParticleSystemSettingsPanel extends AbstractPanel<GameObject> implements Parent<GameObject> {
    private final ParticleSystemModel particleSystem;

    public ParticleSystemSettingsPanel(ParticleSystemModel particleSystem, Disposer disposer) {
        this.particleSystem = particleSystem;

        PanelUtil.stack(this, new ParticleParametersPanel(particleSystem), 0f);

        PanelUtil.stack(this, particlesPerSecond(), PanelUtil.DEFAULT_STACK_MARGIN * 5);
        PanelUtil.stack(this, pulsating());
        PanelUtil.stack(this, defaultCount());
        PanelUtil.stack(this, offsetX());
        PanelUtil.stack(this, offsetY());

        PanelUtil.stack(
                this,
                new ParticlePositionerPanel(particleSystem),
                PanelUtil.DEFAULT_STACK_MARGIN * 5);

        ResizeSlider slider = ResizeSlider.create(particleSystem, disposer);
        PanelUtil.stack(this, slider, PanelUtil.DEFAULT_STACK_MARGIN * 5);
    }

    private ParticleSystemSettings settings() {
        return particleSystem.getSettings();
    }

    private GameObject defaultCount() {
        return new PanelElement.Integer(
                "Particle Count",
                new IntegerProperty(() -> settings().getDefaultCount(), v -> settings().setDefaultCount(v)),
                new IntegerProperty(() -> settings().getDefaultCountVar(), v -> settings().setDefaultCountVar(v))
        );
    }

    private GameObject particlesPerSecond() {
        var property = new IntervalToPerSecondConverter(
                () -> settings().getDefaultInterval(),
                v -> settings().setDefaultInterval(v));

        return new PanelElement.Float(
                "Spawn Rate",
                new FloatProperty(property, property)
        );
    }

    private GameObject pulsating() {
        return new PanelElement.Boolean(
                "Pulsating",
                new BooleanProperty(
                        () -> settings().isPulsating(),
                        v -> settings().setPulsating(v))
        );
    }

    private GameObject offsetX() {
        return new PanelElement.Float(
                "Offset X",
                new FloatProperty(() -> settings().getOffsetX(), v -> settings().setOffsetX(v)),
                new FloatProperty(() -> settings().getOffsetXVar(), v -> settings().setOffsetXVar(v))
        );
    }

    private GameObject offsetY() {
        return new PanelElement.Float(
                "Offset Y",
                new FloatProperty(() -> settings().getOffsetY(), v -> settings().setOffsetY(v)),
                new FloatProperty(() -> settings().getOffsetYVar(), v -> settings().setOffsetYVar(v))
        );
    }

    private static class IntervalToPerSecondConverter implements FloatProperty.Getter, FloatProperty.Setter {
        private final FloatProperty.Getter getter;
        private final FloatProperty.Setter setter;
        private float cachedInterval;
        private float perSecond;

        IntervalToPerSecondConverter(FloatProperty.Getter getter, FloatProperty.Setter setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public float get() {
            var interval = getter.get();
            if (interval != cachedInterval) {
                cachedInterval = interval;
                perSecond = interval == 0 ? 0 : 1f / interval;
            }

            return perSecond;
        }

        @Override
        public void set(float value) {
            if (perSecond != value) {
                perSecond = value;
                cachedInterval = value == 0 ? 0 : 1f / value;
                setter.set(cachedInterval);
            }
        }
    }

    private static class ResizeSlider extends Slider {
        private ParticlePositioner originalParticlePositioner;
        private ParticleParameters originalParticleParameters;

        private ResizeSlider(Renderer handle, ParticleSystemModel particleSystem) {
            super(handle, SliderDirection.HORIZONTAL, -50, 50, 2);
            var saveLoadManager = new SaveLoadManager();
            addDragBeginListener(value -> {
                originalParticlePositioner = saveLoadManager.clone(particleSystem.getPositioner(), ParticlePositioner::new);
                originalParticleParameters = saveLoadManager.clone(particleSystem.getParameters(), ParticleParameters::new);
            });
            addValueChangedListener(value -> {
                float resizeValue = ((value < 0 ? value : value * 2) + 100f) * 0.01f;
                var updatedParticlePositioner = saveLoadManager.clone(originalParticlePositioner, ParticlePositioner::new);
                var updatedParticleParameters = saveLoadManager.clone(originalParticleParameters, ParticleParameters::new);

                updatedParticlePositioner.scale(resizeValue);
                updatedParticleParameters.scale(resizeValue);

                particleSystem.setPositioner(updatedParticlePositioner);
                particleSystem.setParameters(updatedParticleParameters);
            });
            addDragStopListener(v -> setValue(0, false));

            setBounds(Rectangle.centered(handle.getBounds().width() + 200, handle.getBounds().height()));
        }

        static ResizeSlider create(ParticleSystemModel particleSystem, Disposer disposer) {
            var quad = Quad.create(Rectangle.centered(32, 16), disposer);
            var handle = new SurfaceRenderer(quad, Textures.sliderHandle());
            return new ResizeSlider(handle, particleSystem);
        }
    }
}