package com.gamelibrary2d.demos.shaderparticlesystem;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.objects.AbstractFrame;
import com.gamelibrary2d.objects.FrameLayer;
import com.gamelibrary2d.resources.Texture;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class DemoFrame extends AbstractFrame {

    private FrameLayer layer;

    DemoFrame(Game game) {
        super(game);
    }

    @Override
    protected void onPrepare() {
        try {
            Window window = getGame().getWindow();
            float windowWidth = window.getWidth();
            float windowHeight = window.getHeight();
            BufferedImage init = Texture.load(DemoFrame.class.getResource("/Images/before.jpg"));
            BufferedImage goal = Texture.load(DemoFrame.class.getResource("/Images/after.jpg"));

            var particleSystem = ParticleSystemFactory.create(init, goal, windowWidth / 2f,
                    windowHeight / 2f, this);

            // Adjust scale to fit on screen
            float scaleX = Math.min(1f, 0.5f * windowWidth / init.getWidth());
            float scaleY = Math.min(1f, 0.5f * windowHeight / init.getHeight());
            float scale = Math.min(scaleX, scaleY);

            layer = new FrameLayer();
            layer.getScale().set(scale, scale);
            layer.getScaleAndRotationCenter().set(windowWidth / 2, windowHeight / 2);
            layer.backgroundParticles().add(particleSystem);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onLoad() {
        add(layer);
    }

    @Override
    protected void onFinish() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBegin() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEnd() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onUpdate(float deltaTime) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onReset() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDispose() {
        // TODO Auto-generated method stub

    }
}