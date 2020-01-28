package com.gamelibrary2d.tools.particlegenerator;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.framework.lwjgl.GlfwWindow;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;
import com.gamelibrary2d.layers.Frame;
import com.gamelibrary2d.layers.FrameDisposal;
import com.gamelibrary2d.tools.particlegenerator.util.Fonts;

public class ParticleGenerator extends AbstractGame {

    private final Frame frame;

    ParticleGenerator() {
        super(new Lwjgl_Framework());
        frame = new ParticleFrame(this);
    }

    public static void main(String[] args) {
        new ParticleGenerator().start(GlfwWindow.createWindowed("Particle Generator", 1280, 900));
    }

    @Override
    public void update(float deltaTime) {
        getWindow().setTitle("Particle Generator (" + getFPS() + ")");
        super.update(deltaTime);
    }

    @Override
    protected void onStart() {

        // Create global resources:
        Fonts.create(this);

        setFrame(frame, FrameDisposal.NONE);
    }

    @Override
    protected void onExit() {
        // TODO Auto-generated method stub
    }
}