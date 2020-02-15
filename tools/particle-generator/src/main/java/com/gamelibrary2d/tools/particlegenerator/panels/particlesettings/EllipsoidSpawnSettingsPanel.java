package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.particle.settings.SettingsExtensions;
import com.gamelibrary2d.tools.particlegenerator.ParticleFrame;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.FloatPropertyPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.PropertyParameters;

public class EllipsoidSpawnSettingsPanel extends AbstractSpawnSettingsPanel {

    public EllipsoidSpawnSettingsPanel(ParticleFrame frame, ParticleSystemModel particleSystem) {
        super(frame, particleSystem);
        add(new FloatPropertyPanel("Radius", getRadiusParameters()));
        add(new FloatPropertyPanel("Radius Var", getRadiusVarParameters()));
        add(new FloatPropertyPanel("Position Yaw", getPositionYawParameters()));
        add(new FloatPropertyPanel("Position Pitch", getPositionPitchParameters()));

        var slider = ResizeSlider.create(particleSystem, frame);
        slider.position().set(getBounds().width() / 2 + 10, 0);
        add(slider, -slider.getBounds().height() - ROW_HEIGHT);
    }

    private PropertyParameters<Float> getRadiusParameters() {
        return new PropertyParameters<>(3) {
            public void updateSetting() {
                SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).setRadius(getParameter(0), getParameter(1), getParameter(2));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getRadius()[0]);
                changed |= setParameter(1, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getRadius()[1]);
                changed |= setParameter(2, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getRadius()[2]);
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getRadiusVarParameters() {
        return new PropertyParameters<>(3) {
            public void updateSetting() {
                SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).setRadiusVar(getParameter(0), getParameter(1), getParameter(2));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getRadiusVar()[0]);
                changed |= setParameter(1, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getRadiusVar()[1]);
                changed |= setParameter(2, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getRadiusVar()[2]);
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getPositionYawParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).setPositionYaw(getParameter(0));
                SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).setPositionYawVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getPositionYaw());
                changed |= setParameter(1, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getPositionYawVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getPositionPitchParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).setPositionPitch(getParameter(0));
                SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).setPositionPitchVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getPositionPitch());
                changed |= setParameter(1, SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).getPositionPitchVar());
                return changed;
            }
        };
    }
}
