package com.gamelibrary2d.tools.particlegenerator.panels.particlesettings;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.io.SaveLoadManager;
import com.gamelibrary2d.common.random.RandomGenerator;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.framework.Mouse;
import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.particle.settings.ParticleParameters;
import com.gamelibrary2d.particle.settings.ParticlePositioner;
import com.gamelibrary2d.particle.settings.ParticleSystemSettings;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.tools.particlegenerator.ParticleSystemModel;
import com.gamelibrary2d.tools.particlegenerator.objects.Button;
import com.gamelibrary2d.tools.particlegenerator.resources.Fonts;
import com.gamelibrary2d.util.HorizontalAlignment;
import com.gamelibrary2d.util.VerticalAlignment;
import com.gamelibrary2d.util.io.FileChooser;
import com.gamelibrary2d.widgets.events.MouseButtonReleased;

import java.io.File;
import java.io.IOException;

public class SaveLoadResetPanel extends AbstractPanel<GameObject> {

    private final ParticleSystemModel particleSystem;

    private final SaveLoadManager saveLoadManager = new SaveLoadManager();

    private final Game game;
    private final FileChooser fileChooser;

    public SaveLoadResetPanel(ParticleSystemModel particleSystem, Game game) {
        this.particleSystem = particleSystem;
        this.game = game;

        fileChooser = new FileChooser(System.getenv("TEMP") + "/ParticleGenerator/particle_path.txt");

        addButton("Save", 0, 0, new SaveEventHandler());
        addButton("Load", 100, 0, new LoadEventHandler());
        addButton("Reset", 200, 0, new ResetEventHandler());
        addButton("Randomize", 300, 0, new RandomizeEventHandler());
    }

    private void addButton(String text, float posX, float posY, MouseButtonReleased mouseEventHandler) {
        Button button = new Button();

        var buttonContext = button.getContent();
        buttonContext.setText(text);
        buttonContext.setTextRenderer(new TextRenderer(Fonts.getDefaultFont()));
        buttonContext.setFontColor(Color.WHITE);
        buttonContext.setVerticalAlignment(VerticalAlignment.BOTTOM);
        buttonContext.setHorizontalAlignment(HorizontalAlignment.LEFT);

        button.setPosition(posX, posY);
        button.setBounds(Fonts.getDefaultFont().textSize(buttonContext.getText(), buttonContext.getHorizontalAlignment(),
                buttonContext.getVerticalAlignment()));
        button.addMouseButtonReleasedListener(mouseEventHandler);
        add(button);
    }

    private void saveParticleSystem() {
        try {
            File file = fileChooser.browse();
            if (file != null) {
                saveLoadManager.save(particleSystem.getSettings(), file, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadParticleSystem() {
        ParticleSystemSettings settings;
        try {
            File file = fileChooser.browse();

            if (file != null) {
                settings = saveLoadManager.load(file, b -> new ParticleSystemSettings(b, particleSystem.getRenderer()));
            } else {
                settings = new ParticleSystemSettings(
                        new ParticlePositioner(),
                        new ParticleParameters(),
                        particleSystem.getRenderer());
            }
        } catch (IOException e) {
            settings = new ParticleSystemSettings(
                    new ParticlePositioner(),
                    new ParticleParameters(),
                    particleSystem.getRenderer());
        }

        particleSystem.setSettings(settings);
    }

    private void randomizeParticleSystem() {
        RandomGenerator random = RandomInstance.get();

        ParticleParameters particleParameters = particleSystem.getUpdateSettings();

        float life = round(random.nextFloat() * 5, 1);
        particleParameters.setLife(life);
        particleParameters.setLifeVar(round(random.nextFloat() * life, 1));

        particleParameters.setMoveFromCenter(random.nextInt(2) == 1);
        particleParameters.setSpeed(random.nextInt(10));
        particleParameters.setSpeedVar(random.nextInt(10));

        particleParameters.setDirection(round(random.nextFloat() * 360 - 180, 1));
        particleParameters.setDirectionVar(round(random.nextFloat() * 180, 1));

        boolean downScale = random.nextInt(2) == 1;
        float scale = random.nextFloat() * 5;
        particleParameters.setScale(round(downScale ? 1f / scale : scale, 2));

        boolean downScaleVar = random.nextInt(2) == 1;
        float scaleVar = random.nextFloat() * 2;
        particleParameters.setScaleVar(round(downScaleVar ? 1f / scaleVar : scaleVar, 2));

        particleParameters.setUpdateScale(random.nextInt(2) == 1);

        boolean downScaleEnd = random.nextInt(2) == 1;
        float scaleEnd = random.nextFloat() * 5;
        particleParameters.setEndScale(round(downScaleEnd ? 1f / scaleEnd : scaleEnd, 2));

        boolean downScaleEndVar = random.nextInt(2) == 1;
        float endScaleVar = random.nextFloat() * 2;
        particleParameters.setEndScaleVar(round(downScaleEndVar ? 1f / endScaleVar : endScaleVar, 2));

        particleParameters.setAcceleration(
                round(random.nextFloat() * 100 - 50, 1),
                round(random.nextFloat() * 100 - 50, 1));

        particleParameters.setAccelerationVar(
                round(random.nextFloat() * 100 - 50, 1),
                round(random.nextFloat() * 100 - 50, 1));

        particleParameters.setCentripetalAcceleration(round(random.nextFloat() * 100, 1));
        particleParameters.setCentripetalAccelerationVar(round(random.nextFloat() * 100, 1));

        particleParameters.setTangentalAcceleration(round(random.nextFloat() * 100, 1));
        particleParameters.setTangentalAccelerationVar(round(random.nextFloat() * 100, 1));

        particleParameters.setColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        particleParameters.setUpdateColor(random.nextInt(2) == 1);

        particleParameters.setEndColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        boolean varColor = random.nextInt(2) == 1;
        particleParameters.setColorVar(varColor ? random.nextInt(255) : 0, varColor ? random.nextInt(255) : 0,
                varColor ? random.nextInt(255) : 0);

        boolean varEndColor = random.nextInt(2) == 1;
        particleParameters.setEndColorVar(varEndColor ? random.nextInt(255) : 0, varEndColor ? random.nextInt(255) : 0,
                varEndColor ? random.nextInt(255) : 0);

        boolean varEndSpeed = random.nextInt(2) == 1;
        particleParameters.setEndSpeedFactor(varEndSpeed ? random.nextFloat() : 1);

        particleParameters.setAlpha(round(random.nextFloat(), 2));
        particleParameters.setAlphaVar(round(random.nextFloat(), 2));
        particleParameters.setEndAlpha(round(random.nextFloat(), 2));
        particleParameters.setEndAlphaVar(round(random.nextFloat(), 2));
        particleParameters.setUpdateAlpha(random.nextInt(2) == 1);

        boolean randomizeRotation = random.nextInt(2) == 1;
        if (randomizeRotation) {
            boolean rotatedForward = random.nextInt(2) == 1;
            if (rotatedForward) {
                particleParameters.setRotatedForward(true);
                particleParameters.setRotation(0);
                particleParameters.setRotationVar(0);
                particleParameters.setRotationSpeed(0);
                particleParameters.setRotationSpeedVar(0);
                particleParameters.setRotationAcceleration(0);
                particleParameters.setRotationAccelerationVar(0);
            } else {
                particleParameters.setRotatedForward(false);
                particleParameters.setRotation(round(random.nextFloat() * 360 - 180, 2));
                particleParameters.setRotationVar(round(random.nextFloat() * 180, 2));
                particleParameters.setRotationSpeed(round(random.nextFloat() * 100, 2));
                particleParameters.setRotationSpeedVar(round(random.nextFloat() * 100, 2));
                particleParameters.setRotationAcceleration(round(random.nextFloat() * 100, 2));
                particleParameters.setRotationAccelerationVar(round(random.nextFloat() * 100, 2));
            }
        } else {
            particleParameters.setRotation(0);
            particleParameters.setRotationVar(0);
            particleParameters.setRotationSpeed(0);
            particleParameters.setRotationSpeedVar(0);
            particleParameters.setRotationAcceleration(0);
            particleParameters.setRotationAccelerationVar(0);
        }

        int origin = random.nextInt(ParticlePositioner.SpawnArea.values().length);
        var spawnArea = ParticlePositioner.SpawnArea.values()[origin];
        particleSystem.getSpawnSettings().setSpawnArea(spawnArea);

        float count = round(random.nextFloat() * 750, 1);
        float countVar = round(random.nextFloat() * (count / 2f), 1);
        particleSystem.getSettings().setDefaultCount((int) count);
        particleSystem.getSettings().setDefaultCountVar((int) countVar);
        particleSystem.getSettings().setDefaultInterval(1.f / count);
        particleSystem.getSettings().setPulsating(false);
        boolean localCenter = random.nextInt(2) == 1;
        particleSystem.getSpawnSettings().setLocalCenter(localCenter);

        var areaRandomizer = random.nextInt(3);
        if (areaRandomizer == 0) {
            particleSystem.getSpawnSettings().setSpawnAreaWidth(0);
            particleSystem.getSpawnSettings().setSpawnAreaHeight(0);

            particleSystem.getSpawnSettings().setSpawnAreaWidthVar(
                    round(random.nextFloat() * game.getWindow().width() / 4, 0));

            particleSystem.getSpawnSettings().setSpawnAreaHeightVar(
                    round(random.nextFloat() * game.getWindow().height() / 4, 0));
        } else if (areaRandomizer == 1) {
            particleSystem.getSpawnSettings().setSpawnAreaWidth(
                    round(random.nextFloat() * game.getWindow().width() / 4, 0));

            particleSystem.getSpawnSettings().setSpawnAreaHeight(
                    round(random.nextFloat() * game.getWindow().height() / 4, 0));

            particleSystem.getSpawnSettings().setSpawnAreaWidthVar(0);
            particleSystem.getSpawnSettings().setSpawnAreaHeightVar(0);
        } else if (areaRandomizer == 2) {
            particleSystem.getSpawnSettings().setSpawnAreaWidthVar(
                    round(random.nextFloat() * game.getWindow().width() / 8, 0));

            particleSystem.getSpawnSettings().setSpawnAreaHeightVar(
                    round(random.nextFloat() * game.getWindow().height() / 8, 0));

            particleSystem.getSpawnSettings().setSpawnAreaWidth(
                    round(random.nextFloat() * game.getWindow().width() / 8, 0));

            particleSystem.getSpawnSettings().setSpawnAreaHeight(
                    round(random.nextFloat() * game.getWindow().height() / 8, 0));
        }

        particleSystem.getSpawnSettings()
                .setSpawnAngle(round(random.nextFloat() * 360 - 180, 1));
        particleSystem.getSpawnSettings()
                .setSpawnAngleVar(round(random.nextFloat() * 180, 1));

    }

    private float round(float value, int decimals) {
        int tmp = (int) Math.pow(10, decimals);
        return (int) (value * tmp) / (float) tmp;
    }

    private class SaveEventHandler implements MouseButtonReleased {

        @Override
        public void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                saveParticleSystem();
            }
        }
    }

    private class LoadEventHandler implements MouseButtonReleased {

        @Override
        public void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                loadParticleSystem();
            }
        }
    }

    private class ResetEventHandler implements MouseButtonReleased {

        @Override
        public void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                particleSystem.setSettings(new ParticleSystemSettings(new ParticlePositioner(), new ParticleParameters()));
            }
        }
    }

    private class RandomizeEventHandler implements MouseButtonReleased {

        @Override
        public void onMouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
            if (button == Mouse.instance().mouseButton1()) {
                randomizeParticleSystem();
            }
        }
    }
}