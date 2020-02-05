package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.random.RandomGenerator;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.eventlisteners.MouseButtonReleaseListener;
import com.gamelibrary2d.framework.Mouse;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.settings.*;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.VerticalAlignment;
import com.gamelibrary2d.tools.particlegenerator.ParticleFrame;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;
import com.gamelibrary2d.util.io.FileChooser;

import java.io.File;
import java.io.IOException;

public class SaveLoadResetPanel extends AbstractPanel<GameObject> {

    private final ParticleSystemModel particleSystem;

    private final ParticleSettingsSaveLoadManager saveLoadManager = new ParticleSettingsSaveLoadManager();

    private final ParticleFrame frame;

    private final FileChooser fileChooser;

    public SaveLoadResetPanel(ParticleSystemModel particleSystem, ParticleFrame frame) {
        this.particleSystem = particleSystem;
        this.frame = frame;

        fileChooser = new FileChooser(System.getenv("TEMP") + "/ParticleGenerator/particle_path.txt");

        addButton("Save", 0, 0, new SaveEventHandler());
        addButton("Load", 100, 0, new LoadEventHandler());
        addButton("Reset", 200, 0, new ResetEventHandler());
        addButton("Randomize", 300, 0, new RandomizeEventHandler());
    }

    private void addButton(String text, float posX, float posY, MouseButtonReleaseListener mouseEventHandler) {
        Button button = new Button();

        var buttonContext = button.getContent();
        buttonContext.setText(text);
        buttonContext.setTextRenderer(new TextRenderer(Fonts.getDefaultFont()));
        buttonContext.setFontColor(Color.WHITE);
        buttonContext.setVerticalAlignment(VerticalAlignment.BOTTOM);
        buttonContext.setHorizontalAlignment(HorizontalAlignment.LEFT);

        button.getPosition().set(posX, posY);
        button.setBounds(Fonts.getDefaultFont().textSize(buttonContext.getText(), buttonContext.getHorizontalAlignment(),
                buttonContext.getVerticalAlignment()));
        button.addMouseButtonReleaseListener(mouseEventHandler);
        add(button);
    }

    private void saveParticleSystem() {

        File file = fileChooser.browse();

        if (file == null)
            return;

        try {
            saveLoadManager.save(particleSystem.getSpawnSettings(), particleSystem.getUpdateSettings(), file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadParticleSystem() {

        File file = fileChooser.browse();

        if (file == null)
            return;

        ParticleSpawnSettings spawnSettings;
        ParticleUpdateSettings updateSettings;
        Renderer renderer = particleSystem.getUpdateSettings().getRenderer();
        try {
            var result = new ParticleSettingsSaveLoadManager().load(file);
            spawnSettings = result.getSpawnSettings();
            updateSettings = result.getUpdateSettings();
        } catch (IOException e) {
            spawnSettings = new BasicSpawnSettings();
            updateSettings = new ParticleUpdateSettings();
        }

        updateSettings.setRenderer(renderer);

        particleSystem.setSpawnSettings(spawnSettings);
        particleSystem.setUpdateSettings(updateSettings);

        frame.changeSpawnSettings(
                particleSystem.getSpawnSettings() instanceof EllipsoidSpawnSettings ? SpawnSettingsType.ELLIPSOID
                        : SpawnSettingsType.BASIC);
    }

    private void randomizeParticleSystem() {

        RandomGenerator random = RandomInstance.get();

        int origin = random.nextInt(SpawnSettingsType.values().length);
        SpawnSettingsType posTransform = SpawnSettingsType.values()[origin];
        frame.changeSpawnSettings(posTransform);

        ParticleUpdateSettings particleSettings = particleSystem.getUpdateSettings();

        float life = round(random.nextFloat() * 5, 1);
        particleSettings.setLife(life);
        particleSettings.setLifeVar(round(random.nextFloat() * life, 1));

        particleSettings.setInitialDirFromGravityCenter(random.nextInt(2) == 1);
        particleSettings.setSpeed(random.nextInt(10));
        particleSettings.setSpeedVar(random.nextInt(10));

        particleSettings.setInitialYaw(round(random.nextFloat() * 360 - 180, 1));
        particleSettings.setInitialYawVar(round(random.nextFloat() * 180, 1));

        particleSettings.setInitialPitch(round(random.nextFloat() * 360 - 180, 1));
        particleSettings.setInitialPitchVar(round(random.nextFloat() * 180, 1));

        boolean downScale = random.nextInt(2) == 1;
        float scaleX = random.nextFloat() * 10;
        float scaleY = random.nextFloat() * 10;
        particleSettings.setScale(round(downScale ? 1f / scaleX : scaleX, 2),
                round(downScale ? 1f / scaleY : scaleY, 2));

        boolean downScaleVar = random.nextInt(2) == 1;
        float scaleVar = random.nextFloat() * 4;
        particleSettings.setScaleVar(round(downScaleVar ? 1f / scaleVar : scaleVar, 2));

        particleSettings.setUpdateScale(random.nextInt(2) == 1);

        boolean downScaleEnd = random.nextInt(2) == 1;
        float scaleEndX = random.nextFloat() * 10;
        float scaleEndY = random.nextFloat() * 10;
        particleSettings.setEndScale(round(downScaleEnd ? 1f / scaleEndX : scaleEndX, 2),
                round(downScaleEnd ? 1f / scaleEndY : scaleEndY, 2));

        boolean downScaleEndVar = random.nextInt(2) == 1;
        float endScaleVar = random.nextFloat() * 4;
        particleSettings.setEndScaleVar(round(downScaleEndVar ? 1f / endScaleVar : endScaleVar, 2));

        particleSettings.setAcceleration(round(random.nextFloat() * 200 - 100, 1),
                round(random.nextFloat() * 200 - 100, 1), round(random.nextFloat() * 200 - 100, 1));
        particleSettings.setAccelerationVar(round(random.nextFloat() * 200 - 100, 1),
                round(random.nextFloat() * 200 - 100, 1), round(random.nextFloat() * 200 - 100, 1));

        particleSettings.setRadialAcc(round(random.nextFloat() * 100, 1));
        particleSettings.setRadialAccVar(round(random.nextFloat() * 100, 1));

        particleSettings.setTangentalAcc(round(random.nextFloat() * 100, 1));
        particleSettings.setTangentalAccVar(round(random.nextFloat() * 100, 1));

        particleSettings.setColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        particleSettings.setUpdateColor(random.nextInt(2) == 1);

        particleSettings.setEndColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        boolean varColor = random.nextInt(2) == 1;
        particleSettings.setColorVar(varColor ? random.nextInt(255) : 0, varColor ? random.nextInt(255) : 0,
                varColor ? random.nextInt(255) : 0);

        boolean varEndColor = random.nextInt(2) == 1;
        particleSettings.setEndColorVar(varEndColor ? random.nextInt(255) : 0, varEndColor ? random.nextInt(255) : 0,
                varEndColor ? random.nextInt(255) : 0);

        boolean varEndSpeed = random.nextInt(2) == 1;
        particleSettings.setEndSpeedFactor(varEndSpeed ? random.nextFloat() : 1);

        particleSettings.setAlpha(round(random.nextFloat(), 2));
        particleSettings.setAlphaVar(round(random.nextFloat(), 2));
        particleSettings.setEndAlpha(round(random.nextFloat(), 2));
        particleSettings.setEndAlphaVar(round(random.nextFloat(), 2));

        boolean randomizeRotation = random.nextInt(2) == 1;
        if (randomizeRotation) {

            boolean rotatedForward = random.nextInt(2) == 1;
            if (rotatedForward) {
                particleSettings.setRotatedForward(true);
                particleSettings.setRotation(0);
                particleSettings.setRotationVar(0);
                particleSettings.setRotationSpeed(0);
                particleSettings.setRotationSpeedVar(0);
                particleSettings.setRotationAcc(0);
                particleSettings.setRotationAccVar(0);
            } else {
                particleSettings.setRotatedForward(false);
                particleSettings.setRotation(round(random.nextFloat() * 360 - 180, 2));
                particleSettings.setRotationVar(round(random.nextFloat() * 180, 2));
                particleSettings.setRotationSpeed(round(random.nextFloat() * 100, 2));
                particleSettings.setRotationSpeedVar(round(random.nextFloat() * 100, 2));
                particleSettings.setRotationAcc(round(random.nextFloat() * 100, 2));
                particleSettings.setRotationAccVar(round(random.nextFloat() * 100, 2));
            }
        } else {
            particleSettings.setRotation(0);
            particleSettings.setRotationVar(0);
            particleSettings.setRotationSpeed(0);
            particleSettings.setRotationSpeedVar(0);
            particleSettings.setRotationAcc(0);
            particleSettings.setRotationAccVar(0);
        }

        if (SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()) != null) {
            float count = round(random.nextFloat() * 1000, 1);
            float countVar = round(random.nextFloat() * count, 1);
            SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setDefaultCount((int) count);
            SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setDefaultCountVar((int) countVar);
            SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setDefaultInterval(1.f / count);
            SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setPulsating(false);
            boolean localGravityCenter = random.nextInt(2) == 1;
            SettingsExtensions.asAbstract(particleSystem.getSpawnSettings()).setLocalGravityCenter(localGravityCenter);
        }

        if (SettingsExtensions.asBasic(particleSystem.getSpawnSettings()) != null) {

            SettingsExtensions.asBasic(particleSystem.getSpawnSettings()).setPositionVar(
                    round(random.nextFloat() * frame.getGame().getWindow().getWidth() / 2, 0),
                    round(random.nextFloat() * frame.getGame().getWindow().getHeight() / 2, 0),
                    round(random.nextFloat() * frame.getGame().getWindow().getHeight() / 2, 0));

            SettingsExtensions.asBasic(particleSystem.getSpawnSettings())
                    .setPositionYaw(round(random.nextFloat() * 360 - 180, 1));
            SettingsExtensions.asBasic(particleSystem.getSpawnSettings())
                    .setPositionYawVar(round(random.nextFloat() * 180, 1));

            SettingsExtensions.asBasic(particleSystem.getSpawnSettings())
                    .setPositionPitch(round(random.nextFloat() * 360 - 180, 1));
            SettingsExtensions.asBasic(particleSystem.getSpawnSettings())
                    .setPositionPitchVar(round(random.nextFloat() * 180, 1));

        } else if (SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()) != null) {

            SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings()).setRadius(
                    round(random.nextFloat() * frame.getGame().getWindow().getWidth() / 2, 0),
                    round(random.nextFloat() * frame.getGame().getWindow().getHeight() / 2, 0),
                    round(random.nextFloat() * frame.getGame().getWindow().getHeight() / 2, 0));

            SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings())
                    .setPositionYaw(round(random.nextFloat() * 360 - 180, 1));
            SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings())
                    .setPositionYawVar(round(random.nextFloat() * 180, 1));

            SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings())
                    .setPositionPitch(round(random.nextFloat() * 360 - 180, 1));
            SettingsExtensions.asEllipsoid(particleSystem.getSpawnSettings())
                    .setPositionPitchVar(round(random.nextFloat() * 180, 1));
        }

    }

    private float round(float value, int decimals) {
        int tmp = (int) Math.pow(10, decimals);
        return (int) (value * tmp) / (float) tmp;
    }

    private class SaveEventHandler implements MouseButtonReleaseListener {

        @Override
        public void onMouseButtonRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                saveParticleSystem();
            }
        }
    }

    private class LoadEventHandler implements MouseButtonReleaseListener {

        @Override
        public void onMouseButtonRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                loadParticleSystem();
            }
        }
    }

    private class ResetEventHandler implements MouseButtonReleaseListener {

        @Override
        public void onMouseButtonRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                particleSystem.setSpawnSettings(new BasicSpawnSettings());
                particleSystem.setUpdateSettings(new ParticleUpdateSettings());
                frame.changeSpawnSettings(SpawnSettingsType.BASIC);
            }
        }
    }

    private class RandomizeEventHandler implements MouseButtonReleaseListener {

        @Override
        public void onMouseButtonRelease(GameObject obj, int button, int mods, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                randomizeParticleSystem();
            }
        }
    }
}