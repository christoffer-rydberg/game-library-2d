package com.gamelibrary2d.demos.shaderparticlesystem;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.layers.DynamicLayer;
import com.gamelibrary2d.resources.Texture;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class DemoFrame extends AbstractFrame {

    DemoFrame(Game game) {
        super(game);
    }

    @Override
    protected void onInitialize(FrameInitializer initializer) {
        try {
            Window window = getGame().getWindow();
            float windowWidth = window.width();
            float windowHeight = window.height();
            BufferedImage init = Texture.load(DemoFrame.class.getResource("/Images/before.jpg"));
            BufferedImage goal = Texture.load(DemoFrame.class.getResource("/Images/after.jpg"));

            var particleSystem = ParticleSystemFactory.create(init, goal, windowWidth / 2f,
                    windowHeight / 2f, this);

            // Adjust scale to fit on screen
            float scaleX = Math.min(1f, 0.5f * windowWidth / init.getWidth());
            float scaleY = Math.min(1f, 0.5f * windowHeight / init.getHeight());
            float scale = Math.min(scaleX, scaleY);

            var layer = new DynamicLayer<>();
            layer.setScale(scale, scale);
            layer.setScaleAndRotationCenter(windowWidth / 2, windowHeight / 2);
            layer.add(particleSystem);
            add(layer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}