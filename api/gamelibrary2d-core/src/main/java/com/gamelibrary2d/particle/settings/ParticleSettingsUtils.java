package com.gamelibrary2d.particle.settings;

public class ParticleSettingsUtils {

    public static AbstractSpawnSettings asAbstract(ParticleSpawnSettings spawnSettings) {
        return spawnSettings instanceof AbstractSpawnSettings ? (AbstractSpawnSettings) spawnSettings : null;
    }

    public static BasicSpawnSettings asBasic(ParticleSpawnSettings spawnSettings) {
        return spawnSettings instanceof BasicSpawnSettings ? (BasicSpawnSettings) spawnSettings : null;
    }

    public static EllipsoidSpawnSettings asEllipsoid(ParticleSpawnSettings spawnSettings) {
        return spawnSettings instanceof EllipsoidSpawnSettings ? (EllipsoidSpawnSettings) spawnSettings : null;
    }

    public static void scaleUpdateSettings(ParticleUpdateSettings settings, float factor) {
        scaleUpdateSettings(settings, factor, settings);
    }

    public static void scaleUpdateSettings(ParticleUpdateSettings settings, float factor,
                                           ParticleUpdateSettings output) {
        output.setSpeed(settings.getSpeed() * factor);
        output.setSpeedVar(settings.getSpeedVar() * factor);
        output.setScale(settings.getScaleX() * factor, settings.getScaleY() * factor);
        output.setScaleVar(settings.getScaleVar() * factor);
        output.setEndScale(settings.getEndScaleX() * factor, settings.getEndScaleY() * factor);
        output.setEndScaleVar(settings.getEndScaleVar() * factor);
        output.setAcceleration(settings.getAccelerationX() * factor, settings.getAccelerationY() * factor, settings.getAccelerationZ() * factor);
        output.setAccelerationVar(settings.getAccelerationXVar() * factor, settings.getAccelerationYVar() * factor,
                settings.getAccelerationZVar() * factor);
        output.setRadialAcc(settings.getRadialAcc() * factor);
        output.setRadialAccVar(settings.getRadialAccVar() * factor);
        output.setTangentalAcc(settings.getTangentalAcc() * factor);
        output.setTangentalAccVar(settings.getTangentalAccVar() * factor);
    }

    public static void scaleSpawnSettings(ParticleSpawnSettings spawnSettings, float factor) {
        scaleSpawnSettings(spawnSettings, factor, spawnSettings);
    }

    public static void scaleSpawnSettings(ParticleSpawnSettings spawnSettings, float factor,
                                          ParticleSpawnSettings output) {
        if (spawnSettings instanceof BasicSpawnSettings) {
            scaleBasicSpawnSettings((BasicSpawnSettings) spawnSettings, factor, (BasicSpawnSettings) output);
        } else {
            scaleEllipsoidSpawnSettings((EllipsoidSpawnSettings) spawnSettings, factor,
                    (EllipsoidSpawnSettings) output);
        }
    }

    public static BasicSpawnSettings convertToBasic(ParticleSpawnSettings spawnSettings) {
        if (spawnSettings instanceof BasicSpawnSettings) {
            return (BasicSpawnSettings) spawnSettings;
        }

        BasicSpawnSettings basicSettings = new BasicSpawnSettings((AbstractSpawnSettings) spawnSettings);

        if (spawnSettings instanceof EllipsoidSpawnSettings) {
            EllipsoidSpawnSettings sphericalSettings = (EllipsoidSpawnSettings) spawnSettings;
            basicSettings.getPositionVar()[0] = sphericalSettings.getRadius()[0] + sphericalSettings.getRadiusVar()[0];
            basicSettings.getPositionVar()[1] = sphericalSettings.getRadius()[1] + sphericalSettings.getRadiusVar()[1];
            basicSettings.getPositionVar()[2] = sphericalSettings.getRadius()[2] + sphericalSettings.getRadiusVar()[2];
        }

        return basicSettings;
    }

    public static EllipsoidSpawnSettings convertToEllipsoid(ParticleSpawnSettings spawnSettings) {
        if (spawnSettings instanceof EllipsoidSpawnSettings) {
            return (EllipsoidSpawnSettings) spawnSettings;
        }

        EllipsoidSpawnSettings sphericalSettings = new EllipsoidSpawnSettings((AbstractSpawnSettings) spawnSettings);

        if (spawnSettings instanceof BasicSpawnSettings) {
            BasicSpawnSettings basicSpawnSettings = (BasicSpawnSettings) spawnSettings;

            sphericalSettings.getRadius()[0] = basicSpawnSettings.getPositionVar()[0] / 2;
            sphericalSettings.getRadius()[1] = basicSpawnSettings.getPositionVar()[1] / 2;
            sphericalSettings.getRadius()[2] = basicSpawnSettings.getPositionVar()[2] / 2;

            sphericalSettings.getRadiusVar()[0] = basicSpawnSettings.getPositionVar()[0] / 2;
            sphericalSettings.getRadiusVar()[1] = basicSpawnSettings.getPositionVar()[1] / 2;
            sphericalSettings.getRadiusVar()[2] = basicSpawnSettings.getPositionVar()[2] / 2;

            sphericalSettings.setPositionYawVar(180);
        }

        return sphericalSettings;
    }

    private static void scaleBasicSpawnSettings(BasicSpawnSettings spawnSettings, float factor,
                                                BasicSpawnSettings output) {
        output.getPositionVar()[0] = spawnSettings.getPositionVar()[0] * factor;
        output.getPositionVar()[1] = spawnSettings.getPositionVar()[1] * factor;
        output.getPositionVar()[2] = spawnSettings.getPositionVar()[2] * factor;
    }

    private static void scaleEllipsoidSpawnSettings(EllipsoidSpawnSettings spawnSettings, float factor,
                                                    EllipsoidSpawnSettings output) {
        output.getRadius()[0] = spawnSettings.getRadius()[0] * factor;
        output.getRadius()[1] = spawnSettings.getRadius()[1] * factor;
        output.getRadius()[2] = spawnSettings.getRadius()[2] * factor;

        output.getRadiusVar()[0] = spawnSettings.getRadiusVar()[0] * factor;
        output.getRadiusVar()[1] = spawnSettings.getRadiusVar()[1] * factor;
        output.getRadiusVar()[2] = spawnSettings.getRadiusVar()[2] * factor;
    }
}