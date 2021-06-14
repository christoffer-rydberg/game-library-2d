package com.gamelibrary2d.demos.shaderparticlesystem;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.components.frames.AbstractFrame;
import com.gamelibrary2d.components.frames.InitializationContext;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.components.containers.DefaultLayerObject;
import com.gamelibrary2d.components.containers.LayerObject;
import com.gamelibrary2d.particle.systems.ParticleSystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DemoFrame extends AbstractFrame {

    private final Game game;

    DemoFrame(Game game) {
        this.game = game;
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        try {
            Window window = game.getWindow();
            float windowWidth = window.getWidth();
            float windowHeight = window.getHeight();
            BufferedImage init = ImageIO.read(DemoFrame.class.getResource("/Images/before.jpg"));
            BufferedImage goal = ImageIO.read(DemoFrame.class.getResource("/Images/after.jpg"));

            ParticleSystem particleSystem = ParticleSystemFactory.create(init, goal, windowWidth / 2f,
                    windowHeight / 2f, this);

            // Adjust scale to fit on screen
            float scaleX = Math.min(1f, 0.5f * windowWidth / init.getWidth());
            float scaleY = Math.min(1f, 0.5f * windowHeight / init.getHeight());
            float scale = Math.min(scaleX, scaleY);

            LayerObject<Renderable> layer = new DefaultLayerObject<>();
            layer.setScale(scale, scale);
            layer.setScaleAndRotationCenter(windowWidth / 2, windowHeight / 2);
            layer.add(particleSystem);
            add(layer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {

    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }
}