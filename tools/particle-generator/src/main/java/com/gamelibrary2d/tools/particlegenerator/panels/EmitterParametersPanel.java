package com.gamelibrary2d.tools.particlegenerator.panels;

import com.gamelibrary2d.components.containers.AbstractPanel;
import com.gamelibrary2d.components.denotations.Parent;
import com.gamelibrary2d.components.objects.GameObject;
import com.gamelibrary2d.particle.parameters.EmitterParameters;
import com.gamelibrary2d.tools.particlegenerator.models.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.properties.BooleanProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.FloatProperty;
import com.gamelibrary2d.tools.particlegenerator.properties.IntegerProperty;

public class EmitterParametersPanel extends AbstractPanel<GameObject> implements Parent<GameObject> {
    private final ParticleSystemModel particleSystem;

    public EmitterParametersPanel(ParticleSystemModel particleSystem) {
        this.particleSystem = particleSystem;
        PanelUtil.stack(this, particlesPerSecond(), 0f);
        PanelUtil.stack(this, pulsating());
        PanelUtil.stack(this, defaultCount());
        PanelUtil.stack(this, offsetX());
        PanelUtil.stack(this, offsetY());
    }

    private EmitterParameters settings() {
        return particleSystem.getSettings().getEmitterParameters();
    }

    private GameObject defaultCount() {
        return new PanelElement.Integer(
                "Particle Count",
                new IntegerProperty(() -> settings().getDefaultCount(), v -> settings().setDefaultCount(v)),
                new IntegerProperty(() -> settings().getDefaultCountVar(), v -> settings().setDefaultCountVar(v))
        );
    }

    private GameObject particlesPerSecond() {
        IntervalToPerSecondConverter property = new IntervalToPerSecondConverter(
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
            float interval = getter.get();
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
}