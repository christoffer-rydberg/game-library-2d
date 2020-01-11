package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.settings.SettingsExtensions;
import com.gamelibrary2d.tools.particlegenerator.ParticleFrame;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.FloatPropertyPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.PropertyParameters;

public class BasicSpawnSettingsPanel extends AbstractSpawnSettingsPanel {

    public BasicSpawnSettingsPanel(ParticleFrame frame, ParticleSystemModel particleSystem) {

        super(frame, particleSystem);

        add(new FloatPropertyPanel("Position Var", getPositionParameters()));
        add(new FloatPropertyPanel("Position Yaw", getPositionYawParameters()));
        add(new FloatPropertyPanel("Position Pitch", getPositionPitchParameters()));

        GameObject slider = new ResizeSlider(particleSystem, frame);
        slider.getPosition().set(slider.getBounds().getWidth() / 2 + 10, 0);
        add(slider, -slider.getBounds().getHeight() - ROW_HEIGHT);
    }

    private PropertyParameters<Float> getPositionParameters() {
        return new PropertyParameters<>(3) {
            public void updateSetting() {
                SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).setPositionVar(getParameter(0), getParameter(1), getParameter(2));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).getPositionVar()[0]);
                changed |= setParameter(1, SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).getPositionVar()[1]);
                changed |= setParameter(2, SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).getPositionVar()[2]);
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getPositionYawParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).setPositionYaw(getParameter(0));
                SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).setPositionYawVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).getPositionYaw());
                changed |= setParameter(1, SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).getPositionYawVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getPositionPitchParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).setPositionPitch(getParameter(0));
                SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).setPositionPitchVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).getPositionPitch());
                changed |= setParameter(1, SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).getPositionPitchVar());
                return changed;
            }
        };
    }
}
