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
        add(new FloatPropertyPanel("Direction", getDirectionParameters()));
        add(new BooleanPropertyPanel("Move From Center", getMoveFromCenterParameters()));

        // Acceleration
        add(new FloatPropertyPanel("Centripetal Acceleration", getCentripetalAccelerationParameters()));
        add(new FloatPropertyPanel("Tangental Acceleration", getTangentalAccParameters()));
        add(new FloatPropertyPanel("Horizontal Acceleration", getHorizontalAccelerationParameters()));
        add(new FloatPropertyPanel("Vertical Acceleration", getVerticalAccelerationParameters()));

        // Scale
        add(new FloatPropertyPanel("Scale", getScaleParameters()));
        add(new BooleanPropertyPanel("Update Scale", getUpdateScaleParameters()));
        add(new FloatPropertyPanel("End Scale", getEndScaleParameters()));

        // Rotation
        add(new FloatPropertyPanel("Rotation", getRotationParameters()));
        add(new FloatPropertyPanel("Rotation Speed", getRotationSpeedParameters()));
        add(new FloatPropertyPanel("Rotation Acceleration", getRotationAccParameters()));
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
        setBounds(new Rectangle(bounds.xMin(), bounds.yMin(), bounds.xMax() + 100, bounds.yMax()));
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

    private PropertyParameters<Float> getDirectionParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setDirection(getParameter(0));
                particleSystem.getUpdateSettings().setDirectionVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getDirection());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getDirectionVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getScaleParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setScale(getParameter(0));
                particleSystem.getUpdateSettings().setScaleVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getScale());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getScaleVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getEndScaleParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setEndScale(getParameter(0));
                particleSystem.getUpdateSettings().setEndScaleVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getEndScale());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getEndScaleVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getHorizontalAccelerationParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setHorizontalAcceleration(getParameter(0));
                particleSystem.getUpdateSettings().setHorizontalAccelerationVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getHorizontalAcceleration());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getHorizontalAccelerationVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getVerticalAccelerationParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setVerticalAcceleration(getParameter(0));
                particleSystem.getUpdateSettings().setVerticalAccelerationVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getVerticalAcceleration());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getVerticalAccelerationVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getCentripetalAccelerationParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setCentripetalAcceleration(getParameter(0));
                particleSystem.getUpdateSettings().setCentripetalAccelerationVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getCentripetalAcceleration());
                changed |= setParameter(1, particleSystem.getUpdateSettings().getCentripetalAccelerationVar());
                return changed;
            }
        };
    }

    private PropertyParameters<Float> getTangentalAccParameters() {
        return new PropertyParameters<>(2) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setTangentalAcceleration(getParameter(0));
                particleSystem.getUpdateSettings().setTangentalAccVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getTangentalAcceleration());
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
                particleSystem.getUpdateSettings().setRotationAcceleration(getParameter(0));
                particleSystem.getUpdateSettings().setRotationAccVar(getParameter(1));
            }

            public boolean updateIfChanged() {
                boolean changed = setParameter(0, particleSystem.getUpdateSettings().getRotationAcceleration());
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

    private PropertyParameters<Boolean> getMoveFromCenterParameters() {
        return new PropertyParameters<>(1) {
            public void updateSetting() {
                particleSystem.getUpdateSettings().setMoveAwayFromCenter(getParameter(0));
            }

            public boolean updateIfChanged() {
                return setParameter(0, particleSystem.getUpdateSettings().isMovingAwayFromCenter());
            }
        };
    }
}