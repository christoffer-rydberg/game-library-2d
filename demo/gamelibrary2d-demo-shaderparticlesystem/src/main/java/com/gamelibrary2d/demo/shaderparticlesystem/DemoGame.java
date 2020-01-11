package com.gamelibrary2d.demo.shaderparticlesystem;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.GlfwWindow;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;

public class DemoGame extends AbstractGame {

    DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    public void start(Window window) {
        super.start(window);
    }

    private void start() {
        start(GlfwWindow.createWindowed("Shader Particle System"));
    }

    @Override
    protected void onStart() {
        setFrame(new DemoFrame(this));
    }

    @Override
    protected void onExit() {
        // TODO Auto-generated method stub
    }
}