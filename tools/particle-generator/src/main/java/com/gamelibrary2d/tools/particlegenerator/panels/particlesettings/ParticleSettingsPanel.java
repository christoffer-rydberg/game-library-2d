package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.objects.StackPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.BooleanPropertyPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.FloatPropertyPanel;
import com.gamelibrary2d.tools.particlegenerator.panels.common.PropertyParameters;

public class ParticleSettingsPanel extends StackPanel {

    private final static float ROW_HEIGHT = 18;

    private final ParticleSystemModel particleSystem;

    public ParticleSettingsPanel(ParticleSystemModel particleSystem) {

        super(Orientation.VERTICAL, -ROW_HEIGHT);

        this.particleSystem = particleSystem;

        add(new FloatPropertyPanel("Life", getLifeParameters()));

        add(new FloatPropertyPanel("Delay", getDelayParameters()));

        // Velocity
        add(new FloatPropertyPanel("Speed", getSpeedParameters()));
        add(new FloatPropertyPanel("End Speed Factor", getEndSpeedFactorParameters()));
        add(new FloatPropertyPanel("Direction Yaw", getDirectionYawParameters()));
        add(new FloatPropertyPanel("Direction Pitch", getDirectionPitchParameters()));
        add(new BooleanPropertyPanel("Move From Gravity Center", getMoveFromOriginParameters()));

        // Acceleration
        add(new FloatPropertyPanel("Radial Acc", getRadialAccParameters()));
        add(new FloatPropertyPanel("Tangental Acc", getTangentalAccParameters()));
        add(new FloatPropertyPanel("Acceleration X", getAccelerationXParameters()));
        add(new FloatPropertyPanel("Acceleration Y", getAccelerationYParameters()));
        add(new FloatPropertyPanel("Acceleration Z", getAccelerationZParameters()));

        // Scale
        add(new FloatPropertyPanel("Scale X", getScaleXParameters()));
        add(new FloatPropertyPanel("Scale Y", getScaleYParameters()));
        add(new FloatPropertyPanel("Scale Var", getScaleVarParameters()));
        add(new BooleanPropertyPanel("Update Scale", getUpdateScaleParameters()));
        add(new FloatPropertyPanel("End Scale X", getEndScaleXParameters()));
        add(new FloatPropertyPanel("End Scale Y", getEndScaleYParameters()));
        add(new FloatPropertyPanel("End Scale Var", getEndScaleVarParameters()));

        // Rotation
        add(new FloatPropertyPanel("Rotation", getRotationParameters()));
        add(new FloatPropertyPanel("Rotation Speed", getRotationSpeedParameters()));
        add(new FloatPropertyPanel("Rotation Acc", getRotationAccParameters()));
        add(new BooleanPropertyPanel("Rotated Forwards", getRotatedForwardsParameters()));

        // Color
        add(new FloatPropertyPanel("Color R", getColorRParameters()));
        add(new FloatPropertyPanel("Color G", getColorGParameters()));
        add(new FloatPropertyPanel("Color B", getColorBParameters()));
        add(new BooleanPropertyPanel("Update Color", getUpdateColorParameters()));
        add(new FloatPropertyPanel("End Color R", getEndColorRParameters()));
        add(new FloatPropertyPanel("End Color G", getEndColorGParameters()));
        add(new FloatPropertyPanel("End Color B", getEndColorBParameters()));

        // Alpha
        add(new FloatPropertyPanel("Alpha", getAlphaParameters()));
        add(new FloatPropertyPanel("End Alpha", getEndAlphaParameters()));

        // Increase the with of the bounds a bit so that sub elements can grow without
        // having to recalculate the bounds of this panel.
        Rectangle bounds = getBounds();
        setBounds(new Rectangle(bounds.getXMin(), bounds.getYMin(), bounds.getXMax() + 100, bounds.getYMax()));
    }

    private PropertyParameters<Float> getLifeParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {

                particleSystem.getUpdateSettings().setLife(getParameter(0));
                particleSystem.getUpdateSettings().setLifeVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getLife());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getLifeVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getDelayParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setDelay(getParameter(0));
                particleSystem.getUpdateSettings().setDelayVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getDelay());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getDelayVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getSpeedParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setSpeed(getParameter(0));
                particleSystem.getUpdateSettings().setSpeedVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getSpeed());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getSpeedVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getEndSpeedFactorParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndSpeedFactor(getParameter(0));
                particleSystem.getUpdateSettings().setEndSpeedFactorVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getEndSpeedFactor());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getEndSpeedFactorVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getDirectionYawParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setInitialYaw(getParameter(0));
                particleSystem.getUpdateSettings().setInitialYawVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getInitialYaw());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getInitialYawVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getDirectionPitchParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setInitialPitch(getParameter(0));
                particleSystem.getUpdateSettings().setInitialPitchVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getInitialPitch());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getInitialPitchVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getScaleXParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setScaleX(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().getScaleX());
            }
        };
    }

    private PropertyParameters<Float> getScaleYParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setScaleY(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().getScaleY());
            }
        };
    }

    private PropertyParameters<Float> getScaleVarParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setScaleVar(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().getScaleVar());
            }
        };
    }

    private PropertyParameters<Float> getEndScaleXParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndScaleX(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().getEndScaleX());
            }
        };
    }

    private PropertyParameters<Float> getEndScaleYParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndScaleY(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().getEndScaleY());
            }
        };
    }

    private PropertyParameters<Float> getEndScaleVarParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndScaleVar(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().getEndScaleVar());
            }
        };
    }

    private PropertyParameters<Float> getAccelerationXParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setAccelerationX(getParameter(0));
                particleSystem.getUpdateSettings().setAccelerationXVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getAccelerationX());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getAccelerationXVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getAccelerationYParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setAccelerationY(getParameter(0));
                particleSystem.getUpdateSettings().setAccelerationYVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getAccelerationY());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getAccelerationYVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getAccelerationZParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setAccelerationZ(getParameter(0));
                particleSystem.getUpdateSettings().setAccelerationZVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getAccelerationZ());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getAccelerationZVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getRadialAccParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setRadialAcc(getParameter(0));
                particleSystem.getUpdateSettings().setRadialAccVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getRadialAcc());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getRadialAccVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getTangentalAccParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setTangentalAcc(getParameter(0));
                particleSystem.getUpdateSettings().setTangentalAccVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getTangentalAcc());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getTangentalAccVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getColorRParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setColorR(getParameter(0));
                particleSystem.getUpdateSettings().setColorRVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getColorR());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getColorRVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getColorGParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setColorG(getParameter(0));
                particleSystem.getUpdateSettings().setColorGVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getColorG());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getColorGVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getColorBParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setColorB(getParameter(0));
                particleSystem.getUpdateSettings().setColorBVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getColorB());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getColorBVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getEndColorRParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndColorR(getParameter(0));
                particleSystem.getUpdateSettings().setEndColorRVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getEndColorR());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getEndColorRVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getEndColorGParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndColorG(getParameter(0));
                particleSystem.getUpdateSettings().setEndColorGVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getEndColorG());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getEndColorGVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getEndColorBParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndColorB(getParameter(0));
                particleSystem.getUpdateSettings().setEndColorBVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getEndColorB());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getEndColorBVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getAlphaParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setAlpha(getParameter(0));
                particleSystem.getUpdateSettings().setAlphaVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getAlpha());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getAlphaVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getEndAlphaParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndAlpha(getParameter(0));
                particleSystem.getUpdateSettings().setEndAlphaVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getEndAlpha());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getEndAlphaVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getRotationParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setRotation(getParameter(0));
                particleSystem.getUpdateSettings().setRotationVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getRotation());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getRotationVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getRotationSpeedParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setRotationSpeed(getParameter(0));
                particleSystem.getUpdateSettings().setRotationSpeedVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getRotationSpeed());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getRotationSpeedVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getRotationAccParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setRotationAcc(getParameter(0));
                particleSystem.getUpdateSettings().setRotationAccVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getRotationAcc());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getRotationAccVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Boolean> getUpdateColorParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setUpdateColor(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().isUpdatingColor());
            }
        };
    }

    private PropertyParameters<Boolean> getRotatedForwardsParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setRotatedForward(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().isRotatedForward());
            }
        };
    }

    private PropertyParameters<Boolean> getUpdateScaleParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setUpdateScale(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().isUpdatingScale());
            }
        };
    }

    private PropertyParameters<Boolean> getMoveFromOriginParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setInitialDirFromGravityCenter(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().isInitialDirFromGravityCenter());
            }
        };
    }
}