package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.tools.particlegenerator.ParticleFrame;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.objects.StackPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.*;

public class SpawnSettingsPanel extends StackPanel {

    final static float ROW_HEIGHT = 18;

    final ParticleSystemModel particleSystem;

    private final ParticleFrame frame;

    private float particlesPerSecond = 0;
    private float particleInterval = -1;

    public SpawnSettingsPanel(ParticleFrame frame, ParticleSystemModel particleSystem) {
        super(Orientation.VERTICAL, -ROW_HEIGHT);

        this.frame = frame;

        this.particleSystem = particleSystem;


        add(new IntegerPropertyPanel("Particle Count", getCountParameters()));
        add(new FloatPropertyPanel("Particles Per Second", getIntervalParameters()));
        add(new BooleanPropertyPanel("Pulsating", getPulsatingParameters()));
        add(new BooleanPropertyPanel("Local Center", getLocalCenterParameters()));

        add(new EnumPropertyPanel<>("Spawn Area", ParticlePositioner.SpawnArea.class, getSpawnSettingsParameters()));
        add(new FloatPropertyPanel("Spawn Area Offset", getSpawnAreaOffsetParameters()));
        add(new FloatPropertyPanel("Spawn Area Width", getSpawnAreaWidthParameters()));
        add(new FloatPropertyPanel("Spawn Area Height", getSpawnAreaHeightParameters()));
        add(new FloatPropertyPanel("Spawn Angle", getSpawnAngleParameters()));

        var slider = ResizeSlider.create(particleSystem, frame);
        slider.setPosition(getBounds().width() / 2 + 10, 0);
        add(slider, -slider.getBounds().height() - ROW_HEIGHT);
    }

    private PropertyParameters<Boolean> getPulsatingParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getSettings().setPulsating(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getSettings().isPulsating());
            }
        };
    }

    private PropertyParameters<Boolean> getLocalCenterParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getSpawnSettings().setLocalCenter(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getSpawnSettings().isLocalCenter());
            }
        };
    }

    private PropertyParameters<ParticlePositioner.SpawnArea> getSpawnSettingsParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getSpawnSettings().setSpawnArea(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getSpawnSettings().getSpawnArea());
            }
        };
    }

    private PropertyParameters<Integer> getCountParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getSettings().setDefaultCount(getParameter(0));
                particleSystem.getSettings().setDefaultCountVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0,
                        particleSystem.getSettings().getDefaultCount());
                return changed | setParameter(1,
                        particleSystem.getSettings().getDefaultCountVar());
            }
        };
    }

    private PropertyParameters<Float> getIntervalParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particlesPerSecond = getParameter(0);
                particleInterval = particlesPerSecond == 0 ? 0 : 1f / particlesPerSecond;
                particleSystem.getSettings().setDefaultInterval(particleInterval);
            }

            public boolean updateIfChanged() {
                if (particleInterval != particleSystem.getSettings().getDefaultInterval()) {
                    particleInterval = particleSystem.getSettings().getDefaultInterval();
                    particlesPerSecond = particleInterval == 0 ? 0 : 1f / particleInterval;
                    return setParameter(0, particlesPerSecond);
                }
                return false;
            }
        };
    }

    private PropertyParameters<Float> getSpawnAreaOffsetParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                float offsetX = getParameter(0);
                float offsetY = getParameter(1);
                ParticlePositioner spawnSettings = particleSystem.getSpawnSettings();
                spawnSettings.setSpawnAreaOffsetX(offsetX);
                spawnSettings.setSpawnAreaOffsetY(offsetY);
            }

            public boolean updateIfChanged() {
                ParticlePositioner spawnSettings = particleSystem.getSpawnSettings();
                boolean changed = setParameter(0, spawnSettings.getSpawnAreaOffsetX());
                changed |= setParameter(1, spawnSettings.getSpawnAreaOffsetY());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getSpawnAreaWidthParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getSpawnSettings().setSpawnAreaWidth(getParameter(0));
                particleSystem.getSpawnSettings().setSpawnAreaWidthVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getSpawnSettings().getSpawnAreaWidth());
                changed |= setParameter(1, particleSystem.getSpawnSettings().getSpawnAreaWidthVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getSpawnAreaHeightParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getSpawnSettings().setSpawnAreaHeight(getParameter(0));
                particleSystem.getSpawnSettings().setSpawnAreaHeightVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getSpawnSettings().getSpawnAreaHeight());
                changed |= setParameter(1, particleSystem.getSpawnSettings().getSpawnAreaHeightVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getSpawnAngleParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getSpawnSettings().setSpawnAngle(getParameter(0));
                particleSystem.getSpawnSettings().setSpawnAngleVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getSpawnSettings().getSpawnAngle());
                changed |= setParameter(1, particleSystem.getSpawnSettings().getSpawnAngleVar());
                return changed;
            }
        };
    }
}