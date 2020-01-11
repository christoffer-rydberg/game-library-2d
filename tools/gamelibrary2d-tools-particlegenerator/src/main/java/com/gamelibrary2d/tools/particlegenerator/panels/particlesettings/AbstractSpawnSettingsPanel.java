package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.particle.settings.AbstractSpawnSettings;
import com.gamelibrary2d.particle.settings.EllipsoidSpawnSettings;
import com.gamelibrary2d.particle.settings.SettingsExtensions;
import com.gamelibrary2d.tools.particlegenerator.ParticleFrame;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.objects.StackPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.*;

public class AbstractSpawnSettingsPanel extends StackPanel {

    final static float ROW_HEIGHT = 18;

    final ParticleSystemModel particleSystem;

    private final ParticleFrame frame;

    private float particlesPerSecond = 0;
    private float particleInterval = -1;

    AbstractSpawnSettingsPanel(ParticleFrame frame, ParticleSystemModel particleSystem) {

        super(Orientation.VERTICAL, -ROW_HEIGHT);

        this.frame = frame;

        this.particleSystem = particleSystem;

        add(new EnumPropertyPanel<>("Spawn Settings", SpawnSettingsType.class, getSpawnSettingsParameters()));
        add(new IntegerPropertyPanel("Particle Count", getCountParameters()));
        add(new FloatPropertyPanel("Particles/Second", getIntervalParameters()));
        add(new BooleanPropertyPanel("Pulsating", getPulsatingParameters()));
        add(new BooleanPropertyPanel("Local Gravity Center", getLocalGravityCenterParameters()));
        add(new FloatPropertyPanel("Position Offset", getPositionOffsetParameters()));
    }

    private PropertyParameters<Boolean> getPulsatingParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setPulsating(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).isPulsating());
            }
        };
    }

    private PropertyParameters<Boolean> getLocalGravityCenterParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setLocalGravityCenter(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0,
                        SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).isLocalGravityCenter());
            }
        };
    }

    private PropertyParameters<SpawnSettingsType> getSpawnSettingsParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                frame.changeSpawnSettings(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0,
                        particleSystem.getSpawnSettings() instanceof EllipsoidSpawnSettings
                                ? SpawnSettingsType.ELLIPSOID
                                : SpawnSettingsType.BASIC);
            }
        };
    }

    private PropertyParameters<Integer> getCountParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setDefaultCount(getParameter(0));
                SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setDefaultCountVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0,
                        SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).getDefaultCount());
                return changed | setParameter(1,
                        SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).getDefaultCountVar());
            }
        };
    }

    private PropertyParameters<Float> getIntervalParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particlesPerSecond = getParameter(0);
                particleInterval = particlesPerSecond == 0 ? 0 : 1f / particlesPerSecond;
                SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setDefaultInterval(particleInterval);
            }

            public boolean updateIfChanged() {
                if (particleInterval != SettingsExtensions.asAbstract(particleSystem.getSpawnSettings())
                        .getDefaultInterval()) {
                    particleInterval = SettingsExtensions.asAbstract(particleSystem.getSpawnSettings())
                            .getDefaultInterval();
                    particlesPerSecond = particleInterval == 0 ? 0 : 1f / particleInterval;
                    return setParameter(0, particlesPerSecond);
                }
                return false;
            }
        };
    }

    private PropertyParameters<Float> getPositionOffsetParameters() {
        return new PropertyParameters<>(3) {
            public void updateSetting() {
                float offsetX = getParameter(0);
                float offsetY = getParameter(1);
                float offsetZ = getParameter(2);
                AbstractSpawnSettings spawnSettings = SettingsExtensions.asAbstract(particleSystem.getSpawnSettings());
                spawnSettings.setOffsetX(offsetX);
                spawnSettings.setOffsetY(offsetY);
                spawnSettings.setOffsetZ(offsetZ);
            }

            public boolean updateIfChanged() {
                AbstractSpawnSettings spawnSettings = SettingsExtensions.asAbstract(particleSystem.getSpawnSettings());
                boolean changed = setParameter(0, spawnSettings.getOffsetX());
                changed |= setParameter(1, spawnSettings.getOffsetY());
                changed |= setParameter(2, spawnSettings.getOffsetZ());
                return changed;
            }
        };
    }
}